package base.app.ui.fragment.stream;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pixplicity.easyprefs.library.Prefs;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import base.app.R;
import base.app.data.club.Station;
import base.app.ui.fragment.base.BaseFragment;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.util.commons.Constant;
import base.app.util.ui.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Filip on 1/30/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class RadioStationFragment extends BaseFragment implements MediaPlayer.OnPreparedListener {

    private static final String TAG = "Radio Station Fragment";
    private static final int AUDIO_BAR_MAX = 1000;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_club_radio_station, container, false);
        ButterKnife.bind(this, view);

        // TODO Alex Sheiko: station = MediaModel.getStationByName(getPrimaryArgument());
        captionTextView.setText(station.getName());
        String imageUrl = station.getCoverImageUrl();
        ImageLoader.displayImage(imageUrl, backgroundImage, null);

        initializeMediaPlayer();
        float volume = Prefs.getFloat(Constant.RADIO_VOLUME, 0.5f);

        audioSeekBar.setMax(AUDIO_BAR_MAX);
        audioSeekBar.setProgress((int) (volume * AUDIO_BAR_MAX));
        audioSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float percentage = ((float) seekBar.getProgress() / (float) seekBar.getMax());
                if (player != null) {
                    Prefs.putFloat(Constant.RADIO_VOLUME, percentage);
                    player.setVolume(percentage, percentage);
                }
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }
        });

        return view;
    }

    @OnClick(R.id.play_button)
    public void playButtonOnClick() {
        if (playButton.isSelected()) {
            playButton.setSelected(false);
            stopPlaying();
        } else {
            playButton.setSelected(true);
            initializeMediaPlayer();
        }
    }

    @OnClick(R.id.close_button)
    public void closeButtonOnClick() {
        EventBus.getDefault().post(new FragmentEvent(RadioFragment.class, true));
    }

    MediaPlayer player;

    private void initializeMediaPlayer() {
        player = new MediaPlayer();
        try {
            player.setDataSource(station.getUrl());
            player.setOnPreparedListener(this);
            player.prepareAsync();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            Log.d(TAG, "Initialization failed.");
        }

        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                Log.d(TAG, "Buffered to:" + percent);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (player.isPlaying()) {
                player.stop();
            }
        } catch (IllegalStateException exception) {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        player.release();
    }

    public void stopPlaying() {
        if (player.isPlaying()) {
            player.stop();
            player.release();
            playButton.setSelected(false);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        float volume = Prefs.getFloat(Constant.RADIO_VOLUME, 0.5f);
        player.setVolume(volume, volume);
        player.start();
        playButton.setSelected(true);
    }
}
