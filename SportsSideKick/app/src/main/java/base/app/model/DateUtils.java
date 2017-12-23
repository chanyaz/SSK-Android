package base.app.model;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import base.app.util.Utility;

/**
 * Created by Filip on 1/10/2017.
 */

public class DateUtils {

    /**
     * @param value custom formatted string representing timestamp
     * @return Unix timestamp
     */
    public static long getTimestampFromFirebaseDate(String value){
        if(value!=null){
            try {
                double seconds = Double.valueOf(value)*1000;
                return (long) seconds;
            } catch (NumberFormatException nfe){
                return 0;
            }
        }
        return 0;
    }

    /**
     * @param timestamp Unix timestamp
     * @return custom formatted string representing timestamp
     */
    public static String timestampToFirebaseDate(long timestamp){
        String str = String.valueOf(timestamp);
        str = new StringBuilder(str).insert(str.length()-3, ".").append("33").toString();
        return str;
    }

    /**
     * @return current time formatted as Firebase date
     */
    public static String currentTimeToFirebaseDate(){
        return timestampToFirebaseDate(Utility.getCurrentTime());
    }

    public static String dateToFirebaseDate(Date date){
        return timestampToFirebaseDate(date.getTime());
    }


//    public static String getTimeAgo(long timeStamp) {
//        //milliseconds
//        Date date = new Date();
//        long different = date.getTime() - timeStamp * 1000;
//
//        long secondsInMilli = 1000;
//        long minutesInMilli = secondsInMilli * 60;
//        long hoursInMilli = minutesInMilli * 60;
//        long daysInMilli = hoursInMilli * 24;
//
//        long elapsedDays = different / daysInMilli;
//        different = different % daysInMilli;
//
//        long elapsedHours = different / hoursInMilli;
//        return elapsedDays + " Days " + elapsedHours + " Hours ago"; // TODO Hardcoded strings!
//    }

    public static long getTimeDifference(long timeStamp) {
        try {
            Date netDate = (new Date(timeStamp * 1000));
            Date date = new Date();
            long diff = date.getTime() - netDate.getTime();
            return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            return 0;
        }
    }

    public static String getTimeAgo(double timestamp){
        long seconds = (long)((timestamp)*1000);
        return getTimeAgo(seconds);
    }

    public static String getTimeAgo(long timestampEpoch){
        String timeAgo = android.text.format.DateUtils.getRelativeTimeSpanString(timestampEpoch).toString();
        if(timeAgo.equals("0 minutes ago")){
            timeAgo = "Just Now";
        } else {
            timeAgo = timeAgo.replace(" minutes","m");
            timeAgo = timeAgo.replace(" minute","m");
        }
        return timeAgo;
    }
}
