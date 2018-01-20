package base.app.ui.fragment.popup;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import base.app.R;
import base.app.ui.adapter.friends.FriendsAdapter;
import base.app.ui.fragment.base.BaseFragment;
import base.app.data.FragmentEvent;
import base.app.data.Model;
import base.app.data.user.friends.FriendRequest;
import base.app.data.user.friends.FriendsListChangedEvent;
import base.app.data.user.friends.FriendsManager;
import base.app.data.user.UserInfo;
import base.app.util.commons.Utility;
import base.app.util.ui.AutofitDecoration;
import base.app.util.ui.AutofitRecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Djordje on 1/21/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class FriendsFragment extends BaseFragment {

    public static final double GRID_PERCENT_CELL_WIDTH = 0.092;
    public static final double GRID_PERCENT_CELL_WIDTH_PHONE = 0.2;

    @BindView(R.id.friends_recycler_view)
    AutofitRecyclerView friendsRecyclerView;

    @BindView(R.id.progressBar)
    AVLoadingIndicatorView progressBar;
    @Nullable
    @BindView(R.id.friend_requests_count)
    TextView friendRequestCount;

    @BindView(R.id.no_result_text)
    TextView noResultText;

    @BindView(R.id.search_edit_text)
    EditText searchText;
    @Nullable
    @BindView(R.id.official_account_list)
    RecyclerView officialAccountRecyclerView;
    FriendsAdapter officialAccountAdapter;

    List<UserInfo> friends;
    List<UserInfo> officialAccounts;

    @Nullable
    @BindView(R.id.friend_requests_container)
    RelativeLayout friendRequestsContainer;

    FriendsAdapter adapter;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_your_friends, container, false);
        ButterKnife.bind(this, view);
        officialAccounts = new ArrayList<>();
        int screenWidth = Utility.getDisplayWidth(getActivity());
        if (Utility.isTablet(getActivity())) {
            friendsRecyclerView.setCellWidth((int) (screenWidth * GRID_PERCENT_CELL_WIDTH));
        } else {
            friendsRecyclerView.setCellWidth((int) (screenWidth * GRID_PERCENT_CELL_WIDTH_PHONE));
        }

        friendsRecyclerView.addItemDecoration(new AutofitDecoration(getActivity()));
        friendsRecyclerView.setHasFixedSize(true);


        adapter = new FriendsAdapter(this.getClass());
        adapter.setInitiatorFragment(FriendsFragment.class);
        friendsRecyclerView.setAdapter(adapter);

        updateFriends();

        officialAccountAdapter = new FriendsAdapter(this.getClass());
        if (officialAccountRecyclerView != null) {
            officialAccountRecyclerView.setAdapter(officialAccountAdapter);
            officialAccountRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            officialAccountAdapter.screenWidth((int) (screenWidth * GRID_PERCENT_CELL_WIDTH_PHONE));
        }

        Task<List<UserInfo>> officialTask = Model.getInstance().getOfficialAccounts(0);
        officialTask.addOnCompleteListener(new OnCompleteListener<List<UserInfo>>() {
            @Override
            public void onComplete(@NonNull Task<List<UserInfo>> task) {
                if (task.isSuccessful()) {
                    noResultText.setVisibility(View.GONE);
                    friendsRecyclerView.setVisibility(View.VISIBLE);
                    officialAccounts.addAll(task.getResult());
                    officialAccountAdapter.getValues().addAll(officialAccounts);
                    officialAccountAdapter.notifyDataSetChanged();
                } else {
                    noResultText.setVisibility(View.VISIBLE);
                    friendsRecyclerView.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        Task<List<FriendRequest>> task = FriendsManager.getInstance().getOpenFriendRequests(0);
        task.addOnCompleteListener(new OnCompleteListener<List<FriendRequest>>() {
            @Override
            public void onComplete(@NonNull Task<List<FriendRequest>> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        if (friendRequestCount != null) {
                            friendRequestCount.setText(String.valueOf(task.getResult().size()));
                        }
                        if (task.getResult().size() > 0) {
                            if (friendRequestsContainer != null) {
                                friendRequestsContainer.setVisibility(View.VISIBLE);
                            }
                        }
                        return;
                    }
                }
                if (friendRequestCount != null) {
                    friendRequestCount.setText("0");
                }
                if (friendRequestsContainer != null) {
                    friendRequestsContainer.setVisibility(View.GONE);
                }
            }
        });
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (friends != null) {
                    adapter.getValues().clear();
                    adapter.getValues().addAll(Utility.filter(friends, s.toString()));
                    adapter.notifyDataSetChanged();
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
    @OnClick(R.id.backButton)
    public void closeDialog() {
        getActivity().onBackPressed();
    }

    @Optional
    @OnClick(R.id.friend_requests)
    public void friendRequestsDialog() {
        EventBus.getDefault().post(new FragmentEvent(FriendRequestsFragment.class));
    }

    @Optional
    @OnClick(R.id.friend_requests_container)
    public void displayFriendRequests() {
        EventBus.getDefault().post(new FragmentEvent(FriendRequestsFragment.class));
    }

    @Optional
    @OnClick(R.id.add_friend)
    public void addFriend() {
        EventBus.getDefault().post(new FragmentEvent(AddFriendFragment.class));
    }

    @Optional
    @OnClick(R.id.search_icon)
    public void searchFriend() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void updateFriends(){
        Task<List<UserInfo>> friendsTask = FriendsManager.getInstance().getFriends(0);
        friendsTask.addOnCompleteListener(new OnCompleteListener<List<UserInfo>>() {
            @Override
            public void onComplete(@NonNull Task<List<UserInfo>> task) {
                if (task.isSuccessful()) {
                    noResultText.setVisibility(View.GONE);
                    friendsRecyclerView.setVisibility(View.VISIBLE);
                    friends = task.getResult();
                    adapter.getValues().clear();
                    adapter.getValues().addAll(friends);
                    adapter.notifyDataSetChanged();
                } else {
                    noResultText.setVisibility(View.VISIBLE);
                    friendsRecyclerView.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Subscribe
    public void handleFriendsListChanged(FriendsListChangedEvent event){
        updateFriends();
    }


}
