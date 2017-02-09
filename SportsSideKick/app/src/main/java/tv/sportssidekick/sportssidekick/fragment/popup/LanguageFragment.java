package tv.sportssidekick.sportssidekick.fragment.popup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;

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
                break;
            case R.id.portuguese_icon:
                portugueseSelectionView.setVisibility(View.VISIBLE);
                break;
            case R.id.chinese_icon:
                chineseSelectionView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @OnClick(R.id.confirm_button)
    public void confirmOnClick() {
        EventBus.getDefault().post(new FragmentEvent(YourProfileFragment.class, true));
    }

}

