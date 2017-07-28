package base.app.fragment.popup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import base.app.Connection;
import base.app.R;
import base.app.adapter.AccountCreatingAdapter;
import base.app.fragment.BaseFragment;
import base.app.fragment.FragmentEvent;
import base.app.fragment.instance.WallFragment;
import base.app.model.AlertDialogManager;
import base.app.model.Model;
import base.app.model.user.LoginStateReceiver;
import base.app.model.user.PasswordResetReceiver;
import base.app.model.user.UserInfo;
import base.app.util.KeyboardChangeListener;
import base.app.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Filip on 1/16/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class LoginFragment extends BaseFragment implements LoginStateReceiver.LoginStateListener, PasswordResetReceiver.PasswordResetListener {

    private static final String TAG = "LOGIN FRAGMENT";
    @BindView(R.id.login_email)
    EditText emailEditText;
    @BindView(R.id.login_password)
    EditText passwordEditText;
    @BindView(R.id.login_progress_bar)
    AVLoadingIndicatorView progressBar;
    @BindView(R.id.login_text)
    TextView loginText;
    @BindView(R.id.reset_text)
    TextView resetText;

    @BindView(R.id.forgot_password_container)
    RelativeLayout forgotPasswordContainer;

    @BindView(R.id.content_container)
    RelativeLayout loginContainer;

    @BindView(R.id.forgot_password_back)
    ImageView forgotPasswordBack;
    @Nullable
    @BindView(R.id.image_logo)
    ImageView imageLogo;
    @Nullable
    @BindView(R.id.logo_fq_image)
    ImageView logoFqImage;
    @Nullable
    @BindView(R.id.image_player)
    ImageView imagePlayer;


    @Nullable
    @BindView(R.id.forgot_button)
    TextView forgotButton;
    @Nullable
    @BindView(R.id.title_text)
    TextView titleText;


    @BindView(R.id.bottom_buttons_container_reset)
    RelativeLayout resetButtonContainer;
    @Nullable
    @BindView(R.id.bottom_buttons_container_login)
    RelativeLayout loginButtonContainer;

    @BindView(R.id.login_forgot_pass_email)
    EditText emailForgotPassword;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Nullable
    @BindView(R.id.sign_up_facebook)
    LoginButton loginButton;
    private CallbackManager callbackManager;

    private LoginStateReceiver loginStateReceiver;
    private PasswordResetReceiver passwordResetReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (Model.getInstance().getLoggedInUserType() == Model.LoggedInUserType.REAL && !Utility.isTablet(getActivity())) {
            EventBus.getDefault().post(new FragmentEvent(WallFragment.class, true));
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_login, container, false);
        ButterKnife.bind(this, view);
        this.loginStateReceiver = new LoginStateReceiver(this);
        this.passwordResetReceiver = new PasswordResetReceiver(this);
        initFacebook();


        if (titleText != null) {
            titleText.setText(Utility.fromHtml(getString(R.string.login_slider_text_1_phone)));
        }

        forgotPasswordBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginContainer.setVisibility(View.VISIBLE);
                if (loginButtonContainer != null) {
                    loginButtonContainer.setVisibility(View.VISIBLE);
                }
                resetButtonContainer.setVisibility(View.INVISIBLE);
                forgotPasswordContainer.setVisibility(View.GONE);
            }
        });

        // --- TODO For testing only!
//        emailEditText.setText(Prefs.getString("LAST_TEST_EMAIL","marco@polo.com"));
//        passwordEditText.setText("qwerty");
        // ---

        if (Utility.isTablet(getActivity()))
        {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            new KeyboardChangeListener(getActivity()).setKeyBoardListener(new KeyboardChangeListener.KeyBoardListener() {
                @Override
                public void onKeyboardChange(boolean isShow, int keyboardHeight) {
                    if (isShow) {
                        titleText.setVisibility(View.GONE);
                        if (imageLogo != null) {
                            imageLogo.setVisibility(View.GONE);
                        }
                        if (logoFqImage != null) {
                            logoFqImage.setVisibility(View.GONE);
                        }
                        if (forgotButton != null) {
                            forgotButton.setVisibility(View.GONE);
                        }
                        if (imagePlayer != null) {
                            imagePlayer.setImageResource(R.drawable.background_kayboard_open);
                        }


                    } else {
                        titleText.setVisibility(View.VISIBLE);
                        if (imageLogo != null) {
                            imageLogo.setVisibility(View.VISIBLE);
                        }
                        if (logoFqImage != null) {
                            logoFqImage.setVisibility(View.VISIBLE);
                        }
                        if (forgotButton != null) {
                            forgotButton.setVisibility(View.VISIBLE);
                        }
                        if (imagePlayer != null) {
                            imagePlayer.setImageResource(R.drawable.video_chat_background);
                        }


                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void initFacebook() {
        if (loginButton != null) {
            loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        }
        callbackManager = CallbackManager.Factory.create();
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
                                    emailEditText.setText(object.getString("email"));
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
                Log.d(TAG,"Facebook login canceled!");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG,"Facebook login error - error is:" + error.getLocalizedMessage());
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(loginStateReceiver);
        EventBus.getDefault().unregister(passwordResetReceiver);
    }

    @Override
    public void onDestroyView() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onDestroyView();
    }

    @OnClick(R.id.bottom_buttons_container_login)
    public void loginOnClick() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.enter_valid_password_and_display_name), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Connection.getInstance().alertIfNotReachable(getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        })) {
            return;
        }

        // --- TODO For testing only!
        //Prefs.putString("LAST_TEST_EMAIL",email);
        // ---

        Model.getInstance().login(email, password);
        loginText.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Optional
    @OnClick(R.id.reset_text)
    public void forgotPasswordOnClick() {
        String email = emailForgotPassword.getText().toString();
        Model.getInstance().resetPassword(email);
        getActivity().onBackPressed();
    }

//    @Optional
//    @OnClick(R.id.sign_up_button)
//    public void signUpOnClick() {
//        EventBus.getDefault().post(new FragmentEvent(base.app.fragment.popup.SignUpFragment.class));
//
//    }

    @Optional
    @OnClick(R.id.forgot_button)
    public void forgotOnClick() {
        if (Utility.isTablet(getActivity())) {
            if (loginButtonContainer != null) {
                loginButtonContainer.setVisibility(View.GONE);
            }
            loginContainer.setVisibility(View.GONE);
        } else {
            loginContainer.setVisibility(View.INVISIBLE);
            if (loginButtonContainer != null) {
                loginButtonContainer.setVisibility(View.INVISIBLE);
            }
        }


        resetButtonContainer.setVisibility(View.VISIBLE);
        forgotPasswordContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLogout() {
//        progressBar.setVisibility(View.GONE);
//        showLoginForms();
    }

    @Override
    public void onLoginAnonymously() {

    }

    @Override
    public void onLogin(UserInfo user) {
        progressBar.setVisibility(View.GONE);
        loginText.setVisibility(View.VISIBLE);
        EventBus.getDefault().post(Model.getInstance().getUserInfo()); //catch in Lounge Activity
        Utility.hideKeyboard(getActivity());
        if(Utility.isTablet(getActivity()))
        {
            EventBus.getDefault().post(new FragmentEvent(AccountCreatingAdapter.class));
        }
        else {
            EventBus.getDefault().post(new FragmentEvent(WallFragment.class));
        }
    }

    @Override
    public void onLoginError(Error error) {
        progressBar.setVisibility(View.GONE);
        loginText.setVisibility(View.VISIBLE);
        AlertDialogManager.getInstance().showAlertDialog(
                getContext().getResources().getString(R.string.login_login_message_login_failed),
                getContext().getResources().getString(R.string.login_try_again),
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                        EventBus.getDefault().post(new FragmentEvent(LoginFragment.class));
                    }
                }
        );
    }

    // To animate view slide out from left to right
    public void slideToRight(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, view.getWidth(), 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.VISIBLE);
    }

    // To animate view slide out from right to left
    public void slideToLeft(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, -view.getWidth(), 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPasswordResetRequest() {
        AlertDialogManager.getInstance().showAlertDialog(
                getContext().getResources().getString(R.string.forgot_password_text),
                getContext().getResources().getString(R.string.forgot_password_message),
                null, new View.OnClickListener() { // Confirm
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                    }
                });
    }

    @Override
    public void onPasswordResetRequestError(Error error) {
        AlertDialogManager.getInstance().showAlertDialog(
                getContext().getResources().getString(R.string.forgot_password_error),
                getContext().getResources().getString(R.string.forgot_password_error_message),
                null, new View.OnClickListener() { // Confirm
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                        forgotOnClick();
                    }
                });
    }

    @Optional
    @OnClick(R.id.facebook_button)
    public void setLoginFacebook() {
        assert loginButton != null;
        loginButton.performClick();
    }
}
