package base.app.data.wall.sharing;

import android.content.Intent;

import base.app.util.events.BusEvent;

/**
 * Created by Filip on 4/19/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class NativeShareEvent extends BusEvent {

    Intent intent;

    public NativeShareEvent(Intent intent) {
        super("");
        setIntent(intent);
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }
}
