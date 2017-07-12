package base.app.fragment.popup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import base.app.R;
import base.app.adapter.FriendRequestsAdapter;
import base.app.fragment.BaseFragment;
import base.app.fragment.FragmentEvent;
import base.app.fragment.IgnoreBackHandling;
import base.app.fragment.instance.WallFragment;
import base.app.model.Model;
import base.app.model.friendship.FriendRequest;
import base.app.model.friendship.FriendsManager;
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

@IgnoreBackHandling
public class SignUpLoginFragment extends BaseFragment {

    @Nullable
    @BindView(R.id.textView)
    TextView text;

    public SignUpLoginFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (Model.getInstance().getLoggedInUserType() == Model.LoggedInUserType.REAL && !Utility.isTablet(getActivity())){
            EventBus.getDefault().post(new FragmentEvent(WallFragment.class,true));
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_login_sing_up, container, false);
        ButterKnife.bind(this, view);
        if (text != null) {
            text.setText(Utility.fromHtml(getString(R.string.login_slider_text_1_phone)));
        }
        return view;

    }

    @Optional
    @OnClick(R.id.join_now_button)
    public void joinOnClick() {
        EventBus.getDefault().post(new FragmentEvent(SignUpFragment.class));
    }

    @Optional
    @OnClick(R.id.login_button)
    public void loginOnClick() {
        EventBus.getDefault().post(new FragmentEvent(LoginFragment.class));
    }

    @Optional
    @OnClick(R.id.close_dialog_button)
    public void closeDialogButton() {
       getActivity().onBackPressed();
    }




}
