package base.app.util.ui

import android.widget.ImageView
import base.app.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

fun ImageView.show(uri: Any?, error: Int? = null) {
    ImageLoader.displayImage(uri, this, error)
}

fun ImageView.showAvatar(uri: Any?) {
    ImageLoader.displayImage(uri, this, R.drawable.avatar_placeholder)
}

object ImageLoader {

    @JvmStatic
    @JvmOverloads
    fun displayImage(uri: Any?, view: ImageView, error: Int? = null) {
        if (uri != null) {
            Glide.with(view.context)
                    .load(uri)
                    .apply(optionsWith(error))
                    .into(view)
        }
    }

    private fun optionsWith(errorRes: Int?): RequestOptions {
        var options = RequestOptions()
        if (errorRes != null) {
            options = options.error(errorRes)
        }
        return options
    }
}