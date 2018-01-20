package base.app.ui.fragment.content.tv

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.PorterDuff
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import base.app.Keys.YOUTUBE_API_KEY
import base.app.R
import base.app.data.content.tv.MediaModel
import base.app.ui.fragment.base.BaseFragment
import base.app.util.events.FragmentEvent
import base.app.util.events.ClubTVEvent
import base.app.util.ui.inflate
import butterknife.OnClick
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.*
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle.CHROMELESS
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle.DEFAULT
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.google.api.services.youtube.model.Video
import kotlinx.android.synthetic.main.fragment_video_player.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class VideoContainerFragment : BaseFragment(),
        PlaybackEventListener,
        OnFullscreenListener,
        PlayerStateChangeListener {

    private val viewModel by lazy {
        ViewModelProviders.of(activity!!)
                .get(TvViewModel::class.java)
    }
    internal var fullScreen = false
    private val audioManager by lazy {
        activity?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    internal var mute: Boolean = false
    internal var isSeekBarTouched: Boolean = false
    var isFullscreen: Boolean = false
    internal var video: Video? = null
    lateinit var player: YouTubePlayer
    internal val updatesHandler = Handler()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        return container.inflate(R.layout.fragment_video_player)
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        val playerFragment = childFragmentManager.findFragmentById(R.id.youtube_layout)
                as YouTubePlayerSupportFragment
        val playerData = YoutubePlayerLiveData()
        playerFragment.initialize(YOUTUBE_API_KEY, playerData)
        playerData.observe(this, Observer {
            val player = it
            if (player != null) {
                player.setPlayerStyle(CHROMELESS)
                if (video != null)
                    // player.cueVideo(video.id)
                this.player = player
                player.setOnFullscreenListener(this)
                player.setPlaybackEventListener(this)
                player.setPlayerStateChangeListener(this)
            }
        })

        if (primaryArgument != null) {
            video = MediaModel.getVideoById(primaryArgument)
        } else {
            if (MediaModel.videos.size != 0) {
                video = MediaModel.videos[0]
            }
        }
        mute = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0
        muteButton.isSelected = mute
        isSeekBarTouched = false
        seekBar.max = SEEK_BAR_MAX
        seekBar.progress = 0
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val percentage = seekBar.progress.toFloat() / seekBar.max.toFloat()
                val target = (player.durationMillis * percentage).toInt()
                isSeekBarTouched = false
                player.seekToMillis(target)
            }

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                isSeekBarTouched = true
            }
        })
        updateTimeInfo()
        updateButtonsColor()
    }

    fun updateButtonsColor() {
        playButton.clearColorFilter()
        muteButton.clearColorFilter()
        fullscreenButton.clearColorFilter()
    }

    // YouTube
    fun onInitializationFailure(provider: Provider, error: YouTubeInitializationResult) {
        // YouTube error
        val errorMessage = error.toString()
        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
        Log.d(TAG, "ErrorMessage:" + errorMessage)
    }

    override fun onPlaying() {
        Log.d(TAG, "onPlaying")
        playButton.isSelected = true
        beginPlaybackUpdates()
    }

    override fun onPaused() {
        playButton.isSelected = false
    }

    override fun onStopped() {
        playButton.isSelected = false
    }

    override fun onBuffering(b: Boolean) {
        //TODO @Filip Show progress dialog?
    }

    override fun onSeekTo(i: Int) {
        updateTimeInfo()
    }

    @OnClick(R.id.fullscreenButton)
    fun openFullscreen() {
        if (player != null) {
            player.fullscreenControlFlags = YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION
            player.setFullscreen(true)
        }
    }

    @OnClick(R.id.playButton)
    fun togglePlay() {
        if (player != null) {
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
            playButton.setColorFilter(ContextCompat.getColor(activity!!, R.color.white), PorterDuff.Mode.MULTIPLY)
        }
    }

    private fun beginPlaybackUpdates() {
        if (player != null && player.isPlaying) {
            updateTimeInfo()
            if (!isSeekBarTouched) {
                updateSeekBarProgress()
            }
            updatesHandler.postDelayed({ beginPlaybackUpdates() }, 500)
        }
    }

    override fun onFullscreen(fullscreen: Boolean) {
        this.fullScreen = fullscreen
        if (fullscreen) {
            player.setPlayerStyle(DEFAULT)
        } else {
            player.setPlayerStyle(CHROMELESS)
        }
    }

    override fun onLoaded(s: String) {
        updateTimeInfo()
        player.play()
    }


    override fun onVideoEnded() {
        player.seekToMillis(0)
        player.pause()
        playButton.isSelected = false
    }

    override fun onError(errorReason: YouTubePlayer.ErrorReason) {}

    @OnClick(R.id.muteButton)
    fun toggleMute() {
        mute = !mute
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, mute)
        muteButton.isSelected = mute
    }

    private fun updateTimeInfo() {
        var current: Long = 0
        var total: Long = 0
        if (player != null) {
            current = player.currentTimeMillis.toLong()
            total = player.durationMillis.toLong()
        }
        if (activity != null) {
            val text = String.format("%1\$tM:%1\$tS / %2\$tM:%2\$tS", current, total)
            // TODO: timeInfo.text = text
        }
    }

    private fun updateSeekBarProgress() {
        val current = player.currentTimeMillis.toFloat()
        val total = player.durationMillis.toFloat()
        val percentage = current / total
        seekBar.progress = (percentage * SEEK_BAR_MAX).toInt()
    }

    fun goBackToPlaylist() {
        val fragmentEvent = FragmentEvent(TvPlaylistFragment::class.java)
        fragmentEvent.id = MediaModel.getPlaylistId(video!!)
        EventBus.getDefault().post(fragmentEvent)
    }

    override fun onLoading() {}

    override fun onAdStarted() {}

    override fun onVideoStarted() {}

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun displayPlaylist(event: ClubTVEvent) {
        if (event.eventType == ClubTVEvent.Type.FIRST_VIDEO_DATA_DOWNLOADED) {
            video = MediaModel.getVideoById(event.id)
        }
    }

    companion object {
        private val TAG = "YOUTUBE PLAYER"
        private val SEEK_BAR_MAX = 10000
    }
}
