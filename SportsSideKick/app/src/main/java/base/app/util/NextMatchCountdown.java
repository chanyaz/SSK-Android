package base.app.util;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import base.app.R;

/**
 * Created by Filip on 8/22/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class NextMatchCountdown {

    /**
     *
     * This code is rewritten from iOS, without any improvement and refactoring!
     *
     * @param context we need this to get translated strings
     * @param matchTime timestamp of match in SECONDS! (from 1st jan 1970)
     * @param isTablet there is different content on tablet and phone devices
     * @return String to be displayed
     */
    public static String getTextValue(Context context, long matchTime, boolean isTablet){
        String countdownString = "";
        long now = (Utility.getCurrentTime() /1000);
        long totalInterval = matchTime - now;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        while(totalInterval > 86400){  // counting days
            days+=1;
            totalInterval -=86400;
        }
        while(totalInterval > 3600){
            hours+= 1;
            totalInterval -=3600;
        }
        while(totalInterval > 60){
            minutes+=1;
            totalInterval -=60;
        }
        seconds = (int) totalInterval;

        int addedItems = 0;

        if(days > 1){
            addedItems+=1;
            countdownString = context.getString(R.string.match_days,days);
        } else if(days == 1){
            addedItems+=1;
            countdownString = context.getString(R.string.match_day,days);
        }
        if((hours !=0 || days !=0) && addedItems == 0){
            if(hours != 1){
                addedItems += 1;
                countdownString = context.getString(R.string.match_hours,hours);
            } else if (hours == 1){
                addedItems += 1;
                countdownString = context.getString(R.string.match_hour,hours);
            }
        }
        int maxItems = 1;
        if(days == 0 && hours <2){
            maxItems = 1;
        }
        if(addedItems < maxItems){
            if(minutes !=0 || hours !=0){
                if(minutes !=1){
                    addedItems+=1;
                    countdownString = context.getString(R.string.match_mins,minutes);
                } else if (minutes == 1){
                    addedItems += 1;
                    countdownString = context.getString(R.string.match_hour,minutes);
                }
            }
        }
        if(addedItems < maxItems){
            if(seconds !=1){
                    addedItems+=1;
                    countdownString = context.getString(R.string.match_secs,seconds);
            } else if (seconds == 1){
                addedItems += 1;
                countdownString = context.getString(R.string.match_sec,seconds);
            }
        }

        if(matchTime < now){
            return context.getString(R.string.live);
        } else {
            if(isTablet){
                return countdownString;
            } else {
               String result = getDateForMatch(matchTime) + " - " + countdownString;
               return result;
            }
        }
    }

    public static String getDateForMatch(long timeStamp) {
        try {
            DateFormat sdf = new SimpleDateFormat("EEE dd MMM");
            Date netDate = (new Date(timeStamp * 1000));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }
}
