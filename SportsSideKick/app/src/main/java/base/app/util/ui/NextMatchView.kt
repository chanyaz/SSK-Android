package base.app.util.ui

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.View
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

    internal var timestamp: Long = 0

    init {
        showMatchInfo()
    }

    private fun showMatchInfo() {
        inflate(R.layout.next_match_view)

        val info = NextMatchModel.getInstance().loadTickerInfoFromCache()
        if (info != null && NextMatchModel.getInstance().isNextMatchUpcoming) {
            updateCountdownTimer()
            if (logoOfFirstTeam.drawable == null) {
                logoOfFirstTeam.showImage(info.firstClubUrl)
                logoOfSecondTeam.showImage(info.secondClubUrl)
            }
            timestamp = parseLong(info.matchDate)
            nameOfFirstTeam.text = info.firstClubName
            nameOfSecondTeam.text = info.secondClubName
            date.text = NextMatchCountdown.getTextValue(context, timestamp, true)

            val handler = Handler()
            val delay = 100 // milliseconds

            handler.postDelayed(object : Runnable {
                override fun run() {
                    updateCountdownTimer()
                    handler.postDelayed(this, delay.toLong())
                }
            }, delay.toLong())
            nextMatchContainer.visibility = View.VISIBLE
        } else {
            nextMatchContainer.visibility = View.GONE
        }
    }

    private fun View.inflate(layoutRes: Int) =
            View.inflate(context, layoutRes, this@NextMatchView)

    private fun updateCountdownTimer() {
        countdown.text = NextMatchCountdown.getCountdownValue(timestamp)
    }
}
