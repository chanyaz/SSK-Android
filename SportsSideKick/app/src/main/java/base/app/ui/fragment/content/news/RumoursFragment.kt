package base.app.ui.fragment.content.news

import android.os.Bundle
import android.view.View
import base.app.R
import base.app.data.content.news.NewsModel.NewsType.UNOFFICIAL
import kotlinx.android.synthetic.main.fragment_news.*

class RumoursFragment : NewsFragment() {

    override val newsType = UNOFFICIAL

    override fun onViewCreated(view: View, state: Bundle?) {
        super.onViewCreated(view, state)
        topCaption.text = getString(R.string.rumours)
    }
}