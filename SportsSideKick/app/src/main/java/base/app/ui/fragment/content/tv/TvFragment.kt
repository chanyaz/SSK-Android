package base.app.ui.fragment.content.tv

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import base.app.R
import base.app.data.tv.MediaModel.youtubeDataApi
import base.app.data.tv.MediaRepository
import base.app.ui.adapter.stream.TvAdapter
import base.app.ui.fragment.base.BaseFragment
import base.app.data.FragmentEvent
import base.app.ui.fragment.base.IgnoreBackHandling
import base.app.util.ui.inflate
import kotlinx.android.synthetic.main.fragment_tv.*
import org.greenrobot.eventbus.EventBus

@IgnoreBackHandling
class TvFragment : BaseFragment() {

    private val viewModel by lazy {
            ViewModelProviders.of(activity!!)
                .get(TvViewModel::class.java)
    }

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