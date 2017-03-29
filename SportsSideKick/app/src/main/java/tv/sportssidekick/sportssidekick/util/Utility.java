package tv.sportssidekick.sportssidekick.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.pixplicity.easyprefs.library.Prefs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

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
        if(slideTextOne!=null && slideTextTwo!=null && circleOne!=null && circleTwo!=null) {
            if (visibleTextOne) {
                slideTextOne.startAnimation(animation.moveLeft());
                slideTextOne.setVisibility(View.VISIBLE);
                slideTextTwo.startAnimation(animation.moveRight());
                slideTextTwo.setVisibility(View.GONE);
                circleOne.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_white));
                circleTwo.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_green));
            } else {
                slideTextTwo.startAnimation(animation.moveLeft());
                slideTextTwo.setVisibility(View.VISIBLE);
                slideTextOne.startAnimation(animation.moveRight());
                slideTextOne.setVisibility(View.GONE);
                circleOne.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_green));
                circleTwo.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_white));
            }
        }
    }

    private  static volatile DisplayImageOptions blankOptions;

    public static DisplayImageOptions getImageOptionsForUsers() {
        if (blankOptions != null) {
            return blankOptions;
        }
        blankOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.blank_profile_rounded) // resource or drawable
                .showImageForEmptyUri(R.drawable.blank_profile_rounded) // resource or drawable
                .showImageOnFail(R.drawable.blank_profile_rounded) // resource or drawable
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


    public static DisplayImageOptions imageOptionsImageLoader() {
        if (blankOptions != null) {
            return blankOptions;
        }
        blankOptions = new DisplayImageOptions.Builder()
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
        return blankOptions;
    }

    public static boolean isValidEmail(String target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    public static boolean isEditTextEmpty(EditText text, String filedName, AlertDialog alertDialog, Context context) {
        if ("".compareTo(text.getText().toString()) == 0) {
            if (alertDialog != null) {
                alertDialog.setMessage(context.getString(R.string.dialog_message) + " " + filedName + "!");
                alertDialog.show();
            }
            return true;
        }
        return false;
    }

    public static boolean internetAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            boolean connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.v("connectivity", e.toString());
        }
        return false;
    }

    public static void showAlertDialog(String title, String message, Context context)
    {
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(context).create();
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

    public static long getDaysUntilMatch(long timeStamp){
        Date netDate = (new Date(timeStamp*1000));
        Date date = new Date();
        long diff = date.getTime() - netDate.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static String getDate(long timeStamp){
        try{
            DateFormat sdf = new SimpleDateFormat("EEE dd MMM");
            Date netDate = (new Date(timeStamp*1000));
            Date date = new Date();
            long diff = date.getTime() - netDate.getTime();
            long daysTo = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            StringBuilder sb = new StringBuilder(String.valueOf(daysTo));
            return sdf.format(diff);
        }
        catch(Exception ex){
            return "xx";
        }
    }

    public static void setListViewHeight(ListView listView, BaseAdapter baseAdapter) {
        if (listView != null) {
            int totalHeight = 0;
            for (int size = 0; size < baseAdapter.getCount(); size++) {
                View listItem = baseAdapter.getView(size, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (baseAdapter.getCount() - 1));
            listView.setLayoutParams(params);
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

    public static HashMap<String, String> getClubConfig ()
    {
        HashMap<String, String> config = new HashMap<>();
        config.put("Country", Prefs.getString("Country", "portugal"));
        config.put("Language", Prefs.getString("Language", "en"));
        config.put("ID", Prefs.getString("ID", "1680"));
        return config;
    }

    public static void setClubConfig(String country, String id, String language)
    {
        Prefs.putString("Country", country);
        Prefs.putString("Language", language);
        Prefs.putString("ID", id);
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
