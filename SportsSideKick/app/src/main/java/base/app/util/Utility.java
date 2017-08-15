package base.app.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.instacart.library.truetime.TrueTimeRx;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.pixplicity.easyprefs.library.Prefs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import base.app.R;
import base.app.model.user.UserInfo;

/**
 * Created by Djordje Krutil on 6.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class Utility {

    public static final String CHOSEN_LANGUAGE = "CHOSEN_LANGUAGE";

    public static long getCurrentNTPTime(){
        if(TrueTimeRx.isInitialized()){
            try {
               return TrueTimeRx.now().getTime();
            } catch (IllegalStateException ex){
                return System.currentTimeMillis();
            }
        } else {
            return System.currentTimeMillis();
        }
    }

    private static volatile DisplayImageOptions blankOptionsUser;
    private static volatile DisplayImageOptions blankOptions;
    private static volatile DisplayImageOptions wallItemOptions;

    public static DisplayImageOptions getImageOptionsForUsers() {
        if (blankOptionsUser != null) {
            return blankOptionsUser;
        }
        blankOptionsUser = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.blank_profile_rounded)
                .showImageForEmptyUri(R.drawable.blank_profile_rounded)
                .showImageOnFail(R.drawable.blank_profile_rounded)
                .delayBeforeLoading(0) //delay
                .resetViewBeforeLoading(true)  // default
                .considerExifParams(false)
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .bitmapConfig(Bitmap.Config.RGB_565) // default
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(250, true, true, true))  //int durationMillis, boolean animateFromNetwork, boolean animateFromDisk, boolean animateFromMemory))
                .build();
        return blankOptionsUser;
    }

    private static volatile DisplayImageOptions roundedImageOptions;

    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
    public static DisplayImageOptions getRoundedImageOptions() {
        if (roundedImageOptions != null) {
            return roundedImageOptions;
        }
        roundedImageOptions = new DisplayImageOptions.Builder()
                .delayBeforeLoading(0) //delay
                .resetViewBeforeLoading(true)  // default
                .considerExifParams(false)
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .bitmapConfig(Bitmap.Config.RGB_565) // default
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();
        return roundedImageOptions;
    }

    public static DisplayImageOptions imageOptionsImageLoader() {
        if (blankOptions != null) {
            return blankOptions;
        }
        blankOptions = new DisplayImageOptions.Builder()
                .delayBeforeLoading(0) //delay
                .resetViewBeforeLoading(true)  // default
                .considerExifParams(false)
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .bitmapConfig(Bitmap.Config.RGB_565) // default
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(250, true, true, true))  //int durationMillis, boolean animateFromNetwork, boolean animateFromDisk, boolean animateFromMemory))
                .build();
        return blankOptions;
    }

    public static DisplayImageOptions getImageOptionsForWallItem() {
        if (wallItemOptions != null) {
            return wallItemOptions;
        }
        wallItemOptions = new DisplayImageOptions.Builder()
                .delayBeforeLoading(0) //delay
                .resetViewBeforeLoading(true)  // default
                .considerExifParams(false)
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .bitmapConfig(Bitmap.Config.RGB_565) // default
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(250, true, true, true))  //int durationMillis, boolean animateFromNetwork, boolean animateFromDisk, boolean animateFromMemory))
                .build();
        return wallItemOptions;
    }

    public static void showAlertDialog(String title, String message, Context context) {
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(context, R.style.AlertDialog).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.dialog_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static long getDaysUntilMatch(long timeStamp) {
        Date netDate = (new Date(timeStamp * 1000));
        Date date = new Date();
        long diff = date.getTime() - netDate.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static String getDate(long timeStamp) {
        try {
            DateFormat sdf = new SimpleDateFormat("EEE dd MMM");
            Date netDate = (new Date(timeStamp * 1000));
            Date date = new Date();
            long diff = date.getTime() - netDate.getTime();
            long daysTo = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            return sdf.format(diff);
        } catch (Exception ex) {
            return "xx";
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

    public static String getTimeAgo(long timeStamp) {
        //milliseconds
        Date date = new Date();
        long different = date.getTime() - timeStamp * 1000;

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        return elapsedDays + " Days " + elapsedHours + " Hours ago";
    }

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

    public static int getDisplayWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static int getDisplayHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public static HashMap<String, String> getClubConfig() {
        HashMap<String, String> config = new HashMap<>();
        config.put("Country", Prefs.getString("Country", "portugal"));
        config.put("Language", Prefs.getString("Language", "en"));
        config.put("ID", Prefs.getString("ID", "1680"));
        return config;
    }

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    public static boolean checkIfBundlesAreEqual(Bundle one, Bundle two) {
        if (one.size() != two.size())
            return false;

        Set<String> setOne = one.keySet();
        Object valueOne;
        Object valueTwo;

        for (String key : setOne) {
            valueOne = one.get(key);
            valueTwo = two.get(key);
            if (valueOne instanceof Bundle && valueTwo instanceof Bundle &&
                    !checkIfBundlesAreEqual((Bundle) valueOne, (Bundle) valueTwo)) {
                return false;
            } else if (valueOne == null) {
                if (valueTwo != null || !two.containsKey(key))
                    return false;
            } else if (!valueOne.equals(valueTwo))
                return false;
        }

        return true;
    }

    public static List<UserInfo> filter(List<UserInfo> users, String query) {
        String lowerCaseQuery = query.toLowerCase();
        List<UserInfo> filteredUserslList = new ArrayList<>();
        if (users != null) {
            for (UserInfo user : users) {
                String text = (user.getFirstName() + user.getLastName() + user.getNicName()).toLowerCase();
                if (text.contains(lowerCaseQuery)) {
                    filteredUserslList.add(user);
                }
            }
        }
        return filteredUserslList;
    }

    public static final String IS_TABLET = "IS_TABLET";

    public static Boolean isTablet(Context context) {
        if(context!=null) {
            return context.getResources().getBoolean(R.bool.is_tablet);
        } else {
            return Prefs.getBoolean(IS_TABLET, false);
        }
    }

    public static void setSystemBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.system_bar_color));
        } else {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                WindowManager.LayoutParams winParams = activity.getWindow().getAttributes();
                final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                winParams.flags |= bits;
                activity.getWindow().setAttributes(winParams);
            }
        }
    }

    public static boolean isKitKat() {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT;
    }

    public static boolean isLollipopAndUp() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}