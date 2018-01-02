package base.app.ui.fragment.popup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import base.app.R;
import base.app.ui.adapter.profile.StashAdapter;
import base.app.ui.fragment.base.BaseFragment;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.data.achievements.Achievement;
import base.app.data.achievements.AchievementManager;
import base.app.util.commons.Utility;
import base.app.util.ui.AutofitDecoration;
import base.app.util.ui.AutofitRecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

import static base.app.ui.fragment.popup.FriendsFragment.GRID_PERCENT_CELL_WIDTH;

/**
 * Created by Filip on 1/16/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class StashFragment extends BaseFragment {

    @BindView(R.id.stash_recycler_view)
    AutofitRecyclerView stashRecyclerView;

    @BindView(R.id.progressBar)
    AVLoadingIndicatorView progressBar;
    public static final double GRID_PERCENT_CELL_WIDTH_PHONE = 0.26;

    public StashFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_your_stash, container, false);
        ButterKnife.bind(this, view);

        /*GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        stashRecyclerView.setLayoutManager(layoutManager);*/

        int screenWidth = Utility.getDisplayWidth(getActivity());
        if (Utility.isTablet(getActivity())) {
            stashRecyclerView.setCellWidth((int) (screenWidth * GRID_PERCENT_CELL_WIDTH));
        }
        else {
            stashRecyclerView.setCellWidth((int) (screenWidth * GRID_PERCENT_CELL_WIDTH_PHONE));
        }
        stashRecyclerView.addItemDecoration(new AutofitDecoration(getActivity()));
        stashRecyclerView.setHasFixedSize(true);

        final StashAdapter adapter = new StashAdapter();
        stashRecyclerView.setAdapter(adapter);
        Task<List<Achievement>> getAchievementsTask = AchievementManager.getInstance().getUserAchievements();
        getAchievementsTask.addOnCompleteListener(new OnCompleteListener<List<Achievement>>() {
            @Override
            public void onComplete(@NonNull Task<List<Achievement>> task) {
                if (task.isSuccessful()) {
                    adapter.getValues().addAll(task.getResult());
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    @Optional
    @OnClick(R.id.confirm_button)
    public void confirmOnClick() {
        getActivity().onBackPressed();
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

    @Optional
    @OnClick(R.id.your_profile_button)
    public void profileOnClick() {
        EventBus.getDefault().post(new FragmentEvent(YourProfileFragment.class));
    }
}
