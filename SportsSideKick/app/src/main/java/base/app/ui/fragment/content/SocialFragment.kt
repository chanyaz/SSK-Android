package base.app.ui.fragment.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import base.app.R
import base.app.data.news.NewsModel.NewsType
import base.app.data.news.NewsModel.getInstance
import base.app.data.news.NewsPageEvent
import base.app.ui.adapter.content.NewsAdapter
import base.app.ui.fragment.base.BaseFragment
import base.app.util.ui.show
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
    private val adapter: NewsAdapter = NewsAdapter(type)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showTitle()
        showItems()
    }

    private fun showItems() {
        swipeRefreshLayout.isRefreshing = true

        recyclerView.adapter = adapter
        recyclerView.itemAnimator = SlideInUpAnimator(OvershootInterpolator(1f))

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
        topCaption.text = "Social"
        topImage.show(R.drawable.image_wall_background)
    }

    @Subscribe
    fun onNewsReceived(event: NewsPageEvent) {
        if (event.values.isNotEmpty()) {
            adapter.values.addAll(event.values)
            adapter.notifyDataSetChanged()
        }
        swipeRefreshLayout.isRefreshing = false
    }
}