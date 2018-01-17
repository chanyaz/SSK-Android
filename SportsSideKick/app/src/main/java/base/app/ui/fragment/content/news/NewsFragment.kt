package base.app.ui.fragment.content.news

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import base.app.R
import base.app.data.news.NewsModel.NewsType.OFFICIAL
import base.app.data.news.NewsRepository
import base.app.ui.adapter.content.NewsAdapter
import base.app.ui.fragment.base.BaseFragment
import base.app.util.ui.inflate
import base.app.util.ui.show
import kotlinx.android.synthetic.main.fragment_news.*

open class NewsFragment : BaseFragment() {

    open val newsType = OFFICIAL

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        return container.inflate(R.layout.fragment_news)
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        headerImage.show(R.drawable.header_background)

        val adapter = NewsAdapter()
        recyclerView.adapter = adapter
        swipeRefreshLayout.isRefreshing = true

        val viewModel = ViewModelProviders.of(this).get(NewsViewModel::class.java)
        viewModel.newsRepo = NewsRepository()
        viewModel.loadNews(newsType).observe(this, Observer {
            swipeRefreshLayout.isRefreshing = false
            adapter.addAll(it)
        })
    }
}