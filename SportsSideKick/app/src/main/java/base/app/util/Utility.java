package base.app.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.util.DisplayMetrics;
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
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.ByteArrayOutputStream;
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
import base.app.util.ui.SlideTextAnimation;

/**
 * Created by Djordje Krutil on 6.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class Utility {

    public static void slideText(TextView slideTextOne, TextView slideTextTwo, View circleOne, View circleTwo, boolean visibleTextOne, Context context) {
        SlideTextAnimation animation = new SlideTextAnimation(context);
        if (slideTextOne != null && slideTextTwo != null && circleOne != null && circleTwo != null) {
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


    private static volatile DisplayImageOptions blankOptionsUser;
    private static volatile DisplayImageOptions blankOptions;
    private static volatile DisplayImageOptions wallItemOptions;

    public static DisplayImageOptions getImageOptionsForUsers() {
        if (blankOptionsUser != null) {
            return blankOptionsUser;
        }
        blankOptionsUser = new DisplayImageOptions.Builder()
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
        return blankOptionsUser;
    }

    private static volatile DisplayImageOptions roundedImageOptions;

    public static DisplayImageOptions getRoundedImageOptions() {
        if (roundedImageOptions != null) {
            return roundedImageOptions;
        }
        roundedImageOptions = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.blank_profile_rounded) // resource or drawable
//                .showImageForEmptyUri(R.drawable.blank_profile_rounded) // resource or drawable
//                .showImageOnFail(R.drawable.blank_profile_rounded) // resource or drawable
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

    public static DisplayImageOptions getImageOptionsForWallItem() {
        if (wallItemOptions != null) {
            return wallItemOptions;
        }
        wallItemOptions = new DisplayImageOptions.Builder()
                //TODO @Nemanja change when we have placeholder
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
        return wallItemOptions;
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
            StringBuilder sb = new StringBuilder(String.valueOf(daysTo));
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
        different = different % hoursInMilli;
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


    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
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

    public static void setClubConfig(String country, String id, String language) {
        Prefs.putString("Country", country);
        Prefs.putString("Language", language);
        Prefs.putString("ID", id);
    }

    public static void hideKeyboardFrom(Activity activity, View view, boolean hasFocus) {
        if (!hasFocus) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

    public static Boolean isTablet(Context context) {
        return context.getResources().getBoolean(R.bool.is_tablet);
    }


    public static Boolean isTablet2(Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        if (diagonalInches >= 7) {
           return true;
        } else {
           return false;
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

    public static boolean isNandUp() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    public static boolean isAnyCameraAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
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

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    public static String getBase64String(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);

        byte[] imageBytes = baos.toByteArray();

        String base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        return base64String;
    }
}