package base.app.data.im.event;

import java.util.List;

import base.app.data.user.UserInfo;

/**
 * Created by Filip on 4/25/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class UserIsTypingEvent {

    List<UserInfo> users;
    String chatId;

    public UserIsTypingEvent(String chatId, List<UserInfo> users) {
        this.chatId = chatId;
        this.users = users;
    }

    public List<UserInfo> getUsers() {
        return users;
    }

    public String getChatId() {
        return chatId;
    }
}
