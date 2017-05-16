package tv.sportssidekick.sportssidekick.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.provider.VoicemailContract;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.BiMap;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.sportssidekick.sportssidekick.Constant;
import tv.sportssidekick.sportssidekick.GSAndroidPlatform;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.MenuAdapter;
import tv.sportssidekick.sportssidekick.adapter.SideMenuAdapter;
import tv.sportssidekick.sportssidekick.events.NotificationReceivedEvent;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.fragment.FragmentOrganizer;
import tv.sportssidekick.sportssidekick.fragment.instance.ChatFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.ClubRadioFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.ClubTVFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.NewsFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.RumoursFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.StatisticsFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.StoreFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.VideoChatFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.WallFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.AddFriendFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.AlertDialogFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.CreateChatFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.EditProfileFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.FollowersFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.FollowingFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.FriendRequestsFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.FriendsFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.InviteFriendFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.JoinChatFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.LanguageFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.LoginFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.MemberInfoFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.SignUpFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.StartingNewCallFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.StashFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.WalletFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.YourProfileFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.YourStatementFragment;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.notifications.ExternalNotificationEvent;
import tv.sportssidekick.sportssidekick.model.notifications.InternalNotificationManager;
import tv.sportssidekick.sportssidekick.model.purchases.PurchaseModel;
import tv.sportssidekick.sportssidekick.model.sharing.NativeShareEvent;
import tv.sportssidekick.sportssidekick.model.sharing.SharingManager;
import tv.sportssidekick.sportssidekick.model.ticker.NewsTickerInfo;
import tv.sportssidekick.sportssidekick.model.ticker.NextMatchModel;
import tv.sportssidekick.sportssidekick.model.user.LoginStateReceiver;
import tv.sportssidekick.sportssidekick.model.user.UserEvent;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.model.videoChat.VideoChatEvent;
import tv.sportssidekick.sportssidekick.model.videoChat.VideoChatModel;
import tv.sportssidekick.sportssidekick.util.SoundEffects;
import tv.sportssidekick.sportssidekick.util.Utility;
import tv.sportssidekick.sportssidekick.util.ui.LinearItemDecoration;
import tv.sportssidekick.sportssidekick.util.ui.NavigationDrawerItems;
import tv.sportssidekick.sportssidekick.util.ui.NoScrollRecycler;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static tv.sportssidekick.sportssidekick.util.Utility.checkIfBundlesAreEqual;


public class PhoneLoungeActivity extends AppCompatActivity implements LoginStateReceiver.LoginStateListener, SideMenuAdapter.IDrawerCloseSideMeny, MenuAdapter.IDrawerClose {

    public static final String TAG = "Lounge Activity";
    @BindView(R.id.activity_main)
    View rootView;
    @BindView(R.id.menu_recycler_view)
    RecyclerView menu_recycler_view;
    @BindView(R.id.drawer_container)
    PercentRelativeLayout drawerContainer;
    @BindView(R.id.fragment_holder)
    View fragment_holder;
    @BindView(R.id.side_menu_recycler)
    NoScrollRecycler sideMenuRecycler;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.scrolling_news_title)
    TextView newsLabel;
    @BindView(R.id.caption)
    TextView captionLabel;
    FragmentOrganizer fragmentOrganizer;

    ArrayList<Class> popupContainerFragments;
    ArrayList<Class> slidePopupContainerFragments;

    BiMap<Integer, Class> radioButtonsFragmentMap;

    CallbackManager callbackManager;
    ShareDialog facebookShareDialog;
    SideMenuAdapter sideMenuAdapter;
    MenuAdapter menuAdapter;
    private LoginStateReceiver loginStateReceiver;
    int screenWidth;
    @OnClick(R.id.notification_open)
    public void notificationOpen(){

    }

    @OnClick(R.id.friends_open)
    public void friendsOpen(){
        EventBus.getDefault().post(new FragmentEvent(FriendsFragment.class));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // Utility.assignTheme(this);
        setContentView(R.layout.activity_lounge_phone);
        ButterKnife.bind(this);
        this.loginStateReceiver = new LoginStateReceiver(this);
        Model.getInstance().initialize(this);


        setNumberOfNotification("4");
        setupFragments();

        VideoChatModel.getInstance();

        callbackManager = CallbackManager.Factory.create();
        facebookShareDialog = new ShareDialog(this);
        // internal notifications initialization
        InternalNotificationManager.getInstance();
        // this part is optional
        facebookShareDialog.registerCallback(callbackManager, SharingManager.getInstance());

        PurchaseModel.getInstance().onCreate(this);

        setToolbar();
//        profileImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Utility.changeTheme(PhoneLoungeActivity.this);
//
//            }
//        });
    }


    private void setToolbar() {
        NavigationDrawerItems.getInstance().generateList(1);
        menuAdapter = new MenuAdapter(this, this);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth = (int) (displaymetrics.widthPixels * 0.5);
        sideMenuAdapter = new SideMenuAdapter(this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        sideMenuRecycler.setLayoutManager(layoutManager);
        sideMenuRecycler.setAdapter(sideMenuAdapter);
        drawerContainer.setLayoutParams(new RelativeLayout.LayoutParams(screenWidth, DrawerLayout.LayoutParams.MATCH_PARENT));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        double space = Utility.getDisplayWidth(this);
        space = space * 0.005;
        menu_recycler_view.addItemDecoration(new LinearItemDecoration((int) space, true, false));
        menu_recycler_view.setLayoutManager(gridLayoutManager);
        menu_recycler_view.setAdapter(menuAdapter);

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                sideMenuRecycler.setTranslationY(slideOffset * +(float) (screenWidth * 0.22));
                fragment_holder.setPivotY(1);
                fragment_holder.setTranslationY(slideOffset * +(float) (screenWidth * 0.22));
                drawerLayout.bringChildToFront(drawerView);
                drawerLayout.requestLayout();
            }

            @Override
            public void onDrawerOpened(View drawerView) {


            }

            @Override
            public void onDrawerClosed(View drawerView) {
                //   getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

    }


    private void setupFragments() {
        fragmentOrganizer = new FragmentOrganizer(getSupportFragmentManager(), WallFragment.class);

//        ArrayList<Class> leftContainerFragments = new ArrayList<>();
//        leftContainerFragments.add(WallFragment.class);
//        leftContainerFragments.add(VideoChatFragment.class);
//        leftContainerFragments.add(NewsFragment.class);
//        leftContainerFragments.add(RumoursFragment.class);
//        leftContainerFragments.add(StoreFragment.class);
//        leftContainerFragments.add(NewsItemFragment.class);
//        leftContainerFragments.add(WallItemFragment.class);
//        fragmentOrganizer.setUpContainer(R.id.tabs_container_1, leftContainerFragments);

//        ArrayList<Class> topRightContainerFragments = new ArrayList<>();
//        topRightContainerFragments.add(ChatFragment.class);
//        topRightContainerFragments.add(StatisticsFragment.class);
//        topRightContainerFragments.add(FantasyFragment.class);
//        topRightContainerFragments.add(QuizFragment.class);
//        fragmentOrganizer.setUpContainer(R.id.tabs_container_top_right, topRightContainerFragments);

//        ArrayList<Class> bottomRightContainerFragments = new ArrayList<>();
//        bottomRightContainerFragments.add(ClubTVFragment.class);
//        bottomRightContainerFragments.add(ClubTvPlaylistFragment.class);
//        bottomRightContainerFragments.add(ClubRadioFragment.class);
//        bottomRightContainerFragments.add(YoutubePlayerFragment.class);
//        bottomRightContainerFragments.add(ClubRadioStationFragment.class);
//        fragmentOrganizer.setUpContainer(R.id.bottom_right_container, bottomRightContainerFragments);

        popupContainerFragments = new ArrayList<>();
        popupContainerFragments.add(AlertDialogFragment.class);
        popupContainerFragments.add(YourProfileFragment.class);
        popupContainerFragments.add(StashFragment.class);
        popupContainerFragments.add(YourStatementFragment.class);
        popupContainerFragments.add(WalletFragment.class);
        popupContainerFragments.add(LanguageFragment.class);
        popupContainerFragments.add(FriendsFragment.class);
        popupContainerFragments.add(FriendRequestsFragment.class);
        popupContainerFragments.add(StartingNewCallFragment.class);
        popupContainerFragments.add(EditProfileFragment.class);
        popupContainerFragments.add(LoginFragment.class);
        popupContainerFragments.add(SignUpFragment.class);
        popupContainerFragments.add(MemberInfoFragment.class);
        popupContainerFragments.add(FollowersFragment.class);
        popupContainerFragments.add(FollowingFragment.class);
        popupContainerFragments.add(AddFriendFragment.class);
        popupContainerFragments.add(InviteFriendFragment.class);
        popupContainerFragments.add(WallFragment.class);
        popupContainerFragments.add(WallFragment.class);
        popupContainerFragments.add(ChatFragment.class);
        popupContainerFragments.add(NewsFragment.class);
        popupContainerFragments.add(StatisticsFragment.class);
        popupContainerFragments.add(RumoursFragment.class);
        popupContainerFragments.add(ClubRadioFragment.class);
        popupContainerFragments.add(StoreFragment.class);
        popupContainerFragments.add(ClubTVFragment.class);
        popupContainerFragments.add(VoicemailContract.class);
        popupContainerFragments.add(VideoChatFragment.class);

        fragmentOrganizer.setUpContainer(R.id.fragment_holder, popupContainerFragments);

        //Fragments that slides in
//        slidePopupContainerFragments = new ArrayList<>();
//        slidePopupContainerFragments.add(CreateChatFragment.class);
//        slidePopupContainerFragments.add(JoinChatFragment.class);
//        slidePopupContainerFragments.add(EditChatFragment.class);
//        fragmentOrganizer.setUpContainer(R.id.popup_holder_right, slidePopupContainerFragments, true);

//        radioButtonsFragmentMap = HashBiMap.create();
//        radioButtonsFragmentMap.put(R.id.wall_radio_button, WallFragment.class);
//        radioButtonsFragmentMap.put(R.id.video_chat_radio_button, VideoChatFragment.class);
//        radioButtonsFragmentMap.put(R.id.news_radio_button, NewsFragment.class);
//        radioButtonsFragmentMap.put(R.id.roumors_radio_button, RumoursFragment.class);
//        radioButtonsFragmentMap.put(R.id.chat_radio_button, ChatFragment.class);
//        radioButtonsFragmentMap.put(R.id.stats_radio_button, StatisticsFragment.class);
//        radioButtonsFragmentMap.put(R.id.fantasy_radio_button, FantasyFragment.class);
//        radioButtonsFragmentMap.put(R.id.quiz_radio_button, QuizFragment.class);
//        radioButtonsFragmentMap.put(R.id.club_tv_radio_button, ClubTVFragment.class);
//        radioButtonsFragmentMap.put(R.id.club_radio_radio_button, ClubRadioFragment.class);
//        radioButtonsFragmentMap.put(R.id.shop_radio_button, StoreFragment.class);

        // FIXME This will trigger sound?
        EventBus.getDefault().post(new FragmentEvent(WallFragment.class));
//        EventBus.getDefault().post(new FragmentEvent(ChatFragment.class));
//        EventBus.getDefault().post(new FragmentEvent(ClubTVFragment.class));
//        ((RadioButton) ButterKnife.findById(this, R.id.wall_radio_button)).setChecked(true);
//        ((RadioButton) ButterKnife.findById(this, R.id.chat_radio_button)).setChecked(true);
//        ((RadioButton) ButterKnife.findById(this, R.id.club_tv_radio_button)).setChecked(true);

//        profileButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EventBus.getDefault().post(new FragmentEvent(LoginFragment.class));
//            }
//        });
    }

    @Override
    public void onStart() {
        super.onStart();
        GSAndroidPlatform.gs().start();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe
    public void onFragmentEvent(FragmentEvent event) {
        if (event.isReturning()) {
            SoundEffects.getDefault().playSound(SoundEffects.ROLL_OVER);
        } else {
            SoundEffects.getDefault().playSound(SoundEffects.SUBTLE);
        }
        if (popupContainerFragments.contains(event.getType())) {
            // this is popup event

        } else if (slidePopupContainerFragments.contains(event.getType())) {
            showSlidePopupFragmentContainer();
        } else {
            if (radioButtonsFragmentMap.inverse().containsKey(event.getType())) {
                // Detect which radio button was clicked and fetch what Fragment should be opened
                int radioButtonId = radioButtonsFragmentMap.inverse().get(event.getType());
                for (int id : radioButtonsFragmentMap.keySet()) {
                    RadioButton button = ButterKnife.findById(this, radioButtonId);
                    if (radioButtonId == id) {
                        button.setChecked(true);
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NextMatchModel.getInstance().getNextMatchInfo();
        checkAndEmitBackgroundNotification();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Detect which radio button was clicked and fetch what Fragment should be opened
        if (checked && radioButtonsFragmentMap.containsKey(view.getId())) {
            Class fragmentType = radioButtonsFragmentMap.get(view.getId());
            EventBus.getDefault().post(new FragmentEvent(fragmentType));
        }
    }

    @Override
    public void onDestroy() {
        fragmentOrganizer.freeUpResources();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().unregister(loginStateReceiver);
        PurchaseModel.getInstance().onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        SoundEffects.getDefault().playSound(SoundEffects.ROLL_OVER);
        if (fragmentOrganizer.getOpenFragment() instanceof JoinChatFragment ||
                fragmentOrganizer.getOpenFragment() instanceof CreateChatFragment) {
            hideSlidePopupFragmentContainer();
        }
        if (fragmentOrganizer.handleNavigationFragment()) {
            menuAdapter.notifyDataSetChanged();
            sideMenuAdapter.notifyDataSetChanged();
        }
        if (!fragmentOrganizer.handleBackNavigation()) {
            finish();
        } else {

        }
    }

    @Subscribe
    public void onTickerUpdate(NewsTickerInfo newsTickerInfo) {
        newsLabel.setText(newsTickerInfo.getNews().get(0));
        long timestamp = Long.parseLong(newsTickerInfo.getMatchDate());

        long getDaysUntilMatch = Utility.getDaysUntilMatch(timestamp);
        Resources res = getResources();
        String daysValue = res.getQuantityString(R.plurals.days_until_match, (int) getDaysUntilMatch, (int) getDaysUntilMatch);

        captionLabel.setText(newsTickerInfo.getTitle());

        startNewsTimer(newsTickerInfo);
    }

    @Subscribe
    public void onShareOnFacebookEvent(ShareLinkContent linkContent) {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            facebookShareDialog.show(linkContent);
        }
    }

    @Subscribe
    public void onShareOnTwitterEvent(TweetComposer.Builder event) {
        event.show();
    }

    @Subscribe
    public void onShareNativeEvent(NativeShareEvent event) {
        startActivity(Intent.createChooser(event.getIntent(), getResources().getString(R.string.share_using)));
    }


    Timer newsTimer;
    int count;

    private void startNewsTimer(final NewsTickerInfo newsTickerInfo) {
        count = 0;
        if (newsTimer != null) {
            newsTimer.cancel();
        }
        newsTimer = new Timer();
        newsTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        newsLabel.setText(newsTickerInfo.getNews().get(count));
                        if (++count == newsTickerInfo.getNews().size()) {
                            count = 0;
                        }
                    }
                });
            }
        }, 0, Constant.LOGIN_TEXT_TIME);

    }

    private void setYourCoinsValue(String value) {
        //yourCoinsValue.setText(value + " $$K");
    }

    public void onProfileClicked(View view) {
        EventBus.getDefault().post(new FragmentEvent(YourProfileFragment.class));
    }
//
//    public void onFollowersButtonClick(View view) {
//        EventBus.getDefault().post(new FragmentEvent(FollowingFragment.class));
//    }

    private void setNumberOfNotification(String number) {
        //notificationNumber.setText(number);
    }

    public void hideSlidePopupFragmentContainer() {
        popupSlideFragmentContainerVisibility(View.GONE, R.anim.slide_in_right);
    }

    public void showSlidePopupFragmentContainer() {
        popupSlideFragmentContainerVisibility(View.VISIBLE, R.anim.slide_in_left);
    }

    Animation animation;

    private void popupSlideFragmentContainerVisibility(int visibility, int anim) {
        animation = AnimationUtils.loadAnimation(this, anim);
        // slideFragmentContainer.startAnimation(animation);
        //   slideFragmentContainer.setVisibility(visibility);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        PurchaseModel.getInstance().onActivityResult(requestCode, resultCode, data);

    }

    @Subscribe
    public void onNewNotification(NotificationReceivedEvent event) {
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.notification_row, null);

        TextView title = (TextView) v.findViewById(R.id.notification_title);
        title.setText(event.getTitle());

        TextView description = (TextView) v.findViewById(R.id.notification_description);
        description.setText(event.getDescription());

        switch (event.getType()) {
            case 1:
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new FragmentEvent(FriendsFragment.class));
                    }
                });
                break;
            case 2:
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new FragmentEvent(FollowersFragment.class));
                    }
                });
                break;
            case 3:
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new FragmentEvent(WalletFragment.class));
                    }
                });
                break;
        }
        showNotification(v, event.getCloseTime());
    }

    private void showNotification(final View v, int time) {
        // notificationContainer.addView(v, 0);

        Animation animationShow = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_left);
        v.startAnimation(animationShow);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                hideNotification(v);
            }
        }, time * 1000);
    }

    private void hideNotification(final View v) {
        Animation animationHide = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_left);
        v.startAnimation(animationHide);
        animationHide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
//                notificationContainer.removeView(v);
                v.setVisibility(View.GONE);
            }
        });
    }

    Bundle savedIntentData = null;

    private void checkAndEmitBackgroundNotification() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null && !extras.isEmpty()) {
            if (savedIntentData == null) {
                savedIntentData = new Bundle();
            }
            if (!checkIfBundlesAreEqual(savedIntentData, extras)) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, String> notificationData = mapper.convertValue(extras.getString(Constant.NOTIFICATION_DATA, ""), new TypeReference<Map<String, String>>() {
                });
                handleNotificationEvent(new ExternalNotificationEvent(notificationData, false));
                savedIntentData = getIntent().getExtras();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleNotificationEvent(ExternalNotificationEvent event) {
        Map<String, String> notificationData = event.getData();
        if (event.isFromBackground()) { // we ignore notifications that are received while app is active
            if (notificationData.containsKey("chatId")) {
                EventBus.getDefault().post(new FragmentEvent(ChatFragment.class));
            } else if (notificationData.containsKey("wallId")) {
                EventBus.getDefault().post(new FragmentEvent(WallFragment.class));
            }
        }
    }

    @Override
    public void onLogout() {
        //resetUserDetails();
    }

    @Override
    public void onLoginAnonymously() {
        //   resetUserDetails();
    }

    @Override
    public void onLogin(UserInfo user) {
        if (Model.getInstance().getLoggedInUserType() == Model.LoggedInUserType.REAL) {
            if (user.getCircularAvatarUrl() != null) {
                //  ImageLoader.getInstance().displayImage(user.getCircularAvatarUrl(), profileImage, Utility.getImageOptionsForUsers());
            }
            if (user.getFirstName() != null && user.getLastName() != null) {
                // profileName.setText(user.getFirstName() + " " + user.getLastName());
            }
            setYourCoinsValue(String.valueOf(Model.getInstance().getUserInfo().getCurrency()));
//            yourLevel.setVisibility(View.VISIBLE);
//            userLevelBackground.setVisibility(View.VISIBLE);
//            userLevelProgress.setVisibility(View.VISIBLE);
//            yourLevel.setText(String.valueOf(user.getLevel()));
//            userLevelProgress.setProgress((int) (user.getProgress() * userLevelProgress.getMax()));
//
//            profileButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    EventBus.getDefault().post(new FragmentEvent(YourProfileFragment.class));
//                }
//            });
        } else {
            // resetUserDetails();
        }
    }

//    private void resetUserDetails(){
//        //reset profile name and picture to blank values
//        setYourCoinsValue(String.valueOf(0));
//        yourLevel.setVisibility(View.INVISIBLE);
//        userLevelBackground.setVisibility(View.INVISIBLE);
//        userLevelProgress.setVisibility(View.INVISIBLE);
//        profileName.setText("Login / Signup");
//        String imgUri = "drawable://" + getResources().getIdentifier("blank_profile_rounded", "drawable", this.getPackageName());
//        ImageLoader.getInstance().displayImage(imgUri, profileImage, Utility.imageOptionsImageLoader());
//        profileButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EventBus.getDefault().post(new FragmentEvent(LoginFragment.class));
//            }
//        });
//    }

    @Override
    public void onLoginError(Error error) {

    }

    @Subscribe
    public void onVideoChatEvent(VideoChatEvent event) {
        if (!(fragmentOrganizer.getOpenFragment() instanceof VideoChatFragment)) {
            EventBus.getDefault().post(new FragmentEvent(VideoChatFragment.class));
            VideoChatModel.getInstance().setVideoChatEvent(event);
        } else {
            ((VideoChatFragment) fragmentOrganizer.getOpenFragment()).onVideoChatEvent(event);
        }
    }

    public FragmentOrganizer getFragmentOrganizer() {
        return fragmentOrganizer;
    }

    @Subscribe
    public void updateUserName(UserEvent event) {
        if (event.getType() == UserEvent.Type.onDetailsUpdated) {
            setYourCoinsValue(String.valueOf(Model.getInstance().getUserInfo().getCurrency()));
        }

        UserInfo user = event.getUserInfo();
//        if(user!=null){
//            if (user.getFirstName() != null && user.getLastName() != null) {
//                profileName.setText(user.getFirstName() + " " + user.getLastName());
//            }
//        }
    }


    @Override
    public void closeDrawerMenu(int position) {
        NavigationDrawerItems.getInstance().setByPosition(position);
        drawerLayout.closeDrawer(GravityCompat.END);
        sideMenuAdapter.notifyDataSetChanged();
        if (position > 3) {
            menuAdapter.notifyItemRangeChanged(0, 3);
        }
    }

    @Override
    public void closeDrawerSideMenu(int position, boolean openDrawer) {
        if (openDrawer) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            drawerLayout.openDrawer(GravityCompat.END);


        } else {
            NavigationDrawerItems.getInstance().setByPosition(position);
            menuAdapter.notifyDataSetChanged();

        }
    }
}