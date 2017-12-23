package base.app.util.ui;

/**
 * Created by Filip on 12/26/2016.
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
