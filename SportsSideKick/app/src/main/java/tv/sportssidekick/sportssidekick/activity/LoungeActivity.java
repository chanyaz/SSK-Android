package tv.sportssidekick.sportssidekick.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

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
import tv.sportssidekick.sportssidekick.fragment.instance.ClubTVFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.FantasyFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.NewsFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.NewsItemFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.QuizFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.RumoursFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.StatisticsFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.StoreFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.VideoChatFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.WallFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.CreateChatFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.JoinChatFragment;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.Ticker;
import tv.sportssidekick.sportssidekick.util.BlurBuilder;
import tv.sportssidekick.sportssidekick.util.Utility;

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

    @BindView(R.id.wall_radio_button)
    RadioButton radioButtonWall;
    @BindView(R.id.video_chat_radio_button)
    RadioButton radioButtonVideoChat;
    @BindView(R.id.news_radio_button)
    RadioButton radioButtonNews;
    @BindView(R.id.roumors_radio_button)
    RadioButton radioButtonRumours;
    @BindView(R.id.shop_radio_button)
    RadioButton radioButtonShop;
    @BindView(R.id.chat_radio_button)
    RadioButton radioButtonChat;
    @BindView(R.id.stats_radio_button)
    RadioButton radioButtonStatistics;
    @BindView(R.id.fantasy_radio_button)
    RadioButton radioButtonFantasy;
    @BindView(R.id.quiz_radio_button)
    RadioButton radioButtonQuiz;
    @BindView(R.id.club_tv_radio_button)
    RadioButton radioButtonClubTV;
    @BindView(R.id.club_radio_radio_button)
    RadioButton radioButtonClubRadio;

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

    FragmentOrganizer fragmentOrganizer;

    ArrayList<Class> popupContainerFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.setDebug(true);
        setContentView(R.layout.activity_lounge);
        ButterKnife.bind(this);


        Ticker.initializeTicker(Model.getInstance().getDatabase());

        radioButtonWall.setChecked(true);
        radioButtonChat.setChecked(true);
        radioButtonClubTV.setChecked(true);

        setupFragments();
    }

    @Subscribe
    public void onFragmentEvent(FragmentEvent event){
        if(popupContainerFragments.contains(event.getType())){
            popupHolder.setVisibility(View.VISIBLE);
            if (rootView.getWidth() > 0) {
                Bitmap image = BlurBuilder.blur(rootView);
                popupHolder.setBackground(new BitmapDrawable(this.getResources(), image));
            } else {
                rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                    Bitmap image = BlurBuilder.blur(rootView);
                    popupHolder.setBackground(new BitmapDrawable(LoungeActivity.this.getResources(), image));
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
        bottomRightContainerFragments.add(ClubRadioFragment.class);
        fragmentOrganizer.setUpContainer(R.id.bottom_right_container,bottomRightContainerFragments);

        popupContainerFragments = new ArrayList<>();
        popupContainerFragments.add(CreateChatFragment.class);
        popupContainerFragments.add(JoinChatFragment.class);
        fragmentOrganizer.setUpContainer(R.id.popup_holder,popupContainerFragments, true);


        EventBus.getDefault().post(new FragmentEvent(WallFragment.class));
        EventBus.getDefault().post(new FragmentEvent(ChatFragment.class));
//        EventBus.getDefault().post(new FragmentEvent(ClubTVFragment.class));
    }


    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        FragmentEvent fragmentEvent = null;
        if(checked){
            switch (view.getId()) {
                case R.id.wall_radio_button:
                    fragmentEvent = new FragmentEvent(WallFragment.class);
                    break;
                case R.id.video_chat_radio_button:
                    fragmentEvent = new FragmentEvent(VideoChatFragment.class);
                    break;
                case R.id.news_radio_button:
                    fragmentEvent = new FragmentEvent(NewsFragment.class);
                    break;
                case R.id.roumors_radio_button:
                    fragmentEvent = new FragmentEvent(RumoursFragment.class);
                    break;
                case R.id.shop_radio_button:
                    fragmentEvent = new FragmentEvent(StoreFragment.class);
                    break;
                case R.id.chat_radio_button:
                    fragmentEvent = new FragmentEvent(ChatFragment.class);
                    break;
                case R.id.stats_radio_button:
                    fragmentEvent = new FragmentEvent(StatisticsFragment.class);
                    break;
                case R.id.fantasy_radio_button:
                    fragmentEvent = new FragmentEvent(FantasyFragment.class);
                    break;
                case R.id.quiz_radio_button:
                    fragmentEvent = new FragmentEvent(QuizFragment.class);
                    break;
                case R.id.club_tv_radio_button:
                    fragmentEvent = new FragmentEvent(ClubTVFragment.class);
                    break;
                case R.id.club_radio_radio_button:
                    fragmentEvent = new FragmentEvent(ClubRadioFragment.class);
                    break;
            }
        }
        if(fragmentEvent!=null){
            EventBus.getDefault().post(fragmentEvent);
        }
    }

    @Override
    public void onDestroy() {
        fragmentOrganizer.freeUpResources();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        onFragmentEvent(new FragmentEvent(null)); // hide blurred view;
        if (!fragmentOrganizer.handleBackNavigation()) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTickerUpdate(Ticker ticker){
        newsLabel.setText(ticker.getNews().get(0));
        long timestamp = Long.valueOf(ticker.getMatchDate());
        timeOfMatch.setText(Utility.getDate(timestamp));
        long getDaysUntilMatch = Utility.getDaysUntilMatch(timestamp);
        Resources res = getResources();
        String daysValue = res.getQuantityString(R.plurals.days_until_match, (int)getDaysUntilMatch, (int)getDaysUntilMatch);
        daysUntilMatchLabel.setText(daysValue);
        captionLabel.setText(ticker.getTitle());
        ImageLoader.getInstance().displayImage(ticker.getFirstClubUrl(), logoOfFirstTeam, Utility.imageOptionsImageLoader());
        ImageLoader.getInstance().displayImage(ticker.getSecondClubUrl(), logoOfSecondTeam, Utility.imageOptionsImageLoader());
        startNewsTimer(ticker);
    }


    Timer newsTimer;
    int count;
    private void startNewsTimer(final Ticker ticker){
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
                        newsLabel.setText(ticker.getNews().get(count));
                        if(++count == ticker.getNews().size()){
                            count = 0;
                        }
                    }
                });
            }
        }, 0, Constant.LOGIN_TEXT_TIME);

    }


}
