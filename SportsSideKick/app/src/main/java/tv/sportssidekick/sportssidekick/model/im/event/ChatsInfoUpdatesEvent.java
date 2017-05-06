package tv.sportssidekick.sportssidekick.model.im.event;

import java.util.List;

import tv.sportssidekick.sportssidekick.model.im.ChatInfo;

/**
 * Created by Filip on 4/25/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ChatsInfoUpdatesEvent {

    List<ChatInfo> chats;

    public ChatsInfoUpdatesEvent(List<ChatInfo> chats) {
        this.chats = chats;
    }

    public List<ChatInfo> getChats() {
        return chats;
    }
}