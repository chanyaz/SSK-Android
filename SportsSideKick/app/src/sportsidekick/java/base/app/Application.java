package base.app;

import android.content.Context;
import android.content.ContextWrapper;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.keiferstone.nonet.NoNet;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;
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

        initImageLoader(getApplicationContext());
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

    //region AppImage Loader
    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.diskCacheExtraOptions(640, 480, null);
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); //
        L.writeLogs(false);
        ImageLoader.getInstance().init(config.build());
    }

    public static void initTwitter(Context context) {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(Keys.TWITTER_KEY, Keys.TWITTER_SECRET);
        Fabric.with(context, new Twitter(authConfig));
    }
}
