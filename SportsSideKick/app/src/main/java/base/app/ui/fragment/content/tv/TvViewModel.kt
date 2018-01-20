package base.app.ui.fragment.content.tv

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import base.app.data.content.tv.MediaRepository
import base.app.data.content.tv.inBackground
import com.google.api.services.youtube.model.Playlist

class TvViewModel : ViewModel() {

    lateinit var tvRepo: MediaRepository

    fun loadPlaylists(channelId: String): LiveData<List<Playlist>> {
        return tvRepo.getPlaylists(channelId)
    }

    fun loadVideos(playlistId: String) {
        tvRepo.getVideos(playlistId)
                .inBackground()
                .subscribe()
    }
}
