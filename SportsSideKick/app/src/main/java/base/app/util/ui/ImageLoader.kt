package base.app.util.ui

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

fun ImageView.showImage(uri: Any?) {
    ImageLoader.displayImage(uri, this)
}

object ImageLoader {

    @JvmStatic
    @JvmOverloads
    fun displayImage(uri: Any?, view: ImageView,
                     placeholder: Int? = null, error: Int? = null) {
        if (uri != null) {
            Glide.with(view.context)
                    .load(uri)
                    .apply(optionsWith(placeholder, error))
                    .into(view)
        } else {
            displayImage(error, view)
        }
    }

    private fun optionsWith(placeholderRes: Int?, errorRes: Int?): RequestOptions {
        var options = RequestOptions()
        if (placeholderRes != null) {
            options = options.placeholder(placeholderRes)
        }
        if (errorRes != null) {
            options = options.error(errorRes)
        }
        return options
    }
}