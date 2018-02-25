package base.app.ui.fragment.popup;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import base.app.BuildConfig;
import base.app.R;
import base.app.util.AlertDialogManager;
import base.app.data.Model;
import base.app.data.user.LoginStateReceiver;
import base.app.data.user.LoginStateReceiver.LoginStateListener;
import base.app.data.user.PasswordResetReceiver;
import base.app.data.user.PasswordResetReceiver.PasswordResetListener;
import base.app.data.user.UserInfo;
import base.app.ui.activity.BaseActivity;
import base.app.ui.adapter.profile.AccountCreatingAdapter;
import base.app.ui.fragment.base.BaseFragment;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.util.commons.Connection;
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
    View progressBar;
    @BindView(R.id.reset_progress_bar)
    View resetProgressBar;
    @BindView(R.id.forgot_password_container)
    RelativeLayout forgotPasswordContainer;
    @BindView(R.id.content_container)
    RelativeLayout loginContainer;
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
    @BindView(R.id.restoreButton)
    Button restoreButton;
    @Nullable
    @BindView(R.id.loginButton)
    Button loginButton;
    @BindView(R.id.login_forgot_pass_email)
    EditText emailForgotPassword;
    @Nullable
    @BindView(R.id.sign_up_facebook)
    LoginButton facebookLoginButton;
    @BindView(R.id.loadingOverlay)
    View loadingOverlay;
    @BindView(R.id.passwordInputLayout)
    TextInputLayout passwordInputLayout;
    @BindView(R.id.facebook_button)
    View facebookButton;
    @BindView(R.id.orContainer)
    View orContainer;
    @BindView(R.id.backgroundImageView)
    ImageView backgroundImageView;
    @BindView(R.id.logoImageView)
    View logoImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (Model.getInstance().isRealUser() && Utility.isPhone(getActivity())) {
            getActivity().onBackPressed();
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        this.loginStateReceiver = new LoginStateReceiver(this);
        this.passwordResetReceiver = new PasswordResetReceiver(this);
        initFacebook();

        if (titleText != null) {
            titleText.setText(Html.fromHtml(getString(R.string.slogan)));
        }

        if (Utility.isTablet(getContext())) {
            View.OnFocusChangeListener focusChangeListener = Utility.getAdjustResizeFocusListener(getActivity());
            emailEditText.setOnFocusChangeListener(focusChangeListener);
            passwordEditText.setOnFocusChangeListener(focusChangeListener);
            emailForgotPassword.setOnFocusChangeListener(focusChangeListener);
        }

        hideSecondaryViewsOnKeyboardOpen(view);
        Glide.with(getContext())
                .load(R.drawable.background_sporting)
                .into(backgroundImageView);

        return view;
    }

    private void closeForgotView() {
        loginContainer.setVisibility(View.VISIBLE);
        if (loginButton != null) {
            loginButton.setVisibility(View.VISIBLE);
        }
        restoreButton.setVisibility(View.GONE);
        forgotPasswordContainer.setVisibility(View.GONE);

        titleTextView.setText(getString(R.string.sign_in));
    }

    private void hideSecondaryViewsOnKeyboardOpen(final View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //r will be populated with the coordinates of your view that area still visible.
                view.getWindowVisibleDisplayFrame(r);

                int heightDiff = view.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > 500) { // if more than 100 pixels, its probably a keyboard...
                    facebookButton.setVisibility(View.GONE);
                    forgotButton.setVisibility(View.GONE);
                    logoImageView.setVisibility(View.GONE);
                    orContainer.setVisibility(View.INVISIBLE);
                } else {
                    facebookButton.setVisibility(View.VISIBLE);
                    forgotButton.setVisibility(View.VISIBLE);
                    logoImageView.setVisibility(View.VISIBLE);
                    orContainer.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (BuildConfig.DEBUG) {
            emailEditText.setText("alexsheikodev3@gmail.com");
            passwordEditText.setText("temppass");
        }
        passwordInputLayout.setPasswordVisibilityToggleEnabled(true);

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    loginOnClick();
                    handled = true;
                }
                return handled;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void initFacebook() {
        if (facebookLoginButton != null) {
            facebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends", "user_birthday", "user_photos"));
            callbackManager = CallbackManager.Factory.create();
            facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {
                    loadingOverlay.setVisibility(View.VISIBLE);

                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    final HashMap<String, Object> userData = new Gson().fromJson(
                                            object.toString(), new TypeToken<HashMap<String, Object>>() {
                                            }.getType()
                                    );

                                    String email = null;
                                    try {
                                        email = object.getString("email");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    final AccessToken token = loginResult.getAccessToken();

                                    LoginManager.getInstance().logOut();

                                    Model.getInstance().loginFromFacebook(
                                            token,
                                            email,
                                            userData);
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

    @OnClick(R.id.loginButton)
    public void loginOnClick() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.required_credentials), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Connection.alertIfNotReachable
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
        loginButton.setText(null);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Optional
    @OnClick(R.id.restoreButton)
    public void forgotPasswordOnClick() {
        String email = emailForgotPassword.getText().toString();
        Model.getInstance().resetPassword(email);

        restoreButton.setText(null);
        resetProgressBar.setVisibility(View.VISIBLE);
    }

    @Optional
    @OnClick(R.id.forgot_button)
    public void forgotOnClick() {
        showForgotUI();
    }

    protected void showForgotUI() {
        loginContainer.setVisibility(View.INVISIBLE);
        if (loginButton != null) {
            loginButton.setVisibility(View.INVISIBLE);
        }
        restoreButton.setVisibility(View.VISIBLE);
        forgotPasswordContainer.setVisibility(View.VISIBLE);

        emailForgotPassword.setText(emailEditText.getText());

        titleTextView.setText(getString(R.string.forgot_your_password));
    }

    protected void hideForgotUI() {
        loginContainer.setVisibility(View.VISIBLE);
        if (loginButton != null) {
            loginButton.setVisibility(View.VISIBLE);
        }

        restoreButton.setVisibility(View.GONE);
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
        loginButton.setText(R.string.sign_in);
        EventBus.getDefault().post(Model.getInstance().getUserInfo()); //catch in Lounge Activity
        Utility.hideKeyboard(getActivity());
        if (Utility.isTablet(getActivity())) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            EventBus.getDefault().post(new FragmentEvent(AccountCreatingAdapter.class));
        } else {
            BaseActivity activity = (BaseActivity) getActivity();
            if (activity != null && !activity.fragmentOrganizer.handleNavigationFragment()) {
                activity.onBackPressed();
            }
        }
    }

    @Override
    public void onLoginError(Error error) {
        progressBar.setVisibility(View.GONE);
        loginButton.setText(R.string.sign_in);
        loadingOverlay.setVisibility(View.GONE);
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
                null,
                new View.OnClickListener() { // Confirm
                    @Override
                    public void onClick(View v) {
                        hideForgotUI();
                    }
                });
        restoreButton.setText(R.string.password_reset);
        resetProgressBar.setVisibility(View.GONE);
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
        restoreButton.setText(R.string.password_reset);
        resetProgressBar.setVisibility(View.GONE);
    }

    @Optional
    @OnClick(R.id.facebook_button)
    public void setLoginFacebook() {
        if (facebookLoginButton != null) {
            facebookLoginButton.performClick();
        }
    }

    @OnClick(R.id.backButton)
    public void onBackPressed() {
        if (forgotPasswordContainer.getVisibility() == View.VISIBLE) {
            closeForgotView();
        } else {
            getActivity().onBackPressed();
        }
    }

    @OnClick(R.id.clickOutsideContainer)
    public void onClickOutside() {
        Utility.hideKeyboard(this);
    }
}
