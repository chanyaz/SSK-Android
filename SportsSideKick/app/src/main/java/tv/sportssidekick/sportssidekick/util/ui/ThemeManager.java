package tv.sportssidekick.sportssidekick.util.ui;

import android.app.Activity;

import com.pixplicity.easyprefs.library.Prefs;

import tv.sportssidekick.sportssidekick.R;

/**
 * Created by Nemanja Jovanovic on 15/05/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ThemeManager {

    private static final String THEME_KEY = "THEME_KEY";

    private static ThemeManager instance;

    private boolean lightTheme;

    private ThemeManager() {
    }

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }


    public void  changeTheme(Activity activity) {
        switch (Prefs.getInt(THEME_KEY, R.style.AppTheme)) {
            case R.style.LightTheme:
                Prefs.putInt(THEME_KEY, R.style.AppTheme);
                setLightTheme(false);
                break;
            case R.style.AppTheme:
                Prefs.putInt(THEME_KEY, R.style.LightTheme);
                setLightTheme(true);
                break;
        }
        activity.recreate();
    }

    public void assignTheme(Activity activity) {
        switch (Prefs.getInt(THEME_KEY, R.style.AppTheme)) {
            case R.style.AppTheme:
                activity.setTheme(R.style.AppTheme);
                setLightTheme(false);
                break;
            case R.style.LightTheme:
                activity.setTheme(R.style.LightTheme);
                setLightTheme(true);
                break;
        }
    }

    public boolean isLightTheme() {
        return lightTheme;
    }

    private void setLightTheme(boolean lightTheme) {
        this.lightTheme = lightTheme;
    }
}
