package base.app.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import base.app.R;
import base.app.data.Model;
import base.app.data._unused.tutorial.TutorialModel;
import base.app.data._unused.videoChat.VideoChatEvent;
import base.app.data.user.LoginStateReceiver;
import base.app.data.user.UserEvent;
import base.app.data.user.UserInfo;
import base.app.data.wall.ticker.NewsTickerInfo;
import base.app.data.wall.ticker.NextMatchModel;
import base.app.data.wall.ticker.NextMatchUpdateEvent;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.ui.fragment.base.FragmentOrganizer;
import base.app.ui.fragment.content.NewsFragment;
import base.app.ui.fragment.content.NewsItemFragment;
import base.app.ui.fragment.content.QuizFragment;
import base.app.ui.fragment.content.RumoursFragment;
import base.app.ui.fragment.content.StoreFragment;
import base.app.ui.fragment.content.WallItemFragment;
import base.app.ui.fragment.content.wall.WallFragment;
import base.app.ui.fragment.other.ChatFragment;
import base.app.ui.fragment.other.FantasyFragment;
import base.app.ui.fragment.other.StatsFragment;
import base.app.ui.fragment.popup.AlertDialogFragment;
import base.app.ui.fragment.popup.ChatCreateFragment;
import base.app.ui.fragment.popup.EditChatFragment;
import base.app.ui.fragment.popup.FollowersFragment;
import base.app.ui.fragment.popup.FollowingFragment;
import base.app.ui.fragment.popup.FriendFragment;
import base.app.ui.fragment.popup.FriendRequestsFragment;
import base.app.ui.fragment.popup.FriendsFragment;
import base.app.ui.fragment.popup.FriendsSearchFragment;
import base.app.ui.fragment.popup.InviteFriendFragment;
import base.app.ui.fragment.popup.JoinChatFragment;
import base.app.ui.fragment.popup.LanguageFragment;
import base.app.ui.fragment.popup.LoginFragment;
import base.app.ui.fragment.popup.ProfileEditFragment;
import base.app.ui.fragment.popup.ProfileFragment;
import base.app.ui.fragment.popup.SignUpFragment;
import base.app.ui.fragment.popup.SignUpLoginFragment;
import base.app.ui.fragment.popup.SignUpLoginPopupRightFragment;
import base.app.ui.fragment.popup.SignUpLoginVideoFragment;
import base.app.ui.fragment.popup.StartingNewCallFragment;
import base.app.ui.fragment.popup.StashFragment;
import base.app.ui.fragment.popup.WalletFragment;
import base.app.ui.fragment.popup.YourStatementFragment;
import base.app.ui.fragment.stream.ClubRadioFragment;
import base.app.ui.fragment.stream.ClubRadioStationFragment;
import base.app.ui.fragment.stream.ClubTVFragment;
import base.app.ui.fragment.stream.ClubTvPlaylistFragment;
import base.app.ui.fragment.stream.VideoChatFragment;
import base.app.ui.fragment.stream.YoutubePlayerFragment;
import base.app.util.commons.NextMatchCountdown;
import base.app.util.commons.SoundEffects;
import base.app.util.ui.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivityTablet extends BaseActivity implements LoginStateReceiver.LoginStateListener {

    public static final String TAG = "Lounge Activity";
    @BindView(R.id.rootView)
    View rootView;

    @BindView(R.id.tabs_container_1)
    View fragmentContainerLeft;
    @BindView(R.id.tabs_container_top_right)
    View fragmentContainerTopRight;

    @BindView(R.id.popup_holder)
    View popupHolder;
    @BindView(R.id.popup_login_holder)
    View popupLoginHolder;

    @BindView(R.id.bottom_right_container)
    View fragmentContainerBottomRight;

    @BindView(R.id.days_until_match_label)
    TextView daysUntilMatchLabel;

    @BindView(R.id.logo_first_team)
    ImageView logoOfFirstTeam;
    @BindView(R.id.logo_second_team)
    ImageView logoOfSecondTeam;

    @BindView(R.id.coinsTextView)
    TextView yourCoinsValue;
    @BindView(R.id.userLevelTextView)
    TextView yourLevel;
    @BindView(R.id.userLevelBackground)
    ImageView userLevelBackground;
    @BindView(R.id.userLevelProgress)
    ProgressBar userLevelProgress;

    @BindView(R.id.profileImage)
    ImageView profileImage;
    @BindView(R.id.profile_name)
    TextView profileName;
    @BindView(R.id.notification_number)
    TextView notificationNumber;

    ArrayList<Class> popupContainerFragments;
    ArrayList<Class>  loginContainerFragments;
    BiMap<Integer, Class> radioButtonsFragmentMap;

    @BindView(R.id.left_notification_container)
    RelativeLayout notificationContainer;
    @BindView(R.id.user_coin_icon)
    ImageView userCoinIcon;

    @BindView(R.id.splash)
    View splash;

    @BindView(R.id.nextMatchSplashContainer)
    RelativeLayout nextMatchContainer;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tablet);
        ButterKnife.bind(this);
        this.loginStateReceiver = new LoginStateReceiver(this);
        popupHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setNumberOfNotification("4");
        setupFragments();
    }

    private void toggleBlur(boolean visible) {
        if (visible) {
            popupHolder.setVisibility(View.VISIBLE);
        } else {
            popupHolder.setVisibility(View.GONE);
        }
    }

    private void setupFragments() {
        fragmentOrganizer = new FragmentOrganizer(getSupportFragmentManager(), WallFragment.class);

        ArrayList<Class> leftContainerFragments = new ArrayList<>();
        leftContainerFragments.add(WallFragment.class);
        leftContainerFragments.add(VideoChatFragment.class);
        leftContainerFragments.add(NewsFragment.class);
        leftContainerFragments.add(RumoursFragment.class);
        leftContainerFragments.add(StoreFragment.class);
        leftContainerFragments.add(NewsItemFragment.class);
        leftContainerFragments.add(WallItemFragment.class);
        leftContainerFragments.add(SignUpLoginVideoFragment.class);
        fragmentOrganizer.setUpContainer(R.id.tabs_container_1, leftContainerFragments); //WITH BACK STACK

        ArrayList<Class> topRightContainerFragments = new ArrayList<>();
        topRightContainerFragments.add(ChatFragment.class);
        topRightContainerFragments.add(StatsFragment.class);
        topRightContainerFragments.add(FantasyFragment.class);
        topRightContainerFragments.add(QuizFragment.class);
        topRightContainerFragments.add(SignUpLoginPopupRightFragment.class);
        fragmentOrganizer.setUpContainer(R.id.tabs_container_top_right, topRightContainerFragments);  //WITH BACK STACK

        ArrayList<Class> bottomRightContainerFragments = new ArrayList<>();
        bottomRightContainerFragments.add(ClubTVFragment.class);
        bottomRightContainerFragments.add(ClubTvPlaylistFragment.class);
        bottomRightContainerFragments.add(ClubRadioFragment.class);
        bottomRightContainerFragments.add(YoutubePlayerFragment.class);
        bottomRightContainerFragments.add(ClubRadioStationFragment.class);
        fragmentOrganizer.setUpContainer(R.id.bottom_right_container, bottomRightContainerFragments); //WITH BACK STACK

        popupContainerFragments = new ArrayList<>();
        popupContainerFragments.add(ProfileFragment.class);
        popupContainerFragments.add(StashFragment.class);
        popupContainerFragments.add(YourStatementFragment.class);
        popupContainerFragments.add(WalletFragment.class);
        popupContainerFragments.add(LanguageFragment.class);
        popupContainerFragments.add(FriendsFragment.class);
        popupContainerFragments.add(FriendRequestsFragment.class);
        popupContainerFragments.add(StartingNewCallFragment.class);
        popupContainerFragments.add(ProfileEditFragment.class);
        popupContainerFragments.add(FriendFragment.class);
        popupContainerFragments.add(FollowersFragment.class);
        popupContainerFragments.add(FollowingFragment.class);
        popupContainerFragments.add(FriendsSearchFragment.class);
        popupContainerFragments.add(InviteFriendFragment.class);
        popupContainerFragments.add(AlertDialogFragment.class);

        popupContainerFragments.add(ChatCreateFragment.class);
        popupContainerFragments.add(JoinChatFragment.class);
        popupContainerFragments.add(EditChatFragment.class);

        fragmentOrganizer.setUpContainer(R.id.popup_holder, popupContainerFragments, true);  //NO BACK STACK

        loginContainerFragments = new ArrayList<>();
        loginContainerFragments.add(SignUpLoginFragment.class);
        loginContainerFragments.add(SignUpFragment.class);
        loginContainerFragments.add(LoginFragment.class);

        fragmentOrganizer.setUpContainer(R.id.popup_login_holder, loginContainerFragments);  //NO BACK STACK

        radioButtonsFragmentMap = HashBiMap.create();
        radioButtonsFragmentMap.put(R.id.wall_radio_button, WallFragment.class);
        radioButtonsFragmentMap.put(R.id.video_chat_radio_button, VideoChatFragment.class);
        radioButtonsFragmentMap.put(R.id.news_radio_button, NewsFragment.class);
        radioButtonsFragmentMap.put(R.id.roumors_radio_button, RumoursFragment.class);
        radioButtonsFragmentMap.put(R.id.chat_radio_button, ChatFragment.class);
        radioButtonsFragmentMap.put(R.id.stats_radio_button, StatsFragment.class);
        radioButtonsFragmentMap.put(R.id.fantasy_radio_button, FantasyFragment.class);
        radioButtonsFragmentMap.put(R.id.quiz_radio_button, QuizFragment.class);
        radioButtonsFragmentMap.put(R.id.club_tv_radio_button, ClubTVFragment.class);
        radioButtonsFragmentMap.put(R.id.club_radio_radio_button, ClubRadioFragment.class);
        radioButtonsFragmentMap.put(R.id.shop_radio_button, StoreFragment.class);

        EventBus.getDefault().post(new FragmentEvent(WallFragment.class));
        EventBus.getDefault().post(new FragmentEvent(ChatFragment.class));
        EventBus.getDefault().post(new FragmentEvent(ClubTVFragment.class));
        ((RadioButton) ButterKnife.findById(this, R.id.wall_radio_button)).setChecked(true);
        ((RadioButton) ButterKnife.findById(this, R.id.chat_radio_button)).setChecked(true);
        ((RadioButton) ButterKnife.findById(this, R.id.club_tv_radio_button)).setChecked(true);
    }


    @Subscribe
    public void onFragmentEvent(FragmentEvent event) {
        popupLoginHolder.setVisibility(View.GONE);
        if (event.isReturning()) {
            SoundEffects.getDefault().playSound(SoundEffects.ROLL_OVER);
        } else {
            SoundEffects.getDefault().playSound(SoundEffects.SUBTLE);
        }

        if (popupContainerFragments.contains(event.getType())) {
            // this is popup fragment, show blurred background
            toggleBlur(true);
        } else if (loginContainerFragments.contains(event.getType())) {
            // this is login fragment, display login fragments container
            popupLoginHolder.setVisibility(View.VISIBLE);
        } else {
            if (radioButtonsFragmentMap.inverse().containsKey(event.getType())) {
                // Detect which radio button was clicked and fetch what Fragment is about to be opened
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
    protected void onPause() {
        super.onPause();
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        if (checked && radioButtonsFragmentMap.containsKey(view.getId())) {
            Class fragmentType = radioButtonsFragmentMap.get(view.getId());
            EventBus.getDefault().post(new FragmentEvent(fragmentType));
        }
    }

    @Override
    public void onBackPressed() {
        toggleBlur(false); // hide blurred view;
        SoundEffects.getDefault().playSound(SoundEffects.ROLL_OVER);
        if (!fragmentOrganizer.handleBackNavigation()) {
            finish();
        }else {
            popupLoginHolder.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void update(NextMatchUpdateEvent event){
        if(NextMatchModel.getInstance().isNextMatchUpcoming()){
            NewsTickerInfo info = NextMatchModel.getInstance().getTickerInfo();
            // in case where there were no team images (first time initialisation)
            if(logoOfFirstTeam.getDrawable()==null){
                Glide.with(this).load(info.getFirstClubUrl()).into(logoOfFirstTeam);
                Glide.with(this).load(info.getSecondClubUrl()).into(logoOfSecondTeam);
            }
            long timestamp = Long.parseLong(info.getMatchDate());
            daysUntilMatchLabel.setText(NextMatchCountdown.getTextValue(getBaseContext(),timestamp,true));
            nextMatchContainer.setVisibility(View.VISIBLE);
        } else {
            nextMatchContainer.setVisibility(View.GONE);
        }
    }


    private void setYourCoinsValue(String value) {
        yourCoinsValue.setText(value);
    }

    public void onFriendsButtonClick(View view) {
        EventBus.getDefault().post(new FragmentEvent(FriendsFragment.class));
    }

    public void onFollowersButtonClick(View view) {
        EventBus.getDefault().post(new FragmentEvent(FollowingFragment.class));
    }

    private void setNumberOfNotification(String number) {
        notificationNumber.setText(number);
    }

    @Override
    public void onLogout() {
        resetUserDetails();
    }

    @Override
    public void onLoginAnonymously() {
        resetUserDetails();
        Handler handler = new Handler();
        // delaying splash hiding to give enough time for login to be triggered
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                splash.setVisibility(View.GONE);
            }
        },3000);
    }

    @Override
    public void onLogin(UserInfo user) {
        if (Model.getInstance().isRealUser()) {
            if (user.getCircularAvatarUrl() != null) {
                ImageLoader.displayRoundImage(user.getCircularAvatarUrl(), profileImage);
            }
            if (user.getFirstName() != null && user.getLastName() != null) {
                profileName.setText(user.getFirstName() + " " + user.getLastName());
            }
            setYourCoinsValue(String.valueOf(Model.getInstance().getUserInfo().getCurrency()));
            yourLevel.setVisibility(View.VISIBLE);
            userLevelBackground.setVisibility(View.VISIBLE);
            userLevelProgress.setVisibility(View.VISIBLE);
            yourLevel.setText(String.valueOf((int)user.getProgress()));
            userLevelProgress.setProgress((int) (user.getProgress() * userLevelProgress.getMax()));
            TutorialModel.getInstance().setUserId(Model.getInstance().getUserInfo().getUserId());

        } else {
            resetUserDetails();
        }
        splash.setVisibility(View.GONE);
    }

    private void resetUserDetails() {
        //reset profile name and picture to blank values
        EventBus.getDefault().post(new FragmentEvent(SignUpLoginPopupRightFragment.class));
        setYourCoinsValue(String.valueOf(0));
        yourLevel.setVisibility(View.INVISIBLE);
        userLevelBackground.setVisibility(View.INVISIBLE);
        userLevelProgress.setVisibility(View.INVISIBLE);
        profileName.setText(R.string.join_sign_in);
        String imgUri = "drawable://" + getResources().getIdentifier("blank_profile_rounded", "drawable", this.getPackageName());
        ImageLoader.displayImage(imgUri, profileImage, null);

    }

    @Override
    public void onLoginError(Error error) {}

    @Subscribe
    public void updateUserName(UserEvent event) {
        if (event.getType() == UserEvent.Type.onDetailsUpdated) {
            setYourCoinsValue(String.valueOf(Model.getInstance().getUserInfo().getCurrency()));
        }

        UserInfo user = event.getUserInfo();
        if (user != null) {
            if (user.getFirstName() != null && user.getLastName() != null) {
                profileName.setText(user.getFirstName() + " " + user.getLastName());
            }
            if (user.getCircularAvatarUrl() != null) {
                ImageLoader.displayRoundImage(user.getCircularAvatarUrl(), profileImage);
            }
        }
    }

    @OnClick({R.id.user_image_container, R.id.profile_name,R.id.user_info_container})
    public void onLoginClick() {
        if (Model.getInstance().isRealUser()) {
            EventBus.getDefault().post(new FragmentEvent(ProfileFragment.class));
        } else {
            EventBus.getDefault().post(new FragmentEvent(SignUpLoginFragment.class));
        }
    }

    @Subscribe
    public void onVideoChatEventReceived(VideoChatEvent event) {
        switch (event.getType()) {
            case onInvitationRevoked:
            case onChatClosed:
                if(fragmentOrganizer.getCurrentFragment() instanceof AlertDialogFragment){
                    onBackPressed();
                }
                break;
        }
    }

}