package base.app.fragment.instance;

import android.content.Context;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.api.services.youtube.model.Video;

import org.greenrobot.eventbus.EventBus;

import base.app.activity.PhoneLoungeActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import base.app.Constant;
import base.app.R;
import base.app.fragment.BaseFragment;
import base.app.fragment.FragmentEvent;
import base.app.model.club.ClubModel;
import base.app.util.ui.ThemeManager;

/**
 * Created by Filip on 1/25/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class YoutubePlayerFragment extends BaseFragment implements
        YouTubePlayer.OnInitializedListener,
        YouTubePlayer.PlaybackEventListener,
        YouTubePlayer.OnFullscreenListener,
        YouTubePlayer.PlayerStateChangeListener {

    private static final String TAG = "YOUTUBE PLAYER";
    private static final int SEEK_BAR_MAX = 10000;
    public YoutubePlayerFragment() {
        // Required empty public constructor
    }

    AudioManager audioManager;
    boolean mute;
    boolean isSeekBarTouched;

    @BindView(R.id.mute_button)
    ImageView muteButton;

    @BindView(R.id.play_button)
    ImageView playButton;

    @BindView(R.id.fullscreen_button)
    ImageView fullScreenButton;


    @BindView(R.id.time_info)
    TextView timeInfo;

    @BindView(R.id.seek_bar)
    SeekBar seekBar;

    Video video;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(getActivity() instanceof PhoneLoungeActivity)
            ((PhoneLoungeActivity) getActivity()).setMarginTop(true);
        View view = inflater.inflate(R.layout.fragment_video_player, container, false);
        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_layout, youTubePlayerFragment).commit();
        youTubePlayerFragment.initialize(Constant.YOUTUBE_API_KEY,this);
        ButterKnife.bind(this, view);

        video = ClubModel.getInstance().getVideoById(getPrimaryArgument());

        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        mute = (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)==0);
        muteButton.setSelected(mute);
        isSeekBarTouched = false;
        seekBar.setMax(SEEK_BAR_MAX);
        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float percentage = ((float)seekBar.getProgress()/(float)seekBar.getMax());
                int target = (int)(player.getDurationMillis()*percentage);
                Log.d(TAG, "SEEK TO:  (%) " + percentage);
                Log.d(TAG, "SEEK TO: (ms) " + target);
                Log.d(TAG, "SEEK TO: (duration) " + player.getDurationMillis());
                isSeekBarTouched = false;
                player.seekToMillis(target);
            }
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarTouched = true;
            }
        });
//      progressBar.setVisibility(View.VISIBLE);
        updateTimeInfo();

        updateButtonsColor();
        return view;
    }

    public void updateButtonsColor(){
        if(ThemeManager.getInstance().isLightTheme()){
            playButton.setColorFilter(ContextCompat.getColor(getActivity(),R.color.light_green_main),PorterDuff.Mode.MULTIPLY);
            muteButton.setColorFilter(ContextCompat.getColor(getActivity(),R.color.light_green_main),PorterDuff.Mode.MULTIPLY);
            fullScreenButton.setColorFilter(ContextCompat.getColor(getActivity(),R.color.light_green_main),PorterDuff.Mode.MULTIPLY);
        }else {
            playButton.clearColorFilter();
            muteButton.clearColorFilter();
            fullScreenButton.clearColorFilter();
        }
    }

    YouTubePlayer player;
    // YouTube
    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.setFullscreen(false);
            player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
            player.cueVideo(video.getId());
            this.player = player;
            player.setOnFullscreenListener(this);
            player.setPlaybackEventListener(this);
            player.setPlayerStateChangeListener(this);
        }
    }

    // YouTube
    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult error) {
        // YouTube error
        String errorMessage = error.toString();
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "ErrorMessage:" + errorMessage);
    }

    @Override
    public void onPlaying() {
        Log.d(TAG, "onPlaying");
        playButton.setSelected(true);
        beginPlaybackUpdates();
    }

    @Override
    public void onPaused() {
        playButton.setSelected(false);
    }

    @Override
    public void onStopped() {
        playButton.setSelected(false);
    }

    @Override
    public void onBuffering(boolean b) {
        //TODO @Filip Show progress dialog?
    }

    @Override
    public void onSeekTo(int i) {
        updateTimeInfo();
    }

    @OnClick(R.id.fullscreen_button)
    public void openFullscreen(){
        player.setFullscreen(true);
    }


    @OnClick(R.id.play_button)
    public void togglePlay(){
       if(player.isPlaying()){
           player.pause();
       } else {
           player.play();
       }
        if(ThemeManager.getInstance().isLightTheme()){
            playButton.setColorFilter(ContextCompat.getColor(getActivity(),R.color.light_green_main),PorterDuff.Mode.MULTIPLY);
        }else {
            playButton.setColorFilter(ContextCompat.getColor(getActivity(),R.color.white),PorterDuff.Mode.MULTIPLY);
        }
    }
    final Handler updatesHandler = new Handler();
    private void beginPlaybackUpdates(){
        if(player!=null && player.isPlaying()){
            updateTimeInfo();
            if(!isSeekBarTouched){
                updateSeekBarProgress();
            }
            updatesHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    beginPlaybackUpdates();
                }
            }, 500);
        }
    }

    @Override
    public void onFullscreen(boolean fullscreen) {
        if(fullscreen){
            player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
        } else {
            player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
        }
    }


    @Override
    public void onLoaded(String s) {
        updateTimeInfo();
        player.play();
    }



    @Override
    public void onVideoEnded() {
        player.seekToMillis(0);
        player.pause();
        playButton.setSelected(false);
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {}

    @OnClick(R.id.mute_button)
    public void toggleMute(){
        mute = !mute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_TOGGLE_MUTE, 0);
        } else {
            //noinspection deprecation
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, mute);
        }
        muteButton.setSelected(mute);
    }

    private void updateTimeInfo(){
        long current = 0;
        long total = 0;
        if(player!=null){
            current = player.getCurrentTimeMillis();
            total = player.getDurationMillis();
        }
        String text = String.format(getResources().getString(R.string.time_info_player), current, total);
        timeInfo.setText(text);
    }

    private void updateSeekBarProgress(){
        float current = player.getCurrentTimeMillis();
        float total = player.getDurationMillis();
        float percentage = current/total;
        seekBar.setProgress((int) (percentage*SEEK_BAR_MAX));
    }

    @OnClick(R.id.playlist_button)
    public void goBackToPlaylist(){
        FragmentEvent fragmentEvent = new FragmentEvent(ClubTvPlaylistFragment.class, true);
        fragmentEvent.setId(ClubModel.getInstance().getPlaylistId(video));
        EventBus.getDefault().post(fragmentEvent);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
            player = null;
        }
        super.onDestroy();
    }

    @Override
    public void onLoading() {}
    @Override
    public void onAdStarted() {}
    @Override
    public void onVideoStarted() {}
}
