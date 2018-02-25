package base.app.data.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.autogen.GSRequestBuilder;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;
import com.google.android.gms.tasks.TaskCompletionSource;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import base.app.util.GSAndroidPlatform;

/**
 * Created by Filip on 11/14/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class FacebookLogin {

    private static final String TAG = "FACEBOOK MODEL";
    private static FacebookLogin instance;

    public static FacebookLogin getInstance(){
        if(instance==null){
            instance = new FacebookLogin();
        }
        return instance;
    }

    private static final String PROFILE_PARAMETERS= "id, name, first_name, last_name, picture.type(large), email, friends, birthday, age_range, location, gender";
    private static final List<String> LOGIN_PERMISSIONS = Arrays.asList("public_profile", "email", "user_friends","user_birthday", "user_photos");
    private static final String IMAGE_PREFIX = "https://graph.facebook.com/";
    private static final String IMAGE_SUFFIX = "/picture?width=512&height=512";

    public void login(Fragment fragment){
        LoginManager.getInstance().logInWithReadPermissions(fragment, LOGIN_PERMISSIONS);
    }

    public void logout(){
        LoginManager.getInstance().logOut();
        GSRequestBuilder.SocialDisconnectRequest request
                = GSAndroidPlatform.gs().getRequestBuilder().createSocialDisconnectRequest();
        request.setSystemId("FB");
        request.send(new GSEventConsumer<GSResponseBuilder.SocialDisconnectResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.SocialDisconnectResponse response) {
                if (response.hasErrors()) {
                    Log.e("FacebookLogin", "Failed to logout from social");
                }
            }
        });

        AccessToken.setCurrentAccessToken(null);
        Profile.setCurrentProfile(null);
    }

    public void loadProfile(final TaskCompletionSource<Pair<JSONObject,FacebookRequestError>> completion){
        if(AccessToken.getCurrentAccessToken() == null){
            Log.d(TAG,"No access token available!");
            completion.setResult(new Pair<JSONObject, FacebookRequestError>(null,null));
            return;
        }

        GraphRequest request = GraphRequest.newMeRequest( AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        FacebookRequestError error = response.getError();
                        completion.setResult(new Pair<>(object,error));
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields",PROFILE_PARAMETERS);
        request.setParameters(parameters);
        request.executeAsync();
    }

    public String profileUrl(String id){
        return IMAGE_PREFIX + id + IMAGE_SUFFIX;
    }
}
