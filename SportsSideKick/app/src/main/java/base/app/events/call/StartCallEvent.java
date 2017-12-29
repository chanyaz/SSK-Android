package base.app.events.call;

import java.util.List;

import base.app.events.BusEvent;
import base.app.model.user.UserInfo;

/**
 * Created by Filip on 4/2/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class StartCallEvent extends BusEvent {

    public List<UserInfo> getUsers() {
        return users;
    }

    private final List<UserInfo> users;
    public StartCallEvent(List<UserInfo> selectedValues) {
        super("");
        users = selectedValues;
    }
}
