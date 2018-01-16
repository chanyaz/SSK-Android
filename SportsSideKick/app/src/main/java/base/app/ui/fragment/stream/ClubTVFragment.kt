package base.app.ui.fragment.stream

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import base.app.R
import base.app.data.club.ClubModel
import base.app.ui.adapter.stream.ClubTVAdapter
import base.app.ui.fragment.base.BaseFragment
import base.app.ui.fragment.base.FragmentEvent
import base.app.ui.fragment.base.IgnoreBackHandling
import base.app.util.commons.Utility
import base.app.util.events.stream.ClubTVEvent
import base.app.util.events.stream.ClubTVEvent.Type.CHANNEL_PLAYLISTS_DOWNLOADED
import base.app.util.ui.gone
import base.app.util.ui.inflate
import kotlinx.android.synthetic.main.fragment_club_tv.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@IgnoreBackHandling
class ClubTVFragment : BaseFragment() {

    private val adapter: ClubTVAdapter by lazy { ClubTVAdapter(context) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        return container.inflate(R.layout.fragment_club_tv)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
        backButton?.gone()
    }

    override fun onResume() {
        super.onResume()
        if (Utility.isPhone(activity)) {
            val fragmentEvent = FragmentEvent(YoutubePlayerFragment::class.java)
            EventBus.getDefault().post(fragmentEvent)
        }
        val channelId = resources.getString(R.string.club_tv_channel_id)
        ClubModel.getInstance().requestAllPlaylists(channelId) // This is first time we request club tv instance and playlists too
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun displayPlaylists(event: ClubTVEvent) {
        if (event.eventType == CHANNEL_PLAYLISTS_DOWNLOADED) {
            adapter.values.addAll(ClubModel.getInstance().playlists)
            adapter.notifyDataSetChanged()
        }
    }
}
