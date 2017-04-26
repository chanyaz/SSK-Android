package tv.sportssidekick.sportssidekick.model.im.event;

import tv.sportssidekick.sportssidekick.model.im.ChatInfo;

/**
 * Created by Filip on 4/25/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class CreateNewChatSuccessEvent {

    ChatInfo chatInfo;

    public CreateNewChatSuccessEvent(ChatInfo chatInfo) {
        this.chatInfo = chatInfo;
    }

    public ChatInfo getChatInfo() {
        return chatInfo;
    }
}
