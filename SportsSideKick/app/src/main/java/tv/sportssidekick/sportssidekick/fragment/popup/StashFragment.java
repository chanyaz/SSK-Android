package tv.sportssidekick.sportssidekick.fragment.popup;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.StashAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.model.achievements.Achievement;
import tv.sportssidekick.sportssidekick.model.achievements.AchievementManager;
import tv.sportssidekick.sportssidekick.util.AutofitDecoration;
import tv.sportssidekick.sportssidekick.util.AutofitRecyclerView;
import tv.sportssidekick.sportssidekick.util.Utility;

import static tv.sportssidekick.sportssidekick.fragment.popup.FriendsFragment.GRID_PERCENT_CELL_WIDTH;

/**
 * Created by Filip on 1/16/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class StashFragment extends BaseFragment {

    @BindView(R.id.stash_recycler_view)
    AutofitRecyclerView stashRecyclerView;

    @BindView(R.id.progress_bar)
    AVLoadingIndicatorView progressBar;

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

        stashRecyclerView.setCellWidth((int) (screenWidth * GRID_PERCENT_CELL_WIDTH));
        stashRecyclerView.addItemDecoration(new AutofitDecoration(getActivity()));
        stashRecyclerView.setHasFixedSize(true);

        final StashAdapter adapter = new StashAdapter();
        stashRecyclerView.setAdapter(adapter);
        Task<List<Achievement>> getAchievementsTask = AchievementManager.getInstance().getUserAchievements();
        getAchievementsTask.addOnCompleteListener(new OnCompleteListener<List<Achievement>>() {
            @Override
            public void onComplete(@NonNull Task<List<Achievement>> task) {
                if(task.isSuccessful()){
                    adapter.getValues().addAll(task.getResult());
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

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
