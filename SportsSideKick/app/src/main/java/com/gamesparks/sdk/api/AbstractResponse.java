package com.gamesparks.sdk.api;

import java.util.Map;

public class AbstractResponse extends GSData {

	public AbstractResponse(Map data) {
		super(data);
	}
	
	public boolean hasErrors(){
		return data.containsKey("error");
	}
	
	@SuppressWarnings("unchecked")
	public GSData getScriptData(){
		if(data.containsKey("scriptData") && data.get("scriptData") instanceof Map){
			return new GSData((Map<String,Object>)data.get("scriptData"));
		}
		return null;
	}
}
