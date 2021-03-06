package base.app.util.commons;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import base.app.R;

/**
 * Created by Filip on 8/22/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class NextMatchCountdown {

    public static String getCountdownValue(long matchTime) {
        long now = (Utility.getCurrentTime() / 1000);
        long totalInterval = matchTime - now;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds;
        while (totalInterval > 86400) {  // counting days
            days += 1;
            totalInterval -= 86400;
        }
        while (totalInterval > 3600) {
            hours += 1;
            totalInterval -= 3600;
        }
        while (totalInterval > 60) {
            minutes += 1;
            totalInterval -= 60;
        }
        seconds = (int) totalInterval;

        return String.format(Locale.getDefault(), "%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
    }

    /**
     * This code is rewritten from iOS, without any improvement and refactoring!
     *
     * @param context   we need this to get translated strings
     * @param matchTime timestamp of match in SECONDS! (from 1st jan 1970)
     * @param isTablet  there is different content on tablet and phone devices
     * @return String to be displayed
     */
    public static String getTextValue(Context context, long matchTime, boolean isTablet) {
        String countdownString = "";
        long now = (Utility.getCurrentTime() / 1000);
        long totalInterval = matchTime - now;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds;
        while (totalInterval > 86400) {  // counting days
            days += 1;
            totalInterval -= 86400;
        }
        while (totalInterval > 3600) {
            hours += 1;
            totalInterval -= 3600;
        }
        while (totalInterval > 60) {
            minutes += 1;
            totalInterval -= 60;
        }
        seconds = (int) totalInterval;

        int addedItems = 0;

        if (days > 1) {
            addedItems += 1;
            countdownString = context.getString(R.string.days, days);
        } else if (days == 1) {
            addedItems += 1;
            countdownString = context.getString(R.string.day, days);
        }
        if ((hours != 0 || days != 0) && addedItems == 0) {
            if (hours != 1) {
                addedItems += 1;
                countdownString = context.getString(R.string.hours, hours);
            } else {
                addedItems += 1;
                countdownString = context.getString(R.string.hour, hours);
            }
        }
        int maxItems = 1;
        if (days == 0 && hours < 2) {
            maxItems = 1;
        }
        if (addedItems < maxItems) {
            if (minutes != 0 || hours != 0) {
                if (minutes != 1) {
                    addedItems += 1;
                    countdownString = context.getString(R.string.mins, minutes);
                } else {
                    addedItems += 1;
                    countdownString = context.getString(R.string.hour, minutes);
                }
            }
        }
        if (addedItems < maxItems) {
            if (seconds != 1) {
                countdownString = context.getString(R.string.secs, seconds);
            } else {
                countdownString = context.getString(R.string.sec, seconds);
            }
        }

        if (matchTime < now) {
            return context.getString(R.string.live);
        } else {
            if (isTablet) {
                return countdownString;
            } else {
                return getDateForMatch(matchTime) + " - " + countdownString;
            }
        }
    }

    private static String getDateForMatch(long timeStamp) {
        try {
            DateFormat sdf = new SimpleDateFormat("EEE dd MMM", Locale.getDefault());
            Date netDate = (new Date(timeStamp * 1000));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }
}
