package base.app.model.user;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Filip on 4/25/2017.
 */

public class PasswordResetReceiver {

    public interface PasswordResetListener{
        void onPasswordResetRequest();
        void onPasswordResetRequestError(Error error);
    }

    private PasswordResetListener listener;

    public PasswordResetReceiver(PasswordResetListener listener){
        this.listener = listener;
        EventBus.getDefault().register(this);
    }

    // Login state
    @Subscribe
    public void onEvent(UserEvent event){
        switch (event.getType()){
            case onPasswordResetRequest:
                listener.onPasswordResetRequest();
                break;
            case onPasswordResetRequestError:
                listener.onPasswordResetRequestError(event.getError());
                break;
        }
    }

}
