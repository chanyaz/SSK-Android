package tv.sportssidekick.sportssidekick.fragment.popup;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.model.AlertDialogContentManager;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.service.GameSparksEvent;

/**
 * Created by Filip on 1/16/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class LoginFragment extends BaseFragment {

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

    @BindView(R.id.container)
    RelativeLayout mainContentContainer;

    @BindView(R.id.bottom_buttons_container_reset)
    RelativeLayout resetButtonContainer;

    @BindView(R.id.bottom_buttons_container_login)
    RelativeLayout loginButtonContainer;

    @BindView(R.id.login_forgot_pass_email)
    EditText emailForgotPassword;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_login, container, false);
        ButterKnife.bind(this, view);

        forgotPasswordBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                slideToRight(loginContainer);
//                slideToRight(resetButtonContainer);
                loginContainer.setVisibility(View.VISIBLE);
                loginButtonContainer.setVisibility(View.VISIBLE);
                resetButtonContainer.setVisibility(View.INVISIBLE);
                forgotPasswordContainer.setVisibility(View.INVISIBLE);
            }
        });
        return view;
    }

    @OnClick(R.id.bottom_buttons_container_login)
    public void loginOnClick() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Please enter valid password and displayName!", Toast.LENGTH_SHORT).show();
            return;
        }
        Model.getInstance().login(email, password);
        loginText.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.reset_text)
    public void forgotPasswordOnClick() {
        String email = emailForgotPassword.getText().toString();
        //TODO FORGOT PASSWROD ON CLICK
        getActivity().onBackPressed();
    }

    @OnClick(R.id.sign_up_button)
    public void signUpOnClick() {
        EventBus.getDefault().post(new FragmentEvent(tv.sportssidekick.sportssidekick.fragment.popup.SignUpFragment.class));

    }


    @OnClick(R.id.forgot_button)
    public void forgotOnClick() {
        // EventBus.getDefault().post(new FragmentEvent(ForgotPasswordFramegnt.class));
//        slideToLeft(loginContainer);
//        slideToLeft(resetButtonContainer);

        loginContainer.setVisibility(View.INVISIBLE);
        loginButtonContainer.setVisibility(View.INVISIBLE);
        resetButtonContainer.setVisibility(View.VISIBLE);
        forgotPasswordContainer.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onEvent(GameSparksEvent event) {
        switch (event.getEventType()) {
            case SIGNED_OUT:
//                progressBar.setVisibility(View.GONE);
//                showLoginForms();
                break;
            case LOGIN_SUCCESSFUL:
                progressBar.setVisibility(View.GONE);
                loginText.setVisibility(View.VISIBLE);
                EventBus.getDefault().post(Model.getInstance().getUserInfo()); //catch in Lounge Activity
                getActivity().onBackPressed();
                break;
            case LOGIN_FAILED:
                progressBar.setVisibility(View.GONE);
                loginText.setVisibility(View.VISIBLE);
                AlertDialogContentManager.getInstance().showDialog("Login Failed", "Please re-enter your password and try again", null, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getActivity().onBackPressed();
                                EventBus.getDefault().post(new FragmentEvent(LoginFragment.class));
                            }
                        }
                );
                break;
        }
    }

    // To animate view slide out from left to right
    public void slideToRight(View view) {
        TranslateAnimation animate = new TranslateAnimation(0,view.getWidth(), 0, 0);
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
}
