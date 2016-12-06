package tv.sportssidekick.sportssidekick.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.fragment.FragmentOrganizer;
import tv.sportssidekick.sportssidekick.fragment.instance.ChatFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.ClubRadioFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.ClubTVFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.FantasyFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.NewsFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.QuizFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.RumoursFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.StatisticsFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.StoreFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.VideoChatFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.WallFragment;

public class LoungeActivity extends AppCompatActivity {

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
    @BindView(R.id.chat_radio_button)
    RadioButton radioButtonClubRadio;

    FragmentOrganizer fragmentOrganizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lounge);
        ButterKnife.bind(this);
        fragmentOrganizer = new FragmentOrganizer(getSupportFragmentManager());

        ArrayList<Class> leftContainerFragments = new ArrayList<>();
        leftContainerFragments.add(WallFragment.class);
        leftContainerFragments.add(VideoChatFragment.class);
        leftContainerFragments.add(NewsFragment.class);
        leftContainerFragments.add(RumoursFragment.class);
        leftContainerFragments.add(StoreFragment.class);
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


        EventBus.getDefault().post(new FragmentEvent(WallFragment.class));
        EventBus.getDefault().post(new FragmentEvent(ChatFragment.class));
        EventBus.getDefault().post(new FragmentEvent(ClubTVFragment.class));

    }


    @Override
    protected void onResume() {
        super.onResume();
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
}
