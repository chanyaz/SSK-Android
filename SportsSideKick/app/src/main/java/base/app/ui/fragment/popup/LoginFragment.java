package base.app.ui.fragment.popup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import base.app.BuildConfig;
import base.app.R;
import base.app.data.AlertDialogManager;
import base.app.data.Model;
import base.app.data.user.LoginStateReceiver;
import base.app.data.user.LoginStateReceiver.LoginStateListener;
import base.app.data.user.PasswordResetReceiver;
import base.app.data.user.PasswordResetReceiver.PasswordResetListener;
import base.app.data.user.UserInfo;
import base.app.ui.adapter.profile.AccountCreatingAdapter;
import base.app.ui.fragment.base.BaseFragment;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.util.commons.Connection;
import base.app.util.commons.KeyboardChangeListener;
import base.app.util.commons.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Filip on 1/16/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class LoginFragment extends BaseFragment
        implements LoginStateListener, PasswordResetListener {

    private static final String TAG = "LOGIN FRAGMENT";
    private CallbackManager callbackManager;
    private LoginStateReceiver loginStateReceiver;
    private PasswordResetReceiver passwordResetReceiver;
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
    @Nullable
    @BindView(R.id.sign_up_facebook)
    LoginButton loginButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (Model.getInstance().isRealUser() && Utility.isPhone(getActivity())) {
            getActivity().onBackPressed();
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_login, container, false);
        ButterKnife.bind(this, view);
        this.loginStateReceiver = new LoginStateReceiver(this);
        this.passwordResetReceiver = new PasswordResetReceiver(this);
        initFacebook();

        if (titleText != null) {
            titleText.setText(Html.fromHtml(getString(R.string.slogan)));
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

        if (Utility.isTablet(getActivity())) {

            if (Utility.isTablet(getContext())) {
                View.OnFocusChangeListener focusChangeListener = Utility.getAdjustResizeFocusListener(getActivity());
                emailEditText.setOnFocusChangeListener(focusChangeListener);
                passwordEditText.setOnFocusChangeListener(focusChangeListener);
                emailForgotPassword.setOnFocusChangeListener(focusChangeListener);
            }

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
                            Glide.with(getContext())
                                    .load(R.drawable.video_chat_background)
                                    .into(imagePlayer);
                        }
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (BuildConfig.DEBUG) {
            emailEditText.setText("alexsheikodev3@gmail.com");
            passwordEditText.setText("temppass");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void initFacebook() {
        if (loginButton != null) {
            loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends", "user_birthday", "user_photos"));
            callbackManager = CallbackManager.Factory.create();
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {
                    Toast.makeText(getContext(), "Signing in...", Toast.LENGTH_LONG).show();
                    // App code
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    HashMap<String, Object> userData = new Gson().fromJson(
                                            object.toString(), new TypeToken<HashMap<String, Object>>() {}.getType()
                                    );

                                    try {
                                         Model.getInstance().loginWithFacebook(
                                                 loginResult.getAccessToken().getToken(),
                                                 object.getString("email"),
                                                 userData);

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
                    Log.d(TAG, "Facebook login canceled!");
                }

                @Override
                public void onError(FacebookException error) {
                    Log.d(TAG, "Facebook login error - error is:" + error.getLocalizedMessage());
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(loginStateReceiver);
        EventBus.getDefault().unregister(passwordResetReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick(R.id.bottom_buttons_container_login)
    public void loginOnClick() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.required_credentials), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Connection.getInstance().alertIfNotReachable
                (getActivity(),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getActivity().onBackPressed();
                            }
                        }
                )
                ) {
            return;
        }
        Model.getInstance().login(email, password);
        loginText.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Optional
    @OnClick(R.id.reset_text)
    public void forgotPasswordOnClick() {
        String email = emailForgotPassword.getText().toString();
        Model.getInstance().resetPassword(email);
    }

    @Optional
    @OnClick(R.id.forgot_button)
    public void forgotOnClick() {
        showForgotUI();
    }

    protected void showForgotUI() {
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

        emailForgotPassword.setText(emailEditText.getText());
    }

    protected void hideForgotUI() {
        loginContainer.setVisibility(View.VISIBLE);
        if (loginButtonContainer != null) {
            loginButtonContainer.setVisibility(View.VISIBLE);
        }

        resetButtonContainer.setVisibility(View.GONE);
        forgotPasswordContainer.setVisibility(View.GONE);
    }

    @Override
    public void onLogout() {
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
        if (Utility.isTablet(getActivity())) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            EventBus.getDefault().post(new FragmentEvent(AccountCreatingAdapter.class));
        } else {
            getActivity().onBackPressed();
            //EventBus.getDefault().createPost(new FragmentEvent(WallFragment.class));
        }
    }

    @Override
    public void onLoginError(Error error) {
        progressBar.setVisibility(View.GONE);
        loginText.setVisibility(View.VISIBLE);
        AlertDialogManager.getInstance().showAlertDialog(
                getContext().getResources().getString(R.string.login_failed),
                getContext().getResources().getString(R.string.password_try_again),
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

    @Override
    public void onPasswordResetRequest() {
        AlertDialogManager.getInstance().showAlertDialog(
                getContext().getResources().getString(R.string.password_reset),
                getContext().getResources().getString(R.string.reset_check_email),
                new View.OnClickListener() { // Confirm
                    @Override
                    public void onClick(View v) {
                    }
                }, new View.OnClickListener() { // Confirm
                    @Override
                    public void onClick(View v) {
                        hideForgotUI();
                    }
                });
    }

    @Override
    public void onPasswordResetRequestError(Error error) {
        final AlertDialogManager dialog = AlertDialogManager.getInstance();
        dialog.showAlertDialog(
                getContext().getResources().getString(R.string.error),
                getContext().getResources().getString(R.string.email_enter_valid),
                new View.OnClickListener() { // Confirm
                    @Override
                    public void onClick(View v) {
                    }
                }, new View.OnClickListener() { // Confirm
                    @Override
                    public void onClick(View v) {
                    }
                });
    }

    @Optional
    @OnClick(R.id.facebook_button)
    public void setLoginFacebook() {
        if (loginButton != null) {
            loginButton.performClick();
        }
    }
}
