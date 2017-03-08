package tv.sportssidekick.sportssidekick.fragment.instance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.model.UserInfo;

import static com.facebook.FacebookSdk.getApplicationContext;
import static tv.sportssidekick.sportssidekick.util.Utility.internetAvailable;
import static tv.sportssidekick.sportssidekick.util.Utility.isEditTextEmpty;
import static tv.sportssidekick.sportssidekick.util.Utility.showAlertDialog;


/**
 * Created by Djordje Krutil on 6.12.2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class SignUpFragment extends BaseFragment {

    private static final String TAG = "Sign Up fragment";
    AlertDialog alertDialog;
    EditText firstName, lastName, userName, email, phone, password;
    Button signUpButton;
    LoginButton facebookButton;
    TextView termsText;
    Context context;
    private List<String> facebookPermissions =null;
    CallbackManager callbackManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        facebookPermissions = Arrays.asList("public_profile", "email", "user_friends", "user_birthday", "user_photos");

        alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getString(R.string.dialog_warning));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.dialog_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        facebookButton = (LoginButton) view.findViewById(R.id.sign_up_facebook);
        facebookButton.setReadPermissions(facebookPermissions);
        facebookButton.setFragment(this);
        // Callback registration
        facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v(TAG, response.toString());
                                try {
                                    String id = object.getString("id");
                                    String name = object.getString("name");
                                    String first_name = object.getString("first_name");
                                    String last_name = object.getString("last_name");
                                    String email = object.getString("email");
                                    String friends = object.getString("friends");
                                    String birthday = object.getString("birthday");
                                    String age_range = object.getString("age_range");
                                    String location = object.getString("email");
                                    String gender = object.getString("birthday");
                                    String imageURL = "http://graph.facebook.com/" + id + "/picture?type=large";
                                    //TODO send data to FIREBASE

                                    LoginManager.getInstance().logOut();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, first_name, last_name, picture.type(large), email, friends, birthday, age_range, location, gender");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                showAlertDialog(getString(R.string.dialog_warning), getString(R.string.dialog_message_facebook_error), context);
            }

            @Override
            public void onError(FacebookException exception) {
                showAlertDialog(getString(R.string.dialog_warning), getString(R.string.dialog_message_facebook_error), context);
            }
        });

        firstName = (EditText) view.findViewById(R.id.sign_up_firstname);
        lastName = (EditText) view.findViewById(R.id.sign_up_lastname);
        userName = (EditText) view.findViewById(R.id.sign_up_username);
        email = (EditText) view.findViewById(R.id.sign_up_email);
        phone = (EditText) view.findViewById(R.id.sign_up_phone);
        password = (EditText) view.findViewById(R.id.sign_up_password);

        termsText = (TextView) view.findViewById(R.id.sign_up_terms_text);
        termsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent termsAndConditions = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.terms_and_conditions)));
                getActivity().startActivity(termsAndConditions);
            }
        });

        signUpButton = (Button) view.findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEditTextEmpty(firstName, getString(R.string.sign_up_firstname), alertDialog, context))
                    return;
                if (isEditTextEmpty(lastName, getString(R.string.sign_up_lastname), alertDialog, context))
                    return;
                if (isEditTextEmpty(userName, getString(R.string.sign_up_username), alertDialog, context))
                    return;
                if (isEditTextEmpty(email, getString(R.string.sign_up_email), alertDialog, context))
                    return;
                if (isEditTextEmpty(phone, getString(R.string.sign_up_phone), alertDialog, context))
                    return;
                if (isEditTextEmpty(password, getString(R.string.sign_up_passwoed), alertDialog, context))
                    return;

                if (!internetAvailable(context)) {
                    showAlertDialog(getString(R.string.dialog_warning), getString(R.string.dialog_interner_connection_falied), context);
                    return;
                }

                final UserInfo userInfo = new UserInfo(
                        firstName.getText().toString(),
                        lastName.getText().toString(),
                        userName.getText().toString(),
                        phone.getText().toString());
                        // TODO Implement in GS!
            }
        });
    }
}
