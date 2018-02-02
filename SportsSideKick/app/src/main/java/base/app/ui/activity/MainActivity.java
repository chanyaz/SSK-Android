package base.app.ui.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import base.app.BuildConfig;
import base.app.R;
import base.app.data.Model;
import base.app.data.tutorial.TutorialModel;
import base.app.data.user.LoginStateReceiver;
import base.app.data.user.UserEvent;
import base.app.data.user.UserInfo;
import base.app.ui.adapter.menu.MenuAdapter;
import base.app.ui.adapter.menu.SideMenuAdapter;
import base.app.ui.fragment.base.BaseFragment;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.ui.fragment.base.FragmentOrganizer;
import base.app.ui.fragment.content.NewsFragment;
import base.app.ui.fragment.content.NewsItemFragment;
import base.app.ui.fragment.content.RumoursFragment;
import base.app.ui.fragment.content.SocialFragment;
import base.app.ui.fragment.content.StoreFragment;
import base.app.ui.fragment.content.TicketsFragment;
import base.app.ui.fragment.content.WallFragment;
import base.app.ui.fragment.content.WallItemFragment;
import base.app.ui.fragment.other.ChatFragment;
import base.app.ui.fragment.other.StatisticsFragment;
import base.app.ui.fragment.popup.AddFriendFragment;
import base.app.ui.fragment.popup.AlertDialogFragment;
import base.app.ui.fragment.popup.CreateChatFragment;
import base.app.ui.fragment.popup.EditChatFragment;
import base.app.ui.fragment.popup.EditProfileFragment;
import base.app.ui.fragment.popup.FollowersFragment;
import base.app.ui.fragment.popup.FollowingFragment;
import base.app.ui.fragment.popup.FriendFragment;
import base.app.ui.fragment.popup.FriendRequestsFragment;
import base.app.ui.fragment.popup.FriendsFragment;
import base.app.ui.fragment.popup.InviteFriendFragment;
import base.app.ui.fragment.popup.JoinChatFragment;
import base.app.ui.fragment.popup.LanguageFragment;
import base.app.ui.fragment.popup.LoginFragment;
import base.app.ui.fragment.popup.ProfileFragment;
import base.app.ui.fragment.popup.SignUpFragment;
import base.app.ui.fragment.popup.SignUpLoginFragment;
import base.app.ui.fragment.popup.StartingNewCallFragment;
import base.app.ui.fragment.popup.StashFragment;
import base.app.ui.fragment.popup.WalletFragment;
import base.app.ui.fragment.popup.YourStatementFragment;
import base.app.ui.fragment.popup.post.PostCreateFragment;
import base.app.ui.fragment.stream.ClubRadioFragment;
import base.app.ui.fragment.stream.ClubRadioStationFragment;
import base.app.ui.fragment.stream.ClubTVFragment;
import base.app.ui.fragment.stream.ClubTvPlaylistFragment;
import base.app.ui.fragment.stream.VideoChatFragment;
import base.app.ui.fragment.stream.YoutubePlayerFragment;
import base.app.util.commons.Constant;
import base.app.util.commons.SoundEffects;
import base.app.util.commons.Utility;
import base.app.util.ui.BlurBuilder;
import base.app.util.ui.ImageLoader;
import base.app.util.ui.LinearItemDecoration;
import base.app.util.ui.NavigationDrawerItems;
import base.app.util.ui.NoScrollRecycler;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends BaseActivity
        implements LoginStateReceiver.LoginStateListener,
        SideMenuAdapter.IDrawerCloseSideMenu,
        MenuAdapter.IDrawerClose {

    public static final String TAG = "Lounge Activity";

    @BindView(R.id.activity_main)
    View rootView;
    @BindView(R.id.menu_recycler_view)
    RecyclerView menuRecyclerView;
    @BindView(R.id.drawer_container)
    PercentRelativeLayout drawerContainer;
    @BindView(R.id.fragment_left_popup_holder)
    View fragmentLeftPopupHolder;
    @BindView(R.id.fragment_holder)
    View fragmentHolder;
    @BindView(R.id.bottom_menu_recycler_view)
    NoScrollRecycler sideMenuRecycler;
    @BindView(R.id.logo)
    ImageView logoImageView;
    @BindView(R.id.fragment_popup_holder)
    View popupHolder;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.user_level_progress)
    ProgressBar userLevelProgress;
    @BindView(R.id.your_coins_value)
    TextView yourCoinsValue;
    @BindView(R.id.user_level)
    TextView yourLevel;
    @BindView(R.id.profile_image)
    ImageView profileImage;
    @BindView(R.id.user_level_background)
    ImageView userLevelBackground;
    @BindView(R.id.left_top_bar_container)
    RelativeLayout barContainer;
    @BindView(R.id.fragment_tv_container)
    LinearLayout tvContainer;
    @BindView(R.id.fragment_radio_container)
    LinearLayout radioContainer;
    @BindView(R.id.notification_open)
    ImageView notificationIcon;
    @BindView(R.id.friends_open)
    ImageView friendsIcon;
    @BindView(R.id.blurred_background)
    RelativeLayout blurredBackground;
    @BindView(R.id.splash)
    View splash;
    @BindView(R.id.splashBackgroundImage)
    ImageView splashBackgroundImage;

    SideMenuAdapter sideMenuAdapter;
    MenuAdapter menuAdapter;
    int screenWidth;

    ArrayList<Class> popupContainerFragments;
    ArrayList<Class> popupLeftFragments;
    ArrayList<Class> popupDialogFragments;
    ArrayList<Class> youtubePlayer;
    ArrayList<Class> youtubeList;
    ArrayList<Class> radioList;
    ArrayList<Class> radioPlayerList;

    @OnClick(R.id.notification_open)
    public void notificationOpen() {
    }

    @OnClick(R.id.friends_open)
    public void friendsOpen() {
        EventBus.getDefault().post(new FragmentEvent(FriendsFragment.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCrashlytics();

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        loginStateReceiver = new LoginStateReceiver(this);

        setupFragments();
        setToolbar();
        updateTopBar();

        Glide.with(this).load(R.drawable.sportingportugal).into(logoImageView);
        Glide.with(this).load(R.drawable.video_chat_background).into(splashBackgroundImage);
    }

    private void initCrashlytics() {
        // Set up Crashlytics, disabled for debug builds
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();

        // Initialize Fabric with the debug-disabled crashlytics.
        Fabric.with(this, crashlyticsKit);
    }

    public void updateTopBar() {
        int visibility = Model.getInstance().isRealUser() ? View.VISIBLE : View.GONE;
        friendsIcon.setVisibility(visibility);
    }

    private void setToolbar() {
        NavigationDrawerItems.getInstance().generateList(1);
        menuAdapter = new MenuAdapter(this, this);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
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
        menuRecyclerView.addItemDecoration(new LinearItemDecoration((int) space, true, false));
        menuRecyclerView.setLayoutManager(gridLayoutManager);
        menuRecyclerView.setAdapter(menuAdapter);

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    private void setupFragments() {
        fragmentOrganizer = new FragmentOrganizer(getSupportFragmentManager(), WallFragment.class);

        ArrayList<Class> mainContainerFragments = new ArrayList<>();
        mainContainerFragments.add(WallFragment.class);
        mainContainerFragments.add(ChatFragment.class);
        mainContainerFragments.add(NewsFragment.class);
        mainContainerFragments.add(SocialFragment.class);
        mainContainerFragments.add(StatisticsFragment.class);
        mainContainerFragments.add(RumoursFragment.class);
        mainContainerFragments.add(StoreFragment.class);
        mainContainerFragments.add(VideoChatFragment.class);
        mainContainerFragments.add(TicketsFragment.class);
        fragmentOrganizer.setUpContainer(R.id.fragment_holder, mainContainerFragments);

        popupContainerFragments = new ArrayList<>();
        popupContainerFragments.add(ProfileFragment.class);
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
        popupContainerFragments.add(FriendFragment.class);
        popupContainerFragments.add(FollowersFragment.class);
        popupContainerFragments.add(FollowingFragment.class);
        popupContainerFragments.add(AddFriendFragment.class);
        popupContainerFragments.add(InviteFriendFragment.class);
        popupContainerFragments.add(SignUpLoginFragment.class);
        popupContainerFragments.add(CreateChatFragment.class);
        fragmentOrganizer.setUpContainer(R.id.fragment_popup_holder, popupContainerFragments, true);

        popupDialogFragments = new ArrayList<>();
        popupDialogFragments.add(AlertDialogFragment.class);
        fragmentOrganizer.setUpContainer(R.id.fragment_dialog, popupDialogFragments, true);

        popupLeftFragments = new ArrayList<>();
        popupLeftFragments.add(EditChatFragment.class);
        popupLeftFragments.add(JoinChatFragment.class);
        popupLeftFragments.add(WallItemFragment.class);
        popupLeftFragments.add(NewsItemFragment.class);
        popupLeftFragments.add(PostCreateFragment.class);
        fragmentOrganizer.setUpContainer(R.id.fragment_left_popup_holder, popupLeftFragments, true);

        youtubeList = new ArrayList<>();
        youtubeList.add(ClubTVFragment.class);
        youtubeList.add(ClubTvPlaylistFragment.class);
        youtubeList.add(ClubRadioFragment.class);
        fragmentOrganizer.setUpContainer(R.id.play_list_holder, youtubeList, false);

        radioList = new ArrayList<>();
        radioList.add(ClubRadioFragment.class);
        fragmentOrganizer.setUpContainer(R.id.radio_list_holder, radioList, false);

        youtubePlayer = new ArrayList<>();
        youtubePlayer.add(YoutubePlayerFragment.class);
        fragmentOrganizer.setUpContainer(R.id.youtube_holder, youtubePlayer, true);

        radioPlayerList = new ArrayList<>();
        radioPlayerList.add(ClubRadioStationFragment.class);
        fragmentOrganizer.setUpContainer(R.id.radio_holder, radioPlayerList, true);
        // FIXME This will trigger sound?
        EventBus.getDefault().post(new FragmentEvent(WallFragment.class));
    }

    @Subscribe
    public void onFragmentEvent(FragmentEvent event) {
        // play different sound on fragment transition
        if (event.isReturning()) {
            SoundEffects.getDefault().playSound(SoundEffects.ROLL_OVER);
        } else {
            SoundEffects.getDefault().playSound(SoundEffects.SUBTLE);
        }

        // stop playing radio when fragment is changed ( TODO Wrong place to do this? )
        if (fragmentOrganizer.getCurrentFragment().getClass() == ClubRadioStationFragment.class) {
            ((ClubRadioStationFragment) fragmentOrganizer.getCurrentFragment()).stopPlaying();
        }

        // make tv container show or hidden
        if (youtubeList.contains(event.getType()) || youtubePlayer.contains(event.getType())) {
            tvContainer.setVisibility(View.VISIBLE);
        } else {
            tvContainer.setVisibility(View.GONE);
        }

        // make radio container show or hidden
        if (radioList.contains(event.getType()) || radioPlayerList.contains(event.getType())) {
            radioContainer.setVisibility(View.VISIBLE);
        } else {
            radioContainer.setVisibility(View.GONE);
        }

        if (popupLeftFragments.contains(event.getType())) {
            // this is popup event - coming from left
            fragmentLeftPopupHolder.setVisibility(View.VISIBLE);
            barContainer.setVisibility(View.INVISIBLE);
            popupHolder.setVisibility(View.INVISIBLE);
        } else if (popupContainerFragments.contains(event.getType())) {
            // this is popup event
            fragmentLeftPopupHolder.setVisibility(View.INVISIBLE);
            barContainer.setVisibility(View.VISIBLE);
            popupHolder.setVisibility(View.VISIBLE);
        } else if (popupDialogFragments.contains(event.getType())) {
            // this is popup event - Alert Dialog
            if (fragmentLeftPopupHolder.getVisibility() == View.VISIBLE) {
                toggleBlur(true, fragmentLeftPopupHolder);
            } else if (popupHolder.getVisibility() == View.VISIBLE) {
                //fragmentLeftPopupHolder is show so we need to take photo from that layout
                toggleBlur(true, popupHolder);
            } else {
                //main fragment view
                toggleBlur(true, fragmentHolder);
            }
        } else {
            //main fragments
            fragmentLeftPopupHolder.setVisibility(View.INVISIBLE);
            barContainer.setVisibility(View.VISIBLE);
            popupHolder.setVisibility(View.INVISIBLE);
        }
    }

    public void toggleBlur(boolean visible, final View fragmentView) {
        if (visible) {
            blurredBackground.setVisibility(View.VISIBLE);
            if (rootView.getWidth() > 0) {
                Bitmap image = BlurBuilder.blur(fragmentView);
                blurredBackground.setBackground(new BitmapDrawable(this.getResources(), image));
                blurredBackground.getBackground().setAlpha(230);
            } else {
                rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Bitmap image = BlurBuilder.blur(fragmentView);
                        blurredBackground.setBackground(new BitmapDrawable(MainActivity.this.getResources(), image));
                        blurredBackground.getBackground().setAlpha(230);
                    }
                });
            }
        } else {
            blurredBackground.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentOrganizer.getPreviousFragment() == null) return;

        Class<? extends Fragment> previousFragment = fragmentOrganizer.getPreviousFragment().getClass();
        Class<? extends Fragment> penultimateFragment = fragmentOrganizer.getPenultimateFragment().getClass();
        Fragment currentFragment = fragmentOrganizer.getCurrentFragment();

        if (currentFragment.getClass() == NewsItemFragment.class) {
            if (previousFragment == ClubRadioFragment.class) {
                Fragment fragment = fragmentOrganizer.getCurrentFragment();
                View overlay = fragment.getView().findViewById(R.id.commentInputOverlay);
                if (overlay.getVisibility() == View.VISIBLE) {
                    overlay.setVisibility(View.GONE);
                    return;
                }
            }
        }

        toggleBlur(false, null); // hide blurred view;
        SoundEffects.getDefault().playSound(SoundEffects.ROLL_OVER);

        if (currentFragment instanceof FriendFragment) {
            Class initiator = ((BaseFragment) currentFragment).getInitiator();
            EventBus.getDefault().post(new FragmentEvent(initiator, true));
            return;
        }

        // Hiding fragments - TODO Instead of popping them from back stack?
        if (popupLeftFragments.contains(previousFragment)) {
            fragmentLeftPopupHolder.setVisibility(View.VISIBLE);
        } else {
            fragmentLeftPopupHolder.setVisibility(View.INVISIBLE);
        }

        if (previousFragment == YoutubePlayerFragment.class) {
            fragmentOrganizer.getCurrentFragment().getFragmentManager().popBackStack();
        }

        // Hiding terms on Sign up fragment - TODO Should be handled on fragment itself
        if (currentFragment instanceof SignUpFragment) {
            View termsHolder = ((SignUpFragment) currentFragment).getTermsHolder();
            if (termsHolder.getVisibility() == View.VISIBLE) {
                termsHolder.setVisibility(View.INVISIBLE);
                return;
            }
        }

        // make tv container show or hidden
        if (youtubeList.contains(previousFragment) || youtubePlayer.contains(previousFragment)) {
            tvContainer.setVisibility(View.VISIBLE);
        } else {
            tvContainer.setVisibility(View.GONE);
        }

        // make radio container show or hidden
        if (radioList.contains(previousFragment) || radioPlayerList.contains(previousFragment)) {
            radioContainer.setVisibility(View.VISIBLE);
        } else {
            radioContainer.setVisibility(View.GONE);
        }

        if (previousFragment == ClubRadioStationFragment.class && penultimateFragment == ClubRadioFragment.class) {
            NavigationDrawerItems.getInstance().setByPosition(5);
            fragmentOrganizer.getCurrentFragment().getFragmentManager().popBackStack();
            menuAdapter.notifyDataSetChanged();
            sideMenuAdapter.notifyDataSetChanged();
            if (drawerLayout.isDrawerOpen(GravityCompat.END))
                drawerLayout.closeDrawer(GravityCompat.END);
            tvContainer.setVisibility(View.VISIBLE);
            return;
        }

        if (previousFragment == YoutubePlayerFragment.class
                && penultimateFragment == ClubTVFragment.class
                && currentFragment instanceof YoutubePlayerFragment
                ) {
            NavigationDrawerItems.getInstance().setByPosition(7);
            menuAdapter.notifyDataSetChanged();
            sideMenuAdapter.notifyDataSetChanged();
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            }
            tvContainer.setVisibility(View.VISIBLE);
            return;
        }

        if (currentFragment instanceof YoutubePlayerFragment) {
            YoutubePlayerFragment youtubePlayerFragment = (YoutubePlayerFragment) currentFragment;
            if (youtubePlayerFragment.isFullScreen()) {
                youtubePlayerFragment.setFullScreen(false);
                return;
            }

            if (previousFragment == ClubTVFragment.class) {
                fragmentOrganizer.getCurrentFragment().getFragmentManager().popBackStack();
                fragmentOrganizer.getCurrentFragment().getFragmentManager().popBackStack();
                if (penultimateFragment == ClubRadioStationFragment.class) {
                    radioContainer.setVisibility(View.VISIBLE);
                    NavigationDrawerItems.getInstance().setByPosition(5);
                    menuAdapter.notifyDataSetChanged();
                    sideMenuAdapter.notifyDataSetChanged();
                    if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                        drawerLayout.closeDrawer(GravityCompat.END);
                    }
                    return;
                }
                for (int i = 0; i < Constant.PHONE_MENU_OPTIONS.size(); i++) {
                    if (penultimateFragment.equals(Constant.PHONE_MENU_OPTIONS.get(i))) {
                        NavigationDrawerItems.getInstance().setByPosition(i);
                        menuAdapter.notifyDataSetChanged();
                        sideMenuAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }
        }

        if (currentFragment.getClass() == ClubRadioStationFragment.class) {
            if (previousFragment == ClubRadioFragment.class) {
                fragmentOrganizer.getCurrentFragment().getFragmentManager().popBackStack();
                fragmentOrganizer.getCurrentFragment().getFragmentManager().popBackStack();
                if (penultimateFragment == YoutubePlayerFragment.class) {
                    tvContainer.setVisibility(View.VISIBLE);
                    NavigationDrawerItems.getInstance().setByPosition(7);
                    menuAdapter.notifyDataSetChanged();
                    sideMenuAdapter.notifyDataSetChanged();
                    if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                        drawerLayout.closeDrawer(GravityCompat.END);
                    }
                }
                for (int i = 0; i < Constant.PHONE_MENU_OPTIONS.size(); i++) {
                    if (penultimateFragment.equals(Constant.PHONE_MENU_OPTIONS.get(i))) {
                        NavigationDrawerItems.getInstance().setByPosition(i);
                        menuAdapter.notifyDataSetChanged();
                        sideMenuAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }
        }

        if (previousFragment == SignUpLoginFragment.class) {
            //EventBus.getDefault().createPost(new FragmentEvent(WallFragment.class, true));
            return;
        }
        if (popupHolder.getVisibility() == View.VISIBLE) {
            popupHolder.setVisibility(View.INVISIBLE);
        }
        if (barContainer.getVisibility() != View.VISIBLE) {
            barContainer.setVisibility(View.VISIBLE);
        }
        if (fragmentOrganizer.handleNavigationFragment()) {
            menuAdapter.notifyDataSetChanged();
            sideMenuAdapter.notifyDataSetChanged();
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            }
        } else {
            finish();
        }
    }

    private void setYourCoinsValue(String value) {
        yourCoinsValue.setText(value);
    }

    public void onProfileClicked(View view) {
        drawerLayout.closeDrawer(GravityCompat.END);
        if (Model.getInstance().isRealUser()) {
            EventBus.getDefault().post(new FragmentEvent(ProfileFragment.class));
        } else {
            EventBus.getDefault().post(new FragmentEvent(SignUpLoginFragment.class));
        }
    }

    @Override
    public void onLoginError(Error error) {
    }

    @Subscribe
    public void updateUserName(UserEvent event) {
        if (event.getType() == UserEvent.Type.onDetailsUpdated) {
            setYourCoinsValue(String.valueOf(Model.getInstance().getUserInfo().getCurrency()));
        }

    }

    @Override
    public void closeDrawerMenu(int position, boolean goodPosition) {
        if (goodPosition) {
            NavigationDrawerItems.getInstance().setByPosition(position);
            drawerLayout.closeDrawer(GravityCompat.END);
            sideMenuAdapter.notifyDataSetChanged();
            if (position > 3) {
                menuAdapter.notifyItemRangeChanged(0, 3);
            }
        } else {
            drawerLayout.closeDrawer(GravityCompat.END);
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

    @Override
    public void onLogout() {
        resetUserDetails();
        updateTopBar();
    }

    @Override
    public void onLoginAnonymously() {
        resetUserDetails();
        updateTopBar();
        Handler handler = new Handler();
        // delaying splash hiding to give enough time for login to be triggered
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                splash.setVisibility(View.GONE);
            }
        }, 3000);
    }

    @Override
    public void onLogin(UserInfo user) {
        if (Model.getInstance().isRealUser()) {
            String imgUri = "drawable://" + getResources().getIdentifier("blank_profile_rounded", "drawable", this.getPackageName());
            if (user.getCircularAvatarUrl() != null) {
                ImageLoader.displayImage(user.getCircularAvatarUrl(), profileImage, R.drawable.blank_profile_rounded);
            }
            setYourCoinsValue(String.valueOf(Model.getInstance().getUserInfo().getCurrency()));
            yourLevel.setVisibility(View.VISIBLE);
            yourLevel.setText(String.valueOf((int) user.getProgress()));
            userLevelBackground.setVisibility(View.VISIBLE);
            userLevelProgress.setVisibility(View.VISIBLE);
            userLevelProgress.setProgress((int) (user.getProgress() * userLevelProgress.getMax()));
            TutorialModel.getInstance().setUserId(Model.getInstance().getUserInfo().getUserId());
        } else {
            resetUserDetails();
        }
        updateTopBar();
        splash.setVisibility(View.GONE);
    }

    private void resetUserDetails() {
        setYourCoinsValue(String.valueOf(0));
        yourLevel.setVisibility(View.INVISIBLE);
        userLevelBackground.setVisibility(View.INVISIBLE);
        userLevelProgress.setVisibility(View.INVISIBLE);
        String imgUri = "drawable://" + getResources().getIdentifier("blank_profile_rounded", "drawable", this.getPackageName());
        ImageLoader.displayImage(imgUri, profileImage, R.drawable.blank_profile_rounded);
    }
}