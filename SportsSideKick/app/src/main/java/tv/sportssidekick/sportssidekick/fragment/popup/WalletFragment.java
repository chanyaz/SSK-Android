package tv.sportssidekick.sportssidekick.fragment.popup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;

/**
 * Created by Filip on 1/16/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class WalletFragment extends BaseFragment{

    public WalletFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_your_wallet, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.confirm_button)
    public void confirmOnClick(){
        getActivity().onBackPressed();
    }

    @OnClick(R.id.edit_button)
    public void editOnClick(){
        EventBus.getDefault().post(new FragmentEvent(YourStatementFragment.class));
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
