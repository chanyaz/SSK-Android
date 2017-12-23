package base.app.model.user;

import base.app.events.BusEvent;

/**
 * Created by Nemanja Jovanovic on 24/04/2017.
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
