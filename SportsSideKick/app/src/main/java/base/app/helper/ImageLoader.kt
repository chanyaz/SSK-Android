package base.app.helper

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

fun ImageView.showImage(image: Any) {
    Glide.with(context)
            .load(image)
            .into(this)
}

fun ImageView.showRoundImage(image: Any) {
    Glide.with(context)
            .load(image)
            .apply(RequestOptions.circleCropTransform())
            .into(this)
}