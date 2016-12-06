package tv.sportssidekick.sportssidekick.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import tv.sportssidekick.sportssidekick.R;

/**
 * Created by Djordje Krutil on 6.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class Utility {

    public static void slideText (TextView slideTextOne, TextView slideTextTwo, View circleOne, View circleTwo, boolean visibleTextOne, Context context)
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

    private static DisplayImageOptions options;


    public static DisplayImageOptions imageOptionsImageLoader() {
        if (options != null) {
            return options;
        }
        options = new DisplayImageOptions.Builder()
                //TODO change when we have placeholder
//                .showImageOnLoading(R.drawable.booking_top_image) // resource or drawable
//                .showImageForEmptyUri(R.drawable.booking_top_image) // resource or drawable
//                .showImageOnFail(R.drawable.booking_top_image) // resource or drawable
                .delayBeforeLoading(0) //delay
                .resetViewBeforeLoading(true)  // default
                .considerExifParams(false)
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .bitmapConfig(Bitmap.Config.RGB_565) // default
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(250, true, true, true))  //int durationMillis, boolean animateFromNetwork, boolean animateFromDisk, boolean animateFromMemory))
                .build();
        return options;
    }
}
