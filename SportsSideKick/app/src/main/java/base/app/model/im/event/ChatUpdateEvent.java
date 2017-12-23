package base.app.model.im.event;

import base.app.model.im.ChatInfo;

/**
 * Created by Filip on 4/25/2017.
 */

public class ChatUpdateEvent {

    ChatInfo chatInfo;

    public ChatInfo getChatInfo() {
        return chatInfo;
    }

    public ChatUpdateEvent(ChatInfo chatInfo) {
        this.chatInfo = chatInfo;
    }
}
