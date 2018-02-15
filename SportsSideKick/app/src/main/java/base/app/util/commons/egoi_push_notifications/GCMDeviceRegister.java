package base.app.util.commons.egoi_push_notifications;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class GCMDeviceRegister extends Activity {

    /**
     * Interface to retrieve the success of the operation
     */
    interface didGetDeviceTokenDelegate {
        void didGetDeviceToken(boolean success, String deviceToken);
    }

    /**
     * Variables
     */
    public didGetDeviceTokenDelegate delegate;
    private static String TAG = "GCMDeviceRegister:";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String PROPERTY_REG_ID = "registration_id";
    private String senderID;
    private GoogleCloudMessaging gcm;
    private String regid;
    private Context context;

    /**
     * Default constructor
     */
    public GCMDeviceRegister() {

    }

    /**
     * Register device in the GCM Server
     *
     * @param context the App context
     * @param senderID the sender ID you get from the Google Play Console
     */
    public void registerDevice(Context context, String senderID) {

        this.setSenderID(senderID);
        this.setContext(context);

        if (checkPlayServices()) {

            gcm = GoogleCloudMessaging.getInstance(context);
            regid = getRegistrationId();

            if (regid.isEmpty()) {
                registerInBackground();
            } else {
                this.delegate.didGetDeviceToken(true, regid);
            }

        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
            this.delegate.didGetDeviceToken(false, null);
        }
    }

    /**
     * Check if Google Play Services are available
     *
     * @return result
     */
    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getContext());

        if (resultCode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity)this.getContext(), PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }

            return false;
        }

        return true;
    }

    /**
     * Register the device in GCM
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getBaseContext());
                    }

                    String sender = getSenderID();
                    regid = gcm.register(sender);

                    // You can change here were you want to store this registration
                    storeRegistrationId(regid);
                    finishedRegistration(regid);

                }
                catch (IOException ex) {
                    Log.i(TAG, "Unable to Register Device.");
                }

                return null;
            }
        }.execute(null, null, null);
    }

    /**
     * Finished registering device
     *
     * @param device the device id received from the GCM register call
     */
    private void finishedRegistration(String device) {

        if (device.length() > 0) {
            this.delegate.didGetDeviceToken(true, device);
        } else {
            this.delegate.didGetDeviceToken(false, null);
        }
    }

    /**
     * Get the stored token
     *
     * @return the device token, if exists
     */
    public String getRegistrationId() {

        SharedPreferences prefs = getGCMPreferences();
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");

        if (registrationId != null) {
            if (registrationId.isEmpty()) {
                Log.i(TAG, "Registration not found.");
                return "";
            }
        }

        return registrationId;
    }

    /**
     * Store the device token
     *
     * @param regId the device token to be stored
     */
    private void storeRegistrationId(String regId) {

        final SharedPreferences prefs = getGCMPreferences();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.apply();
    }

    /**
     * Get the Shared Preferences
     *
     * @return the Shared Preferences
     */
    private SharedPreferences getGCMPreferences() {
        return context.getSharedPreferences(GCMDeviceRegister.class.getSimpleName(), MODE_PRIVATE);
    }

    /*
     * Getters and Setters
     */
    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}