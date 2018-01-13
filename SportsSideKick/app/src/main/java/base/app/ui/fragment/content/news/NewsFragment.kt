package base.app.ui.fragment.content.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import base.app.R
import base.app.data.news.NewsModel
import base.app.data.news.NewsModel.NewsType.OFFICIAL
import base.app.data.wall.WallNews
import base.app.ui.adapter.content.NewsAdapter
import base.app.ui.fragment.base.BaseFragment
import base.app.util.ui.inflate
import base.app.util.ui.show
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import kotlinx.android.synthetic.main.fragment_news.*

class NewsFragment : BaseFragment() {

    private var type = OFFICIAL
    private val viewModel = NewsViewModel()
    private val disposables = CompositeDisposable()
    private var adapter: NewsAdapter = NewsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        return container.inflate(R.layout.fragment_news)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showHeader()

        recyclerView.adapter = adapter
        loadNews()
    }

    private fun showHeader() {
        headerImage.show(R.drawable.image_wall_background)
    }

    override fun onResume() {
        super.onResume()
        disposables.add(viewModel.getNews()
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribe(this::showNews))
    }

    override fun onPause() {
        disposables.clear()
        super.onPause()
    }

    private fun showNews(news : List<WallNews>) {
        swipeRefreshLayout.isRefreshing = false
        adapter.values.addAll(news)
        adapter.notifyDataSetChanged()
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
}