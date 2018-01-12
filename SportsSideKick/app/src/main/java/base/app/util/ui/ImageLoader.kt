package base.app.util.ui

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

fun ImageView.show(uri: Any?) {
    ImageLoader.displayImage(uri, this)
}

object ImageLoader {

    @JvmStatic
    @JvmOverloads
    fun displayImage(uri: Any?, view: ImageView,
                     error: Int? = null) {
        if (uri != null) {
            Glide.with(view.context)
                    .load(uri)
                    .apply(optionsWith(error))
                    .into(view)
        } else {
            displayImage(error, view)
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