package tv.sportssidekick.sportssidekick.fragment.popup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.fragment.instance.ForgotPasswordFramegnt;

/**
 * Created by Filip on 1/16/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class SignUpFragment extends BaseFragment {


    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_signup, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.bottom_buttons_container_sign_up)
    public void signUpOnClick(){
        //TODO Sign up to GS
        getActivity().onBackPressed();
    }

    @OnClick(R.id.sign_up_login_button)
    public void loginOnClick(){
        EventBus.getDefault().post(new FragmentEvent(tv.sportssidekick.sportssidekick.fragment.popup.LoginFragment.class));
    }

    @OnClick(R.id.sign_up_facebook)
    public void facebookOnClick(){
        //TODO facebook sign up
    }

}
