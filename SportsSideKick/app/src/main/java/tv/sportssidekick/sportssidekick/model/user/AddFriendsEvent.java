package tv.sportssidekick.sportssidekick.model.user;

import tv.sportssidekick.sportssidekick.service.BusEvent;

/**
 * Created by Nemanja Jovanovic on 24/04/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class AddFriendsEvent extends BusEvent {

    private UserInfo userInfo;
    private boolean remove;
    public AddFriendsEvent(UserInfo userInfo,boolean remove) {
        super("");
        this.userInfo = userInfo;
        this.remove = remove;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public boolean isRemove() {
        return remove;
    }
}
