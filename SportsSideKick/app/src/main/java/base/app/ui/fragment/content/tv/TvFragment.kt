package base.app.ui.fragment.content.tv

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import base.app.R
import base.app.data.content.tv.MediaModel.youtubeDataApi
import base.app.data.content.tv.MediaRepository
import base.app.ui.adapter.stream.TvAdapter
import base.app.ui.fragment.base.BaseFragment
import base.app.ui.fragment.base.IgnoreBackHandling
import base.app.ui.fragment.content.wall.injectViewModel
import base.app.util.events.FragmentEvent
import base.app.util.ui.inflate
import kotlinx.android.synthetic.main.fragment_tv.*
import org.greenrobot.eventbus.EventBus

@IgnoreBackHandling
class TvFragment : BaseFragment() {

    private val viewModel by lazy { injectViewModel<TvViewModel>() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        return container.inflate(R.layout.fragment_tv)
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        EventBus.getDefault().post(
                FragmentEvent(VideoContainerFragment::class.java))

        val adapter = TvAdapter()
        recyclerView.adapter = adapter

        val channelId = getString(R.string.youtube_channel_id)
        viewModel.tvRepo = MediaRepository(youtubeDataApi)
        viewModel.loadPlaylists(channelId).observe(this, Observer {
            adapter.addAll(it)
        })
    }
}