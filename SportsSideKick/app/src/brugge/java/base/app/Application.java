package base.app;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.keiferstone.nonet.NoNet;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import base.app.util.ui.ImageLoader;
import base.app.util.ui.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;
import com.pixplicity.easyprefs.library.Prefs;

import base.app.data.FileUploader;
import base.app.data.TranslateManager;
import base.app.data.user.purchases.PurchaseModel;
import base.app.data.content.wall.nextmatch.NextMatchModel;
import base.app.util.SoundEffects;
import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Djordje Krutil on 5.12.2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class Application extends MultiDexApplication {


    private static Application instance;

    public static Application getAppInstance() { return instance; }

    @Override
    public void onCreate() {
        super.onCreate();

        NoNet.configure()
                .endpoint("http://google.com")
                .timeout(5)
                .connectedPollFrequency(60)
                .disconnectedPollFrequency(1);
        instance = this;
        Connection.getInstance().initialize(this);

        // TODO @Djordje - update according to recommended approach (this one is deprecated)
        // TODO @Djordje - It should be called automatically, test and remove it is like that
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.isInitialized();
        AppEventsLogger.activateApp(this);

        // Shared prefs initialization
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        NextMatchModel.getInstance().loadTickerInfoFromCache();
        // Custom fonts
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/TitilliumWeb-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        FileUploader.getInstance().initialize(getApplicationContext());
        SoundEffects.getDefault().initialize(this);
        PurchaseModel.getInstance().initialize(this);
        TranslateManager.getInstance().initialize(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(Application.this);
    }
}
