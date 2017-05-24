package base.app.model.im.event;

import java.util.List;

import base.app.model.user.UserInfo;

/**
 * Created by Filip on 4/25/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class UserIsTypingEvent {

    List<UserInfo> users;

    public UserIsTypingEvent(List<UserInfo> users) {
        this.users = users;
    }

    public List<UserInfo> getUsers() {
        return users;
    }
}
