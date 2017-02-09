package tv.sportssidekick.sportssidekick.fragment.popup;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.FriendsAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.model.UserInfo;

/**
 * Created by Djordje on 1/21/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class YourFriendsFragment extends BaseFragment {

    @BindView(R.id.friends_recycler_view)
    RecyclerView friendsRecyclerView;

    public YourFriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_your_friends, container, false);
        ButterKnife.bind(this, view);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 11);
        friendsRecyclerView.setLayoutManager(layoutManager);

        List<UserInfo> friends = new ArrayList<>();
        for (int i = 0; i <100; i++)
        {
            UserInfo user = new UserInfo();
            user.setFirstName("Anna");
            user.setLastName("Blum");
            user.setOnline(false);
            user.setCircularAvatarUrl("https://firebasestorage.googleapis.com/v0/b/sportssidekickdev.appspot.com/o/images%2Fuser_photo_rounded_sLqHBMbL3BQNgddTK0a4wmPfuA531480068265.14973.png?alt=media&token=19ed9230-aad6-4278-ac77-67bd589adc7f");

            UserInfo user2 = new UserInfo();
            user2.setFirstName("KayleeMorrison");
            user2.setLastName("Morrison");
            user.setOnline(false);
            user2.setCircularAvatarUrl("https://firebasestorage.googleapis.com/v0/b/sportssidekickdev.appspot.com/o/static%2Fimages%2FblankProfile_Rounded.png?alt=media&token=cdb527a2-718c-4ebc-8f5f-dbedbde827d3");

            UserInfo user3 = new UserInfo();
            user3.setFirstName("David");
            user3.setLastName("Williams");
            user.setOnline(true);
            user3.setCircularAvatarUrl("https://firebasestorage.googleapis.com/v0/b/sportssidekickdev.appspot.com/o/images%2Fuser_photo_rounded_7oY7ljIUvEVgxGT35RfpS1RSYfJ21481044451.47358.png?alt=media&token=0f2e4cec-8e43-4e92-b255-961326087459");

            friends.add(user);
            friends.add(user2);
            friends.add(user3);
        }

        FriendsAdapter adapter = new FriendsAdapter();
        adapter.getValues().addAll(friends);
        friendsRecyclerView.setAdapter(adapter);
        return view;
    }

    @OnClick(R.id.confirm_button)
    public void confirmOnClick(){
        getActivity().onBackPressed();
    }

    @OnClick(R.id.close_dialog_button)
    public void closeDialog(){
        getActivity().onBackPressed();
    }

    @OnClick(R.id.friend_requests)
    public void friendRequestsDialog(){
        EventBus.getDefault().post(new FragmentEvent(FriendRequestsFragment.class));
    }

}
