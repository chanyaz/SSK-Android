package tv.sportssidekick.sportssidekick.model.im;

/**
 * Created by Filip on 4/25/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ChatNotificationsEvent {

    Object data;
    Key key;

    public enum Key {
        UPDATED_CHAT_USERS, // iOS - kUpdatedChatUsers
        CHANGED_CHAT_MESSAGE, // iOS - kChangedChatMessage
        UPDATED_CHAT_MESSAGES, // iOS - kUpdatedChatMessages
        SET_CURRENT_CHAT // iOS - kSetCurrentChat
    }


    public ChatNotificationsEvent(Object data, Key key) {
        this.data = data;
        this.key = key;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Key getKey() {
        return key;
    }
}
