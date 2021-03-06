package base.app.ui.fragment.popup;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import base.app.R;
import base.app.ui.fragment.user.auth.LoginFragment;
import base.app.ui.fragment.user.auth.LoginApi;
import base.app.util.ui.BaseFragment;
import base.app.util.events.FragmentEvent;
import base.app.util.commons.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Aleksandar Marinkovic on 30/05/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class SignUpLoginFragment extends BaseFragment {

    @Nullable
    @BindView(R.id.textView)
    TextView text;

    public SignUpLoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_login_sing_up, container, false);
        ButterKnife.bind(this, view);
        if (text != null) {
            text.setText(Html.fromHtml(getString(R.string.slogan)));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (LoginApi.getInstance().isLoggedIn() && Utility.isPhone(getActivity())){
            getActivity().onBackPressed();
        }
    }

    @Optional
    @OnClick(R.id.registerButton)
    public void joinOnClick() {
        EventBus.getDefault().post(new FragmentEvent(RegisterFragment.class));
    }

    @Optional
    @OnClick(R.id.loginButton)
    public void loginOnClick() {
        EventBus.getDefault().post(new FragmentEvent(LoginFragment.class));
    }

    @Optional
    @OnClick(R.id.backButton)
    public void closeDialogButton() {
       getActivity().onBackPressed();
    }




}
