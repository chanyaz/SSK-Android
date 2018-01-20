package base.app.ui.fragment.content.news

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import base.app.R
import base.app.data.content.news.NewsModel.NewsType.OFFICIAL
import base.app.data.content.news.NewsRepository
import base.app.ui.adapter.content.NewsAdapter
import base.app.util.ui.BaseFragment
import base.app.util.ui.show
import kotlinx.android.synthetic.main.fragment_news.*

open class NewsFragment : BaseFragment(R.layout.fragment_news) {

    open val newsType = OFFICIAL

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