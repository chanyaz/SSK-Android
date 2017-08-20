package base.app.model.im.event;

import base.app.model.im.ChatInfo;
import base.app.model.im.ImsMessage;

/**
 * Created by Filip on 4/25/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ChatNotificationsEvent {

    ChatInfo chatInfo;
    String chatInfoId;
    ImsMessage message;
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
    public ChatNotificationsEvent(String chatInfoId, Key key) {
        this.chatInfoId = chatInfoId;
        this.key = key;
    }

    public ChatNotificationsEvent(ImsMessage data, Key key) {
        this.message = data;
        this.key = key;
    }
    public String getChatInfoId() {
        return chatInfoId;
    }

    public ChatInfo getChatInfo() {
        return chatInfo;
    }

    public ImsMessage getMessage() {
        return message;
    }

    public Key getKey() {
        return key;
    }
}
