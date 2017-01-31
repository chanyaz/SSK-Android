package tv.sportssidekick.sportssidekick.fragment.instance;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.model.club.ClubModel;
import tv.sportssidekick.sportssidekick.model.club.Station;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Filip on 1/30/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ClubRadioStationFragment extends BaseFragment {

    @BindView(R.id.play_button)
    ImageView playButton;

    @BindView(R.id.seek_bar)
    SeekBar seekBar;

    @BindView(R.id.seek_bar_audio)
    SeekBar audioSeekBar;

    @BindView(R.id.live_dot)
    ImageView liveDot;

    @BindView(R.id.background)
    ImageView backgroundImage;

    @BindView(R.id.caption)
    TextView captionTextView;

    @BindView(R.id.close_button)
    View close_button;
    Station station;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_club_radio_station, container, false);
        ButterKnife.bind(this, view);

        station = ClubModel.getInstance().getStationByName(getPrimaryArgument());
        captionTextView.setText(station.getName());
        // display image
        DisplayImageOptions imageOptions = Utility.imageOptionsImageLoader();
        String imageUrl = station.getCoverImageUrl();
        ImageLoader.getInstance().displayImage(imageUrl, backgroundImage, imageOptions);

        initializeMediaPlayer();

        return view;
    }

    @OnClick(R.id.play_button)
    public void playButtonOnClick(){
        if(playButton.isSelected()){
            playButton.setSelected(false);
            stopPlaying();
        } else {
            playButton.setSelected(true);
            startPlaying();
        }
    }

    @OnClick(R.id.close_button)
    public void closeButtonOnClick(){
        EventBus.getDefault().post(new FragmentEvent(ClubRadioFragment.class));
    }
    MediaPlayer player;
    private void initializeMediaPlayer() {
        player = new MediaPlayer();
        try {
            player.setDataSource(station.getUrl());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                //
            }
        });

        startPlaying();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player.isPlaying()) {
            player.stop();
        }
    }

    private void stopPlaying() {
        if (player.isPlaying()) {
            player.stop();
            player.release();
            initializeMediaPlayer();
        }
    }

    private void startPlaying() {
        player.start();
//        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            public void onPrepared(MediaPlayer mp) {
//                player.start();
//            }
//        });
//        player.prepareAsync();
    }
}
