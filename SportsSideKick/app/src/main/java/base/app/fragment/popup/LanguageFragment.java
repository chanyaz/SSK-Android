package base.app.fragment.popup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pixplicity.easyprefs.library.Prefs;

import org.greenrobot.eventbus.EventBus;

import base.app.R;
import base.app.activity.LoungeActivity;
import base.app.activity.PhoneLoungeActivity;
import base.app.adapter.LanguageAdapter;
import base.app.fragment.BaseFragment;
import base.app.fragment.FragmentEvent;
import base.app.model.Model;
import base.app.model.user.UserEvent;
import base.app.model.user.UserInfo;
import base.app.util.Utility;
import base.app.util.ui.AutofitDecoration;
import base.app.util.ui.AutofitRecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

import static base.app.util.Utility.CHOSEN_LANGUAGE;

/**
 * Created by Filip on 1/20/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class LanguageFragment extends BaseFragment implements LanguageAdapter.LanguageOnClick {

    @Nullable
    @BindView(R.id.english_background)
    ImageView englishSelectionView;
    @Nullable
    @BindView(R.id.chinese_background)
    ImageView chineseSelectionView;
    @Nullable
    @BindView(R.id.portuguese_background)
    ImageView portugueseSelectionView;
    String languageToLoad = "en";
    boolean isTablet = true;
    @Nullable
    @BindView(R.id.auto_language_recycler_view)
    AutofitRecyclerView languageRecyclerView;
    int screenWidth;

    public LanguageFragment() {
        // Required empty public constructor
    }

    public static final double GRID_PERCENT_CELL_WIDTH_PHONE = 0.3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_language, container, false);
        ButterKnife.bind(this, view);
        isTablet = Utility.isTablet(getActivity());
        screenWidth = Utility.getDisplayWidth(getActivity());
        if (!isTablet) {
            assert languageRecyclerView != null;
            languageRecyclerView.setCellWidth((int) (screenWidth * GRID_PERCENT_CELL_WIDTH_PHONE));
            languageRecyclerView.addItemDecoration(new AutofitDecoration(getActivity()));
            languageRecyclerView.setHasFixedSize(true);
            languageRecyclerView.setAdapter(new LanguageAdapter(getActivity(), this));
        }

        String selectedLanguage = Prefs.getString(CHOSEN_LANGUAGE,"en");
        showSelectedLanguage(selectedLanguage);
        return view;
    }

    @Optional
    @OnClick({R.id.english_icon, R.id.portuguese_icon, R.id.chinese_icon})
    public void languageIconOnClick(View view) {
        switch (view.getId()) {
            case R.id.english_icon:
                languageToLoad = "en";
                break;
            case R.id.portuguese_icon:
                languageToLoad = "pt";
                break;
            case R.id.chinese_icon:
                languageToLoad = "zh";
                break;
        }
        showSelectedLanguage(languageToLoad);
    }

    private void showSelectedLanguage(String language){
        if (englishSelectionView != null) {
            englishSelectionView.setVisibility(View.INVISIBLE);
        }
        if (chineseSelectionView != null) {
            chineseSelectionView.setVisibility(View.INVISIBLE);
        }
        if (portugueseSelectionView != null) {
            portugueseSelectionView.setVisibility(View.INVISIBLE);
        }
        switch (language) {
            case "en":
                if (englishSelectionView != null) {
                    englishSelectionView.setVisibility(View.VISIBLE);
                }
                break;
            case "pt":
                if (portugueseSelectionView != null) {
                    portugueseSelectionView.setVisibility(View.VISIBLE);
                }
                break;
            case "zh":
                if (chineseSelectionView != null) {
                    chineseSelectionView.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Optional
    @OnClick(R.id.confirm_button)
    public void confirmOnClick() {
        EventBus.getDefault().post(new FragmentEvent(YourProfileFragment.class, true));
        Model.getInstance().getUserInfo().setLanguage(languageToLoad);
        Prefs.putString(CHOSEN_LANGUAGE,languageToLoad);
        Intent intent;
        if (Utility.isTablet(getContext())) {
            intent = new Intent(getActivity(), LoungeActivity.class);
        } else {
            intent = new Intent(getActivity(), PhoneLoungeActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

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
    public void languageChange(String language,String  shortLanguage) {
        languageToLoad = shortLanguage;
        confirmOnClick();

    }





}

