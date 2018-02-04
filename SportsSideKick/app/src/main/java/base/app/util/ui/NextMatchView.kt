package base.app.util.ui

import android.content.Context
import android.os.Handler
import android.text.Html
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import base.app.R
import base.app.data.ticker.NextMatchModel
import base.app.util.commons.NextMatchCountdown
import kotlinx.android.synthetic.main.next_match_view.view.*
import java.lang.Long.parseLong

/**
 * Created by Filip on 10/23/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
class NextMatchView(context: Context, attrs: AttributeSet)
    : RelativeLayout(context, attrs) {

    init {
        showMatchInfo()
    }

    private fun showMatchInfo() {
        inflate(R.layout.next_match_view)

        val info = NextMatchModel.getInstance().loadTickerInfoFromCache()
        if (info != null && NextMatchModel.getInstance().isNextMatchUpcoming) {
            logoOfFirstTeam.show(info.firstClubUrl)
            logoOfSecondTeam.show(info.secondClubUrl)

            nameOfFirstTeam.text = info.firstClubName
            nameOfSecondTeam.text = info.secondClubName

            val timestamp = parseLong(info.matchDate)
            date.text = NextMatchCountdown.getTextValue(context, timestamp, true)

            Handler().postDelayed(object : Runnable {
                override fun run() {
                    updateCountdownTimer(timestamp)
                    postDelayed(this, 500L)
                }
            }, 500L)

            nextMatchContainer.visible()
        } else {
            nextMatchContainer.hide()
        }
        label.text = Html.fromHtml(context.getString(R.string.slogan))
    }

    private fun updateCountdownTimer(timestamp: Long) {
        countdown.text = NextMatchCountdown.getCountdownValue(timestamp)
    }

    fun ViewGroup.inflate(layoutId: Int) =
            View.inflate(context, layoutId, this)
}
