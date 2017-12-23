package base.app.model.im.event;

import java.util.List;

import base.app.model.im.ChatInfo;

/**
 * Created by Filip on 4/25/2017.
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
