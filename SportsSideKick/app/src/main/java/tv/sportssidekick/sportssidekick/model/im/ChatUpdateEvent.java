package tv.sportssidekick.sportssidekick.model.im;

/**
 * Created by Filip on 4/25/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
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
