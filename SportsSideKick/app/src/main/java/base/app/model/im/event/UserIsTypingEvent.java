package base.app.model.im.event;

import java.util.List;

import base.app.model.user.UserInfo;

/**
 * Created by Filip on 4/25/2017.
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
