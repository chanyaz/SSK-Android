package base.app.fragment.popup;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import base.app.adapter.LanguageAdapter;
import base.app.fragment.instance.ChatFragment;
import base.app.fragment.instance.ClubRadioFragment;
import base.app.fragment.instance.ClubTVFragment;
import base.app.fragment.instance.NewsFragment;
import base.app.fragment.instance.RumoursFragment;
import base.app.fragment.instance.StatisticsFragment;
import base.app.fragment.instance.StoreFragment;
import base.app.fragment.instance.VideoChatFragment;
import base.app.fragment.instance.WallFragment;
import base.app.util.Utility;
import base.app.util.ui.AutofitDecoration;
import base.app.util.ui.AutofitRecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import base.app.R;
import base.app.fragment.BaseFragment;
import base.app.fragment.FragmentEvent;
import butterknife.Optional;

import static base.app.fragment.popup.FriendsFragment.GRID_PERCENT_CELL_WIDTH;

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
            languageRecyclerView.setCellWidth((int) (screenWidth * GRID_PERCENT_CELL_WIDTH_PHONE));
            languageRecyclerView.addItemDecoration(new AutofitDecoration(getActivity()));
            languageRecyclerView.setHasFixedSize(true);
            languageRecyclerView.setAdapter(new LanguageAdapter(getActivity(), this));
        }


        return view;
    }

    @Optional
    @OnClick({R.id.english_icon, R.id.portuguese_icon, R.id.chinese_icon})
    public void languageIconOnClick(View view) {
        englishSelectionView.setVisibility(View.INVISIBLE);
        chineseSelectionView.setVisibility(View.INVISIBLE);
        portugueseSelectionView.setVisibility(View.INVISIBLE);

        switch (view.getId()) {
            case R.id.english_icon:
                englishSelectionView.setVisibility(View.VISIBLE);
                languageToLoad = "en";
                break;
            case R.id.portuguese_icon:
                portugueseSelectionView.setVisibility(View.VISIBLE);
                languageToLoad = "pt";
                break;
            case R.id.chinese_icon:
                chineseSelectionView.setVisibility(View.VISIBLE);
                languageToLoad = "zh";
                break;
        }
    }

    //TODO @Filip dont work
    @Optional
    @OnClick(R.id.confirm_button)
    public void confirmOnClick() {
        EventBus.getDefault().post(new FragmentEvent(YourProfileFragment.class, true));
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        //TODO @Filip deprecation
        config.locale = locale;
        getActivity().recreate();
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
    public void languageChange(String string, int position) {
        //TODO remove this code
        if (short_language.get(position).equals("Error")) {
            languageToLoad = "en";
        } else {
            languageToLoad = short_language.get(position);
        }
        confirmOnClick();

    }

    //TODO Change, error with short language name
    public static final List<String> short_language = Collections.unmodifiableList(
            new ArrayList<String>() {{
                add("Error");
                add("zh");
                add("en");
                add("fr");
                add("de");
                add("Error");
                add("Error");
                add("Error");
                add("pt");
                add("Error");
                add("Error");
                // etc
            }});


}

