package tv.sportssidekick.sportssidekick.service;

/**
 * Created by Filip on 12/14/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class GameSparksEvent extends BusEvent {

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

    public GameSparksEvent(String message, Type eventType) {
        super("");
        this.message = message;
        this.eventType = eventType;
    }

    public GameSparksEvent(String message) {
        super("");
        this.message = message;
    }

    public GameSparksEvent(String message, Type eventType, Object data) {
        super("");
        this.message = message;
        this.eventType = eventType;
        this.data = data;
    }

    public String getFilterId() {
        return filterId;
    }

    public GameSparksEvent setFilterId(String filterId) {
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
            CHAT_USERS_DOWNLOADED,
            CHAT_CREATED,
            NEXT_PAGE_LOADED_PROCESSED,
            CHAT_REMOVED_PROCESSED,
            CHAT_DELETED_PROCESSED,
            USER_INFO_BY_ID,
            PUBLIC_CHAT_DETECTED,
            GLOBAL_CHAT_DETECTED,
            USER_CHAT_DETECTED,
            SIGNED_OUT,
            AUDIO_FILE_UPLOADED,
            MESSAGE_IMAGE_FILE_UPLOADED,
            PROFILE_IMAGE_FILE_UPLOADED,
            VIDEO_IMAGE_FILE_UPLOADED,
            VIDEO_FILE_UPLOADED,
            FRIEND_DOWNLOADED,
            TYPING,
            ACCOUNT_DETAILS_ERROR,
            REGISTRATION_ERROR,
            REGISTRATION_SUCCESSFUL,
            PASSWORD_RECOVERY_ERROR,
            PASSWORD_RECOVERY_SUCCESSFUL,
            USER_STATE_UPDATE_ERROR,
            USER_STATE_UPDATE_SUCCESSFUL,
    }

}
