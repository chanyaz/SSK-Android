package base.app.fragment.popup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.OnClick;
import base.app.R;
import base.app.fragment.BaseFragment;
import base.app.fragment.FragmentEvent;
import base.app.model.purchases.PurchaseModel;
import butterknife.Optional;

/**
 * Created by Filip on 1/16/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class WalletFragment extends BaseFragment {

    public WalletFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_your_wallet, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.bundle_1)
    public void onBundleOneClick() {
        PurchaseModel.getInstance().purchase(PurchaseModel.ProductShortCode.CurrencyBundle10);
    }

    @OnClick(R.id.bundle_2)
    public void onBundleTwoClick() {
        PurchaseModel.getInstance().purchase(PurchaseModel.ProductShortCode.CurrencyBundle100);
    }

    @OnClick(R.id.bundle_3)
    public void onBundleThreeClick() {
        PurchaseModel.getInstance().purchase(PurchaseModel.ProductShortCode.CurrencyBundle500);
    }

    @OnClick(R.id.bundle_4)
    public void onBundleFourClick() {
        PurchaseModel.getInstance().purchase(PurchaseModel.ProductShortCode.CurrencyBundle1000);
    }

    @OnClick(R.id.bundle_5)
    public void onBundleFiveClick() {
        PurchaseModel.getInstance().purchase(PurchaseModel.ProductShortCode.CurrencyBundle1500);
    }

    @OnClick(R.id.monthly_bag_image)
    public void onMonthlyBagClick() {
        PurchaseModel.getInstance().purchase(PurchaseModel.ProductShortCode.Bag_OfTheMonth);
    }

    @OnClick(R.id.subscribe_image)
    public void onSubscribeClick() {
        PurchaseModel.getInstance().purchase(PurchaseModel.ProductShortCode.Subscription_Monthly);
    }

    @Optional
    @OnClick(R.id.confirm_button)
    public void confirmOnClick() {
        getActivity().onBackPressed();
    }

    @Optional
    @OnClick(R.id.edit_button)
    public void editOnClick() {
        EventBus.getDefault().post(new FragmentEvent(YourStatementFragment.class));
    }

    @Optional
    @OnClick(R.id.your_stash_button)
    public void stashOnClick() {
        EventBus.getDefault().post(new FragmentEvent(StashFragment.class));
    }

    @Optional
    @OnClick(R.id.your_profile_button)
    public void profileOnClick() {
        EventBus.getDefault().post(new FragmentEvent(YourProfileFragment.class));
    }

}
