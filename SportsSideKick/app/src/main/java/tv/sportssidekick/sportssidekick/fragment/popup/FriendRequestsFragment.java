package tv.sportssidekick.sportssidekick.fragment.popup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.FriendRequestsAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.model.friendship.FriendRequest;
import tv.sportssidekick.sportssidekick.model.friendship.FriendsManager;

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
                if(task.isSuccessful()){
                    adapter.getValues().addAll(task.getResult());
                    adapter.notifyDataSetChanged();
                } else {
                    // TODO @Filip - No friend request to display!
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        return view;
    }

    @OnClick(R.id.confirm_button)
    public void confirmOnClick(){
        getActivity().onBackPressed();
        EventBus.getDefault().post(new FragmentEvent(FriendRequestsFragment.class, true));
    }

}
