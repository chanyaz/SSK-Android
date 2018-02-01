package base.app.util.ui

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

fun ImageView.show(uri: Any?, error: Int? = null) {
    ImageLoader.displayImage(uri, this, error)
}

object ImageLoader {

    @JvmStatic
    @JvmOverloads
    fun displayImage(uri: Any?, view: ImageView, error: Int? = null) {
        if (uri != null) {
            if (view.context != null) {
                Glide.with(view.context)
                        .load(uri)
                        .apply(optionsWith(error))
                        .into(view)
            }
        } else {
            if (error != null) {
                displayImage(error, view)
            }
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
