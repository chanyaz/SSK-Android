package base.app.fragment.popup;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import base.app.R;
import base.app.adapter.UserStatsAdapter;
import base.app.fragment.BaseFragment;

/**
 * Created by Filip on 12/20/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class FriendProfileFragment extends BaseFragment {

    @BindView(R.id.profile_stats_recycler_view)
    RecyclerView statsRecyclerView;

    //TODO @Filip  This fragment is not used
    public FriendProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_friend_profile_fragment, container, false);
        ButterKnife.bind(this, view);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        statsRecyclerView.setLayoutManager(layoutManager);

        ArrayList<Pair<String, String>> values = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            values.add(new Pair<>("Caption " + i, "Value " + i));
        }

        UserStatsAdapter adapter = new UserStatsAdapter();
        adapter.getValues().addAll(values);
        statsRecyclerView.setAdapter(adapter);
        return view;
    }

    @OnClick(R.id.confirm_button)
    public void confirmOnClick() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.block_button)
    public void blockOnClick() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.unfriend_button)
    public void unfriendOnClick() {
        getActivity().onBackPressed();
    }
}
