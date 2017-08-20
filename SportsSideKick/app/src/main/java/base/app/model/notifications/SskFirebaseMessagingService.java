package base.app.model.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;

import base.app.R;
import base.app.activity.BaseActivity;


public class SskFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "Ssk Firebase Service";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            //We have message data, display it:
            sendNotification(remoteMessage);
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        //EventBus.getDefault().post(new ExternalNotificationEvent(remoteMessage.getMessage(), false));

    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * */
    private void sendNotification(RemoteMessage remoteMessage) {
        //Set up intent that is going to be handled in Lounge Activity
        Map<String,String> messageData = remoteMessage.getData();
        String alert = null;
        if(messageData.containsKey("alert")){
             alert = (String) messageData.get("alert");
        }
        Gson gson = new Gson();
        Intent intent = new Intent(this, BaseActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("SSK_PUSH_NOTIFICATION_DATA",gson.toJson(messageData));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        //Extract data for display!
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_ssk)
                .setContentTitle("SIDEKICK")
                .setContentText(alert)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 , notificationBuilder.build());
    }
}