package tv.sportssidekick.sportssidekick.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.fragment.FragmentOrganizer;

public class LoungeActivity extends AppCompatActivity {

    @BindView(R.id.tabs_container_1)
    View fragmentContainerLeft;
    @BindView(R.id.tabs_container_top_right)
    View fragmentContainerTopRight;
    @BindView(R.id.video_player_container)
    View fragmentContainerBottomRight;

    @BindView(R.id.wall_radio_button)
    RadioButton radioButtonWall;
    @BindView(R.id.video_chat_radio_button)
    RadioButton radioButtonVideoChat;
    @BindView(R.id.news_radio_button)
    RadioButton radioButtonNews;
    @BindView(R.id.roumors_radio_button)
    RadioButton radioButtonRoumours;
    @BindView(R.id.shop_radio_button)
    RadioButton radioButtonShop;

    FragmentOrganizer leftFragmentOrganizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lounge);
        ButterKnife.bind(this);
        leftFragmentOrganizer = new FragmentOrganizer(getSupportFragmentManager(),R.id.tabs_container_1);
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
        switch (view.getId()) {
            case R.id.wall_radio_button:
                if (checked) {
                    fragmentEvent = new FragmentEvent(FragmentEvent.Type.WALL);
                }
                break;
            case R.id.video_chat_radio_button:
                if (checked) {
                    fragmentEvent = new FragmentEvent(FragmentEvent.Type.VIDEO_CHAT);
                }
                break;
            case R.id.news_radio_button:
                if (checked) {
                    fragmentEvent = new FragmentEvent(FragmentEvent.Type.NEWS);
                }
                break;
            case R.id.roumors_radio_button:
                if (checked) {
                    fragmentEvent = new FragmentEvent(FragmentEvent.Type.RUMOURS);
                }
                break;
            case R.id.shop_radio_button:
                if (checked) {
                    fragmentEvent = new FragmentEvent(FragmentEvent.Type.STORE);
                }
                break;
        }

        if(fragmentEvent!=null){
            EventBus.getDefault().post(fragmentEvent);
        }
    }

    @Override
    public void onDestroy() {
        leftFragmentOrganizer.freeUpResources();
        super.onDestroy();
    }
}
