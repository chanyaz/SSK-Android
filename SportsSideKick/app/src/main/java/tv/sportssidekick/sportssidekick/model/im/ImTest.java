package tv.sportssidekick.sportssidekick.model.im;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.service.FirebaseEvent;

/**
 * Created by Filip on 12/14/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ImTest {

        public void beginTest(){
            EventBus.getDefault().register(this);
            Model.getInstance().getAllUsersInfo();
            ImModel.getInstance().reload(Model.getInstance().getUserInfo().getUserId());
            ImModel.getInstance().getAllPublicChats();
        }

        @Subscribe
        public void onEvent(FirebaseEvent event){
            Log.d("firebase-testing", event.getMessage());
        }


        public void closeTest(){

        }
}
