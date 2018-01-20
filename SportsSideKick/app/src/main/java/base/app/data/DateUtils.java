package base.app.data;

import base.app.util.commons.Utility;

/**
 * Created by Filip on 1/10/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
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
    private static String timestampToFirebaseDate(long timestamp){
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
