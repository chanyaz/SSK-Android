package base.app.model.tutorial;

import android.graphics.drawable.Drawable;

/**
 * Created by Filip on 3/30/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class WallStep {

    String stepText;
    Drawable stepIcon;

    public WallStep(String stepText, Drawable stepIcon) {
        this.stepText = stepText;
        this.stepIcon = stepIcon;
    }

    public String getStepText() {
        return stepText;
    }

    public Drawable getStepIcon() {
        return stepIcon;
    }
}
