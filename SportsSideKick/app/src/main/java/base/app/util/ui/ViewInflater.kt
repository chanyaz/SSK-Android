package base.app.util.ui

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup

fun ViewGroup.inflate(layoutRes: Int) =
        View.inflate(context, layoutRes, this)

fun View.visible() {
    visibility = VISIBLE
}

fun View.hide() {
    visibility = GONE
}