package tv.sportssidekick.sportssidekick.fragment.popup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.friendship.FriendsManager;
import tv.sportssidekick.sportssidekick.model.im.ChatInfo;
import tv.sportssidekick.sportssidekick.model.im.ImsManager;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Filip on 28/03/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class MemberInfoFragment extends BaseFragment {

    @BindView(R.id.profile_image)
    ImageView profileImage;

    @BindView(R.id.profile_name)
    TextView profileName;

    @BindView(R.id.progressBar)
    ProgressBar progressBarCircle;

    @BindView(R.id.profile_image_level)
    TextView profileImageLevel;

    @BindView(R.id.online_status)
    TextView onlineStatus;

    @BindView(R.id.chat_button)
    Button chatButton;

    @BindView(R.id.friend_button)
    Button friendButton;

    @BindView(R.id.follow_button)
    Button followButton;

    private Class initiatorFragment;


    public MemberInfoFragment() {
        // Required empty public constructor
    }

    UserInfo user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_member_info, container, false);
        initiatorFragment = getInitiator();
        if(initiatorFragment==null){
            initiatorFragment = FriendsFragment.class; // Resolve to default parent!
        }

        ButterKnife.bind(this, view);
        Task<UserInfo> getUserTask = Model.getInstance().getUserInfoById(getPrimaryArgument());
        getUserTask.addOnCompleteListener(new OnCompleteListener<UserInfo>() {
            @Override
            public void onComplete(@NonNull Task<UserInfo> task) {
                if(task.isSuccessful()){
                    user = task.getResult();
                    profileName.setText(user.getFirstName() + " " + user.getLastName());
                    onlineStatus.setText(user.isOnline() ? "online" : "offline");
                    ImageLoader.getInstance().displayImage(user.getCircularAvatarUrl(), profileImage, Utility.getImageOptionsForUsers());
                    progressBarCircle.setProgress((int) (user.getProgress()*progressBarCircle.getMax()));
                    profileImageLevel.setText(String.valueOf(user.getLevel()));

                    if(user.isaFriend()){ // this user is my friend
                        friendButton.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getActivity(), R.drawable.friend_unfollow_button),null,null,null);
                        friendButton.setText(getContext().getResources().getString(R.string.remove_friend));
                    } else {
                        friendButton.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getActivity(), R.drawable.friend_follow_button),null,null,null);
                        friendButton.setText(getContext().getResources().getString(R.string.friend_reqest));
                    }

                    if(user.isiFollowHim()){ // I am friends this user
                        followButton.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getActivity(), R.drawable.friend_unfollow_button),null,null,null);
                        followButton.setText(getContext().getResources().getString(R.string.friend_unfollow));
                    } else {
                        followButton.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getActivity(), R.drawable.friend_follow_button),null,null,null);
                        followButton.setText(getContext().getResources().getString(R.string.friend_follow));
                    }
                }
            }
        });
        return view;
    }

    @OnClick({R.id.confirm_button,R.id.close})
    public void confirmOnClick(){
        getActivity().onBackPressed();
        EventBus.getDefault().post(new FragmentEvent(initiatorFragment, true));
    }

    @OnClick(R.id.chat_button)
    public void chatOnClick(){
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
        for(UserInfo info : selectedUsers){
            userIds.add(info.getUserId());
        }
        userIds.add(newChatInfo.getOwner());
        newChatInfo.setUsersIds(userIds);

        ImsManager.getInstance().createNewChat(newChatInfo);
        EventBus.getDefault().post(new FragmentEvent(getInitiator(), true));
    }

    @OnClick(R.id.friend_button)
    public void friendOnClick(){
        Task<UserInfo> manageFriendTask;
        if(user.isaFriend()){ // this user is my friend, remove it from friends
            manageFriendTask = FriendsManager.getInstance().deleteFriend(user.getUserId());
        } else { // send friend request
            manageFriendTask = FriendsManager.getInstance().sendFriendRequest(user.getUserId());
        }
        manageFriendTask.addOnCompleteListener(new OnCompleteListener<UserInfo>() {
            @Override
            public void onComplete(@NonNull Task<UserInfo> task) {
                if(task.isSuccessful()){
                    user.setaFriend(!user.isaFriend());
                }
            }
        });
        EventBus.getDefault().post(new FragmentEvent(initiatorFragment, true));
    }

    @OnClick(R.id.follow_button)
    public void followButton(){
        Task<UserInfo> changeFollowTask;
        if(user.isiFollowHim()){ // I am friends this user, un-follow him
            changeFollowTask = FriendsManager.getInstance().unFollowUser(user.getUserId());
        } else { // not friends, start friends this user
            changeFollowTask = FriendsManager.getInstance().followUser(user.getUserId());
        }
        changeFollowTask.addOnCompleteListener(new OnCompleteListener<UserInfo>() {
            @Override
            public void onComplete(@NonNull Task<UserInfo> task) {
                if(task.isSuccessful()){
                    user.setiFollowHim(!user.isiFollowHim());
                }
            }
        });
        EventBus.getDefault().post(new FragmentEvent(initiatorFragment, true));
    }
}
