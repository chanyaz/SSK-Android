package base.app.ui.fragment.content

import android.os.Bundle
import android.view.View
import base.app.R
import base.app.data.news.NewsModel.NewsType.UNOFFICIAL
import base.app.ui.fragment.content.news.NewsFragment
import kotlinx.android.synthetic.main.fragment_news.*

class RumoursFragment : NewsFragment() {

    override val newsType = UNOFFICIAL

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topCaption.text = getString(R.string.rumours)
    }
}