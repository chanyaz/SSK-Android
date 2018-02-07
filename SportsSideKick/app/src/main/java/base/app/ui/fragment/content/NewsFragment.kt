package base.app.ui.fragment.content

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import base.app.R
import base.app.data.news.NewsModel
import base.app.data.news.NewsModel.NewsType.OFFICIAL
import base.app.data.news.NewsPageEvent
import base.app.data.wall.WallNews
import base.app.ui.adapter.content.NewsAdapter
import base.app.ui.fragment.base.BaseFragment
import base.app.util.events.post.ItemUpdateEvent
import base.app.util.ui.inflate
import base.app.util.ui.show
import kotlinx.android.synthetic.main.fragment_news.*
import org.greenrobot.eventbus.Subscribe

/**
 * Created by Djordje on 12/29/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
open class NewsFragment : BaseFragment() {

    internal val type = OFFICIAL
    internal var adapter: NewsAdapter = NewsAdapter(OFFICIAL)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        return container?.inflate(R.layout.fragment_news)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        topImage.show(R.drawable.image_wall_background)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.isNestedScrollingEnabled = false

        loadNews()
    }

    private fun loadNews() {
        swipeRefreshLayout.isRefreshing = true
        val existingItems = NewsModel.getInstance().getAllCachedItems(type)
        if (existingItems != null && existingItems.size > 0) {
            adapter.values.addAll(existingItems)
            swipeRefreshLayout.isRefreshing = false
        } else {
            NewsModel.getInstance().loadPage(type)
        }
        swipeRefreshLayout.setOnRefreshListener {
            NewsModel.getInstance().setLoading(false, type)
            NewsModel.getInstance().loadPage(type)
        }
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