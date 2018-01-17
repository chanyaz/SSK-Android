package base.app.ui.fragment.content.tv

import android.arch.lifecycle.LiveData
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener
import com.google.android.youtube.player.YouTubePlayer.Provider

class YoutubePlayerLiveData : LiveData<YouTubePlayer>(), OnInitializedListener {

    internal var player: YouTubePlayer? = null

    override fun onActive() {

    }

    override fun onInitializationSuccess(provider: Provider, player: YouTubePlayer, wasRestored: Boolean) {

    }

    override fun onInitializationFailure(provider: Provider, error: YouTubeInitializationResult) {

    }

    override fun onInactive() {
        player?.release()
    }
}