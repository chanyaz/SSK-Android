package tv.sportssidekick.sportssidekick.util.ui;

/**
 * Created by Filip on 12/26/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class BlurEvent {

    public boolean isDisplayBlur() {
        return displayBlur;
    }

    boolean displayBlur;

    public BlurEvent(boolean displayBlur) {
        this.displayBlur = displayBlur;
    }
}
