package base.app.util.ui

import android.widget.ImageView
import base.app.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

fun ImageView.show(uri: Any?, error: Int? = null) {
    ImageLoader.displayImage(uri, this, error)
}

object ImageLoader {

    @JvmStatic
    fun displayRoundImage(uri: Any?, view: ImageView?) {
        displayImage(uri, view, R.drawable.blank_profile_rounded, true)
    }

    @JvmStatic
    @JvmOverloads
    fun displayImage(uri: Any?, view: ImageView?, error: Int? = null, isRound: Boolean = false) {
        if (uri != null) {
            if (view != null && view.context != null) {
                try {
                    Glide.with(view.context)
                            .load(uri)
                            .apply(optionsWith(error, isRound))
                            .into(view)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            if (error != null) {
                displayImage(error, view)
            }
        }
    }

    private fun optionsWith(errorRes: Int?, isRound: Boolean): RequestOptions {
        var options = RequestOptions()
        if (errorRes != null) {
            options = options.error(errorRes)
        }
        if (isRound) {
            options = options.circleCrop()
        }
        return options
    }
}
