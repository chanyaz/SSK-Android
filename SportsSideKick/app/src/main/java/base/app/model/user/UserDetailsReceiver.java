package base.app.model.user;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Filip on 4/25/2017.
 */

public class UserDetailsReceiver {

    interface UserDetailsListener{
        void onDetailsUpdated(UserInfo user);
        void onDetailsUpdateError(Error error);
    }

    private UserDetailsListener listener;

    public UserDetailsReceiver(UserDetailsListener listener){
        this.listener = listener;
        EventBus.getDefault().register(this);
    }

    // Login state
    @Subscribe
    public void onEvent(UserEvent event){
        switch (event.getType()){
            case onDetailsUpdated:
                listener.onDetailsUpdated(event.getUserInfo());
                break;
            case onDetailsUpdateError:
                listener.onDetailsUpdateError(event.getError());
                break;
        }
    }
}
