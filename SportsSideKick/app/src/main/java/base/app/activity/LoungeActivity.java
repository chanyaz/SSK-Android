package base.app.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import base.app.R;
import base.app.fragment.FragmentEvent;
import base.app.fragment.FragmentOrganizer;
import base.app.fragment.instance.ChatFragment;
import base.app.fragment.instance.ClubRadioFragment;
import base.app.fragment.instance.ClubRadioStationFragment;
import base.app.fragment.instance.ClubTVFragment;
import base.app.fragment.instance.ClubTvPlaylistFragment;
import base.app.fragment.instance.FantasyFragment;
import base.app.fragment.instance.NewsFragment;
import base.app.fragment.instance.NewsItemFragment;
import base.app.fragment.instance.QuizFragment;
import base.app.fragment.instance.RumoursFragment;
import base.app.fragment.instance.StatisticsFragment;
import base.app.fragment.instance.StoreFragment;
import base.app.fragment.instance.VideoChatFragment;
import base.app.fragment.instance.WallFragment;
import base.app.fragment.instance.WallItemFragment;
import base.app.fragment.instance.YoutubePlayerFragment;
import base.app.fragment.popup.AccountCreatingFragment;
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
import base.app.fragment.popup.SignUpLoginPopupRightFragment;
import base.app.fragment.popup.SignUpLoginVideoFragment;
import base.app.fragment.popup.StartingNewCallFragment;
import base.app.fragment.popup.StashFragment;
import base.app.fragment.popup.WalletFragment;
import base.app.fragment.popup.YourProfileFragment;
import base.app.fragment.popup.YourStatementFragment;
import base.app.model.Model;
import base.app.model.TranslateManager;
import base.app.model.ticker.NewsTickerInfo;
import base.app.model.ticker.NextMatchModel;
import base.app.model.ticker.NextMatchUpdateEvent;
import base.app.model.tutorial.TutorialModel;
import base.app.model.user.LoginStateReceiver;
import base.app.model.user.UserEvent;
import base.app.model.user.UserInfo;
import base.app.model.videoChat.VideoChatEvent;
import base.app.util.NextMatchCountdown;
import base.app.util.SoundEffects;
import base.app.util.Utility;
import base.app.util.ui.BlurBuilder;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoungeActivity extends BaseActivity implements LoginStateReceiver.LoginStateListener {

    public static final String TAG = "Lounge Activity";
    @BindView(R.id.activity_main)
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

    @BindView(R.id.your_coins_value)
    TextView yourCoinsValue;
    @BindView(R.id.user_level)
    TextView yourLevel;
    @BindView(R.id.user_level_background)
    ImageView userLevelBackground;
    @BindView(R.id.user_level_progress)
    ProgressBar userLevelProgress;

    @BindView(R.id.profile_image)
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lounge);
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

        TranslateManager.getInstance().initialize(this);
    }

    private void toggleBlur(boolean visible) {
        if (visible) {
            popupHolder.setVisibility(View.VISIBLE);
            if (rootView.getWidth() > 0) {
                Bitmap image = BlurBuilder.blur(rootView);
                popupHolder.setBackground(new BitmapDrawable(this.getResources(), image));
                popupHolder.getBackground().setAlpha(230);
            } else {
                rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Bitmap image = BlurBuilder.blur(rootView);
                        popupHolder.setBackground(new BitmapDrawable(LoungeActivity.this.getResources(), image));
                        popupHolder.getBackground().setAlpha(230);
                    }
                });
            }
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
        topRightContainerFragments.add(StatisticsFragment.class);
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
        popupContainerFragments.add(YourProfileFragment.class);
        popupContainerFragments.add(StashFragment.class);
        popupContainerFragments.add(YourStatementFragment.class);
        popupContainerFragments.add(WalletFragment.class);
        popupContainerFragments.add(LanguageFragment.class);
        popupContainerFragments.add(FriendsFragment.class);
        popupContainerFragments.add(FriendRequestsFragment.class);
        popupContainerFragments.add(StartingNewCallFragment.class);
        popupContainerFragments.add(EditProfileFragment.class);
        popupContainerFragments.add(MemberInfoFragment.class);
        popupContainerFragments.add(FollowersFragment.class);
        popupContainerFragments.add(FollowingFragment.class);
        popupContainerFragments.add(AddFriendFragment.class);
        popupContainerFragments.add(InviteFriendFragment.class);
        popupContainerFragments.add(AlertDialogFragment.class);

        popupContainerFragments.add(CreateChatFragment.class);
        popupContainerFragments.add(JoinChatFragment.class);
        popupContainerFragments.add(EditChatFragment.class);

        fragmentOrganizer.setUpContainer(R.id.popup_holder, popupContainerFragments, true);  //NO BACK STACK

        loginContainerFragments = new ArrayList<>();
        loginContainerFragments.add(SignUpLoginFragment.class);
        loginContainerFragments.add(SignUpFragment.class);
        loginContainerFragments.add(LoginFragment.class);
        loginContainerFragments.add(AccountCreatingFragment.class);

        fragmentOrganizer.setUpContainer(R.id.popup_login_holder, loginContainerFragments);  //NO BACK STACK

        radioButtonsFragmentMap = HashBiMap.create();
        radioButtonsFragmentMap.put(R.id.wall_radio_button, WallFragment.class);
        radioButtonsFragmentMap.put(R.id.video_chat_radio_button, VideoChatFragment.class);
        radioButtonsFragmentMap.put(R.id.news_radio_button, NewsFragment.class);
        radioButtonsFragmentMap.put(R.id.roumors_radio_button, RumoursFragment.class);
        radioButtonsFragmentMap.put(R.id.chat_radio_button, ChatFragment.class);
        radioButtonsFragmentMap.put(R.id.stats_radio_button, StatisticsFragment.class);
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
        NewsTickerInfo info = NextMatchModel.getInstance().getTickerInfo();
        // in case where there were no team images (first time initialisation)
        if(logoOfFirstTeam.getDrawable()==null){
            ImageLoader.getInstance().displayImage(info.getFirstClubUrl(), logoOfFirstTeam, Utility.getDefaultImageOptions());
            ImageLoader.getInstance().displayImage(info.getSecondClubUrl(), logoOfSecondTeam, Utility.getDefaultImageOptions());
        }
        long timestamp = Long.parseLong(info.getMatchDate());
        daysUntilMatchLabel.setText(NextMatchCountdown.getTextValue(getBaseContext(),timestamp,true));

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
        splash.setVisibility(View.GONE);
        resetUserDetails();
    }

    @Override
    public void onLogin(UserInfo user) {
        splash.setVisibility(View.GONE);
        if (Model.getInstance().isRealUser()) {
            if (user.getCircularAvatarUrl() != null) {
                ImageLoader.getInstance().displayImage(user.getCircularAvatarUrl(), profileImage, Utility.getImageOptionsForUsers());
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
    }

    private void resetUserDetails() {
        //reset profile name and picture to blank values
        EventBus.getDefault().post(new FragmentEvent(SignUpLoginPopupRightFragment.class));
        setYourCoinsValue(String.valueOf(0));
        yourLevel.setVisibility(View.INVISIBLE);
        userLevelBackground.setVisibility(View.INVISIBLE);
        userLevelProgress.setVisibility(View.INVISIBLE);
        profileName.setText(R.string.login_or_sign_up);
        String imgUri = "drawable://" + getResources().getIdentifier("blank_profile_rounded", "drawable", this.getPackageName());
        ImageLoader.getInstance().displayImage(imgUri, profileImage, Utility.getDefaultImageOptions());

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
                ImageLoader.getInstance().displayImage(user.getCircularAvatarUrl(), profileImage, Utility.getImageOptionsForUsers());
            }
        }
    }

    @OnClick({R.id.user_image_container, R.id.profile_name,R.id.user_info_container})
    public void onLoginClick() {
        if (Model.getInstance().isRealUser()) {
            EventBus.getDefault().post(new FragmentEvent(YourProfileFragment.class));
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