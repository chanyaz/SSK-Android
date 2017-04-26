package tv.sportssidekick.sportssidekick.model.user;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Filip on 4/25/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class LoginStateReceiver {

    public interface LoginStateListener{
        void onLogout();
        void onLoginAnonymously();
        void onLogin(UserInfo user);
        void onLoginError(Error error);
    }

    private LoginStateListener listener;

    public LoginStateReceiver(LoginStateListener listener){
        this.listener = listener;
        EventBus.getDefault().register(this);
    }

    // Login state
    @Subscribe
    public void onEvent(UserEvent event){
        switch (event.getType()){
            case onLogout:
                listener.onLogout();
                break;
            case onLogin:
                listener.onLogin(event.getUserInfo());
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