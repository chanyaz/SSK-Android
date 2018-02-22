package base.app.util.commons;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import base.app.R;
import base.app.data.user.UserInfo;

/**
 * Created by Djordje Krutil on 6.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class Utility {

    public static final String CHOSEN_LANGUAGE = "CHOSEN_LANGUAGE";
    public static final String AUTO_TRANSLATE = "AUTO_TRANSLATE";
    public static final String WALL_NOTIFICATIONS = "WALL_NOTIFICATIONS";
    public static final String NEWS_NOTIFICATIOINS = "NEWS_NOTIFICATIOINS";
    public static final String RUMOURS_NOTIFICATIONS = "RUMOURS_NOTIFICATIONS";
    public static final String SOCIAL_NOTIFICATIONS = "SOCIAL_NOTIFICATIONS";

    public static long getCurrentTime(){
        return System.currentTimeMillis();
    }

    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showAlertDialog(String title, String message, Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.AlertDialog).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
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

    public static Boolean isPhone(Context context){
        return !isTablet(context);
    }

    public static Boolean isTablet(Context context) {
        return false;
        /*if(context!=null) {
            return context.getResources().getBoolean(R.bool.is_tablet);
        } else {
            return Prefs.getBoolean(IS_TABLET, false);
        }*/
    }

    public static boolean isKitKat() {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT;
    }

    public static boolean isLollipopAndUp() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static View.OnFocusChangeListener getAdjustResizeFocusListener(final Activity activity){
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                } else {
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                }
            }
        };
    }

    public static void showKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }

    public static void hideKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.toggleSoftInput(0, 0);
        }
    }

    public static void hideKeyboard(Fragment fragment) {
        InputMethodManager imm = (InputMethodManager)
                fragment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.hideSoftInputFromWindow(fragment.getView().getWindowToken(), 0);
        }
    }

    public static int dpToPixels(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) (dp * metrics.density);
    }

    public static String capitalizeFirst(String input) {
        if (input == null) return null;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}