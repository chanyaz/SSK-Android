package tv.sportssidekick.sportssidekick.fragment.popup;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.model.GSConstants;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.user.RegistrationStateReceiver;

/**
 * Created by Filip on 1/16/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class SignUpFragment extends BaseFragment implements RegistrationStateReceiver.RegistrationStateListener {


    @BindView(R.id.sign_up_progress_bar)
    AVLoadingIndicatorView progressBar;

    @BindView(R.id.sign_up_text)
    TextView signUpText;

    @BindView(R.id.sign_up_firstname)
    TextView firstName;
    @BindView(R.id.sign_up_lastname)
    TextView lastName;
    @BindView(R.id.sign_up_email)
    TextView email;
    @BindView(R.id.sign_up_display_name)
    TextView displayName;
    @BindView(R.id.sign_up_phone)
    TextView phone;
    @BindView(R.id.sign_up_password)
    TextView password;

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private RegistrationStateReceiver registrationStateReceiver;

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_signup, container, false);
        ButterKnife.bind(this, view);
        this.registrationStateReceiver = new RegistrationStateReceiver(this);

        //TODO DEMO
//        firstName.setText("Marco");
//        lastName.setText("Polo");
//        displayName.setText("marco polo");
//        phone.setText("123456789");
//        email.setText("marco@polo.com");
//        password.setText("qwerty");

        loginButton = (LoginButton) view.findViewById(R.id.sign_up_facebook);

        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                // Application code
                                try {
                                    email.setText(object.getString("email"));
                                    firstName.setText(object.getString("first_name"));
                                    lastName.setText(object.getString("last_name"));
                                    displayName.setText(object.getString("first_name") + " " +object.getString("last_name"));
                                    LoginManager.getInstance().logOut();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "name,email,first_name,last_name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException error) {
            }
        });

        return view;
    }

    @OnClick(R.id.bottom_buttons_container_sign_up)
    public void signUpOnClick(){
        if (TextUtils.isEmpty(firstName.getText()) ||
                TextUtils.isEmpty(lastName.getText())||
                TextUtils.isEmpty(email.getText())||
                TextUtils.isEmpty(displayName.getText())||
                TextUtils.isEmpty(password.getText())||
                TextUtils.isEmpty(phone.getText()))
        {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.fiil_all_data), Toast.LENGTH_LONG).show();
        }
        else {
            HashMap<String, Object> userDetails = new HashMap<>();
            userDetails.put(GSConstants.FIRST_NAME, firstName.getText().toString());
            userDetails.put(GSConstants.LAST_NAME, lastName.getText().toString());
            userDetails.put(GSConstants.PHONE, phone.getText().toString());
            userDetails.put(GSConstants.EMAIL, email.getText().toString());
            signUpText.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            Model.getInstance().registrationRequest(
                    displayName.getText().toString(),
                    password.getText().toString(),
                    email.getText().toString(),
                    userDetails);
        }
    }

    @OnClick(R.id.sign_up_login_button)
    public void loginOnClick(){
        EventBus.getDefault().post(new FragmentEvent(LoginFragment.class));
    }

    @OnClick(R.id.sign_up_facebook)
    public void facebookOnClick(){
        //TODO facebook sign up
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(registrationStateReceiver);
    }

    @Override
    public void onRegister() {
        progressBar.setVisibility(View.GONE);
        signUpText.setVisibility(View.VISIBLE);
        getActivity().onBackPressed();
    }

    @Override
    public void onRegisterError(Error error) {
        progressBar.setVisibility(View.GONE);
        signUpText.setVisibility(View.VISIBLE);
        Toast.makeText(getContext(), getContext().getResources().getString(R.string.registarion_error), Toast.LENGTH_LONG).show(); // TODO inform user about login failed
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
