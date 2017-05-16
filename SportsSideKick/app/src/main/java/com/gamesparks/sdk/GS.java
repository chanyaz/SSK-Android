package com.gamesparks.sdk;

import com.gamesparks.sdk.api.AbstractRequest;
import com.gamesparks.sdk.api.autogen.GSMessageHandler;
import com.gamesparks.sdk.api.autogen.GSRequestBuilder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class GS {

    protected LinkedList<QueuedRequest> sendQueue = new LinkedList<>();
    private LinkedList<Runnable> recieveQueue = new LinkedList<>();

	protected List<QueuedRequest> persistentQueue = new ArrayList<>();
	private String persistentQueuePlayerId = "";
	protected boolean durableQueueDirty = false;
	protected boolean durableQueuePaused = false;

    protected LinkedList<GSConnection> connections = new LinkedList<GSConnection>();

    private Thread workerThread;

    protected boolean available = false;
    protected boolean authenticated = false;
    
	private long nextReconnect = 0;
	
	private GSMessageHandler messageHandler = new GSMessageHandler();
	
	private GSRequestBuilder requestBuilder = new GSRequestBuilder(this);
	
	private GSEventConsumer<Boolean> onAvailable;
	
	private GSEventConsumer<String> onAuthenticated;
	
	String secret, serviceUrl, currentUrl, sessionId;
	
	IGSPlatform platform;
	
	protected static long currentRequestPrefix = 0L;
	
	protected boolean autoUpdate;
	
	public GS(String apiKey, String secret, String credential, boolean liveMode, boolean autoUpdate, IGSPlatform platform)
	{	
		int index;
		String urlAddition = apiKey;
	
		if (platform == null)
		{
			throw new IllegalArgumentException("platform cannot be null");
		}
		
		this.secret = secret;
		this.platform = platform;
		this.autoUpdate = autoUpdate;
		
		if (credential == null || credential.length() == 0)
		{
			credential = "device";
		}
		
		if (liveMode)
		{
			serviceUrl = "wss://live-";
		}
		else
		{
			serviceUrl = "wss://preview-";
		}
		
		index = secret.indexOf(":"); 
		if (index > 0)
		{
			credential = "secure";
		
			urlAddition = secret.substring(0, index) + "/" + urlAddition;
		}
		
		serviceUrl += urlAddition + ".ws.gamesparks.net/ws/" + credential + "/" + urlAddition;
		
		currentUrl = serviceUrl;
	}
	
	/**
	 * starts the worker process
	 */
	public void start(){
		stop();
		
		workerThread = new Thread( new GSWorker() );
		workerThread.start();
	}
	
	/**
	 * stops the worker process
	 */
	public void stop(){
		if(workerThread != null){
			workerThread.interrupt();
			workerThread = null;
		}
		
		synchronized(connections)
		{
			List<GSConnection> toRemove = new ArrayList<GSConnection>();
            
            for (GSConnection connection : connections)
            {
            	if (connection.pendingQueue.size() == 0)
                {
            		connection.close();
            		toRemove.add(connection);
                }
            }
            for (GSConnection connection : toRemove)
            {
            	connections.remove(connection);
            }
		}
		
		currentUrl = serviceUrl;
		
		available = false;
	    authenticated = false;
	}
	
	public void resetAuthTokenAndStop() {
		stop();
		
	    setAuthToken("0");
		setPlayerId("");
	}
	
	/**
	 * runs any runnables in the receive queue
	 * This ensures all tasks are run on the main (calling) thread
	 */
	public void update(){
		while (recieveQueue.size() > 0){
        	synchronized (recieveQueue){
        		try{
        			Runnable job = recieveQueue.remove();
        			if(job != null)
        				platform.executeOnMainThread(job);
        		}catch(Exception e){
        			
        		}
            }
        }
	}
	
	protected void processQueues()
    {
		try
        {
			ConnectIfRequired();
			TrimOldConnections();
            ProcessPersistentQueue();
            ProcessSendQueue();
            ProcessPendingQueue();
            
            if(autoUpdate){
            	update();
            }
        }
        catch (Exception e)
        {
            //TODO : Something
        }
    }
	
	protected void ConnectIfRequired()
    {
		if (nextReconnect < System.currentTimeMillis()) {
			synchronized(connections) 
			{
				if (connections.size() == 0 || !connections.get(0).isRunning()) {
					NewConnection ();
				}
			}
		}
		
    }
	
	protected void NewConnection()
    {
    	synchronized (connections)
        {
    		try {
    			connections.addFirst(new GSConnection(this));
    		} catch (Exception e) {
    			stop();
    		}
        }
    }
	
	protected void TrimOldConnections()
    {
		synchronized (connections)
        {
            List<GSConnection> toRemove = new ArrayList<GSConnection>();
            
            for (GSConnection connection : connections)
            {
                if (connection.pendingQueue.size() == 0 && !connection.isRunning())
                {
                    connection.close();
                    toRemove.add(connection);
                }
            }
            for (GSConnection connection : toRemove)
            {
            	connections.remove(connection);
            }
        }
    }
	
	private String loadPersistentQueue(final String playerId)
	{
		String queue = "";
		String line;
		
		if ("".equals(playerId)) {
			return "";
		}
		
		try {
			File location = new File(platform.getWritableLocation().getAbsolutePath(), playerId);
			
			platform.logMessage("path: " + location.getAbsolutePath());
			
			if (location != null && location.exists()) {
				FileReader fr = new FileReader(location.getAbsoluteFile());
				BufferedReader br = new BufferedReader(fr);
				
				while ((line = br.readLine()) != null) {
					queue = queue.concat(line);
				}
				
				br.close();
			}
		} catch (Exception e) {
			platform.logError(e);
		}
		
		return queue;
	}
	
	private void savePersistentQueue(String queue)
	{
		if ("".equals(platform.getPlayerId())) {
			return;
		}
		
		try {
			File location = new File(platform.getWritableLocation().getAbsolutePath(), platform.getPlayerId());
			
			if (location != null) {
				if (!location.exists()) {
					location.createNewFile();
				}
	
				FileWriter fw = new FileWriter(location.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				
				bw.write(queue);
				bw.close();
			}
		} catch (Exception e) {
			platform.logError(e);
		}
	}
	
	protected void resetPersistentQueue()
	{
		if ("".equals(platform.getPlayerId())) {
			return;
		}
		
		try {
			File location = new File(platform.getWritableLocation().getAbsolutePath(), platform.getPlayerId());
			
			if (location != null) {
				if (location.exists()) {
					location.delete();
				}
			}
		} catch (Exception e) {
			platform.logError(e);
		}
	}
	
	private AbstractRequest<?> stringToRequest(String line)
	{
		try {
			JSONParser jsonParser = new JSONParser();
			Map map = (Map)jsonParser.parse(line);
			String json = null;
			String signature = null;
			String properSig = null;
			String className = null;
			
			if (map.get("rq") != null) {
				json = map.get("rq").toString();
				
				properSig = platform.getHmac(json, secret).toString();
			}
			
			if (map.get("sq") != null) {
				signature = map.get("sq").toString();
			}
			
			if (properSig != null && signature != null && properSig.equals(signature)) {
				map = (Map)jsonParser.parse(json);
				
				if (map.get("@class") != null) {
					className = map.get("@class").toString().substring(1);
					
					platform.logMessage(className);
					
					AbstractRequest<?> request = null;
					Class c = requestBuilder.getClass();
		            Method[] m = c.getDeclaredMethods();
		            
		            for (int i = 0; i < m.length; i++)
		            {
		            	if (m[i].toString().contains(className))
		            	{
		            		request = (AbstractRequest<?>)m[i].invoke(requestBuilder);
		            		
		            		request.getBaseData().clear();
		            		request.getBaseData().putAll(map);
		            		
		            		return request;
		            	}
		            }
				}
			}
		} catch (Exception e) {
			platform.logError(e);
		}
		
		return null;
	}
	
	private void initialisePersistentQueue(final String playerId)
	{
		if (persistentQueuePlayerId.equals(playerId)) 
		{
			return;
		}
		
		boolean prevDurableQueuePaused = durableQueuePaused;
		
		durableQueuePaused = true;
		
		List<QueuedRequest> queueArray = new ArrayList<>();
		String queueString = loadPersistentQueue(playerId);
		
		if (queueString.length() > 0)
		{
			String[] lines = queueString.split(System.getProperty("line.separator"));
			
			for (int i = 0; i < lines.length; i ++)
			{
				String line = lines[i].trim();
				
				if (line.length() > 0)
				{
					QueuedRequest queuedRequest = new QueuedRequest();
					queuedRequest.request = stringToRequest(line);
					
					if (queuedRequest != null)
					{
						queueArray.add(queuedRequest);
					}		
				}
			}
		}
		
		persistentQueue.clear();
		persistentQueue = queueArray;
		
		persistentQueuePlayerId = playerId;
		
		durableQueuePaused = prevDurableQueuePaused;
		
		platform.logMessage("persistentQueue COUNT: " + persistentQueue.size());
	}
	
	@SuppressWarnings("unchecked")
	protected void writeDurableQueueIfDirty()
	{
		Iterator<QueuedRequest> persistentQueueIterator = persistentQueue.iterator();
		
		if (durableQueueDirty)
		{
			durableQueueDirty = false;
			
			String lines = "";
		
			while (persistentQueueIterator.hasNext()) 
			{
				QueuedRequest persistentRequest = persistentQueueIterator.next();
				String json = JSONObject.toJSONString(persistentRequest.request.getBaseData());
				
				Map<String, Object> queuedItem = new JSONObject();
				
				queuedItem.put("rq", json);
				queuedItem.put("sq", platform.getHmac(json, secret));
				
				String line = JSONObject.toJSONString(queuedItem);
				
				lines = lines.concat(line);
				lines = lines.concat(System.getProperty("line.separator"));
			}
			
			savePersistentQueue(lines);
		}
	}
	
	protected void ProcessPersistentQueue()
	{
		Iterator<QueuedRequest> persistentQueueIterator = persistentQueue.iterator();
		
		writeDurableQueueIfDirty();
		
		if (!durableQueuePaused && authenticated)
		{
			while (persistentQueueIterator.hasNext()) 
			{
        		QueuedRequest persistentRequest = persistentQueueIterator.next();
                
                if (connections.size() > 0 && connections.get(0).isReady()) {
	                if (persistentRequest.durableRetryTicks == 0 || persistentRequest.durableRetryTicks < System.currentTimeMillis()) {
	                	persistentRequest.durableRetryTicks = System.currentTimeMillis() + 10000;
	                	
	                	persistentRequest.cancelTime = (persistentRequest.request.getResponseTimeout() * 1000) + System.currentTimeMillis();
	                	persistentRequest.request.getBaseData().put("requestId", "d_" + System.currentTimeMillis() + "_" + ++currentRequestPrefix);
	               
	        			synchronized (sendQueue)
	        			{
	        	            sendQueue.addLast(persistentRequest);
	        	        }  
	                }
                } else {
                	return;
                }   
	        }
		}
	}
	
	protected void ProcessSendQueue()
    {
        while (sendQueue.size() > 0)
        {
        	synchronized (sendQueue)
            {
                QueuedRequest queuedRequest = sendQueue.getFirst();
                
                if (queuedRequest.cancelTime > 0 && queuedRequest.cancelTime < System.currentTimeMillis()){
                	sendQueue.remove(queuedRequest);
                    cancelRequest(queuedRequest.request);
                    continue;
                }

                if (connections.size() > 0 && connections.get(0).isReady()){
                	connections.get(0).send(queuedRequest);
                	queuedRequest.waitForResponseTime = System.currentTimeMillis() + (queuedRequest.request.getResponseTimeout() * 1000);
                    sendQueue.remove(queuedRequest);
                } else {
                	return;
                }
            }
        }
    }
	
	protected void ProcessPendingQueue()
    {
		synchronized (connections) 
		{
			for (GSConnection connection : connections){
				synchronized (connection)
				{
	            	Iterator<Entry<String, QueuedRequest>> entries = connection.pendingQueue.entrySet().iterator();
	            	while(entries.hasNext()){
	            		QueuedRequest pendingRequest = entries.next().getValue();
	            		if(pendingRequest.waitForResponseTime > 0 && pendingRequest.waitForResponseTime < System.currentTimeMillis()){
	                		entries.remove();
	                		cancelRequest(pendingRequest.request);
	                	}
	            	}
	            }
	        }
		}
    }
	
	protected void cancelRequest(AbstractRequest<?> request){
		
		if (request.isDurable()) {
            return;
        }
		Map<String,Object> response = new HashMap<>();
		response.put("error", "timeout");
		response.put("requestId", request.getBaseData().get("requestId"));
		request.onResponse(response);
		
	}
	
	public void send(AbstractRequest<?> request)
    {
		QueuedRequest queuedRequest = new QueuedRequest();
		queuedRequest.request = request;
		
		if (request.isDurable())
		{
			durableQueueDirty = true;
			
			synchronized (persistentQueue)
			{
				persistentQueue.add(queuedRequest);
	        }
		}
		else
		{
			queuedRequest.cancelTime = (request.getResponseTimeout() * 1000) + System.currentTimeMillis();
			request.getBaseData().put("requestId", System.currentTimeMillis() + "_" + ++currentRequestPrefix);
	
			synchronized (sendQueue)
			{
	            sendQueue.addLast(queuedRequest);
	        }
		}   
    }
	
	private class GSWorker implements Runnable {
		public void run() {
			while(!Thread.interrupted()){
				GS.this.processQueues();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}

	URI getCurrentURI(){
		try {
			return new URI(currentUrl);
		} catch (URISyntaxException e) {}
		return null;
	}

	void onResponse(final Map<String, Object> map, GSConnection gsConnection) {
		if (map.containsKey("requestId")) {
			final QueuedRequest qr = gsConnection.pendingQueue.remove(map.get("requestId").toString());
			if (qr != null) {
				if (qr.durableRetryTicks > 0) {
					durableQueueDirty = persistentQueue.remove(qr);
					
					writeDurableQueueIfDirty();
				}
				
				synchronized (recieveQueue) {
					recieveQueue.add(new Runnable() {
						
						@Override
						public void run() {
							qr.request.onResponse(map);
							
						}
					});	
				}
			}
		}
	}
	
	public GSMessageHandler getMessageHandler() {
		return messageHandler;
	}
	
	public GSRequestBuilder getRequestBuilder() {
		return requestBuilder;
	}
	
	public static class QueuedRequest {
		long cancelTime;
		long waitForResponseTime;
		long durableRetryTicks;
		AbstractRequest<?> request;
	}

	public void onMessage(final Map<String,Object> map) {
		recieveQueue.add(new Runnable() {
			@Override
			public void run() {
				messageHandler.onMessageReceived(map);
				
			}
		});
	}
	
	public void setOnAvailable(GSEventConsumer<Boolean> onAvailable) {
		this.onAvailable = onAvailable;
	}
	
	public void setOnAuthenticated(GSEventConsumer<String> onAuthenticated) {
		this.onAuthenticated = onAuthenticated;
	}

	void onAvailablilityStateChange(final boolean available, final GSConnection gsConnection) {
		synchronized (connections) {
			if(connections.getFirst() == gsConnection && available != this.available){
				this.available = available;
				if (onAvailable != null)
				{
					recieveQueue.add(new Runnable() {
						@Override
						public void run() {
							onAvailable.onEvent(available);
						}
					});
				}
			}	
		}
	}

	public void setPlayerId(final String playerId) {
		if (!"".equals(playerId)) {
			authenticated = true;
			
			initialisePersistentQueue(playerId);
		} else {
			authenticated = false;
		}
		
		recieveQueue.add(new Runnable() {
			@Override
			public void run() {
				platform.setPlayerId(playerId);
				if(onAuthenticated != null && !"".equals(playerId)) {
					onAuthenticated.onEvent(playerId);
				}
			}
		});
	}
	
	public void setAuthToken(final String authToken) {
		recieveQueue.add(new Runnable() {
			@Override
			public void run() {
				platform.setAuthToken(authToken);
			}
		});
	}
	
	public boolean isAuthenticated() {
		return authenticated;
	}
}
