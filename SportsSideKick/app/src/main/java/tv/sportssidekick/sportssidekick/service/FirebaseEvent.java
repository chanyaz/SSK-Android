package tv.sportssidekick.sportssidekick.service;

/**
 * Created by Filip on 12/14/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class FirebaseEvent extends BusEvent {

    private String message;
    private String filterId;

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

    public String getFilterId() {
        return filterId;
    }

    public FirebaseEvent setFilterId(String filterId) {
        this.filterId = filterId;
        return this;
    }

    public enum Type {
        LOGIN_SUCCESSFUL,
        LOGIN_FAILED,
        ALL_DATA_ACQUIRED,
        NEW_MESSAGE,
        NEW_MESSAGE_ADDED,
        NEXT_PAGE_LOADED,
        MESSAGE_UPDATED,
        CHAT_REMOVED,
        CHAT_UPDATED,
        CHAT_CREATED,
        NEXT_PAGE_LOADED_PROCESSED,
        CHAT_REMOVED_PROCESSED,
        CHAT_DELETED_PROCESSED,
        USER_INFO_BY_ID,
        PUBLIC_CHAT_DETECTED,
        USER_CHAT_DETECTED,
        SIGNED_OUT,
        AUDIO_FILE_UPLOADED,
        MESSAGE_IMAGE_FILE_UPLOADED,
        VIDEO_IMAGE_FILE_UPLOADED,
        VIDEO_FILE_UPLOADED,
    }

}
