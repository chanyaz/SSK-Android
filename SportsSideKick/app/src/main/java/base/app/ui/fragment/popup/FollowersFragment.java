package base.app.ui.fragment.popup;

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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import base.app.R;
import base.app.data.user.User;
import base.app.ui.adapter.friends.FriendsAdapter;
import base.app.ui.fragment.user.auth.LoginApi;
import base.app.util.ui.BaseFragment;
import base.app.util.events.FragmentEvent;
import base.app.data.user.friends.FriendsManager;
import base.app.util.commons.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Djordje Krutil on 28.3.2017..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class FollowersFragment extends BaseFragment {

    @BindView(R.id.followers_recycler_view)
    RecyclerView followersRecyclerView;

    @BindView(R.id.progressBar)
    AVLoadingIndicatorView progressBar;

    @BindView(R.id.no_result)
    TextView noResult;

    @BindView(R.id.search_edit_text)
    EditText searchText;
    List<User> folowers;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_your_followers, container, false);
        ButterKnife.bind(this, view);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 5);
        followersRecyclerView.setLayoutManager(layoutManager);

        final FriendsAdapter adapter = new FriendsAdapter(this.getClass());
        followersRecyclerView.setAdapter(adapter);
        Task<List<User>> friendsTask = FriendsManager.getInstance().getUserFollowersList(LoginApi.getInstance().getUser().getUserId(), 0);
        friendsTask.addOnCompleteListener(new OnCompleteListener<List<User>>() {
            @Override
            public void onComplete(@NonNull Task<List<User>> task) {
                if (task.isSuccessful()) {
                    folowers = task.getResult();
                    adapter.getValues().clear();
                    adapter.getValues().addAll(folowers);
                    adapter.notifyDataSetChanged();
                    followersRecyclerView.setVisibility(View.VISIBLE);
                    noResult.setVisibility(View.GONE);
                } else {
                    followersRecyclerView.setVisibility(View.GONE);
                    noResult.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (folowers != null) {
                    adapter.getValues().clear();
                    adapter.getValues().addAll(Utility.filter(folowers, s.toString()));
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

    @OnClick(R.id.you_following_button)
    public void followingOnClick() {
        EventBus.getDefault().post(new FragmentEvent(FollowingFragment.class));
    }
}
