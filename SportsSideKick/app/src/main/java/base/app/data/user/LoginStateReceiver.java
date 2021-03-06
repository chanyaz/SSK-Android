package base.app.data.user;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Filip on 4/25/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class LoginStateReceiver {

    public interface LoginListener {
        void onLogin(User user);
        void onLoginAnonymously();
        void onLoginError(Error error);
        void onLogout();
    }

    private LoginListener listener;

    public LoginStateReceiver(LoginListener listener){
        this.listener = listener;
        EventBus.getDefault().register(this);
    }

    // Login state
    @Subscribe(threadMode= ThreadMode.MAIN)
    public void onEvent(UserEvent event){
        switch (event.getType()){
            case onLogout:
                listener.onLogout();
                break;
            case onLogin:
                listener.onLogin(event.getUser());
                break;
            case onLoginAnonymously:
                listener.onLoginAnonymously();
                break;
            case onLoginError:
                listener.onLoginError(event.getError());
                break;
        }
    }
}
