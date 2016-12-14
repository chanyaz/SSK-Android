package tv.sportssidekick.sportssidekick.service;

import tv.sportssidekick.sportssidekick.service.BusEvent;

/**
 * Created by Filip on 12/14/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class FirebaseEvent extends BusEvent {

    private String message;

    public String getMessage() {
        return message;
    }

    public Type getEventType() {
        return eventType;
    }

    public Object getData() {
        return data;
    }

    private Type eventType;
    private Object data;

    public FirebaseEvent(String message, Type eventType) {
        super("");
        this.message = message;
        this.eventType = eventType;
    }

    public FirebaseEvent(String message) {
        super("");
        this.message = message;
    }

    public FirebaseEvent(String message, Type eventType, Object data) {
        super("");
        this.message = message;
        this.eventType = eventType;
        this.data = data;
    }

    public enum Type {
        ALL_USERS_ACQUIRED, NEW_MESSAGE, NEW_MESSAGE_ADDED, NEXT_PAGE_LOADED, MESSAGE_UPDATED, CHAT_REMOVED, CHAT_UPDATED, CHAT_CREATED, NEXT_PAGE_LOADED_PROCESSED, CHAT_REMOVED_PROCESSED, CHAT_DELETED_PROCESSED, USER_INFO_BY_ID, PUBLIC_CHAT_DETECTED, USER_CHAT_DETECTED,
    }

}
