package base.app;

import android.content.Context;
import android.content.ContextWrapper;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.keiferstone.nonet.NoNet;
import com.pixplicity.easyprefs.library.Prefs;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import base.app.model.FileUploader;
import base.app.model.TranslateManager;
import base.app.model.purchases.PurchaseModel;
import base.app.model.ticker.NextMatchModel;
import base.app.util.SoundEffects;
import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Djordje Krutil on 5.12.2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class Application extends android.app.Application{


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

        initTwitter(getApplicationContext());

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

    public static void initTwitter(Context context) {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(Keys.TWITTER_KEY, Keys.TWITTER_SECRET);
        Fabric.with(context, new Twitter(authConfig));
    }
}
