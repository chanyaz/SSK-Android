package tv.sportssidekick.sportssidekick.util.ui;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

import tv.sportssidekick.sportssidekick.R;

/**
 * Created by Djordje Krutil on 6.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class SlideTextAnimation {

    Animation rightSwipe, leftSwipe, fadeIn, fadeOut;
    AnimationSet moveRight;
    AnimationSet moveLeft;


    public SlideTextAnimation(Context context) {

        rightSwipe = AnimationUtils.loadAnimation(context, R.anim.slide_right);
        leftSwipe = AnimationUtils.loadAnimation(context, R.anim.slide_left);
        fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);

        moveRight = new AnimationSet(false);
        moveRight.addAnimation(rightSwipe);
        moveRight.addAnimation(fadeOut);

        moveLeft = new AnimationSet(false);
        moveLeft.addAnimation(leftSwipe);
        moveLeft.addAnimation(fadeIn);
    }

    public AnimationSet moveLeft() {
        return moveLeft;
    }

    public AnimationSet moveRight() {
        return moveRight;
    }
}
