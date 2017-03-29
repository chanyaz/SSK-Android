package tv.sportssidekick.sportssidekick.activity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.Constant;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.fragment.FragmentOrganizer;
import tv.sportssidekick.sportssidekick.fragment.instance.ChatFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.ClubRadioFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.ClubRadioStationFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.ClubTVFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.ClubTvPlaylistFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.FantasyFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.NewsFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.NewsItemFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.QuizFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.RumoursFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.StatisticsFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.StoreFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.VideoChatFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.WallFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.YoutubePlayerFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.AddFriendFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.CreateChatFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.EditProfileFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.FriendRequestsFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.InviteFriendFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.JoinChatFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.LanguageFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.LoginFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.ManageChatFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.MemberInfoFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.SignUpFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.StartingNewCallFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.StashFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.WalletFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.FollowingFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.FollowersFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.YourFriendsFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.YourProfileFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.YourStatementFragment;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.ticker.NewsTickerInfo;
import tv.sportssidekick.sportssidekick.model.ticker.NextMatchModel;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.service.GSAndroidPlatform;
import tv.sportssidekick.sportssidekick.util.BlurBuilder;
import tv.sportssidekick.sportssidekick.util.SoundEffects;
import tv.sportssidekick.sportssidekick.util.Utility;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoungeActivity extends AppCompatActivity {

    @BindView(R.id.activity_main)
    View rootView;

    @BindView(R.id.popup_holder)
    View popupHolder;

    @BindView(R.id.tabs_container_1)
    View fragmentContainerLeft;
    @BindView(R.id.tabs_container_top_right)
    View fragmentContainerTopRight;
    @BindView(R.id.bottom_right_container)
    View fragmentContainerBottomRight;
    @BindView(R.id.scrolling_news_title)
    TextView newsLabel;
    @BindView(R.id.caption)
    TextView captionLabel;

    @BindView(R.id.days_until_match_label)
    TextView daysUntilMatchLabel;
    @BindView(R.id.time_of_match_label)
    TextView timeOfMatch;
    @BindView(R.id.logo_first_team)
    ImageView logoOfFirstTeam;
    @BindView(R.id.logo_second_team)
    ImageView logoOfSecondTeam;
    @BindView(R.id.your_coins)
    LinearLayout yourCoinsContainer;
    @BindView(R.id.your_coins_value)
    TextView yourCoinsValue;
    @BindView(R.id.user_level)
    TextView yourLevel;
    @BindView(R.id.user_level_background)
    ImageView userLevelBackground;
    @BindView(R.id.user_level_progress)
    ProgressBar userLevelProgress;

    @BindView(R.id.profile_button)
    RelativeLayout profileButton;
    @BindView(R.id.profile_image)
    ImageView profileImage;
    @BindView(R.id.profile_name)
    TextView profileName;

    @BindView(R.id.notification_number)
    TextView notificationNumber;

    FragmentOrganizer fragmentOrganizer;

    ArrayList<Class> popupContainerFragments;
    BiMap<Integer,Class> radioButtonsFragmentMap;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lounge);
        ButterKnife.bind(this);

        Model.getInstance().initialize(this);

        yourCoinsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO open dialog ?
            }
        });

        popupHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setYourCoinsValue("0"); //TODO get value from server

        setNumberOfNotification("4");

        setupFragments();
    }

    private void toggleBlur(boolean visible){ // TODO Extract to popup base class ?
        if(visible){
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

    private void setupFragments(){
        fragmentOrganizer = new FragmentOrganizer(getSupportFragmentManager(), WallFragment.class);

        ArrayList<Class> leftContainerFragments = new ArrayList<>();
        leftContainerFragments.add(WallFragment.class);
        leftContainerFragments.add(VideoChatFragment.class);
        leftContainerFragments.add(NewsFragment.class);
        leftContainerFragments.add(RumoursFragment.class);
        leftContainerFragments.add(StoreFragment.class);
        leftContainerFragments.add(NewsItemFragment.class);
        fragmentOrganizer.setUpContainer(R.id.tabs_container_1,leftContainerFragments);

        ArrayList<Class> topRightContainerFragments = new ArrayList<>();
        topRightContainerFragments.add(ChatFragment.class);
        topRightContainerFragments.add(StatisticsFragment.class);
        topRightContainerFragments.add(FantasyFragment.class);
        topRightContainerFragments.add(QuizFragment.class);
        fragmentOrganizer.setUpContainer(R.id.tabs_container_top_right,topRightContainerFragments);

        ArrayList<Class> bottomRightContainerFragments = new ArrayList<>();
        bottomRightContainerFragments.add(ClubTVFragment.class);
        bottomRightContainerFragments.add(ClubTvPlaylistFragment.class);
        bottomRightContainerFragments.add(ClubRadioFragment.class);
        bottomRightContainerFragments.add(YoutubePlayerFragment.class);
        bottomRightContainerFragments.add(ClubRadioStationFragment.class);
        fragmentOrganizer.setUpContainer(R.id.bottom_right_container,bottomRightContainerFragments);

        popupContainerFragments = new ArrayList<>();
        popupContainerFragments.add(CreateChatFragment.class);
        popupContainerFragments.add(JoinChatFragment.class);
        popupContainerFragments.add(ManageChatFragment.class);
        popupContainerFragments.add(YourProfileFragment.class);
        popupContainerFragments.add(StashFragment.class);
        popupContainerFragments.add(YourStatementFragment.class);
        popupContainerFragments.add(WalletFragment.class);
        popupContainerFragments.add(LanguageFragment.class);
        popupContainerFragments.add(YourFriendsFragment.class);
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
        fragmentOrganizer.setUpContainer(R.id.popup_holder,popupContainerFragments, true);


        radioButtonsFragmentMap =  HashBiMap.create();
        radioButtonsFragmentMap.put(R.id.wall_radio_button,WallFragment.class);
        radioButtonsFragmentMap.put(R.id.video_chat_radio_button,VideoChatFragment.class);
        radioButtonsFragmentMap.put(R.id.news_radio_button,NewsFragment.class);
        radioButtonsFragmentMap.put(R.id.roumors_radio_button,RumoursFragment.class);
        radioButtonsFragmentMap.put(R.id.chat_radio_button,ChatFragment.class);
        radioButtonsFragmentMap.put(R.id.stats_radio_button,StatisticsFragment.class);
        radioButtonsFragmentMap.put(R.id.fantasy_radio_button,FantasyFragment.class);
        radioButtonsFragmentMap.put(R.id.quiz_radio_button,QuizFragment.class);
        radioButtonsFragmentMap.put(R.id.club_tv_radio_button,ClubTVFragment.class);
        radioButtonsFragmentMap.put(R.id.club_radio_radio_button,ClubRadioFragment.class);
        radioButtonsFragmentMap.put(R.id.shop_radio_button,StoreFragment.class);

        // FIXME This will trigger sound?
        EventBus.getDefault().post(new FragmentEvent(WallFragment.class));
        EventBus.getDefault().post(new FragmentEvent(ChatFragment.class));
        EventBus.getDefault().post(new FragmentEvent(ClubTVFragment.class));
        ((RadioButton)ButterKnife.findById(this,R.id.wall_radio_button)).setChecked(true);
        ((RadioButton)ButterKnife.findById(this,R.id.chat_radio_button)).setChecked(true);
        ((RadioButton)ButterKnife.findById(this,R.id.club_tv_radio_button)).setChecked(true);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new FragmentEvent(LoginFragment.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        GSAndroidPlatform.gs().start();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe
    public void onFragmentEvent(FragmentEvent event){
        if(event.isReturning()){
            SoundEffects.getDefault().playSound(SoundEffects.ROLL_OVER);
        } else {
            SoundEffects.getDefault().playSound(SoundEffects.SUBTLE);
        }
        if(popupContainerFragments.contains(event.getType())){
            // this is popup event
            toggleBlur(true);
        } else {
            if(radioButtonsFragmentMap.inverse().containsKey(event.getType())){
                // Detect which radio button was clicked and fetch what Fragment should be opened
                int radioButtonId = radioButtonsFragmentMap.inverse().get(event.getType());
                for(int id : radioButtonsFragmentMap.keySet()){
                    RadioButton button = ButterKnife.findById(this,radioButtonId);
                    if(radioButtonId == id){
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Detect which radio button was clicked and fetch what Fragment should be opened
        if(checked && radioButtonsFragmentMap.containsKey(view.getId())){
            Class fragmentType = radioButtonsFragmentMap.get(view.getId());
            EventBus.getDefault().post(new FragmentEvent(fragmentType));
        }
    }

    @Override
    public void onDestroy() {
        fragmentOrganizer.freeUpResources();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        toggleBlur(false); // hide blurred view;
        SoundEffects.getDefault().playSound(SoundEffects.ROLL_OVER);
        if (!fragmentOrganizer.handleBackNavigation()) {
            finish();
        }
    }

    @Subscribe
    public void onTickerUpdate(NewsTickerInfo newsTickerInfo){
        newsLabel.setText(newsTickerInfo.getNews().get(0));
        long timestamp = Long.parseLong(newsTickerInfo.getMatchDate());
        timeOfMatch.setText(Utility.getDate(timestamp));
        long getDaysUntilMatch = Utility.getDaysUntilMatch(timestamp);
        Resources res = getResources();
        String daysValue = res.getQuantityString(R.plurals.days_until_match, (int)getDaysUntilMatch, (int)getDaysUntilMatch);
        daysUntilMatchLabel.setText(daysValue);
        captionLabel.setText(newsTickerInfo.getTitle());
        ImageLoader.getInstance().displayImage(newsTickerInfo.getFirstClubUrl(), logoOfFirstTeam, Utility.imageOptionsImageLoader());
        ImageLoader.getInstance().displayImage(newsTickerInfo.getSecondClubUrl(), logoOfSecondTeam, Utility.imageOptionsImageLoader());
        startNewsTimer(newsTickerInfo);
    }


    Timer newsTimer;
    int count;
    private void startNewsTimer(final NewsTickerInfo newsTickerInfo){
        count = 0;
        if(newsTimer!=null){
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
                        if(++count == newsTickerInfo.getNews().size()){
                            count = 0;
                        }
                    }
                });
            }
        }, 0, Constant.LOGIN_TEXT_TIME);

    }

    private void setYourCoinsValue (String value)
    {
        yourCoinsValue.setText(value + " $$K");
    }

    public void onFriendsButtonClick(View view) {
        EventBus.getDefault().post(new FragmentEvent(YourFriendsFragment.class));
    }

    public void onFollowersButtonClick(View view) {
        EventBus.getDefault().post(new FragmentEvent(FollowingFragment.class));
    }

    private void setNumberOfNotification(String number){
        notificationNumber.setText(number);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserLogin(UserInfo user)
    {
        if (Model.getInstance().getLoggedInUserType() == Model.LoggedInUserType.REAL)
        {
            if (user.getCircularAvatarUrl()!=null )
            {
                ImageLoader.getInstance().displayImage(user.getCircularAvatarUrl(), profileImage, Utility.getImageOptionsForUsers());
            }
            if (user.getFirstName() != null && user.getLastName() != null) {
                profileName.setText(user.getFirstName() + " " + user.getLastName());
            }
            setYourCoinsValue("0"); // TODO get user coins
            yourLevel.setVisibility(View.VISIBLE);
            userLevelBackground.setVisibility(View.VISIBLE);
            userLevelProgress.setVisibility(View.VISIBLE);
            yourLevel.setText(String.valueOf(user.getLevel()));
            userLevelProgress.setProgress((int)(user.getProgress()*userLevelProgress.getMax()));

            profileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new FragmentEvent(YourProfileFragment.class));

                }
            });
        }
        else {
            //reset profile name and picture to blank values
            setYourCoinsValue(String.valueOf(0)); // TODO get user coins
            yourLevel.setVisibility(View.INVISIBLE);
            userLevelBackground.setVisibility(View.INVISIBLE);
            userLevelProgress.setVisibility(View.INVISIBLE);
            profileName.setText("Login / Signup");
            String imgUri = "drawable://" + getResources().getIdentifier("blank_profile_rounded", "drawable", this.getPackageName());
            ImageLoader.getInstance().displayImage(imgUri, profileImage, Utility.imageOptionsImageLoader());
            profileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new FragmentEvent(LoginFragment.class));
                }
            });
        }
    }
}
