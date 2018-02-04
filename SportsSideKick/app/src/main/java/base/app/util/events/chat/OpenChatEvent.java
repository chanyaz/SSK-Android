package base.app.util.events.chat;

import base.app.util.events.BusEvent;
import base.app.data.im.ChatInfo;

/**
 * Created by Nemanja Jovanovic on 14/06/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
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
