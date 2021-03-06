package base.app.ui.fragment.content.tv

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import base.app.R
import base.app.data.content.tv.MediaModel.youtubeDataApi
import base.app.data.content.tv.MediaRepository
import base.app.ui.adapter.stream.TvAdapter
import base.app.ui.fragment.base.IgnoreBackHandling
import base.app.util.events.FragmentEvent
import base.app.util.ui.BaseFragment
import base.app.util.ui.inject
import kotlinx.android.synthetic.main.fragment_tv.*
import org.greenrobot.eventbus.EventBus

@IgnoreBackHandling
class TvFragment : BaseFragment(R.layout.fragment_tv) {

    override fun onViewCreated(view: View, state: Bundle?) {
        EventBus.getDefault().post(
                FragmentEvent(VideoContainerFragment::class.java))

        val adapter = TvAdapter()
        recyclerView.adapter = adapter

        val channelId = getString(R.string.youtube_channel_id)
        val viewModel = inject<TvViewModel>()
        viewModel.tvRepo = MediaRepository(youtubeDataApi)
        viewModel.loadPlaylists(channelId).observe(this, Observer {
            adapter.addAll(it)
        })
    }
}