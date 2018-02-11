package base.app.ui.fragment.popup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import base.app.BuildConfig;
import base.app.R;
import base.app.data.GSConstants;
import base.app.data.Model;
import base.app.data.user.RegistrationStateReceiver;
import base.app.ui.adapter.profile.AccountCreatingAdapter;
import base.app.ui.fragment.base.BaseFragment;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.ui.fragment.content.WallFragment;
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

public class SignUpFragment extends BaseFragment implements RegistrationStateReceiver.RegistrationStateListener {

    String url;
    @BindView(R.id.web_view)
    WebView webView;

    View view;
    @BindView(R.id.progress_bar_veb_view)
    View progressBarVebView;

    @BindView(R.id.sign_up_progress_bar)
    View progressBar;
    @BindView(R.id.sign_up_text)
    TextView signUpText;
    @BindView(R.id.sign_up_firstname)
    EditText firstName;
    @BindView(R.id.sign_up_lastname)
    EditText lastName;
    @BindView(R.id.sign_up_email)
    EditText email;
    @Nullable
    @BindView(R.id.sign_up_display_name)
    EditText displayName;
    @Nullable
    @BindView(R.id.sign_up_phone)
    EditText phone;
    @BindView(R.id.sign_up_password)
    EditText password;

    @BindView(R.id.sign_up_facebook)
    LoginButton loginButton;
    @BindView(R.id.web_view_holder)
    RelativeLayout termsHolder;
    @Nullable
    @BindView(R.id.politic_and_privacy_android)
    TextView policyText;
    @Nullable
    @BindView(R.id.popupSignupContainer)
    RelativeLayout editTextContainer;
    @Nullable
    @BindView(R.id.facebook_button)
    Button facebookButton;
    @Nullable
    @BindView(R.id.popupSignupJoinNowButton)
    RelativeLayout joinNowContainer;
    @BindView(R.id.loadingOverlay)
    View loadingOverlay;


    private CallbackManager callbackManager;

    private RegistrationStateReceiver registrationStateReceiver;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public void scaleViews() {
        int width = Utility.getDisplayWidth(getActivity());
        if (editTextContainer != null) {
            editTextContainer.getLayoutParams().width = (int) (width * 0.55);
        }
        if (facebookButton != null) {
            facebookButton.getLayoutParams().width = (int) (width * 0.4);
        }
        if (joinNowContainer != null) {
            joinNowContainer.getLayoutParams().width = (int) (width * 0.4);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_signup, container, false);
        ButterKnife.bind(this, view);
        this.registrationStateReceiver = new RegistrationStateReceiver(this);

        initFacebook();
        setupPolicyText();
        if (Utility.isTablet(getActivity())) {
            scaleViews();
        }
        termsHolder.setVisibility(View.INVISIBLE);
        setUpPolicyWebView();

        if(Utility.isTablet(getContext())){
            View.OnFocusChangeListener focusChangeListener = Utility.getAdjustResizeFocusListener(getActivity());
            firstName.setOnFocusChangeListener(focusChangeListener);
            lastName.setOnFocusChangeListener(focusChangeListener);
            email.setOnFocusChangeListener(focusChangeListener);
            password.setOnFocusChangeListener(focusChangeListener);
            displayName.setOnFocusChangeListener(focusChangeListener);
            phone.setOnFocusChangeListener(focusChangeListener);
        }
            if (BuildConfig.DEBUG) {
            firstName.setText("A");
            lastName.setText("B");
            displayName.setText("A B");
            email.setText("alexsheikodev5@gmail.com");
            password.setText("temppass");
            phone.setText("123");
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (Model.getInstance().isRealUser() && Utility.isPhone(getActivity())) {
            getActivity().onBackPressed();
            //EventBus.getDefault().createPost(new FragmentEvent(WallFragment.class, true));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setUpPolicyWebView() {

        progressBarVebView.setVisibility(View.VISIBLE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setVisibility(View.INVISIBLE);
        webView.setWebViewClient(webViewClient);
        url = getResources().getString(R.string.terms_and_privacy_url) + Locale.getDefault().getLanguage();
    }

    WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
        }

    };

    public void setupPolicyText() {
        SpannableString spannableString = new SpannableString(policyText.getText());
        ClickableSpan clickableSpanTerms = new ClickableSpan() {
            @Override
            public void onClick(View view) { }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.WHITE);
            }
        };
        ClickableSpan clickableSpanPolicy = new ClickableSpan() {
            @Override
            public void onClick(View view) { }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.WHITE);
            }
        };

        policyText.setMovementMethod(LinkMovementMethod.getInstance());
        policyText.setHighlightColor(Color.TRANSPARENT);
        policyText.setText(spannableString);
    }

    private void initFacebook() {
        if (loginButton != null) {
            loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends", "user_birthday", "user_photos"));
            callbackManager = CallbackManager.Factory.create();
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
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
                    Log.d("SignUpFragment", "Facebook login canceled!");
                }

                @Override
                public void onError(FacebookException error) {
                    Log.d("SignUpFragment", "Facebook login error - error is:" + error.getLocalizedMessage());
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private boolean validateInputs(){
        int idOfMessageResourceToDisplay = -1;
        if (TextUtils.isEmpty(firstName.getText())) {
            idOfMessageResourceToDisplay = R.string.required_first_name;
        } else if (TextUtils.isEmpty(email.getText())) {
            idOfMessageResourceToDisplay = R.string.required_email;
        } else if (TextUtils.isEmpty(displayName != null ? displayName.getText() : "string")) {
            idOfMessageResourceToDisplay = R.string.required_nickname;
        } else if (TextUtils.isEmpty(password.getText())) {
            idOfMessageResourceToDisplay = R.string.required_password;
        } else if (TextUtils.isEmpty(phone != null ? phone.getText() : "string")) {
            idOfMessageResourceToDisplay = R.string.required_phone;
        } else if (TextUtils.isEmpty(lastName.getText())) {
            idOfMessageResourceToDisplay = R.string.required_last_name;
        }
        if (idOfMessageResourceToDisplay >= 0) {
            String message = getContext().getResources().getString(idOfMessageResourceToDisplay);
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    @Optional
    @OnClick(R.id.bottom_buttons_container_sign_up)
    public void signUpOnClick() {

       if(validateInputs()) {

            if (!Connection.getInstance().alertIfNotReachable(getActivity(),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().onBackPressed();
                        }
                    }
            )) {
                return;
            }

            HashMap<String, Object> userDetails = new HashMap<>();
            userDetails.put(GSConstants.FIRST_NAME, firstName.getText().toString());
            userDetails.put(GSConstants.LAST_NAME, lastName.getText().toString());
            if (phone != null)
                userDetails.put(GSConstants.PHONE, phone.getText().toString());
            userDetails.put(GSConstants.EMAIL, email.getText().toString());
            signUpText.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            if (displayName != null) {
                Model.getInstance().registrationRequest(
                        displayName.getText().toString(),
                        password.getText().toString(),
                        email.getText().toString(),
                        userDetails);
            } else {
                Model.getInstance().registrationRequest(
                        firstName.getText().toString() + lastName.getText().toString(),
                        password.getText().toString(),
                        email.getText().toString(),
                        userDetails);
            }
        }
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
        Utility.hideKeyboard(getActivity());
        if (Utility.isTablet(getActivity())) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            EventBus.getDefault().post(new FragmentEvent(AccountCreatingAdapter.class));
        } else {
            EventBus.getDefault().post(new FragmentEvent(WallFragment.class));
        }
    }

    @Override
    public void onRegisterError(Error error) {
        progressBar.setVisibility(View.GONE);
        signUpText.setVisibility(View.VISIBLE);
        String errorMessage = getContext().getResources().getString(R.string.registration_failed);
        if(error!=null && error.getMessage()!=null){
            if(error.getMessage().contains("TAKEN")){
                errorMessage = getContext().getResources().getString(R.string.error_username_taken);
            } else {
                errorMessage += "\n\n" + error.getMessage();
            }
        }
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe
    public void onConnectionEvent(Connection.OnChangeEvent event) {
        if (event.getStatus() == Connection.Status.notReachable) {
            Connection.getInstance().alertIfNotReachable(getActivity(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
            progressBar.setVisibility(View.GONE);
            signUpText.setVisibility(View.VISIBLE);
        }
    }

    @Optional
    @OnClick(R.id.politic_and_privacy_android)
    public void politicAndPrivacy() {
        Utility.hideKeyboard(getActivity());
        termsHolder.setVisibility(View.VISIBLE);
        webView.loadUrl(url);
    }

    @Optional
    @OnClick(R.id.facebook_button)
    public void setLoginFacebook() {
        loginButton.performClick();
    }

    public RelativeLayout getTermsHolder() {
        return termsHolder;
    }

}
