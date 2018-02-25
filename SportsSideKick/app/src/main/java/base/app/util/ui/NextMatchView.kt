package base.app.util.ui

import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import base.app.R
import base.app.R.string
import base.app.data.wall.ticker.NextMatchModel
import base.app.util.commons.NextMatchCountdown
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.next_match_view.view.*
import java.lang.Long.parseLong
import java.util.concurrent.TimeUnit

/**
 * Created by Filip on 10/23/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
class NextMatchView(context: Context, attrs: AttributeSet)
    : RelativeLayout(context, attrs) {

    private val disposables = CompositeDisposable()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        showMatchInfo()
    }

    override fun onDetachedFromWindow() {
        disposables.clear()
        super.onDetachedFromWindow()
    }

    private fun showMatchInfo() {
        inflate(R.layout.next_match_view)

        val info = NextMatchModel.getInstance().loadTickerInfoFromCache()
        if (info != null && NextMatchModel.getInstance().isNextMatchUpcoming) {
            logoOfFirstTeam.show(info.firstClubUrl)
            logoOfSecondTeam.show(R.drawable.club_logo)

            nameOfFirstTeam.text = info.firstClubName
            nameOfSecondTeam.text = info.secondClubName

            val timestamp = parseLong(info.matchDate)
            val textValue = NextMatchCountdown.getTextValue(context, timestamp, true)
            date.text = textValue

            disposables.add(Observable.interval(
                    1000,
                    500,
                    TimeUnit.MILLISECONDS)
                    .observeOn(mainThread())
                    .subscribe {
                        if (textValue == context.getString(R.string.live)) {
                            countdown.text = date.text
                        } else {
                            updateCountdownTimer(timestamp)
                        }
                    })

            nextMatchSplashContainer.visible()
        } else {
            nextMatchSplashContainer.hide()
        }
        showSloganText()
    }

    private fun showSloganText() {
        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            label.text = Html.fromHtml(context.getString(string.slogan), FROM_HTML_MODE_LEGACY)
        } else {
            @SuppressWarnings("deprecation")
            label.text = Html.fromHtml(context.getString(string.slogan))
        }
    }

    private fun updateCountdownTimer(timestamp: Long) {
        countdown.text = NextMatchCountdown.getCountdownValue(timestamp)
    }

    fun ViewGroup.inflate(layoutId: Int) =
            View.inflate(context, layoutId, this)
}
