package base.app.ui.fragment.popup;

import base.app.util.ui.BaseFragment;

/**
 * Created by Filip on 28/03/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */


public class FriendFragment extends BaseFragment {
/* TODO
    public static final double GRID_PERCENT_CELL_WIDTH_PHONE = 0.18;

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

    @Nullable
    @BindView(R.id.all_friends_recycler_view)
    RecyclerView allFriendsRecyclerView;
    @Nullable
    @BindView(R.id.in_common_recycler_view)
    RecyclerView inCommonRecyclerView;

    @BindView(R.id.chat_button_image)
    ImageView chatButtonImage;

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

    User user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);
        initiatorFragment = FriendsFragment.class;
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

        Task<User> getUserTask = AuthApi.getInstance().refreshUserInfo(getPrimaryArgument());
        getUserTask.addOnCompleteListener(onUserDataLoadedListener);
        return view;
    }

    private OnCompleteListener<User> onUserDataLoadedListener = new OnCompleteListener<User>() {
        @Override
        public void onComplete(@NonNull Task<User> task) {
            if (task.isSuccessful()) {
                user = task.getResult();
                setupUIWithUserInfo(user);
            }
        }
    };

    private void setupUIWithUserInfo(User user) {
        profileName.setText(user.getFirstName() + " " + user.getLastName());
        //TODO @Filip refactoring Put this in a  string file
        onlineStatus.setText(user.isOnline() ? "online" : "offline");
        ImageLoader.displayImage(user.getAvatar(), profileImage, null);
        progressBarCircle.setProgress((int) (user.getProgress() * progressBarCircle.getMax()));
        profileImageLevel.setText(String.valueOf((int)user.getProgress()));

        if (Utility.isPhone(getActivity())) {

            // setup buttons depending on friendship state
            if(user.isaFriend()){
                videoButton.setVisibility(View.VISIBLE);
                moreButton.setVisibility(View.VISIBLE);
                chatButtonPhoneCaption.setText(getContext().getResources().getString(R.string.chat));
                changeViewClickable(true, chatButtonImage);
            } else {
                if(user.isFriendPendingRequest()){
                    chatButtonPhoneCaption.setText(getContext().getResources().getString(R.string.friend_request_pending));
                    changeViewClickable(false, chatButtonImage);
                } else {
                    chatButtonPhoneCaption.setText(getContext().getResources().getString(R.string.friend_request_send));
                    changeViewClickable(true, chatButtonImage);
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

    private void getAllUserChats(final User user) {
        Task<List<User>> taskAllChats = FriendsManager.getInstance().getFriendsForUser(user.getUserId(), 0,30);
        taskAllChats.addOnCompleteListener(new OnCompleteListener<List<User>>() {
            @Override
            public void onComplete(@NonNull Task<List<User>> task) {
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

    private void getMutualFriendsListWithUser(final User user) {

        Task<List<User>> task = FriendsManager.getInstance().getMutualFriendsListWithUser(user.getUserId(), 0);
        task.addOnCompleteListener(new OnCompleteListener<List<User>>() {
            @Override
            public void onComplete(@NonNull Task<List<User>> task) {
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
        EventBus.getDefault().post(new FragmentEvent(initiatorFragment));
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

        List<User> selectedUsers = new ArrayList<>();
        selectedUsers.add(user);
        String chatName = user.getNicName() + " & " + AuthApi.getInstance().getUser().getNicName();

        ChatInfo newChatInfo = new ChatInfo();
        newChatInfo.setOwner(AuthApi.getInstance().getUser().getUserId());
        newChatInfo.setIsPublic(true);
        newChatInfo.setName(chatName);
        ArrayList<String> userIds = new ArrayList<>();
        for (User info : selectedUsers) {
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
        // TODO: Prevent duplication of default posts after removing a friend and going back to wall
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
            FriendsManager.getInstance().sendFriendRequest(user.getUserId()).addOnCompleteListener(new OnCompleteListener<User>() {
                @Override
                public void onComplete(@NonNull Task<User> task) {
                    if (task.isSuccessful()) {
                        user.setFriendPendingRequest(true);
                        setupUIWithUserInfo(user);
                    }
                }
            });
        }

    }

    private void removeUser(){
        FriendsManager.getInstance().deleteFriend(user.getUserId()).addOnCompleteListener(new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {
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
                ArrayList<User> usersToCall = new ArrayList<>();
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
        Task<User> changeFollowTask;
        if (user.isiFollowHim()) { // I am friends this user, un-follow him
            changeFollowTask = FriendsManager.getInstance().unFollowUser(user.getUserId());
        } else { // not friends, start friends this user
            changeFollowTask = FriendsManager.getInstance().followUser(user.getUserId());
        }
        changeFollowTask.addOnCompleteListener(new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {
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
*/
}
