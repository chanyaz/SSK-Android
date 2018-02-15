package base.app.util.commons.egoi_push_notifications;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {

            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                try {
                    sendNotification(extras);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(Bundle extras) throws JSONException {

        int NOTIFICATION_ID = 0;

        String message = extras.getString("message");

        JSONObject jsonMessage = new JSONObject(message);
        Intent intent = getPackageManager().getLaunchIntentForPackage(getApplicationContext().getApplicationInfo().packageName);

        String pushMessage = "";

        // Decompose the message received from the E-Goi server to catch the variables
        if (jsonMessage.getJSONObject("aps") != null) {

            JSONObject aps = jsonMessage.getJSONObject("aps");

            intent.putExtra("message", message);

            if (jsonMessage.getString("t") != null) {
                intent.putExtra("popuptitle", jsonMessage.getString("t"));
            }

            if (aps.getString("alert") != null) {
                pushMessage = aps.getString("alert");
                intent.putExtra("popupmessage", pushMessage);
            }

            if (jsonMessage.getString("at") != null) {
                intent.putExtra("at", jsonMessage.getString("at"));
            }

            if (jsonMessage.getString("al") != null) {
                intent.putExtra("al", jsonMessage.getString("al"));
            }

            if (jsonMessage.getString("mid") != null) {
                intent.putExtra("mid", jsonMessage.getString("mid"));
            }

            if (jsonMessage.getString("cid") != null) {
                intent.putExtra("cid", jsonMessage.getString("cid"));
            } else {
                intent.putExtra("cid", "0");
            }
        }

        // If app is running show the notification
        if (isAppOnForeground(this)) {
            intent.putExtra("showAlert", true);
            startActivity(intent);
        }

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,intent, PendingIntent.FLAG_CANCEL_CURRENT);

        int iconID = getApplicationContext().getResources().
                getIdentifier(getApplicationContext().getPackageName() + ":drawable/egoi", null, null);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(iconID)
                .setContentTitle("My App")
                .setContentText(pushMessage)
                .setContentIntent(contentIntent);

        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;

        mBuilder.setDefaults(defaults);

        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        @SuppressWarnings("deprecation")
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK| PowerManager.ACQUIRE_CAUSES_WAKEUP, "My App");
        wl.acquire(500);
    }

    /*
    Private methods
     */
    private boolean isAppOnForeground(Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

        if (appProcesses == null) {
            return false;
        }

        final String packageName = context.getPackageName();

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}