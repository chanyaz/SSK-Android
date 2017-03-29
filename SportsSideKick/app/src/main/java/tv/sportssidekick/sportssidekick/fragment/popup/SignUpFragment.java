package tv.sportssidekick.sportssidekick.fragment.popup;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.model.GSConstants;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.service.GameSparksEvent;

/**
 * Created by Filip on 1/16/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class SignUpFragment extends BaseFragment {


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


    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_signup, container, false);
        ButterKnife.bind(this, view);

        //TODO DEMO
        firstName.setText("Jon");
        lastName.setText("Snow");
        displayName.setText("jonsnow1");
        phone.setText("1298515735");
        email.setText("jon@snow.com");
        password.setText("qwerty");
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
            Toast.makeText(getContext(), "You have to fill all data", Toast.LENGTH_LONG).show();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GameSparksEvent event){
        switch (event.getEventType()){
            case REGISTRATION_ERROR:
                progressBar.setVisibility(View.GONE);
                signUpText.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Registration error. Try again later.", Toast.LENGTH_LONG).show(); // TODO inform user about login failed
                break;
            case REGISTRATION_SUCCESSFUL:
                progressBar.setVisibility(View.GONE);
                signUpText.setVisibility(View.VISIBLE);
                getActivity().onBackPressed();
        }
    }
}
