package base.app.ui.fragment.content.tv

import com.google.api.services.youtube.model.Playlist

interface ITvView {
    fun showPlaylists(items: List<Playlist>)
}