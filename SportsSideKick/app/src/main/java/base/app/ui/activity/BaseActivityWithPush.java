package base.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.egoi.push.EGoiPushFramework;

import base.app.R;
import base.app.util.commons.egoi_push_notifications.EGoiWebView;
import base.app.util.commons.egoi_push_notifications.GCMDeviceRegister;

abstract public class BaseActivityWithPush extends BaseActivity
        implements GCMDeviceRegister.didGetDeviceTokenDelegate,
        EGoiPushFramework.EGoiPushFrameworkInterface {

    // TODO: Insert real credentials for push notifications from E-goi
    private String clientID = "00000";
    private String appID = "000";
    private String senderID = "00000000";

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EGoiPushFramework.sharedInstance().clientId = this.clientID;
        EGoiPushFramework.sharedInstance().applicationId = this.appID;
        EGoiPushFramework.sharedInstance().delegate = this;

        /**
         * Register the device in the GCM servers
         */
        GCMDeviceRegister deviceRegister = new GCMDeviceRegister();
        deviceRegister.delegate = this;
        deviceRegister.registerDevice(this, senderID);
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();

        if (intent != null) {
            if (intent.getStringExtra("popuptitle") != null) {
                boolean showAlert = intent.getBooleanExtra("showAlert", false);
                EGoiPushFramework.sharedInstance().initPushNotification(this, intent);
            }
        }
    }

    @Override
    public void didGetDeviceToken(boolean success, String deviceToken) {

        // Check if device was successfull registered
        if (success) {
            EGoiPushFramework.sharedInstance().token = deviceToken;
            EGoiPushFramework.sharedInstance().registerDevice(this, deviceToken);

        } else {
            // Handle error
        }
    }

    @Override
    public void didRegisterDevice(boolean b, String s) {

        if (b) {
            Log.e("Push Framework", "Device was registered");
        } else {
            Log.e("Push Framework", "There was an error registering the device!");
        }
    }

    @Override
    public void openWebView(String link, String title) {
        Intent i = new Intent(this, EGoiWebView.class);
        i.putExtra("link", link);
        i.putExtra("title", title);
        startActivity(i);
    }
}