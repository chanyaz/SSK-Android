package tv.sportssidekick.sportssidekick.model.im;

import android.util.Log;

import org.greenrobot.eventbus.Subscribe;

import tv.sportssidekick.sportssidekick.service.GameSparksEvent;

/**
 * Created by Filip on 12/14/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ImTest {

        public void beginTest(){

        }

        @Subscribe
        public void onEvent(GameSparksEvent event){
            Log.d("firebase-testing", event.getMessage());
        }


        public void closeTest(){

        }
}
