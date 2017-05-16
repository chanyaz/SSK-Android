package com.gamesparks.sdk;

import java.io.File;

public interface IGSPlatform {

	File getWritableLocation();
	
	void executeOnMainThread(Runnable job);
	
	String getPlayerId();
	
	String getAuthToken();
	
	void setPlayerId(String playerId);
	
	void setAuthToken(String authToken);

	Object getHmac(String nonce, String secret);
	
	void logMessage(String msg);
	void logError(Throwable t);
	
}
