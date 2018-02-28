package base.app;

import android.content.ContextWrapper;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.leakcanary.LeakCanary;

import base.app.data.FileUploader;
import base.app.data.Translator;
import base.app.util.commons.SoundEffects;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Djordje Krutil on 5.12.2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class Application extends MultiDexApplication {

    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public static Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }
        return sTracker;
    }

    protected void enableStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .permitDiskReads()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        // LeakCanary.install(this);

        // if (BuildConfig.DEBUG) enableStrictMode();

        // Shared prefs initialization
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        // Custom fonts
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/TitilliumWeb-Regular.ttf")
                .build());

        FileUploader.getInstance().initialize(getApplicationContext());
        SoundEffects.getDefault().initialize(this);
        // PurchaseModel.getInstance().initialize(this);
        Translator.getInstance().initialize(this);

        RxPaparazzo.register(this);

        sAnalytics = GoogleAnalytics.getInstance(this);
        if (BuildConfig.DEBUG) sAnalytics.setDryRun(true);

        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);
    }
}
