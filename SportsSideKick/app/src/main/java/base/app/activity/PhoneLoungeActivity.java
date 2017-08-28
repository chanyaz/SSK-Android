package base.app.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.VoicemailContract;
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

import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import base.app.Constant;
import base.app.R;
import base.app.adapter.MenuAdapter;
import base.app.adapter.SideMenuAdapter;
import base.app.fragment.FragmentEvent;
import base.app.fragment.FragmentOrganizer;
import base.app.fragment.instance.ChatFragment;
import base.app.fragment.instance.ClubRadioFragment;
import base.app.fragment.instance.ClubRadioStationFragment;
import base.app.fragment.instance.ClubTVFragment;
import base.app.fragment.instance.ClubTvPlaylistFragment;
import base.app.fragment.instance.NewsFragment;
import base.app.fragment.instance.NewsItemFragment;
import base.app.fragment.instance.RumoursFragment;
import base.app.fragment.instance.StatisticsFragment;
import base.app.fragment.instance.StoreFragment;
import base.app.fragment.instance.VideoChatFragment;
import base.app.fragment.instance.WallFragment;
import base.app.fragment.instance.WallItemFragment;
import base.app.fragment.instance.YoutubePlayerFragment;
import base.app.fragment.popup.AddFriendFragment;
import base.app.fragment.popup.AlertDialogFragment;
import base.app.fragment.popup.CreateChatFragment;
import base.app.fragment.popup.EditChatFragment;
import base.app.fragment.popup.EditProfileFragment;
import base.app.fragment.popup.FollowersFragment;
import base.app.fragment.popup.FollowingFragment;
import base.app.fragment.popup.FriendRequestsFragment;
import base.app.fragment.popup.FriendsFragment;
import base.app.fragment.popup.InviteFriendFragment;
import base.app.fragment.popup.JoinChatFragment;
import base.app.fragment.popup.LanguageFragment;
import base.app.fragment.popup.LoginFragment;
import base.app.fragment.popup.MemberInfoFragment;
import base.app.fragment.popup.SignUpFragment;
import base.app.fragment.popup.SignUpLoginFragment;
import base.app.fragment.popup.StartingNewCallFragment;
import base.app.fragment.popup.StashFragment;
import base.app.fragment.popup.WalletFragment;
import base.app.fragment.popup.YourProfileFragment;
import base.app.fragment.popup.YourStatementFragment;
import base.app.model.Model;
import base.app.model.tutorial.TutorialModel;
import base.app.model.user.LoginStateReceiver;
import base.app.model.user.UserEvent;
import base.app.model.user.UserInfo;
import base.app.util.SoundEffects;
import base.app.util.Utility;
import base.app.util.ui.BlurBuilder;
import base.app.util.ui.LinearItemDecoration;
import base.app.util.ui.NavigationDrawerItems;
import base.app.util.ui.NoScrollRecycler;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PhoneLoungeActivity extends BaseActivity implements LoginStateReceiver.LoginStateListener, SideMenuAdapter.IDrawerCloseSideMenu, MenuAdapter.IDrawerClose {

    public static final String TAG = "Lounge Activity";
    @BindView(R.id.activity_main)
    View rootView;
    @BindView(R.id.menu_recycler_view)
    RecyclerView menu_recycler_view;
    @BindView(R.id.drawer_container)
    PercentRelativeLayout drawerContainer;
    @BindView(R.id.fragment_left_popup_holder)
    View fragmentLeftPopupHolder;
    @BindView(R.id.fragment_holder)
    View fragmentHolder;
    @BindView(R.id.side_menu_recycler)
    NoScrollRecycler sideMenuRecycler;

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

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
    ArrayList<Class> popupContainerFragments;
    SideMenuAdapter sideMenuAdapter;
    MenuAdapter menuAdapter;
    ArrayList<Class> popupLeftFragments;
    ArrayList<Class> popupDialogFragments;
    ArrayList<Class> youtubePlayer;
    ArrayList<Class> youtubeList;
    ArrayList<Class> radioList;
    ArrayList<Class> radioPlayerList;
    int screenWidth;

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

    @BindView(R.id.fragment_dialog)
    RelativeLayout popupDialogLayout;

    @BindView(R.id.splash)
    View splash;

    @OnClick(R.id.notification_open)
    public void notificationOpen() {

    }

    @OnClick(R.id.friends_open)
    public void friendsOpen() {
        EventBus.getDefault().post(new FragmentEvent(FriendsFragment.class));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lounge_phone);
        ButterKnife.bind(this);
        this.loginStateReceiver = new LoginStateReceiver(this);

        setupFragments();
        setToolbar();
        setMarginTop(false);
        updateTopBar();
        Utility.setSystemBarColor(this);
    }

    public void updateTopBar() {
        if (Model.getInstance().getLoggedInUserType() == Model.LoggedInUserType.REAL) {
            //Check if user is logged in
            notificationIcon.setVisibility(View.VISIBLE);
            friendsIcon.setVisibility(View.VISIBLE);
        } else {
            notificationIcon.setVisibility(View.GONE);
            friendsIcon.setVisibility(View.GONE);
        }
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
        menu_recycler_view.addItemDecoration(new LinearItemDecoration((int) space, true, false));
        menu_recycler_view.setLayoutManager(gridLayoutManager);
        menu_recycler_view.setAdapter(menuAdapter);

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                float slideOffsetMyScreen = slideOffset * +(float) (screenWidth * 0.22);
                sideMenuRecycler.setTranslationY(slideOffsetMyScreen);
                fragmentHolder.setPivotY(1);
                fragmentHolder.setTranslationY(slideOffsetMyScreen);
                fragmentLeftPopupHolder.setTranslationY(slideOffsetMyScreen);
                drawerLayout.bringChildToFront(drawerView);
                drawerLayout.requestLayout();
            }

            @Override
            public void onDrawerOpened(View drawerView) {  }

            @Override
            public void onDrawerClosed(View drawerView) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

    }

    public void setMarginTop(boolean set) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ABOVE, R.id.side_menu_recycler);
        if (set) {
            lp.addRule(RelativeLayout.BELOW, R.id.left_top_bar_container);
        } else {
            lp.addRule(RelativeLayout.BELOW, R.id.base_line_spliter);
        }
        fragmentHolder.setLayoutParams(lp);
    }


    private void setupFragments() {
        fragmentOrganizer = new FragmentOrganizer(getSupportFragmentManager(), WallFragment.class);

        ArrayList<Class> mainContainerFragments = new ArrayList<>();
        mainContainerFragments.add(WallFragment.class);
        mainContainerFragments.add(ChatFragment.class);
        mainContainerFragments.add(NewsFragment.class);
        mainContainerFragments.add(StatisticsFragment.class);
        mainContainerFragments.add(RumoursFragment.class);
        mainContainerFragments.add(StoreFragment.class);
        mainContainerFragments.add(VoicemailContract.class);
        mainContainerFragments.add(VideoChatFragment.class);
        fragmentOrganizer.setUpContainer(R.id.fragment_holder, mainContainerFragments);
        popupContainerFragments = new ArrayList<>();
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
        popupContainerFragments.add(SignUpLoginFragment.class);
        popupContainerFragments.add(CreateChatFragment.class);
        fragmentOrganizer.setUpContainer(R.id.fragment_popup_holder, popupContainerFragments, true);

        popupDialogFragments = new ArrayList<>();
        popupDialogFragments.add(AlertDialogFragment.class);
        fragmentOrganizer.setUpContainer(R.id.fragment_dialog, popupDialogFragments, true);

//left Join
        popupLeftFragments = new ArrayList<>();
        popupLeftFragments.add(EditChatFragment.class);
        popupLeftFragments.add(JoinChatFragment.class);
        popupLeftFragments.add(WallItemFragment.class);
        popupLeftFragments.add(NewsItemFragment.class);
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
        if (event.isReturning()) {
            SoundEffects.getDefault().playSound(SoundEffects.ROLL_OVER);
        } else {
            SoundEffects.getDefault().playSound(SoundEffects.SUBTLE);
        }
        if (fragmentOrganizer.getOpenFragment().getClass() == ClubRadioStationFragment.class) {
            ((ClubRadioStationFragment) fragmentOrganizer.getOpenFragment()).stopPlaying();
        }
        if (youtubeList.contains(event.getType()) || youtubePlayer.contains(event.getType())) {
            tvContainer.setVisibility(View.VISIBLE);
        } else {
            tvContainer.setVisibility(View.GONE);
        }

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
                //fragmentLeftPopupHolder is visible so we need to take photo from that layout
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

    private void toggleBlur(boolean visible, final View fragmentView) {
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
                        blurredBackground.setBackground(new BitmapDrawable(PhoneLoungeActivity.this.getResources(), image));
                        blurredBackground.getBackground().setAlpha(230);
                    }
                });
            }
        } else {
            blurredBackground.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        toggleBlur(false, null); // hide blurred view;
        SoundEffects.getDefault().playSound(SoundEffects.ROLL_OVER);
        Fragment fragmentOpened = fragmentOrganizer.getOpenFragment();
        if (!Utility.isTablet(this)) {
            if (popupLeftFragments.contains(fragmentOrganizer.getBackFragment().getClass())) {
                fragmentLeftPopupHolder.setVisibility(View.VISIBLE);
            } else {
                fragmentLeftPopupHolder.setVisibility(View.INVISIBLE);
            }
        }
        if (fragmentOrganizer.getBackFragment().getClass() == YoutubePlayerFragment.class) {
            fragmentOrganizer.getOpenFragment().getFragmentManager().popBackStack();
        }
        if (fragmentOpened.getClass() == SignUpFragment.class) {
            if (((SignUpFragment) fragmentOpened).getTermsHolder().getVisibility() == View.VISIBLE) {
                ((SignUpFragment) fragmentOpened).getTermsHolder().setVisibility(View.INVISIBLE);
                return;
            }
        }


        if (youtubeList.contains(fragmentOrganizer.getBackFragment().getClass()) || youtubePlayer.contains(fragmentOrganizer.getBackFragment().getClass())) {
            tvContainer.setVisibility(View.VISIBLE);
        } else {
            tvContainer.setVisibility(View.GONE);
        }

        if (radioList.contains(fragmentOrganizer.getBackFragment().getClass()) || radioPlayerList.contains(fragmentOrganizer.getBackFragment().getClass())) {
            radioContainer.setVisibility(View.VISIBLE);
        } else {
            radioContainer.setVisibility(View.GONE);
        }


        if (fragmentOrganizer.getBackFragment().getClass() == ClubRadioStationFragment.class && fragmentOrganizer.get2BackFragment().getClass() == ClubRadioFragment.class) {
            NavigationDrawerItems.getInstance().setByPosition(5);
            fragmentOrganizer.getOpenFragment().getFragmentManager().popBackStack();
            menuAdapter.notifyDataSetChanged();
            sideMenuAdapter.notifyDataSetChanged();
            if (drawerLayout.isDrawerOpen(GravityCompat.END))
                drawerLayout.closeDrawer(GravityCompat.END);
            tvContainer.setVisibility(View.VISIBLE);
            return;
        }

        if (fragmentOrganizer.getBackFragment().getClass() == YoutubePlayerFragment.class
            && fragmentOrganizer.get2BackFragment().getClass() == ClubTVFragment.class
            && fragmentOpened.getClass() != YoutubePlayerFragment.class
        ){
            NavigationDrawerItems.getInstance().setByPosition(7);
            menuAdapter.notifyDataSetChanged();
            sideMenuAdapter.notifyDataSetChanged();
            if (drawerLayout.isDrawerOpen(GravityCompat.END))
                drawerLayout.closeDrawer(GravityCompat.END);
            tvContainer.setVisibility(View.VISIBLE);
            return;
        }


        if (fragmentOpened instanceof YoutubePlayerFragment) {
            YoutubePlayerFragment youtubePlayerFragment = (YoutubePlayerFragment) fragmentOpened;
            if (youtubePlayerFragment.isFullScreen()){
                youtubePlayerFragment.setFullScreen(false);
                return;
            }

            if (fragmentOrganizer.getBackFragment().getClass() == ClubTVFragment.class) {
                fragmentOrganizer.getOpenFragment().getFragmentManager().popBackStack();
                fragmentOrganizer.getOpenFragment().getFragmentManager().popBackStack();
                if (fragmentOrganizer.get2BackFragment().getClass() == ClubRadioStationFragment.class) {
                    radioContainer.setVisibility(View.VISIBLE);
                    NavigationDrawerItems.getInstance().setByPosition(5);
                    menuAdapter.notifyDataSetChanged();
                    sideMenuAdapter.notifyDataSetChanged();
                    if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                        drawerLayout.closeDrawer(GravityCompat.END);
                    }
                    return;
                }
                for (int i = 0; i < Constant.CLASS_LIST.size(); i++) {
                    if (fragmentOrganizer.get2BackFragment().getClass().equals(Constant.CLASS_LIST.get(i))) {
                        NavigationDrawerItems.getInstance().setByPosition(i);
                        menuAdapter.notifyDataSetChanged();
                        sideMenuAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }
        }

        if (fragmentOpened.getClass() == ClubRadioStationFragment.class) {
            if (fragmentOrganizer.getBackFragment().getClass() == ClubRadioFragment.class) {
                fragmentOrganizer.getOpenFragment().getFragmentManager().popBackStack();
                fragmentOrganizer.getOpenFragment().getFragmentManager().popBackStack();
                if (fragmentOrganizer.get2BackFragment().getClass() == YoutubePlayerFragment.class) {
                    tvContainer.setVisibility(View.VISIBLE);
                    NavigationDrawerItems.getInstance().setByPosition(7);
                    menuAdapter.notifyDataSetChanged();
                    sideMenuAdapter.notifyDataSetChanged();
                    if (drawerLayout.isDrawerOpen(GravityCompat.END)){
                        drawerLayout.closeDrawer(GravityCompat.END);
                    }
                }
                for (int i = 0; i < Constant.CLASS_LIST.size(); i++) {
                    if (fragmentOrganizer.get2BackFragment().getClass().equals(Constant.CLASS_LIST.get(i))) {
                        NavigationDrawerItems.getInstance().setByPosition(i);
                        menuAdapter.notifyDataSetChanged();
                        sideMenuAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }

        }


        if (fragmentOrganizer.getBackFragment().getClass() == SignUpLoginFragment.class) {
            EventBus.getDefault().post(new FragmentEvent(WallFragment.class, true));
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
        if (Model.getInstance().getLoggedInUserType() == Model.LoggedInUserType.REAL)
            EventBus.getDefault().post(new FragmentEvent(YourProfileFragment.class));
        else
            EventBus.getDefault().post(new FragmentEvent(SignUpLoginFragment.class));
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
        splash.setVisibility(View.GONE);
        resetUserDetails();
        updateTopBar();
    }

    @Override
    public void onLogin(UserInfo user) {
        splash.setVisibility(View.GONE);
        if (Model.getInstance().getLoggedInUserType() == Model.LoggedInUserType.REAL) {
            if (user.getCircularAvatarUrl() != null) {
                ImageLoader.getInstance().displayImage(user.getCircularAvatarUrl(), profileImage, Utility.getImageOptionsForUsers());
            }
            setYourCoinsValue(String.valueOf(Model.getInstance().getUserInfo().getCurrency()));
            yourLevel.setVisibility(View.VISIBLE);
            userLevelBackground.setVisibility(View.VISIBLE);
            userLevelProgress.setVisibility(View.VISIBLE);
            yourLevel.setText(String.valueOf((int) user.getProgress()));
            userLevelProgress.setProgress((int) (user.getProgress() * userLevelProgress.getMax()));
            TutorialModel.getInstance().setUserId(Model.getInstance().getUserInfo().getUserId());
        } else {
            resetUserDetails();
        }
        updateTopBar();
    }

    private void resetUserDetails() {
        setYourCoinsValue(String.valueOf(0));
        yourLevel.setVisibility(View.INVISIBLE);
        userLevelBackground.setVisibility(View.INVISIBLE);
        userLevelProgress.setVisibility(View.INVISIBLE);
        String imgUri = "drawable://" + getResources().getIdentifier("blank_profile_rounded", "drawable", this.getPackageName());
        ImageLoader.getInstance().displayImage(imgUri, profileImage, Utility.imageOptionsImageLoader());
    }
}