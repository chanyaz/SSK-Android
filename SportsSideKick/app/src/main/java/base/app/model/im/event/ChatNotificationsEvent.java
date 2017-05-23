package base.app.model.im.event;

import base.app.model.im.ChatInfo;

/**
 * Created by Filip on 4/25/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ChatNotificationsEvent {

    ChatInfo chatInfo;
    Object chatId;
    Key key;

    public enum Key {
        UPDATED_CHAT_USERS, // iOS - kUpdatedChatUsers
        CHANGED_CHAT_MESSAGE, // iOS - kChangedChatMessage
        UPDATED_CHAT_MESSAGES, // iOS - kUpdatedChatMessages
        SET_CURRENT_CHAT // iOS - kSetCurrentChat
    }


    public ChatNotificationsEvent(ChatInfo chatInfo, Key key) {
        this.chatInfo = chatInfo;
        this.key = key;
    }


    public ChatNotificationsEvent(Object data, Key key) {
        this.chatId = data;
        this.key = key;
    }


    public ChatInfo getChatInfo() {
        return chatInfo;
    }

    public Object getChatId() {
        return chatId;
    }

    public Key getKey() {
        return key;
    }
}
