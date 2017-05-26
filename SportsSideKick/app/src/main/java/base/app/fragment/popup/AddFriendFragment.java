package base.app.fragment.popup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import base.app.adapter.FindOfficialAdapter;
import base.app.model.friendship.FriendsManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import base.app.R;
import base.app.adapter.FriendsAdapter;
import base.app.fragment.BaseFragment;
import base.app.fragment.FragmentEvent;
import base.app.model.friendship.PeopleSearchManager;
import base.app.model.user.UserInfo;
import base.app.util.Utility;
import butterknife.Optional;

/**
 * Created by Djordje Krutil on 29.3.2017..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class AddFriendFragment extends BaseFragment {

    @BindView(R.id.people_recycler_view)
    RecyclerView people;
    public static final double GRID_PERCENT_CELL_WIDTH_PHONE = 0.2;
    @BindView(R.id.add_friend_name)
    EditText friendName;
    @Nullable
    @BindView(R.id.special_recycler_view)
    RecyclerView specialRecyclerView;
    @Nullable
    @BindView(R.id.special_list_holder)
    RelativeLayout specialListContainer;
    @BindView(R.id.progress_bar)
    AVLoadingIndicatorView progressBar;
    @Nullable
    @BindView(R.id.no_result_special)
    TextView noResultSpecialText;
    @BindView(R.id.no_result)
    TextView noResultCaption;
    @Nullable
    @BindView(R.id.common_friend_list)
    RecyclerView commonFriend;
    @BindView(R.id.friends_list)
    RelativeLayout listContainer;
    FindOfficialAdapter officialAdapter;
    boolean isTablet;
    List<UserInfo> specialUserInfoList;
    FriendsAdapter adapter;
    FriendsAdapter commonFriendAdapter;
    List<Integer> mutualFriendsNumberList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.popup_add_friend, container, false);
        ButterKnife.bind(this, view);
        isTablet = getResources().getBoolean(R.bool.is_tablet);
        adapter = new FriendsAdapter(this.getClass());
        adapter.setInitiatorFragment(this.getClass());
        if (isTablet) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            people.setLayoutManager(layoutManager);
            people.setAdapter(adapter);
        } else {
            setCommonFriendAdapter();
            adapter.setLayout(R.layout.row_add_friend_item);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            people.setLayoutManager(layoutManager);
            people.setAdapter(adapter);
            setSpecialUserList();
        }
        setTextWatcher();

        return view;
    }

    @Optional
    @OnClick(R.id.invite_friend_button)
    public void onClickInviteFriend() {
        EventBus.getDefault().post(new FragmentEvent(InviteFriendFragment.class));
    }

    private void onDataSetChange(boolean visible) {
        if (visible) {
            noResultCaption.setVisibility(View.GONE);
            people.setVisibility(View.VISIBLE);
            listContainer.setVisibility(View.VISIBLE);
        } else {
            noResultCaption.setVisibility(View.VISIBLE);
            listContainer.setVisibility(View.VISIBLE);
            people.setVisibility(View.GONE);
        }


    }


    private void onSpecialDataSetChange(boolean visible) {
        if (specialListContainer != null && specialRecyclerView != null && noResultSpecialText != null)
            if (visible) {
                noResultSpecialText.setVisibility(View.GONE);
                specialRecyclerView.setVisibility(View.VISIBLE);
                specialListContainer.setVisibility(View.VISIBLE);
            } else {
                specialListContainer.setVisibility(View.VISIBLE);
                specialRecyclerView.setVisibility(View.INVISIBLE);
                noResultSpecialText.setVisibility(View.VISIBLE);
            }


    }


    private void setSpecialUserList() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        //   LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        specialUserInfoList = new ArrayList<>();
        assert specialRecyclerView != null;
        specialRecyclerView.setLayoutManager(gridLayoutManager);
        officialAdapter = new FindOfficialAdapter(getActivity(), this.getClass());
        specialRecyclerView.setAdapter(officialAdapter);
        onDataSetChange(false);
        listContainer.setVisibility(View.INVISIBLE);
        //TODO @Filip, add new api call, get all admins.
        Task<List<UserInfo>> peopleTask = PeopleSearchManager.getInstance().searchPeople("", 0);
        peopleTask.addOnCompleteListener(new OnCompleteListener<List<UserInfo>>() {
            @Override
            public void onComplete(@NonNull Task<List<UserInfo>> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        for (UserInfo userInfo : task.getResult())
                            if (userInfo.getUserType().equals(UserInfo.UserType.special))
                                specialUserInfoList.add(userInfo);
                        if (!specialUserInfoList.isEmpty())
                            officialAdapter.setValues(specialUserInfoList);
                        officialAdapter.notifyDataSetChanged();
                        onSpecialDataSetChange(true);
                    } else {
                        onSpecialDataSetChange(false);
                    }
                } else {
                    onSpecialDataSetChange(false);
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setTextWatcher() {
        friendName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    listContainer.setVisibility(View.GONE);
                    if (specialListContainer != null) {
                        specialListContainer.setVisibility(View.VISIBLE);
                    }
                } else {
                    listContainer.setVisibility(View.VISIBLE);
                    if (specialListContainer != null) {
                        specialListContainer.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {


                String text = s.toString();
                //TODO @Filip, add item in the Api mutual friends List.
                Task<List<UserInfo>> peopleTask = PeopleSearchManager.getInstance().searchPeople(text, 0);
                peopleTask.addOnCompleteListener(new OnCompleteListener<List<UserInfo>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<UserInfo>> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                onDataSetChange(true);
                                adapter.setValues(task.getResult());
                                adapter.notifyDataSetChanged();
                                if (!isTablet)
                                    commonFriendAddItems(task.getResult());
                            } else {
                                onDataSetChange(false);
                            }
                        } else {
                            onDataSetChange(false);
                        }
                        progressBar.setVisibility(View.GONE);
//
                    }
                });

            }
        });
    }

    private void commonFriendAddItems(final List<UserInfo> result) {

        commonFriendAdapter.setValues(new ArrayList<UserInfo>());
        for (final UserInfo userInfo : result) {
            Task<List<UserInfo>> peopleTask = FriendsManager.getInstance().getMutualFriendsListWithUser(userInfo.getUserId(), 0);
            peopleTask.addOnCompleteListener(new OnCompleteListener<List<UserInfo>>() {
                @Override
                public void onComplete(@NonNull Task<List<UserInfo>> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() > 0) {
                            commonFriendAdapter.getValues().addAll(task.getResult());
                            commonFriendAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }

    @Optional
    @OnClick(R.id.your_friends_open_button)
    public void friendsOnClick() {
        EventBus.getDefault().post(new FragmentEvent(FriendsFragment.class));
    }


    @Optional
    @OnClick(R.id.friend_requests)
    public void profileOnClick() {
        EventBus.getDefault().post(new FragmentEvent(FriendRequestsFragment.class));
    }

    public void setCommonFriendAdapter() {
        if (commonFriend != null) {
            int screenWidth = Utility.getDisplayWidth(getActivity());
            commonFriendAdapter = new FriendsAdapter(this.getClass());
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            commonFriend.setLayoutManager(layoutManager);
            commonFriendAdapter.screenWidth((int) (screenWidth * GRID_PERCENT_CELL_WIDTH_PHONE));
            // commonFriend.setHasFixedSize(true);
            commonFriend.setAdapter(commonFriendAdapter);
        }
    }

}
