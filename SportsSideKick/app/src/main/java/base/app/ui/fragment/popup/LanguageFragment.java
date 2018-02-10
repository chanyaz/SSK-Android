package base.app.ui.fragment.popup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pixplicity.easyprefs.library.Prefs;

import org.greenrobot.eventbus.EventBus;

import base.app.R;
import base.app.data.Model;
import base.app.data.user.UserEvent;
import base.app.data.user.UserInfo;
import base.app.ui.activity.MainActivity;
import base.app.ui.activity.MainActivityTablet;
import base.app.ui.adapter.menu.LanguageAdapter;
import base.app.ui.fragment.base.BaseFragment;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.util.commons.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

import static base.app.util.commons.Utility.CHOSEN_LANGUAGE;

/**
 * Created by Filip on 1/20/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class LanguageFragment extends BaseFragment implements LanguageAdapter.LanguageOnClick {

    String selectedLanguage;
    @BindView(R.id.language_recycler_view)
    RecyclerView languageRecyclerView;

    public LanguageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_language, container, false);
        ButterKnife.bind(this, view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),3, LinearLayoutManager.VERTICAL,false);
        languageRecyclerView.setLayoutManager(layoutManager);
        languageRecyclerView.setAdapter(new LanguageAdapter(getActivity(), this));

        selectedLanguage = Prefs.getString(CHOSEN_LANGUAGE, "en");
        return view;
    }

    @Optional
    @OnClick(R.id.confirm_button)
    public void confirmOnClick() {
        EventBus.getDefault().post(new FragmentEvent(ProfileFragment.class, true));
        Model.getInstance().getUserInfo().setLanguage(selectedLanguage);
        Prefs.putString(CHOSEN_LANGUAGE, selectedLanguage);
        Intent intent;
        if (Utility.isTablet(getContext())) {
            intent = new Intent(getActivity(), MainActivityTablet.class);
        } else {
            intent = new Intent(getActivity(), MainActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        if (getActivity() != null) {
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                UserInfo currentUser = Model.getInstance().getUserInfo();
                EventBus.getDefault().post(new UserEvent(UserEvent.Type.onLogin, currentUser));
            }
        }, 300);
    }



    @Optional
    @OnClick(R.id.your_wallet_button)
    public void walletOnClick() {
        EventBus.getDefault().post(new FragmentEvent(WalletFragment.class));
    }

    @Optional
    @OnClick(R.id.your_stash_button)
    public void stashOnClick() {
        EventBus.getDefault().post(new FragmentEvent(StashFragment.class));
    }

    @Override
    public void languageChange(String language,String shortLanguage) {
        selectedLanguage = shortLanguage;
        confirmOnClick();

    }





}

