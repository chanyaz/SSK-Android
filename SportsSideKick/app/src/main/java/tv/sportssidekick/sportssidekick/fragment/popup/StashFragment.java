package tv.sportssidekick.sportssidekick.fragment.popup;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.StashAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;

/**
 * Created by Filip on 1/16/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class StashFragment extends BaseFragment {

    @BindView(R.id.stash_recycler_view)
    RecyclerView stashRecyclerView;

    public StashFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_your_stash, container, false);
        ButterKnife.bind(this, view);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        stashRecyclerView.setLayoutManager(layoutManager);

        ArrayList<Pair<String,String>> values = new ArrayList<>();
        for(int i = 0; i< 100; i++){
            values.add(new Pair<>("SOCCER BOOT " + i,"Profile frame " + i));
        }

        StashAdapter adapter = new StashAdapter();
        adapter.getValues().addAll(values);
        stashRecyclerView.setAdapter(adapter);
        return view;
    }

    @OnClick(R.id.confirm_button)
    public void confirmOnClick(){
        getActivity().onBackPressed();
    }

    @OnClick(R.id.your_wallet_button)
    public void walletOnClick(){
        EventBus.getDefault().post(new FragmentEvent(WalletFragment.class));
    }

    @OnClick(R.id.your_stash_button)
    public void stashOnClick(){
        EventBus.getDefault().post(new FragmentEvent(StashFragment.class));
    }

    @OnClick(R.id.your_profile_button)
    public void profileOnClick(){
        EventBus.getDefault().post(new FragmentEvent(YourProfileFragment.class));
    }
}
