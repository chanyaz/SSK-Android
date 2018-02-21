package base.app.ui.fragment.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import base.app.R
import base.app.R.dimen
import base.app.data.news.NewsModel.NewsType
import base.app.data.news.NewsModel.getInstance
import base.app.data.news.NewsPageEvent
import base.app.ui.adapter.content.NewsAdapter
import base.app.ui.fragment.base.BaseFragment
import base.app.util.events.post.AutoTranslateEvent
import base.app.util.ui.LinearItemDecoration
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.android.synthetic.main.fragment_news.*
import org.greenrobot.eventbus.Subscribe

/**
 * Created by Djordje on 01/03/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
class SocialFragment : BaseFragment() {

    private val type = NewsType.SOCIAL
    private val adapter: NewsAdapter by lazy { NewsAdapter(type, getString(R.string.social)) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showItems()
    }

    private fun showItems() {
        swipeRefreshLayout.isRefreshing = true

        recyclerView.adapter = adapter
        recyclerView.itemAnimator = SlideInUpAnimator(OvershootInterpolator(1f))
        recyclerView.addItemDecoration(LinearItemDecoration(resources.getDimension(dimen.item_spacing_news).toInt()))

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

    @Subscribe
    fun onNewsReceived(event: NewsPageEvent) {
        if (event.values.isNotEmpty()) {
            adapter.values.clear()
            adapter.values.addAll(event.values)
            adapter.notifyDataSetChanged()
        }
        swipeRefreshLayout.isRefreshing = false
    }

    @Subscribe
    fun onAutoTranslateToggle(event: AutoTranslateEvent) {
        if (event.isEnabled) {
            // Translate
            adapter.notifyDataSetChanged()
        } else {
            // Undo translation
            showItems()
        }
    }
}