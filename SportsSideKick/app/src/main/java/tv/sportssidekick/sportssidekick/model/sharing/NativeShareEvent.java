package tv.sportssidekick.sportssidekick.model.sharing;

import android.content.Intent;

import tv.sportssidekick.sportssidekick.events.BusEvent;

/**
 * Created by Filip on 4/19/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class NativeShareEvent extends BusEvent{

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    Intent intent;

    public NativeShareEvent(Intent intent) {
        super("");
        setIntent(intent);
    }
}
