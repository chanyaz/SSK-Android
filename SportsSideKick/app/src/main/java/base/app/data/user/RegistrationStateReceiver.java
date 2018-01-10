package base.app.data.user;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Filip on 4/25/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class RegistrationStateReceiver {

    public interface RegistrationStateListener{
        void onRegister();
        void onRegisterError(Error error);
    }

    private RegistrationStateListener listener;

    public RegistrationStateReceiver(RegistrationStateListener listener){
        this.listener = listener;
        EventBus.getDefault().register(this);
    }

    // Login state
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UserEvent event){
        switch (event.getType()){
            case onRegister:
                listener.onRegister();
                break;
            case onRegisterError:
                listener.onRegisterError(event.getError());
                break;
        }
    }

}
