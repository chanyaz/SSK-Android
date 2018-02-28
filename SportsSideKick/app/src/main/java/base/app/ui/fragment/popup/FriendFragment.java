package base.app.ui.fragment.popup;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import base.app.R;
import base.app.data.Model;
import base.app.data.chat.ChatInfo;
import base.app.data.chat.ImsManager;
import base.app.data.user.UserInfo;
import base.app.data.user.friends.FriendsManager;
import base.app.ui.adapter.friends.FriendsAdapter;
import base.app.ui.fragment.base.BaseFragment;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.ui.fragment.other.ChatFragment;
import base.app.ui.fragment.stream.VideoChatFragment;
import base.app.util.AlertDialogManager;
import base.app.util.commons.Utility;
import base.app.util.events.call.StartCallEvent;
import base.app.util.events.chat.OpenChatEvent;
import base.app.util.ui.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Filip on 28/03/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class FriendFragment extends BaseFragment {

    public static final double GRID_PERCENT_CELL_WIDTH_PHONE = 0.18;

    @Nullable
    @BindView(R.id.profileImage)
    ImageView profileImage;
    @Nullable
    @BindView(R.id.profile_name)
    TextView profileName;
    @Nullable
    @BindView(R.id.progressBar)
    ProgressBar progressBarCircle;
    @Nullable
    @BindView(R.id.profile_image_level)
    TextView profileImageLevel;
    @Nullable
    @BindView(R.id.online_status)
    TextView onlineStatus;

    @Nullable
    @BindView(R.id.all_friends_recycler_view)
    RecyclerView allFriendsRecyclerView;
    @Nullable
    @BindView(R.id.in_common_recycler_view)
    RecyclerView inCommonRecyclerView;

    @Nullable
    @BindView(R.id.chat_button_image)
    ImageView chatButtonImage;
    @Nullable
    @BindView(R.id.chat_button_phone_image)
    ImageView chatButtonPhoneImage;




    @Nullable
    @BindView(R.id.follow_button_image)
    ImageView followButtonImage;
    @Nullable
    @BindView(R.id.friend_button_image)
    ImageView friendButtonImage;

    @Nullable
    @BindView(R.id.chat_button_text)
    TextView chatButtonText;

    @Nullable
    @BindView(R.id.chat_button_phone_caption)
    TextView chatButtonPhoneCaption;

    @Nullable
    @BindView(R.id.follow_button_text)
    TextView followButtonText;
    @Nullable
    @BindView(R.id.friend_button_text)
    TextView friendButtonText;


    @Nullable
    @BindView(R.id.chat_room_progress_bar)
    View roomProgressBar;
    @Nullable
    @BindView(R.id.friend_progress_bar)
    View friendProgressBar;
    @Nullable
    @BindView(R.id.friends_community)
    TextView friendsCommunityText;
    @Nullable
    @BindView(R.id.public_chat_text)
    TextView publicChatText;
    @Nullable
    @BindView(R.id.comments_text)
    TextView commentsCountText;
    @Nullable
    @BindView(R.id.posts_count_value)
    TextView postsCountText;
    @Nullable
    @BindView(R.id.friends_text)
    TextView friendsCountText;
    @Nullable
    @BindView(R.id.profile_nick)
    TextView profileNickText;

    @Nullable
    @BindView(R.id.video_button)
    ConstraintLayout videoButton;
    @Nullable
    @BindView(R.id.more_button)
    ConstraintLayout moreButton;

    @Nullable
    @BindView(R.id.public_chats_container)
    View publicChatsContainer;

    @Nullable
    @BindView(R.id.common_friends_container)
    View commonFriendsContainer;


    private Class initiatorFragment;

    FriendsAdapter friendsInCommonAdapter;
    FriendsAdapter allFriendsAdapter;

    public FriendFragment() {
        // Required empty public constructor
    }

    UserInfo user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);
        initiatorFragment = getInitiator();
        if (initiatorFragment == null) {
            initiatorFragment = FriendsFragment.class; // Resolve to default parent!
        }
        ButterKnife.bind(this, view);
        if (Utility.isPhone(getActivity())) {
            int width = Utility.getDisplayWidth(getActivity());
            friendsInCommonAdapter = new FriendsAdapter(this.getClass());
            int screenWidth = (int) (GRID_PERCENT_CELL_WIDTH_PHONE * width);
            allFriendsAdapter = new FriendsAdapter(this.getClass());
            allFriendsAdapter.screenWidth(screenWidth);

            friendsInCommonAdapter.screenWidth(screenWidth);
            LinearLayoutManager allFriendsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            LinearLayoutManager friendsInCommonLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            allFriendsRecyclerView.setLayoutManager(allFriendsLayoutManager);
            inCommonRecyclerView.setLayoutManager(friendsInCommonLayoutManager);
            allFriendsRecyclerView.setAdapter(allFriendsAdapter);
            inCommonRecyclerView.setAdapter(friendsInCommonAdapter);


        }

        Task<UserInfo> getUserTask = Model.getInstance().refreshUserInfo(getPrimaryArgument());
        getUserTask.addOnCompleteListener(onUserDataLoadedListener);
        return view;
    }

    private OnCompleteListener<UserInfo> onUserDataLoadedListener = new OnCompleteListener<UserInfo>() {
        @Override
        public void onComplete(@NonNull Task<UserInfo> task) {
            if (task.isSuccessful()) {
                user = task.getResult();
                setupUIWithUserInfo(user);
            }
        }
    };

    private void setupUIWithUserInfo(UserInfo user) {
        if (!isAdded()) return; // fixed crash when accessing strings in detached mode

        profileName.setText(user.getFirstName() + " " + user.getLastName());
        //TODO @Filip refactoring Put this in a  string file
        onlineStatus.setText(user.isOnline() ? "online" : "offline");
        ImageLoader.displayRoundImage(user.getCircularAvatarUrl(), profileImage);
        progressBarCircle.setProgress((int) (user.getProgress() * progressBarCircle.getMax()));
        profileImageLevel.setText(String.valueOf((int)user.getProgress()));

        if (Utility.isPhone(getActivity())) {

            // setup buttons depending on friendship state
            if(user.isaFriend()){
                videoButton.setVisibility(View.VISIBLE);
                moreButton.setVisibility(View.VISIBLE);
                chatButtonPhoneCaption.setText(getContext().getResources().getString(R.string.chat));
                changeViewClickable(true,chatButtonPhoneImage);
            } else {
                if(user.isFriendPendingRequest()){
                    chatButtonPhoneCaption.setText(getContext().getResources().getString(R.string.friend_request_pending));
                    changeViewClickable(false,chatButtonPhoneImage);
                } else {
                    chatButtonPhoneCaption.setText(getContext().getResources().getString(R.string.friend_request_send));
                    changeViewClickable(true,chatButtonPhoneImage);
                }
            }


            if (user.getUserId() != null) {
                friendsCountText.setText(String.valueOf(user.getRequestedUserFriendsCount()));
                commentsCountText.setText(String.valueOf(user.getComments()));
                postsCountText.setText(String.valueOf(user.getWallPosts()));
                profileNickText.setText("(" + user.getNicName() + ")");
                getMutualFriendsListWithUser(user);
                getAllUserChats(user);
            } else {
                if (roomProgressBar != null) {
                    roomProgressBar.setVisibility(View.GONE);
                }
                if (friendProgressBar != null) {
                    friendProgressBar.setVisibility(View.GONE);
                }
            }
        } else {
            if (user.isaFriend()) { // this user is my friend
                friendButtonImage.setImageResource(R.drawable.friend_unfollow_button);
                friendButtonText.setText(getContext().getResources().getString(R.string.remove_friend));

                if (user.isiFollowHim()) { // I am following this user
                    followButtonImage.setImageResource(R.drawable.friend_unfollow_button);
                    followButtonText.setText(getContext().getResources().getString(R.string.friend_unfollow));
                } else {
                    followButtonImage.setImageResource(R.drawable.friend_follow_button);
                    followButtonText.setText(getContext().getResources().getString(R.string.friend_follow));
                }

                // enable all buttons!
                changeViewClickable(true, friendButtonImage);
                changeViewClickable(true, friendButtonText);
                changeViewClickable(true, chatButtonImage);
                changeViewClickable(true, chatButtonText);
                changeViewClickable(true, followButtonImage);
                changeViewClickable(true, followButtonText);

            } else { // this user is not my friend

                // set default value for follow button
                followButtonImage.setImageResource(R.drawable.friend_follow_button);
                followButtonText.setText(getContext().getResources().getString(R.string.friend_follow));
                // disable chat button
                changeViewClickable(false, chatButtonImage);
                changeViewClickable(false, chatButtonText);
                // disable follow button
                changeViewClickable(false, followButtonImage);
                changeViewClickable(false, followButtonText);

                if (user.isFriendPendingRequest()) { // Check if request is pending - if so, disable button
                    friendButtonImage.setImageResource(R.drawable.friend_unfollow_button);
                    friendButtonText.setText(getContext().getResources().getString(R.string.friend_request_pending));
                    // disable send request button -  request is sent
                    changeViewClickable(false, friendButtonImage);
                    changeViewClickable(false, friendButtonText);
                } else {
                    friendButtonImage.setImageResource(R.drawable.friend_follow_button);
                    friendButtonText.setText(getContext().getResources().getString(R.string.friend_request_send));
                    //enable send request button
                    changeViewClickable(true, friendButtonImage);
                    changeViewClickable(true, friendButtonText);
                }
            }
        }
    }

    private void changeViewClickable(boolean value, View view) {
        view.setClickable(value);
        if (value) {
            view.setAlpha(1.0f);
        } else {
            view.setAlpha(0.5f);
        }
    }

    private void getAllUserChats(final UserInfo user) {
        Task<List<UserInfo>> taskAllChats = FriendsManager.getInstance().getFriendsForUser(user.getUserId(), 0,30);
        taskAllChats.addOnCompleteListener(new OnCompleteListener<List<UserInfo>>() {
            @Override
            public void onComplete(@NonNull Task<List<UserInfo>> task) {
                if (getActivity() == null) return;
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        if (publicChatsContainer != null) {
                            publicChatsContainer.setVisibility(View.VISIBLE);
                        }
                        publicChatText.setText(user.getFirstName() + " " + getString(R.string.friends));
                        allFriendsAdapter.getValues().clear();
                        allFriendsAdapter.getValues().addAll(task.getResult());
                        allFriendsAdapter.notifyDataSetChanged();
                    } else {
                        if (publicChatsContainer != null) {
                            publicChatsContainer.setVisibility(View.GONE);
                        }
                    }
                } else {
                    if (publicChatsContainer != null) {
                        publicChatsContainer.setVisibility(View.GONE);
                    }
                }

                assert roomProgressBar != null;
                roomProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getMutualFriendsListWithUser(final UserInfo user) {

        Task<List<UserInfo>> task = FriendsManager.getInstance().getMutualFriendsListWithUser(user.getUserId(), 0);
        task.addOnCompleteListener(new OnCompleteListener<List<UserInfo>>() {
            @Override
            public void onComplete(@NonNull Task<List<UserInfo>> task) {
                if (task.isSuccessful()) {
                    if (getActivity() == null) return;
                    if (task.getResult().size() > 0) {
                        if (commonFriendsContainer != null) {
                            commonFriendsContainer.setVisibility(View.VISIBLE);
                        }
                        if (friendsCommunityText != null) {
                            friendsCommunityText.setText(getString(R.string.friend_you_and) + user.getFirstName() + getString(R.string.have_in_common));
                            friendsInCommonAdapter.getValues().clear();
                            friendsInCommonAdapter.getValues().addAll(task.getResult());
                            friendsInCommonAdapter.notifyDataSetChanged();
                        }
                    } else if (commonFriendsContainer != null) {
                        commonFriendsContainer.setVisibility(View.GONE);
                    }
                }
                if (friendProgressBar != null) {
                    friendProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Optional
    @OnClick({R.id.confirm_button, R.id.close})
    public void confirmOnClick() {
        getActivity().onBackPressed();
        EventBus.getDefault().post(new FragmentEvent(initiatorFragment, true));
    }


    @Optional
    @OnClick(R.id.chat_button_image)
    public void click() {
        chatOnClick();
    }

    @Optional
    @OnClick(R.id.chat_button)
    public void chatOnClick() {
        // TODO @Filip Check if chat exists?

        // on phone, change this button's behaviour to send friend request if this is not a friend
        if(Utility.isPhone(getActivity()) && !user.isaFriend()){
            friendOnClick();
            return;
        }

        List<UserInfo> selectedUsers = new ArrayList<>();
        selectedUsers.add(user);
        String chatName = user.getNicName() + " & " + Model.getInstance().getUserInfo().getNicName();

        ChatInfo newChatInfo = new ChatInfo();
        newChatInfo.setOwner(Model.getInstance().getUserInfo().getUserId());
        newChatInfo.setIsPublic(true);
        newChatInfo.setName(chatName);
        ArrayList<String> userIds = new ArrayList<>();
        for (UserInfo info : selectedUsers) {
            userIds.add(info.getUserId());
        }

        userIds.add(newChatInfo.getOwner());
        newChatInfo.setUsersIds(userIds);

        ImsManager.getInstance().createNewChat(newChatInfo);
        getActivity().onBackPressed();

        EventBus.getDefault().post(new OpenChatEvent(newChatInfo));

        if(Utility.isPhone(getActivity())){
            EventBus.getDefault().post(new FragmentEvent(ChatFragment.class));
        }
    }

    @Optional
    @OnClick(R.id.friend_button_image)
    public void friendOnClick() {
        if (user.isaFriend()) { // this user is my friend, remove it from friends
            AlertDialogManager.getInstance()
                    .showAlertDialog(
                            "",
                            getContext().getResources().getString(R.string.unfriend_confirm),
                            new View.OnClickListener() {// Cancel listener
                                @Override
                                public void onClick(View v) {
                                    getActivity().onBackPressed();
                                }
                            }, new View.OnClickListener() {// Confirm listener
                                @Override
                                public void onClick(View v) {
                                    removeUser();
                                    getActivity().onBackPressed();
                                }
                            });
        } else {
            FriendsManager.getInstance().sendFriendRequest(user.getUserId()).addOnCompleteListener(new OnCompleteListener<UserInfo>() {
                @Override
                public void onComplete(@NonNull Task<UserInfo> task) {
                    if (task.isSuccessful()) {
                        user.setFriendPendingRequest(true);
                        setupUIWithUserInfo(user);
                    }
                }
            });
        }

    }

    private void removeUser(){
        FriendsManager.getInstance().deleteFriend(user.getUserId()).addOnCompleteListener(new OnCompleteListener<UserInfo>() {
            @Override
            public void onComplete(@NonNull Task<UserInfo> task) {
                if (task.isSuccessful()) {
                    user.setaFriend(false);
                    setupUIWithUserInfo(user);
                }
            }
        });
    }

    @Optional
    @OnClick(R.id.video_button)
    public void videoButtonOnClick() {
        EventBus.getDefault().post(new FragmentEvent(VideoChatFragment.class));
        new Handler().postDelayed(new Runnable() {
            public void run() {
                ArrayList<UserInfo> usersToCall = new ArrayList<>();
                usersToCall.add(user);
                EventBus.getDefault().post(new StartCallEvent(usersToCall));
            }
        }, 500);
    }

    @Optional
    @OnClick(R.id.more_button)
    public void moreButtonOnClick() {
        startAlterDialog();
    }


    @Optional
    @OnClick(R.id.follow_button_image)
    public void followButtonOnClick() {
        Task<UserInfo> changeFollowTask;
        if (user.isiFollowHim()) { // I am friends this user, un-follow him
            changeFollowTask = FriendsManager.getInstance().unFollowUser(user.getUserId());
        } else { // not friends, start friends this user
            changeFollowTask = FriendsManager.getInstance().followUser(user.getUserId());
        }
        changeFollowTask.addOnCompleteListener(new OnCompleteListener<UserInfo>() {
            @Override
            public void onComplete(@NonNull Task<UserInfo> task) {
                if (task.isSuccessful()) {
                    user.setiFollowHim(!user.isiFollowHim());
                    setupUIWithUserInfo(user);
                }
            }
        });
    }

    private void startAlterDialog() {
        if (user != null) {
            String follow;
            String friend;
            if (user.isaFriend()) {
                friend = getString(R.string.remove_friend);
            } else {
                friend = getString(R.string.friend_request_send);
            }
            if (user.isiFollowHim()) { // I am following this user
                follow = getString(R.string.friend_unfollow);
            } else {
                follow = getString(R.string.friend_follow);
            }

            TextView report = new TextView(getActivity());
            report.setText(getString(R.string.report_abuse));
            report.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialog);
            builder.setTitle(getString(R.string.what_would_you_like_to_do));
            builder.setItems(new CharSequence[]
                            {getString(R.string.report_abuse), follow, friend, getString(R.string.cancel)},
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            switch (which) {
                                case 0:
                                    reportAbuse();
                                    break;
                                case 1:
                                    followButtonOnClick();
                                    break;
                                case 2:
                                    friendOnClick();
                                    break;
                                case 3:
                                    break;
                            }

                            dialog.cancel();
                        }
                    });
            builder.create().show();
        }
    }

    private void reportAbuse() {
        //TODO @Filip add this api call
    }
}
