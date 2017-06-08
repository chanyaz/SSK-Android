package base.app.fragment.popup;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import base.app.R;
import base.app.adapter.ChatGroupAdapter;
import base.app.adapter.FriendsAdapter;
import base.app.fragment.BaseFragment;
import base.app.fragment.FragmentEvent;
import base.app.fragment.instance.VideoChatFragment;
import base.app.model.Model;
import base.app.model.friendship.FriendsManager;
import base.app.model.im.ChatInfo;
import base.app.model.im.ImsManager;
import base.app.model.user.UserInfo;
import base.app.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Filip on 28/03/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class MemberInfoFragment extends BaseFragment {
    @Nullable
    @BindView(R.id.profile_image)
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
    public static final double GRID_PERCENT_CELL_WIDTH_PHONE = 0.18;
    @Nullable
    @BindView(R.id.friend_button)
    Button friendButton;
    @Nullable
    @BindView(R.id.reverse_is_in_recycler_view)
    RecyclerView publicChatRecyclerView;
    @Nullable
    @BindView(R.id.in_common_recycler_view)
    RecyclerView inCommonRecyclerView;
    @Nullable
    @BindView(R.id.follow_button)
    Button followButton;
    @Nullable
    @BindView(R.id.chat_room_progress_bar)
    AVLoadingIndicatorView roomProgressBar;
    @Nullable
    @BindView(R.id.friend_progress_bar)
    AVLoadingIndicatorView friendProgressBar;
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
    @BindView(R.id.pinned_text)
    TextView pinnedCountText;
    @Nullable
    @BindView(R.id.friends_text)
    TextView friendsCountText;
    @Nullable
    @BindView(R.id.profile_nick)
    TextView profileNickText;


    private Class initiatorFragment;

    FriendsAdapter friendsInCommonAdapter;
    ChatGroupAdapter publicChatsIsInCommonAdapter;

    public MemberInfoFragment() {
        // Required empty public constructor
    }

    UserInfo user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_member_info, container, false);
        initiatorFragment = getInitiator();
        if (initiatorFragment == null) {
            initiatorFragment = FriendsFragment.class; // Resolve to default parent!
        }
        ButterKnife.bind(this, view);
        if (!Utility.isTablet(getActivity())) {
            int width = Utility.getDisplayWidth(getActivity());
            friendsInCommonAdapter = new FriendsAdapter(this.getClass());
            int screenWidth = (int) (GRID_PERCENT_CELL_WIDTH_PHONE * width);
            publicChatsIsInCommonAdapter = new ChatGroupAdapter(getActivity(), screenWidth);
            friendsInCommonAdapter.screenWidth(screenWidth);
            LinearLayoutManager publicChatsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            LinearLayoutManager friendsInCommonLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            publicChatRecyclerView.setLayoutManager(publicChatsLayoutManager);
            inCommonRecyclerView.setLayoutManager(friendsInCommonLayoutManager);
            publicChatRecyclerView.setAdapter(publicChatsIsInCommonAdapter);
            inCommonRecyclerView.setAdapter(friendsInCommonAdapter);


        }


        Task<UserInfo> getUserTask = Model.getInstance().getUserInfoById(getPrimaryArgument());
        getUserTask.addOnCompleteListener(new OnCompleteListener<UserInfo>() {
            @Override
            public void onComplete(@NonNull Task<UserInfo> task) {
                if (task.isSuccessful()) {
                    user = task.getResult();
                    profileName.setText(user.getFirstName() + " " + user.getLastName());
                    //TODO @Filip refactoring Put this in a  string file
                    onlineStatus.setText(user.isOnline() ? "online" : "offline");
                    ImageLoader.getInstance().displayImage(user.getCircularAvatarUrl(), profileImage, Utility.getImageOptionsForUsers());
                    progressBarCircle.setProgress((int) (user.getProgress() * progressBarCircle.getMax()));
                    profileImageLevel.setText(String.valueOf(user.getLevel()));
                    if (!Utility.isTablet(getActivity())) {
                        if (user.getUserId() != null) {
                            friendsCountText.setText(String.valueOf(user.getFriendsCount()));
                            commentsCountText.setText(String.valueOf(user.getComments()));
                            pinnedCountText.setText(String.valueOf(user.getWallPins()));
                            profileNickText.setText("(" + user.getNicName() + ")");
                            getMutualFriendsListWithUser(user);
                            getAllUserChats(user);
                        } else {
                            assert roomProgressBar != null;
                            roomProgressBar.setVisibility(View.GONE);
                            assert friendProgressBar != null;
                            friendProgressBar.setVisibility(View.GONE);
                        }
                    } else {
                        if (user.isaFriend()) { // this user is my friend
                            friendButton.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getActivity(), R.drawable.friend_unfollow_button), null, null, null);
                            friendButton.setText(getContext().getResources().getString(R.string.remove_friend));
                        } else {
                            friendButton.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getActivity(), R.drawable.friend_follow_button), null, null, null);
                            friendButton.setText(getContext().getResources().getString(R.string.friend_reqest));
                        }

                        if (user.isiFollowHim()) { // I am friends this user
                            followButton.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getActivity(), R.drawable.friend_unfollow_button), null, null, null);
                            followButton.setText(getContext().getResources().getString(R.string.friend_unfollow));
                        } else {
                            followButton.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getActivity(), R.drawable.friend_follow_button), null, null, null);
                            followButton.setText(getContext().getResources().getString(R.string.friend_follow));
                        }
                    }
                }
            }
        });


        return view;


    }

    private void getAllUserChats(final UserInfo user) {

        Task<List<ChatInfo>> taskAllChats = ImsManager.getInstance().getAllPublicChatsForUser(user.getUserId());
        taskAllChats.addOnCompleteListener(new OnCompleteListener<List<ChatInfo>>() {
            @Override
            public void onComplete(@NonNull Task<List<ChatInfo>> task) {
                if (task.isSuccessful()) {
                    publicChatText.setText(getString(R.string.public_chats_profile) + " " + user.getFirstName() + " " + user.getLastName() + " " + getString(R.string.is_in));
                    publicChatsIsInCommonAdapter.setValues(task.getResult());
                } else {
//TODO add error handle, ios dont have
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
                if (task.isSuccessful())
                    if (task.getResult().size() > 0) {
                        friendsCommunityText.setText(getString(R.string.friend_you_and) + " " + user.getFirstName() + " " + getString(R.string.have_in_common));
                        friendsInCommonAdapter.getValues().addAll(task.getResult());
                        friendsInCommonAdapter.notifyDataSetChanged();
                    } else {
//TODO add error handle, ios dont have
                    }
                assert friendProgressBar != null;
                friendProgressBar.setVisibility(View.GONE);
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
    @OnClick(R.id.chat_button)
    public void chatOnClick() {
        // TODO @Filip Check if chat exists?
        List<UserInfo> selectedUsers = new ArrayList<>();
        selectedUsers.add(user);
        String chatName = user.getNicName() + " & " + Model.getInstance().getUserInfo().getNicName();
        boolean isPrivate = true;

        ChatInfo newChatInfo = new ChatInfo();
        newChatInfo.setOwner(Model.getInstance().getUserInfo().getUserId());
        newChatInfo.setIsPublic(!isPrivate);
        newChatInfo.setName(chatName);
        ArrayList<String> userIds = new ArrayList<>();
        for (UserInfo info : selectedUsers) {
            userIds.add(info.getUserId());
        }


        userIds.add(newChatInfo.getOwner());
        newChatInfo.setUsersIds(userIds);

        ImsManager.getInstance().createNewChat(newChatInfo);
        EventBus.getDefault().post(new FragmentEvent(getInitiator(), true));
    }

    @Optional
    @OnClick(R.id.friend_button)
    public void friendOnClick() {
        Task<UserInfo> manageFriendTask;
        if (user.isaFriend()) { // this user is my friend, remove it from friends
            manageFriendTask = FriendsManager.getInstance().deleteFriend(user.getUserId());
        } else { // send friend request
            manageFriendTask = FriendsManager.getInstance().sendFriendRequest(user.getUserId());
        }
        manageFriendTask.addOnCompleteListener(new OnCompleteListener<UserInfo>() {
            @Override
            public void onComplete(@NonNull Task<UserInfo> task) {
                if (task.isSuccessful()) {
                    user.setaFriend(!user.isaFriend());
                }
            }
        });
        EventBus.getDefault().post(new FragmentEvent(initiatorFragment, true));
    }

    @Optional
    @OnClick(R.id.video_button)
    public void wideoButton() {
        //TODO finish this
        EventBus.getDefault().post(new FragmentEvent(VideoChatFragment.class));
    }

    @Optional
    @OnClick(R.id.more_button)
    public void moreButton() {
        startAlterDialog();
    }


    @Optional
    @OnClick(R.id.follow_button)
    public void followButton() {
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
                }
            }
        });
        EventBus.getDefault().post(new FragmentEvent(initiatorFragment, true));
    }

    private void startAlterDialog() {

        if (user != null) {
            String follow;
            String friend;
            if (user.isaFriend()) {
                friend = getString(R.string.remove_friend);
            } else {
                friend = getString(R.string.friend_reqest);
            }
            if (user.isiFollowHim()) { // I am friends this user

                follow = getString(R.string.friend_unfollow);
            } else {

                follow = getString(R.string.friend_follow);
            }

            TextView report= new TextView(getActivity());
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
                                    followButton();
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
