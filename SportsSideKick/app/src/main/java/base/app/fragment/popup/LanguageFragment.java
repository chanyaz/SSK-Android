package base.app.fragment.popup;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import base.app.R;
import base.app.fragment.BaseFragment;
import base.app.fragment.FragmentEvent;

/**
 * Created by Filip on 1/20/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class LanguageFragment extends BaseFragment {


    @BindView(R.id.english_background)
    ImageView englishSelectionView;
    @BindView(R.id.chinese_background)
    ImageView chineseSelectionView;
    @BindView(R.id.portuguese_background)
    ImageView portugueseSelectionView;
    String languageToLoad  = "en";

    public LanguageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_language, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.english_icon,R.id.portuguese_icon,R.id.chinese_icon})
    public void languageIconOnClick(View view) {
        englishSelectionView.setVisibility(View.INVISIBLE);
        chineseSelectionView.setVisibility(View.INVISIBLE);
        portugueseSelectionView.setVisibility(View.INVISIBLE);

        switch (view.getId()){
            case R.id.english_icon:
                englishSelectionView.setVisibility(View.VISIBLE);
                languageToLoad  = "en";
                break;
            case R.id.portuguese_icon:
                portugueseSelectionView.setVisibility(View.VISIBLE);
                languageToLoad  = "pt";
                break;
            case R.id.chinese_icon:
                chineseSelectionView.setVisibility(View.VISIBLE);
                languageToLoad  = "zh";
                break;
        }
    }

    @OnClick(R.id.confirm_button)
    public void confirmOnClick() {
        EventBus.getDefault().post(new FragmentEvent(YourProfileFragment.class, true));
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getActivity().recreate();
    }

}

