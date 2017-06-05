package base.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.provider.VoicemailContract;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import base.app.fragment.popup.SignUpLoginFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
import base.app.fragment.popup.StartingNewCallFragment;
import base.app.fragment.popup.StashFragment;
import base.app.fragment.popup.WalletFragment;
import base.app.fragment.popup.YourProfileFragment;
import base.app.fragment.popup.YourStatementFragment;
import base.app.model.Model;
import base.app.model.ticker.NewsTickerInfo;
import base.app.model.user.LoginStateReceiver;
import base.app.model.user.UserEvent;
import base.app.model.user.UserInfo;
import base.app.util.SoundEffects;
import base.app.util.Utility;
import base.app.util.ui.LinearItemDecoration;
import base.app.util.ui.NavigationDrawerItems;
import base.app.util.ui.NoScrollRecycler;
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


    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.scrolling_news_title)
    TextView newsLabel;
    @BindView(R.id.caption)
    TextView captionLabel;
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
    ArrayList<Class> slidePopupContainerFragments;
    SideMenuAdapter sideMenuAdapter;
    MenuAdapter menuAdapter;
    ArrayList<Class> popupLeftFragments;
    int screenWidth;

    @BindView(R.id.notification_open)
    ImageView notificationIcon;
    @BindView(R.id.friends_open)
    ImageView friendsIcon;

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

        updateTopBar();
        Utility.setSystemBarColor(this);
    }

    public void updateTopBar(){
        if(Model.getInstance().getLoggedInUserType() == Model.LoggedInUserType.REAL){
            //Check if user is logged in
            notificationIcon.setVisibility(View.VISIBLE);
            friendsIcon.setVisibility(View.VISIBLE);
        }else{
            notificationIcon.setVisibility(View.GONE);
            friendsIcon.setVisibility(View.GONE);
        }
    }

    private void setToolbar() {
        NavigationDrawerItems.getInstance().generateList(1);
        menuAdapter = new MenuAdapter(this, this);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

        ArrayList<Class> mainContainerFragments = new ArrayList<>();
        mainContainerFragments.add(WallFragment.class);
        mainContainerFragments.add(ChatFragment.class);
        mainContainerFragments.add(NewsFragment.class);
        mainContainerFragments.add(StatisticsFragment.class);
        mainContainerFragments.add(RumoursFragment.class);
        mainContainerFragments.add(ClubRadioFragment.class);
        mainContainerFragments.add(StoreFragment.class);
        mainContainerFragments.add(ClubTVFragment.class);
        mainContainerFragments.add(VoicemailContract.class);
        mainContainerFragments.add(VideoChatFragment.class);
        fragmentOrganizer.setUpContainer(R.id.fragment_holder, mainContainerFragments);
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
        popupContainerFragments.add(SignUpLoginFragment.class);
        popupContainerFragments.add(CreateChatFragment.class);
        fragmentOrganizer.setUpContainer(R.id.fragment_popup_holder, popupContainerFragments, true);
//left Join
        popupLeftFragments = new ArrayList<>();
        popupLeftFragments.add(ClubTvPlaylistFragment.class);
        popupLeftFragments.add(YoutubePlayerFragment.class);
        popupLeftFragments.add(ClubRadioStationFragment.class);
        popupLeftFragments.add(EditChatFragment.class);
        popupLeftFragments.add(JoinChatFragment.class);
        popupLeftFragments.add(WallItemFragment.class);
        popupLeftFragments.add(NewsItemFragment.class);
        fragmentOrganizer.setUpContainer(R.id.fragment_left_popup_holder, popupLeftFragments, true);

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
        if (popupLeftFragments.contains(event.getType())) {
            // this is popup event
            fragmentLeftPopupHolder.setVisibility(View.VISIBLE);
            barContainer.setVisibility(View.INVISIBLE);
        } else {
            fragmentLeftPopupHolder.setVisibility(View.INVISIBLE);
            barContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        SoundEffects.getDefault().playSound(SoundEffects.ROLL_OVER);
        if (barContainer.getVisibility() != View.VISIBLE)
            barContainer.setVisibility(View.VISIBLE);
        if (fragmentOrganizer.handleNavigationFragment()) {
            menuAdapter.notifyDataSetChanged();
            sideMenuAdapter.notifyDataSetChanged();
            if (drawerLayout.isDrawerOpen(GravityCompat.END))
                drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            finish();
        }
    }

    @Subscribe
    public void onTickerUpdate(NewsTickerInfo newsTickerInfo) {
        newsLabel.setText(newsTickerInfo.getNews().get(0));
        captionLabel.setText(newsTickerInfo.getTitle());
        startNewsTimer(newsTickerInfo, newsLabel);
    }

    private void setYourCoinsValue(String value) {
        yourCoinsValue.setText(value);
    }

    public void onProfileClicked(View view) {
        drawerLayout.closeDrawer(GravityCompat.END);
        if (yourLevel.getVisibility() == View.VISIBLE)
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

    @Override
    public void onLogout() {
        resetUserDetails();
        updateTopBar();
    }

    @Override
    public void onLoginAnonymously() {
        resetUserDetails();
        updateTopBar();
    }

    @Override
    public void onLogin(UserInfo user) {
        if (Model.getInstance().getLoggedInUserType() == Model.LoggedInUserType.REAL) {
            if (user.getCircularAvatarUrl() != null) {
                ImageLoader.getInstance().displayImage(user.getCircularAvatarUrl(), profileImage, Utility.getImageOptionsForUsers());
            }
            setYourCoinsValue(String.valueOf(Model.getInstance().getUserInfo().getCurrency()));
            yourLevel.setVisibility(View.VISIBLE);
            userLevelBackground.setVisibility(View.VISIBLE);
            userLevelProgress.setVisibility(View.VISIBLE);
            yourLevel.setText(String.valueOf(user.getLevel()));
            userLevelProgress.setProgress((int) (user.getProgress() * userLevelProgress.getMax()));

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