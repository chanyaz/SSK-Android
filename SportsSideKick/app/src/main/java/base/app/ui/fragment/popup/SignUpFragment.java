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

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.commons.lang3.text.WordUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Locale;

import base.app.R;
import base.app.data.GSConstants;
import base.app.data.Model;
import base.app.data.user.RegistrationStateReceiver;
import base.app.ui.adapter.profile.AccountCreatingAdapter;
import base.app.ui.fragment.base.BaseFragment;
import base.app.util.events.FragmentEvent;
import base.app.util.commons.Connection;
import base.app.util.commons.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Optional;
import me.atrox.haikunator.Haikunator;
import me.atrox.haikunator.HaikunatorBuilder;

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
    AVLoadingIndicatorView progressBarVebView;

    @BindView(R.id.sign_up_progress_bar)
    AVLoadingIndicatorView progressBar;
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
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (Model.getInstance().isRealUser() && Utility.isPhone(getActivity())) {
            getActivity().onBackPressed();
            //EventBus.getDefault().composePost(new FragmentEvent(WallFragment.class, true));
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
//        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
//        // Callback registration
//        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                // App code
//                GraphRequest request = GraphRequest.newMeRequest(
//                        loginResult.getAccessToken(),
//                        new GraphRequest.GraphJSONObjectCallback() {
//                            @Override
//                            public void onCompleted(JSONObject object, GraphResponse response) {
//                                // Application code
//                                try {
//                                    email.setText(object.getString("email"));
//                                    firstName.setText(object.getString("first_name"));
//                                    lastName.setText(object.getString("last_name"));
//                                    if (displayName != null) {
//                                        displayName.setText(object.getString("first_name") + " " + object.getString("last_name"));
//                                    }
//                                    LoginManager.getInstance().logOut();
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                Bundle parameters = new Bundle();
//                parameters.putString("fields", "name,email,first_name,last_name");
//                request.setParameters(parameters);
//                request.executeAsync();
//            }
//
//            @Override
//            public void onCancel() { }
//
//            @Override
//            public void onError(FacebookException error) { }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Optional
    @OnLongClick({R.id.or_label,R.id.sign_up_firstname})
    public boolean populateWithDemoData() {
        Haikunator haikunator = new HaikunatorBuilder().setTokenLength(0).setDelimiter(" ").build();
        String name = haikunator.haikunate();
        firstName.setText(WordUtils.capitalize(name.substring(0, name.indexOf(" "))));
        lastName.setText(WordUtils.capitalize(name.substring(name.indexOf(" ") + 1)));
        if (displayName != null) {
            displayName.setText(name);
        }
        if (phone != null) {
            phone.setText(
                    new HaikunatorBuilder()
                            .setTokenLength(10)
                            .setDelimiter("")
                            .setTokenChars("0123456789")
                            .build()
                            .haikunate().replaceAll("[^\\d.]", "")
            );
        }
        email.setText(name.replace(" ", "@") + ".com");
        password.setText("qwerty");
        return true;
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
            getActivity().onBackPressed();
            //EventBus.getDefault().composePost(new FragmentEvent(WallFragment.class));
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
