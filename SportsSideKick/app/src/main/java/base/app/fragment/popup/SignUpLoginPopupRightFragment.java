package base.app.fragment.popup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import base.app.R;
import base.app.fragment.BaseFragment;
import base.app.fragment.FragmentEvent;
import base.app.fragment.IgnoreBackHandling;
import base.app.fragment.instance.ChatFragment;
import base.app.fragment.instance.WallFragment;
import base.app.model.Model;
import base.app.model.user.LoginStateReceiver;
import base.app.model.user.UserInfo;
import base.app.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Aleksandar Marinkovic on 30/05/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */


public class SignUpLoginPopupRightFragment extends SignUpLoginFragment implements LoginStateReceiver.LoginStateListener {


    public SignUpLoginPopupRightFragment() {
        // Required empty public constructor
    }

    @Nullable
    @BindView(R.id.description)
    TextView description;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_right_login_sing_up, container, false);
        ButterKnife.bind(this, view);
        if (text != null) {
            text.setText(Utility.fromHtml(getString(R.string.chat_with_friends)));
        }
        if (description != null) {
            description.setText(Utility.fromHtml(getString(R.string.login_slider_chat)));
        }

        return view;

    }

    @Optional
    @OnClick(R.id.close_Button)
    public void closeDialog() {
        EventBus.getDefault().post(new FragmentEvent(ChatFragment.class));
    }


    @Override
    public void onLogout() {

    }

    @Override
    public void onLoginAnonymously() {

    }

    @Override
    public void onLogin(UserInfo user) {
        if (Model.getInstance().isRealUser()) {
            EventBus.getDefault().post(new FragmentEvent(ChatFragment.class));
        }
    }

    @Override
    public void onLoginError(Error error) {

    }
}
