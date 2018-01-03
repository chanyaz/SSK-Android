package base.app.util.ui

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

object ImageLoader {

    @JvmStatic
    fun displayImage(uri: String?, view: ImageView, placeholder: Int? = null) {
        if (uri != null) {
            Glide.with(view.context)
                    .load(uri)
                    .apply(optionsWith(placeholder))
                    .into(view)
        }
    }

    private fun optionsWith(placeholder: Int?): RequestOptions {
        var options = RequestOptions()
        if (placeholder != null) {
            options = options.placeholder(placeholder)
        }
        return options
    }
}