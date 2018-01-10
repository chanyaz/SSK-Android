package base.app.data.facebook;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.TaskCompletionSource;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Filip on 11/14/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class FacebookModel {

    private static final String TAG = "FACEBOOK MODEL";
    private static FacebookModel instance;

    public static FacebookModel getInstance(){
        if(instance==null){
            instance = new FacebookModel();
        }
        return instance;
    }

    private static final String PROFILE_PARAMETERS= "id, name, first_name, last_name, picture.type(large), email, friends, birthday, age_range, location, gender";
    private static final List<String> LOGIN_PERIMISSIONS  = Arrays.asList("public_profile", "email", "user_friends","user_birthday", "user_photos");
    private static final String IMAGE_PREFIX = "https://graph.facebook.com/";
    private static final String IMAGE_SUFIX = "/picture?width=512&height=512";

    public void login(Fragment fragment){
        LoginManager.getInstance().logInWithReadPermissions(fragment,LOGIN_PERIMISSIONS);
    }

    public void logout(){
        LoginManager.getInstance().logOut();
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
        return IMAGE_PREFIX + id + IMAGE_SUFIX;
    }
}
