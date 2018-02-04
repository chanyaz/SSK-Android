package base.app.ui.fragment.content

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import base.app.R
import base.app.data.news.NewsModel.NewsType.UNOFFICIAL
import base.app.data.news.NewsModel.getInstance
import base.app.data.news.NewsPageEvent
import base.app.data.wall.WallNews
import base.app.ui.adapter.content.RumoursAdapter
import base.app.ui.fragment.base.BaseFragment
import base.app.util.events.post.ItemUpdateEvent
import base.app.util.ui.show
import kotlinx.android.synthetic.main.fragment_news.*
import org.greenrobot.eventbus.Subscribe

/**
 * Created by Djordje on 01/03/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
class RumoursFragment : BaseFragment() {

    private val type = UNOFFICIAL
    private val adapter: RumoursAdapter = RumoursAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showTitle()
        showRumours()
    }

    private fun showRumours() {
        swipeRefreshLayout.isRefreshing = true
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        if (getInstance().getAllCachedItems(type).size > 0) {
            val event = NewsPageEvent(getInstance().getAllCachedItems(type))
            onNewsReceived(event)
        } else {
            getInstance().loadPage(type)
        }
        swipeRefreshLayout.setOnRefreshListener {
            getInstance().setLoading(false, type)
            getInstance().loadPage(type)
        }
    }

    private fun showTitle() {
        topCaption.text = getString(R.string.rumours)
        topImage.show(R.drawable.image_wall_background)
    }

    @Subscribe
    fun onNewsReceived(event: NewsPageEvent) {
        swipeRefreshLayout.isRefreshing = false
        adapter.values.addAll(event.values)
        adapter.notifyDataSetChanged()
    }

    @Subscribe
    fun onItemUpdate(event: ItemUpdateEvent) {
        val news = event.post as WallNews
        adapter.values.forEachIndexed { index, item ->
            if (item.postId == news.postId) {
                adapter.values.remove(item)
                adapter.values.add(index, news)
                adapter.notifyDataSetChanged()
                return
            }
        }
    }
}