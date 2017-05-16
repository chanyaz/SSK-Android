package com.gamesparks.sdk;


public interface GSEventConsumer<T> {
	void onEvent(T event);
}
