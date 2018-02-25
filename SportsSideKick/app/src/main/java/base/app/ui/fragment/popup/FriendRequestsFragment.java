package base.app.ui.fragment.popup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import org.greenrobot.eventbus.EventBus;

import java.util.List;

import base.app.R;
import base.app.ui.adapter.friends.FriendRequestsAdapter;
import base.app.ui.fragment.base.BaseFragment;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.data.user.friends.FriendRequest;
import base.app.data.user.friends.FriendsManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Djordje on 21/01/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */


public class FriendRequestsFragment extends BaseFragment {

    @BindView(R.id.requests_recycler_view)
    RecyclerView requestsRecyclerView;

    @BindView(R.id.progressBar)
    View progressBar;

    @BindView(R.id.no_result)
    TextView noResult;



    public FriendRequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_friend_requests, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        requestsRecyclerView.setLayoutManager(layoutManager);


        final FriendRequestsAdapter adapter = new FriendRequestsAdapter(this.getClass());
        requestsRecyclerView.setAdapter(adapter);

        Task<List<FriendRequest>> task = FriendsManager.getInstance().getOpenFriendRequests(0);
        task.addOnCompleteListener(new OnCompleteListener<List<FriendRequest>>() {
            @Override
            public void onComplete(@NonNull Task<List<FriendRequest>> task) {
                if (task.isSuccessful()) {
                    adapter.getValues().addAll(task.getResult());
                    adapter.notifyDataSetChanged();
                    noResult.setVisibility(View.GONE);
                } else {
                    noResult.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        return view;
    }

    @Optional
    @OnClick(R.id.confirm_button)
    public void confirmOnClick() {
        getActivity().onBackPressed();
        EventBus.getDefault().post(new FragmentEvent(FriendRequestsFragment.class, true));
    }


    @Optional
    @OnClick(R.id.close)
    public void closeOnClick() {
        getActivity().onBackPressed();
    }

    @Optional
    @OnClick(R.id.your_friends_open_button)
    public void friendsOnClick() {
        EventBus.getDefault().post(new FragmentEvent(FriendsFragment.class));
    }


    @Optional
    @OnClick(R.id.add_friend)
    public void profileOnClick() {
        EventBus.getDefault().post(new FragmentEvent(AddFriendFragment.class));
    }

}
