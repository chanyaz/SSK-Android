package tv.sportssidekick.sportssidekick.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import tv.sportssidekick.sportssidekick.R;

/**
 * Created by Djordje Krutil on 6.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class Utility {

    public void slideText (TextView slideTextOne, TextView slideTextTwo, View circleOne, View circleTwo, boolean visibleTextOne, Context context)
    {
        SlideTextAnimation animation = new SlideTextAnimation(context);
        if (animation!=null)
        {
            if(slideTextOne!=null && slideTextTwo!=null && circleOne!=null && circleTwo!=null)
                if(visibleTextOne)
                {
                    slideTextOne.startAnimation(animation.moveLeft());
                    slideTextOne.setVisibility(View.VISIBLE);
                    slideTextTwo.startAnimation(animation.moveRight());
                    slideTextTwo.setVisibility(View.GONE);
                    circleOne.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_white));
                    circleTwo.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_green));
                }
                else
                {
                    slideTextTwo.startAnimation(animation.moveLeft());
                    slideTextTwo.setVisibility(View.VISIBLE);
                    slideTextOne.startAnimation(animation.moveRight());
                    slideTextOne.setVisibility(View.GONE);
                    circleOne.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_green));
                    circleTwo.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_white));
                }
        }
    }
}
