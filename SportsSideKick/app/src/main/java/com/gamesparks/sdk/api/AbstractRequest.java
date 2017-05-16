package com.gamesparks.sdk.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.gamesparks.sdk.GS;
import com.gamesparks.sdk.GSEventConsumer;

public abstract class AbstractRequest<RESPONSE_CLASS extends AbstractResponse> extends GSData {

	private GSEventConsumer<RESPONSE_CLASS> responseHandler;

	private GS gs;
	
	protected int responseTimeout = 10;
	protected boolean durable;
	
	public AbstractRequest(GS gs){
		this.gs = gs;
	}
	
	public void send(GSEventConsumer<RESPONSE_CLASS> responseHandler){
		this.responseHandler = responseHandler;
		gs.send(this);
	}
	
	@SuppressWarnings("unchecked")
	public void setResponseHandler(Object responseHandler) {
		this.responseHandler = (GSEventConsumer<RESPONSE_CLASS>)responseHandler;
	}
	
	public void onResponse(Map<String,Object> data){
		if (responseHandler != null) {
			responseHandler.onEvent(createResponse(data));
		}
	}
	
	protected abstract RESPONSE_CLASS createResponse(Map<String, Object> data);

	public int getResponseTimeout() {
		return responseTimeout;
	}
	
	public void setDurable(boolean durable) {
		this.durable = durable;
	}
	
	public boolean isDurable() {
		return durable;
	}
	
	protected void setDateField(String key, Date date){
		data.put(key, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'").format(date));
	}
	
}
