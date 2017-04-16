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
            // user, login, logout...
            SIGNED_OUT,
            LOGIN_SUCCESSFUL,
            LOGGED_OUT,
            LOGIN_FAILED,
            ACCOUNT_DETAILS_ERROR,
            REGISTRATION_SUCCESSFUL,
            REGISTRATION_ERROR,
            PASSWORD_RECOVERY_ERROR,
            PASSWORD_RECOVERY_SUCCESSFUL,
            USER_STATE_UPDATE_ERROR,
            USER_STATE_UPDATE_SUCCESSFUL,
            ALL_DATA_ACQUIRED,
            CHAT_NEW_MESSAGE,
            CHAT_NEW_MESSAGE_ADDED,
            CHAT_NEXT_PAGE_LOADED,
            CHAT_MESSAGE_UPDATED,
            CHAT_CREATED,
            CHAT_REMOVED,
            CHAT_REMOVED_PROCESSED,
            CHAT_DELETED_PROCESSED,
            CHAT_UPDATED,
            CHAT_USERS_UPDATED,
            CLEAR_CHATS,
            PUBLIC_CHAT_DETECTED,
            GLOBAL_CHAT_DETECTED,
            USER_CHAT_DETECTED,
            AUDIO_FILE_UPLOADED,
            MESSAGE_IMAGE_FILE_UPLOADED,
            PROFILE_IMAGE_FILE_UPLOADED,
            VIDEO_IMAGE_FILE_UPLOADED,
            VIDEO_FILE_UPLOADED,
            TYPING,
            // wall post
            POST_IMAGE_FILE_UPLOADED,
            POST_VIDEO_IMAGE_FILE_UPLOADED,
            POST_VIDEO_FILE_UPLOADED
    }

}
