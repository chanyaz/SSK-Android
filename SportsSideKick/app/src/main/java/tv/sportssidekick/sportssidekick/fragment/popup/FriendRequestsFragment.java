package tv.sportssidekick.sportssidekick.fragment.popup;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.FriendRequestsAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.model.friendship.FriendRequest;

/**
 * Created by Djordje on 21/01/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */


public class FriendRequestsFragment extends BaseFragment {

    @BindView(R.id.requests_recycler_view)
    RecyclerView requestsRecyclerView;

    public FriendRequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_friend_requests, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        requestsRecyclerView.setLayoutManager(layoutManager);

        ArrayList<FriendRequest> values = new ArrayList<>();
        for(int i = 0; i< 100; i++){
            values.add(new FriendRequest("","Cheistofer Ogden","",new Date()));
        }

        FriendRequestsAdapter adapter = new FriendRequestsAdapter();
        adapter.getValues().addAll(values);
        requestsRecyclerView.setAdapter(adapter);
        return view;
    }

    @OnClick(R.id.confirm_button)
    public void confirmOnClick(){
        getActivity().onBackPressed();
        EventBus.getDefault().post(new FragmentEvent(FriendRequestsFragment.class, true));
    }

}
