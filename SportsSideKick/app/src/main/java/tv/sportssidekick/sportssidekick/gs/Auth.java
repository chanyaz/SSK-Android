package tv.sportssidekick.sportssidekick.gs;

import android.util.Log;

import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.android.GSAndroidPlatform;
import com.gamesparks.sdk.api.GSData;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;
import com.gamesparks.sdk.api.autogen.GSTypes;

/**
 * Created by Filip on 2/12/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class Auth {

    private static final String TAG = "Auth";


    public static void registerForPushNotifications(String pushId){
//        String authorizedEntity = PROJECT_ID; // Project id from Google Developer Console
//        String scope = "GCM"; // e.g. communicating using GCM, but you can use any
//        // URL-safe characters up to a maximum of 1000, or
//        // you can also leave it blank.
//        String token = InstanceID.getInstance(context).getToken(authorizedEntity,scope);

        Log.d(TAG, "Registering for push notifications");
        GSAndroidPlatform.gs().getRequestBuilder().createPushRegistrationRequest()
                .setDeviceOS("android")
                .setPushId(pushId)
                .send(new GSEventConsumer<GSResponseBuilder.PushRegistrationResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.PushRegistrationResponse pushRegistrationResponse) {
                        String registrationId = pushRegistrationResponse.getRegistrationId();
                        Log.d(TAG, "Registration id is:" + registrationId);
                        GSData scriptData = pushRegistrationResponse.getScriptData();
                    }
                });
    }

    public static void registrationRequest(String displayName, String password, String userName){
        GSAndroidPlatform.gs().getRequestBuilder().createRegistrationRequest()
                .setDisplayName(displayName)
                .setPassword(password)
                .setUserName(userName)
                .send(new GSEventConsumer<GSResponseBuilder.RegistrationResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.RegistrationResponse response) {
                        String authToken = response.getAuthToken();
                        String displayName = response.getDisplayName();
                        Boolean newPlayer = response.getNewPlayer();
                        GSData scriptData = response.getScriptData();
                        GSTypes.Player switchSummary = response.getSwitchSummary();
                        String userId = response.getUserId();
                        Log.d(TAG, "REGISTRATION authToken:" + authToken);
                        Log.d(TAG, "REGISTRATION userId:" + userId);
                    }
                });
    }

    public static void authenticationRequest(String userName, String password){
        GSAndroidPlatform.gs().getRequestBuilder().createAuthenticationRequest()
                .setUserName(userName)
                .setPassword(password)
                .send(new GSEventConsumer<GSResponseBuilder.AuthenticationResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.AuthenticationResponse response) {
                        String authToken = response.getAuthToken();
                        String displayName = response.getDisplayName();
                        Boolean newPlayer = response.getNewPlayer();
                        GSData scriptData = response.getScriptData();
                        GSTypes.Player switchSummary = response.getSwitchSummary();
                        String userId = response.getUserId();
                        Log.d(TAG, "AUTHENTICATION authToken:" + authToken);
                        Log.d(TAG, "AUTHENTICATION userId:" + userId);
                        registerForPushNotifications(userId);
                    }
                });
    }
}
