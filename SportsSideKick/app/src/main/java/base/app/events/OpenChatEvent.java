package base.app.events;

import base.app.model.im.ChatInfo;

/**
 * Created by Nemanja Jovanovic on 14/06/2017.
 */

public class OpenChatEvent extends BusEvent {
    private ChatInfo chatInfo;

    public OpenChatEvent() {
        super("");
    }

    public OpenChatEvent(ChatInfo chatInfo) {
        super("");
        this.chatInfo = chatInfo;
    }

    public ChatInfo getChatInfo() {
        return chatInfo;
    }

    public void setChatInfo(ChatInfo chatInfo) {
        this.chatInfo = chatInfo;
    }
}
