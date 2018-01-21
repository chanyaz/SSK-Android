package base.app.ui.fragment.content.wall

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import base.app.R
import base.app.data.content.wall.nextmatch.NextMatchModel
import base.app.util.commons.NextMatchCountdown
import base.app.util.ui.ImageLoader
import base.app.util.ui.inflate
import base.app.util.ui.show
import kotlinx.android.synthetic.main.match_header_view.view.*

class MatchHeaderView(context: Context, attrs: AttributeSet)
    : RelativeLayout(context, attrs) {

    init {
        showMatchInfo()
    }

    private fun showMatchInfo() {
        inflate(R.layout.match_header_view)

        headerImage.show(R.drawable.header_background)

        if (NextMatchModel.getInstance().isNextMatchUpcoming) {
            val newsTickerInfo = NextMatchModel.getInstance().tickerInfo
            ImageLoader.displayImage(newsTickerInfo.firstClubUrl, wallLeftTeamImage, null)
            ImageLoader.displayImage(newsTickerInfo.secondClubUrl, wallRightTeamImage, null)
            wallLeftTeamName.text = newsTickerInfo.firstClubName
            wallRightTeamName.text = newsTickerInfo.secondClubName
            val timestamp = java.lang.Long.parseLong(newsTickerInfo.matchDate)
            wallTeamTime.text = NextMatchCountdown.getTextValue(context, timestamp, false)
        } else {
            wallTopInfoContainer.visibility = View.GONE
            topCaption.visibility = View.VISIBLE
        }
    }
}