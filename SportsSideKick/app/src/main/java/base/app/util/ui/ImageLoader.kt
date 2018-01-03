package base.app.util.ui

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

object ImageLoader {

    @JvmStatic
    @JvmOverloads
    fun displayImage(uri: String?, view: ImageView,
                     placeholder: Int? = null, error: Int? = null) {
        if (uri != null) {
            Glide.with(view.context)
                    .load(uri)
                    .apply(optionsWith(placeholder, error))
                    .into(view)
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