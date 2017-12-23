package base.app;

import android.content.ContextWrapper;
import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.keiferstone.nonet.NoNet;
import com.pixplicity.easyprefs.library.Prefs;

import base.app.model.FileUploader;
import base.app.model.Model;
import base.app.model.TranslateManager;
import base.app.model.notifications.InternalNotificationReciever;
import base.app.model.purchases.PurchaseModel;
import base.app.model.ticker.NextMatchModel;
import base.app.model.videoChat.VideoChatModel;
import base.app.util.SoundEffects;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Djordje Krutil on 5.12.2016.
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

        Model.getInstance().initialize(this);
        VideoChatModel.getInstance();
        InternalNotificationReciever.init();
    }
}
