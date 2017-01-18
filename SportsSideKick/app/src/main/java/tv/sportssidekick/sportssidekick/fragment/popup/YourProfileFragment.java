package tv.sportssidekick.sportssidekick.fragment.popup;

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
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.UserStatsAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;

/**
 * Created by Filip on 1/16/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class YourProfileFragment  extends BaseFragment {

    @BindView(R.id.profile_stats_recycler_view)
    RecyclerView statsRecyclerView;

    public YourProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_your_profile, container, false);
        ButterKnife.bind(this, view);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        statsRecyclerView.setLayoutManager(layoutManager);

        ArrayList<Pair<String,String>> values = new ArrayList<>();
        for(int i = 0; i< 100; i++){
            values.add(new Pair<String, String>("Caption " + i,"Value " + i));
        }

        UserStatsAdapter adapter = new UserStatsAdapter();
        adapter.getValues().addAll(values);
        statsRecyclerView.setAdapter(adapter);
        return view;
    }

    public void confirmOnClick(View view){
        getActivity().onBackPressed();

    }
}
