package base.app.fragment.popup;

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
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import base.app.R;
import base.app.adapter.FriendRequestsAdapter;
import base.app.fragment.BaseFragment;
import base.app.fragment.FragmentEvent;
import base.app.model.friendship.FriendRequest;
import base.app.model.friendship.FriendsManager;
import butterknife.Optional;

/**
 * Created by Djordje on 21/01/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */


public class FriendRequestsFragment extends BaseFragment {

    @BindView(R.id.requests_recycler_view)
    RecyclerView requestsRecyclerView;

    @BindView(R.id.progress_bar)
    AVLoadingIndicatorView progressBar;

    @BindView(R.id.no_result)
    TextView noResult;



    public FriendRequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_friend_requests, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        requestsRecyclerView.setLayoutManager(layoutManager);


        final FriendRequestsAdapter adapter = new FriendRequestsAdapter();
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
