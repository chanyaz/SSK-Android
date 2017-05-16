package com.gamesparks.sdk;

import com.gamesparks.sdk.GS.QueuedRequest;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GSConnection extends WebSocketAdapter {

	GS gs;
	
	WebSocket ws;
	
	ConcurrentHashMap<String, QueuedRequest> pendingQueue = new ConcurrentHashMap<>();
	
	private JSONParser jsonParser = new JSONParser();
	
	boolean ready = false;
	
	private static WebSocketFactory factory = new WebSocketFactory();
	
	public GSConnection(GS gs) throws Exception {
		gs.platform.logMessage("GSConnection. gs.currentUrl: " + gs.currentUrl);
		this.gs = gs;
		try {
			ws = factory.createSocket(gs.currentUrl);
			ws.setPingInterval(2000);
			ws.addListener(this);
			ws.connect();
		} catch (IOException | WebSocketException e) {
			if (e.getMessage().contains("UNKNOWN SERVICE") || e.getMessage().contains("INVALID LOCATION")) {
				gs.platform.logMessage(e.getMessage());
				
				if (ws != null) {
					ready = false;
					
					ws.removeListener(this);
					ws.disconnect();
					ws = null;
				}
						
				throw new Exception();
			} else {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onDisconnected(WebSocket websocket,
			WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame,
			boolean closedByServer) {
		gs.platform.logMessage("onDisconnected. websocket: " + websocket
				+ " serverCloseFrame: " + serverCloseFrame
				+ " clientCloseFrame: " + clientCloseFrame
				+ " closedByServer: " + closedByServer);
		gs.onAvailablilityStateChange(false, this);
	}
	

	@Override
	public void onError(WebSocket websocket, WebSocketException cause) {
		ready = false;
		gs.onAvailablilityStateChange(false, this);
	}
	
	
	@Override
	public void onTextMessage(WebSocket websocket, String text) {
		
		gs.platform.logMessage("onTextMessage. websocket: " + websocket + " text: "
				+ text);
		
		try {
			
			Map map = (Map) jsonParser.parse(text);
			
			if (map.get("connectUrl") != null) {
				gs.currentUrl = map.get("connectUrl").toString();
				websocket.disconnect();
			}
			
			if (map.containsKey("authToken")){
				gs.setAuthToken(map.get("authToken").toString());
			}
			
			String type = map.get("@class").toString();
			
			if (type.equals(".AuthenticatedConnectResponse")) {
				
				/*if (map.get("connectUrl") != null) {
					gs.currentUrl = map.get("connectUrl").toString();
					websocket.disconnect();
				} else*/ if (map.get("nonce") != null) {
					processNonce(map.get("nonce").toString());
				} else if (map.get("sessionId") != null) {
					gs.sessionId = map.get("sessionId").toString();
					
					if(map.containsKey("authToken")){
						gs.setAuthToken(map.get("authToken").toString());
						
						if(map.containsKey("userId"))
						{
							gs.setPlayerId(map.get("userId").toString());
						}
					} else {
						gs.setAuthToken("0");
						gs.setPlayerId("");
					}
					
					ready = true;
					
					gs.onAvailablilityStateChange(true, this);
				}
			} else if (type.endsWith("Response")){
				if(map.containsKey("userId"))
				{
					gs.setPlayerId(map.get("userId").toString());
				}
				
				gs.onResponse(map, this);
			} else if (type.endsWith("Message")){
				gs.onMessage(map);
			}

		} catch (Exception e) {
			gs.platform.logError(e);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void processNonce(String nonce) throws Exception {
		
		Map<String, Object> authenticatedConnectRequest = new JSONObject();
		authenticatedConnectRequest.put("@class", ".AuthenticatedConnectRequest");
		authenticatedConnectRequest.put("hmac", gs.platform.getHmac(nonce, gs.secret));
		
		if (gs.platform.getAuthToken() != null && !gs.platform.getAuthToken().equals("0") && !gs.platform.getAuthToken().equals(""))
		{
			authenticatedConnectRequest.put("authToken", gs.platform.getAuthToken());
		}
		
		if(gs.sessionId != null){
			authenticatedConnectRequest.put("sessionId", gs.sessionId);
		}
		
		authenticatedConnectRequest.put("platform", "Service API Tests");
		send(authenticatedConnectRequest);
	}

	void send(QueuedRequest request) {
		pendingQueue.put(request.request.getAttribute("requestId").toString(), request);
		send(request.request.getBaseData());
	}
	
	private void send(Map<String,Object> data) {
		String json = JSONObject.toJSONString(data);
		gs.platform.logMessage(json);
		ws.sendText(json);
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public boolean isRunning(){
		
		return (ws.getState() == WebSocketState.OPEN || ws.getState() == WebSocketState.CREATED || ws.getState() == WebSocketState.CONNECTING); 
	}

	public void close() {
		ws.disconnect();
	}
	
	
		
}
