package tv.sportssidekick.sportssidekick.activity;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.MenuAdapter;
import tv.sportssidekick.sportssidekick.adapter.SideMenuAdapter;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.fragment.FragmentOrganizer;
import tv.sportssidekick.sportssidekick.fragment.instance.ChatFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.ClubRadioFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.ClubRadioStationFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.ClubTVFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.ClubTvPlaylistFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.NewsFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.NewsItemFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.RumoursFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.StatisticsFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.StoreFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.VideoChatFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.WallFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.WallItemFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.YoutubePlayerFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.AddFriendFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.AlertDialogFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.CreateChatFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.EditChatFragment;
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
import tv.sportssidekick.sportssidekick.model.ticker.NewsTickerInfo;
import tv.sportssidekick.sportssidekick.model.user.LoginStateReceiver;
import tv.sportssidekick.sportssidekick.model.user.UserEvent;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.util.SoundEffects;
import tv.sportssidekick.sportssidekick.util.Utility;
import tv.sportssidekick.sportssidekick.util.ui.LinearItemDecoration;
import tv.sportssidekick.sportssidekick.util.ui.NavigationDrawerItems;
import tv.sportssidekick.sportssidekick.util.ui.NoScrollRecycler;
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
        fragmentOrganizer.setUpContainer(R.id.fragment_popup_holder, popupContainerFragments, true);
//left Join
        popupLeftFragments = new ArrayList<>();
        popupLeftFragments.add(ClubTvPlaylistFragment.class);
        popupLeftFragments.add(YoutubePlayerFragment.class);
        popupLeftFragments.add(ClubRadioStationFragment.class);
        popupLeftFragments.add(EditChatFragment.class);
        popupLeftFragments.add(CreateChatFragment.class);
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
            EventBus.getDefault().post(new FragmentEvent(LoginFragment.class));
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
    }

    @Override
    public void onLoginAnonymously() {
        resetUserDetails();
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