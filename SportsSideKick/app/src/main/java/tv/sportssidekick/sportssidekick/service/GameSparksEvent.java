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

    public enum Type {
            AUDIO_FILE_UPLOADED,
            MESSAGE_IMAGE_FILE_UPLOADED,
            PROFILE_IMAGE_FILE_UPLOADED,
            VIDEO_IMAGE_FILE_UPLOADED,
            VIDEO_FILE_UPLOADED,
            // wall post
            POST_IMAGE_FILE_UPLOADED,
            POST_VIDEO_IMAGE_FILE_UPLOADED,
            POST_VIDEO_FILE_UPLOADED,
            // create chat
            EDIT_CHAT_IMAGE_FILE_UPLOADED,
            CREATE_CHAT_IMAGE_FILE_UPLOADED
    }

}
