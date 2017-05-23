package base.app.fragment.popup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import base.app.R;
import base.app.fragment.BaseFragment;
import base.app.fragment.FragmentEvent;
import base.app.model.friendship.FriendsManager;

/**
 * Created by Djordje Krutil on 29.3.2017..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class InviteFriendFragment extends BaseFragment {

    @BindView(R.id.invvite_progress_bar)
    AVLoadingIndicatorView progressBar;

    @BindView(R.id.invite_text)
    TextView inviteText;

    @BindView(R.id.bottom_buttons_container_invite)
    RelativeLayout inviteButton;

    @BindView(R.id.invite_friend_name)
    EditText inviteFriendName;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_invite_friend, container, false);
        ButterKnife.bind(this, view);

        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(inviteFriendName.getText())) {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.enter_friend_name_invite), Toast.LENGTH_SHORT).show();
                } else {
                    FriendsManager.getInstance().inviteFriend(inviteFriendName.getText().toString());
                    //TODO @Filip on successful remove pop up and show toast or inform user to rewrite email address and try again - Missing API
                }
            }
        });

        return view;
    }

    @OnClick(R.id.add_friend_button)
    public void onClickAddFriend() {
        EventBus.getDefault().post(new FragmentEvent(AddFriendFragment.class));
    }
}
