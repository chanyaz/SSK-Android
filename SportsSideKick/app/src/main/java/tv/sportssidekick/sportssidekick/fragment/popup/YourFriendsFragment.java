package tv.sportssidekick.sportssidekick.fragment.popup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.FriendsAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.model.friendship.FriendRequest;
import tv.sportssidekick.sportssidekick.model.friendship.FriendsManager;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;

/**
 * Created by Djordje on 1/21/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class YourFriendsFragment extends BaseFragment {

    @BindView(R.id.friends_recycler_view)
    RecyclerView friendsRecyclerView;

    @BindView(R.id.progress_bar)
    AVLoadingIndicatorView progressBar;

    @BindView(R.id. friend_requests_count)
    TextView friendRequestCount;

    public YourFriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_your_friends, container, false);
        ButterKnife.bind(this, view);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 11);
        friendsRecyclerView.setLayoutManager(layoutManager);

        final FriendsAdapter adapter = new FriendsAdapter(this.getClass());
        friendsRecyclerView.setAdapter(adapter);
        Task<List<UserInfo>> friendsTask =  FriendsManager.getInstance().getFriends(0);
        friendsTask.addOnCompleteListener(new OnCompleteListener<List<UserInfo>>() {
            @Override
            public void onComplete(@NonNull Task<List<UserInfo>> task) {
                if(task.isSuccessful()){
                    adapter.getValues().addAll(task.getResult());
                    adapter.notifyDataSetChanged();
                } else {
                    // TODO - No friends to display!
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        Task<List<FriendRequest>> task = FriendsManager.getInstance().getOpenFriendRequests(0);
        task.addOnCompleteListener(new OnCompleteListener<List<FriendRequest>>() {
            @Override
            public void onComplete(@NonNull Task<List<FriendRequest>> task) {
                if(task.isSuccessful()){
                    if(task.getResult()!=null){
                        friendRequestCount.setText(String.valueOf(task.getResult().size()));
                        return;
                    }
                }
                friendRequestCount.setText("0");
            }
        });

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
