package base.app.model.user;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Filip on 4/25/2017.
 */

public class UserStateReceiver {

    interface UserStateListener{
        void onStateUpdated();
        void onStateUpdateError(Error error);
    }

    private UserStateReceiver.UserStateListener listener;

    public UserStateReceiver(UserStateReceiver.UserStateListener listener){
        this.listener = listener;
        EventBus.getDefault().register(this);
    }

    // Login state
    @Subscribe
    public void onEvent(UserEvent event){
        switch (event.getType()){
            case onStateUpdated:
                listener.onStateUpdated();
                break;
            case onStateUpdateError:
                listener.onStateUpdateError(event.getError());
                break;
        }
    }
}
