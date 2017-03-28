package tv.sportssidekick.sportssidekick.fragment.popup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.FriendsAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.friendship.FriendsManager;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;

/**
 * Created by Djordje Krutil on 28.3.2017..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class FollowingFragment extends BaseFragment {

    @BindView(R.id.following_recycler_view)
    RecyclerView followingRecyclerView;

    @BindView(R.id.progress_bar)
    AVLoadingIndicatorView progressBar;

    @BindView(R.id.no_result)
    TextView noResult;

    @BindView(R.id.follow_more)
    ImageView followMore;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_you_following, container, false);
        ButterKnife.bind(this, view);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 5);
        followingRecyclerView.setLayoutManager(layoutManager);

        final FriendsAdapter adapter = new FriendsAdapter(this.getClass());
        adapter.setInitiatorFragment(this.getClass());
        followingRecyclerView.setAdapter(adapter);
        Task<List<UserInfo>> friendsTask = FriendsManager.getInstance().getUserFollowingList(Model.getInstance().getUserInfo().getUserId(), 0);
        friendsTask.addOnCompleteListener(new OnCompleteListener<List<UserInfo>>() {
            @Override
            public void onComplete(@NonNull Task<List<UserInfo>> task) {
                if (task.isSuccessful()) {
                    adapter.getValues().addAll(task.getResult());
                    adapter.notifyDataSetChanged();
                    followingRecyclerView.setVisibility(View.VISIBLE);
                    noResult.setVisibility(View.GONE);
                } else {
                    followingRecyclerView.setVisibility(View.GONE);
                    noResult.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        return view;
    }

    @OnClick(R.id.confirm_button)
    public void confirmOnClick() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.follow_more)
    public void followOnClick() {
        EventBus.getDefault().post(new FragmentEvent(YourFriendsFragment.class));
    }

    @OnClick(R.id.your_followers_button)
    public void followersOnClick() {
        EventBus.getDefault().post(new FragmentEvent(FollowersFragment.class));
    }
}
