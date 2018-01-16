package base.app.ui.fragment.content.tv

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import base.app.R
import base.app.data.club.MediaModel.youtubeDataApi
import base.app.data.club.MediaRepository
import base.app.ui.adapter.stream.ClubTVAdapter
import base.app.ui.fragment.base.BaseFragment
import base.app.ui.fragment.base.IgnoreBackHandling
import base.app.ui.fragment.content.tv.viewmodel.ITvView
import base.app.ui.fragment.content.tv.viewmodel.TvViewModel
import base.app.util.ui.inflate
import com.google.api.services.youtube.model.Playlist
import kotlinx.android.synthetic.main.fragment_tv.*

@IgnoreBackHandling
class TvFragment : BaseFragment(), ITvView {

    private val viewModel by lazy {
        ViewModelProviders.of(this)
                .get(TvViewModel::class.java)
    }
    val adapter = ClubTVAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        return container.inflate(R.layout.fragment_tv)
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        recyclerView.adapter = adapter

        viewModel.tvRepo = MediaRepository(youtubeDataApi)
        viewModel.view = this

        /* TODO
        EventBus.getDefault().post(
                FragmentEvent(YoutubePlayerFragment::class.java))
                */

        val channelId = getString(R.string.youtube_channel_id)
        viewModel.getPlaylists(channelId)
    }

    override fun showPlaylists(items: List<Playlist>) {
        adapter.addAll(items)
    }

    override fun onDestroy() {
        viewModel.onDestroy()
        super.onDestroy()
    }
}