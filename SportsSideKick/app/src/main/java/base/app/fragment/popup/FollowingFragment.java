package base.app.fragment.popup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import base.app.R;
import base.app.adapter.FriendsAdapter;
import base.app.fragment.BaseFragment;
import base.app.fragment.FragmentEvent;
import base.app.model.Model;
import base.app.model.friendship.FriendsManager;
import base.app.model.user.UserInfo;
import base.app.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @BindView(R.id.search_text)
    EditText searchText;

    List<UserInfo> following;

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
        UserInfo userInfo = Model.getInstance().getUserInfo();
        if (userInfo != null)
        {
            Task<List<UserInfo>> friendsTask = FriendsManager.getInstance().getUserFollowingList(userInfo.getUserId(), 0);
            friendsTask.addOnCompleteListener(new OnCompleteListener<List<UserInfo>>() {
                @Override
                public void onComplete(@NonNull Task<List<UserInfo>> task) {
                    if (task.isSuccessful()) {
                        following = task.getResult();
                        adapter.getValues().clear();
                        adapter.getValues().addAll(following);
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
        }
        else {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.try_again_latter), Toast.LENGTH_SHORT).show();
        }

        searchText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (following != null)
                {
                    adapter.getValues().clear();
                    adapter.getValues().addAll(Utility.filter(following, s.toString()));
                    adapter.notifyDataSetChanged();
                }
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
        EventBus.getDefault().post(new FragmentEvent(FriendsFragment.class));
    }

    @OnClick(R.id.your_followers_button)
    public void followersOnClick() {
        EventBus.getDefault().post(new FragmentEvent(FollowersFragment.class));
    }
}
