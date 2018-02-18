package base.app;

import android.content.ContextWrapper;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;

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

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        // LeakCanary.install(this);

        if (BuildConfig.DEBUG) enableStrictMode();

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
                .build()
        );

        FileUploader.getInstance().initialize(getApplicationContext());
        SoundEffects.getDefault().initialize(this);
        // PurchaseModel.getInstance().initialize(this);
        Translator.getInstance().initialize(this);

        RxPaparazzo.register(this);
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
}
