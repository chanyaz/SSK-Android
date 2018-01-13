package base.app.ui.fragment.content.news

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import base.app.R
import base.app.data.news.NewsModel
import base.app.data.news.NewsModel.NewsType.OFFICIAL
import base.app.data.news.NewsPageEvent
import base.app.ui.adapter.content.NewsAdapter
import base.app.ui.fragment.base.BaseFragment
import base.app.util.ui.inflate
import base.app.util.ui.show
import kotlinx.android.synthetic.main.fragment_news_list.*
import org.greenrobot.eventbus.Subscribe

/**
 * Created by Djordje on 12/29/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
class NewsListFragment : BaseFragment() {

    internal var type = OFFICIAL
    internal var adapter: NewsAdapter = NewsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        return container?.inflate(R.layout.fragment_news_list)
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
}