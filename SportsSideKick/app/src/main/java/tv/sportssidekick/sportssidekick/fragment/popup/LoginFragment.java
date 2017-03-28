package tv.sportssidekick.sportssidekick.fragment.popup;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import tv.sportssidekick.sportssidekick.fragment.instance.ForgotPasswordFramegnt;
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

    public LoginFragment() {

        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_login, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.bottom_buttons_container_login)
    public void loginOnClick(){
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(getContext(),"Please enter valid password and displayName!", Toast.LENGTH_SHORT).show();
            return;
        }
        Model.getInstance().login(email,password);
        loginText.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.sign_up_button)
    public void signUpOnClick(){
        EventBus.getDefault().post(new FragmentEvent(tv.sportssidekick.sportssidekick.fragment.popup.SignUpFragment.class));
    }

    @OnClick(R.id.forgot_button)
    public void forgotOnClick(){
        EventBus.getDefault().post(new FragmentEvent(ForgotPasswordFramegnt.class));
    }

    @Subscribe
    public void onEvent(GameSparksEvent event){
        switch (event.getEventType()){
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
                Toast.makeText(getContext(), "Login error", Toast.LENGTH_LONG).show(); // TODO inform user about login failed
        }
    }

}
