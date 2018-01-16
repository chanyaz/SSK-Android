package base.app.ui.fragment.content.tv.viewmodel

import android.arch.lifecycle.ViewModel
import base.app.data.club.MediaRepository
import base.app.data.club.inBackground
import io.reactivex.disposables.CompositeDisposable

class TvViewModel : ViewModel() {

    lateinit var tvRepo: MediaRepository
    lateinit var view: ITvView

    private val disposables = CompositeDisposable()

    fun getPlaylists(channelId: String) {
        disposables.add(tvRepo
                .getPlaylists(channelId)
                .inBackground()
                .subscribe { view.showPlaylists(it) })
    }

    fun getVideos(playlistId: String) {
        disposables.add(tvRepo
                .getVideos(playlistId)
                .inBackground()
                .subscribe())
    }

    fun onDestroy() {
        disposables.clear()
    }
}
