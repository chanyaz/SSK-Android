package base.app.ui.fragment.popup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import base.app.R;
import base.app.data.AlertDialogManager;
import base.app.data.user.friends.FriendsManager;
import base.app.data.AddFriendsEvent;
import base.app.data.user.UserInfo;
import base.app.ui.adapter.friends.AddFriendsAdapter;
import base.app.ui.adapter.friends.SelectableFriendsAdapter;
import base.app.ui.fragment.base.BaseFragment;
import base.app.data.FragmentEvent;
import base.app.util.commons.Utility;
import base.app.data.AddUsersToCallEvent;
import base.app.util.events.call.StartCallEvent;
import base.app.util.ui.AutofitDecoration;
import base.app.util.ui.AutofitRecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

import static base.app.ui.fragment.popup.FriendsFragment.GRID_PERCENT_CELL_WIDTH;

/**
 * Created by Djordje on 1/21/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class StartingNewCallFragment extends BaseFragment {
    private static final String TAG = "Start New Call Fragment";
    public static final double GRID_PERCENT_CELL_WIDTH_PHONE = 0.2;
    @BindView(R.id.friends_recycler_view)
    AutofitRecyclerView friendsRecyclerView;
    @Nullable
    @BindView(R.id.chat_name_edit_text)
    EditText friendName;
    @BindView(R.id.progressBar)
    AVLoadingIndicatorView progressBar;
    @Nullable
    @BindView(R.id.confirm_button)
    ImageButton confirmButton;
    @Nullable
    @BindView(R.id.confirm_button_phone)
    Button confirmButtonPhone;
    @Nullable
    @BindView(R.id.chat_friends_in_chat_recycler_view)
    RecyclerView addFriendsRecyclerView;
    @Nullable
    @BindView(R.id.chat_friends_in_chat_headline)
    TextView headlineFriendsInChat;
    @Nullable
    @BindView(R.id.no_result)
    TextView noResult;
    boolean isTablet;
    AddFriendsAdapter addFriendsAdapter;
    SelectableFriendsAdapter chatFriendsAdapter;
    int space;
    List<UserInfo> users;
    boolean addUsersToCall;

    public StartingNewCallFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_new_call, container, false);
        ButterKnife.bind(this, view);
        space = getResources().getDimensionPixelOffset(R.dimen.margin_15);
        int screenWidth = Utility.getDisplayWidth(getActivity());
        isTablet = Utility.isTablet(getActivity());
        addUsersToCall = false;
        users = new ArrayList<>();
        setTextWatcher();
        if (isTablet) {
            friendsRecyclerView.setCellWidth((int) (screenWidth * GRID_PERCENT_CELL_WIDTH));
            confirmButton.setEnabled(false);
        } else {
            friendsRecyclerView.setCellWidth((int) (screenWidth * GRID_PERCENT_CELL_WIDTH_PHONE));
            confirmButtonPhone.setEnabled(false);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            addFriendsRecyclerView.setLayoutManager(linearLayoutManager);
            addFriendsAdapter = new AddFriendsAdapter(getActivity(), this);
            addFriendsRecyclerView.setAdapter(addFriendsAdapter);
        }
        friendsRecyclerView.addItemDecoration(new AutofitDecoration(getActivity()));
        friendsRecyclerView.setHasFixedSize(true);


        Task<List<UserInfo>> task = FriendsManager.getInstance().getFriends(0);
        task.addOnSuccessListener(
                new OnSuccessListener<List<UserInfo>>() {
                    @Override
                    public void onSuccess(List<UserInfo> userInfos) {
                        chatFriendsAdapter = new SelectableFriendsAdapter(getContext());
                        List<String> presentUsers = getStringArrayArgument();
                        users = userInfos;
                        if (presentUsers != null) {
                            addUsersToCall = true;
                            List<UserInfo> usersToRemove = new ArrayList<>();
                            for (UserInfo userInfo : userInfos) {
                                if (presentUsers.contains(userInfo.getUserId())) {
                                    usersToRemove.add(userInfo);
                                }
                            }
                            userInfos.removeAll(usersToRemove);
                        }

                        chatFriendsAdapter.add(userInfos);
                        friendsRecyclerView.setAdapter(chatFriendsAdapter);
                        progressBar.setVisibility(View.GONE);
                        if (isTablet) {
                            confirmButton.setEnabled(true);
                        } else {
                            confirmButtonPhone.setEnabled(true);
                        }
                    }
                });
        return view;
    }

    @Optional
    @OnClick(R.id.confirm_button_phone)
    public void confirmOnClickPhone() {
        confirmOnClick();
    }

    @Optional
    @OnClick(R.id.confirm_button)
    public void confirmOnClick() {
        if (chatFriendsAdapter != null) {
            if (chatFriendsAdapter.getSelectedValues().size() > 0) {
                getActivity().onBackPressed();
                if (addUsersToCall) {
                    EventBus.getDefault().post(new AddUsersToCallEvent(chatFriendsAdapter.getSelectedValues()));
                } else {
                    EventBus.getDefault().post(new StartCallEvent(chatFriendsAdapter.getSelectedValues()));
                }

            } else {
                AlertDialogManager.getInstance().showAlertDialog(getContext().getResources().getString(R.string.no_users_selected), getContext().getResources().getString(R.string.no_users_selected_message),
                        null,
                        new View.OnClickListener() { // Confirm
                            @Override
                            public void onClick(View v) {
                                EventBus.getDefault().post(new FragmentEvent(StartingNewCallFragment.class));
                            }
                        });
            }
        } else {
            Log.e(TAG, "Adapter is null - wait for friends to be loaded!");
        }

    }


    @Subscribe
    public void updateAddFriendsAdapter(AddFriendsEvent event) {
        if (!isTablet) {
            if (event.isRemove()) {
                addFriendsAdapter.remove(event.getUserInfo());
            } else {
                addFriendsAdapter.add(event.getUserInfo());
            }
            int friendCount = addFriendsAdapter.getItemCount();
            String friendsInchat = " " + getContext().getResources().getString(R.string.friends_in_video_chat);
            if (friendCount == 0) {
                headlineFriendsInChat.setText(friendsInchat);
            } else if (friendCount == 1) {
                headlineFriendsInChat.setText(getContext().getResources().getString(R.string.friend_in_video_chat));
            } else {
                String friendsTotal = friendCount + friendsInchat;
                headlineFriendsInChat.setText(friendsTotal);
            }

        }
    }


    private void setTextWatcher() {
        if (friendName != null) {
            friendName.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String text = s.toString();
                    friendsRecyclerView.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    findUsers(text);

                }
            });
        }
    }

    private void findUsers(String sequence) {
        if (users != null) {
            noResult.setVisibility(View.GONE);
            final List<UserInfo> filteredModelList = Utility.filter(users, sequence);
            if (filteredModelList.size() > 0) {
                friendsRecyclerView.setVisibility(View.VISIBLE);
                chatFriendsAdapter.replaceAll(filteredModelList);
                friendsRecyclerView.scrollToPosition(0);
            } else {
                noResult.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.GONE);
        }
    }

}





