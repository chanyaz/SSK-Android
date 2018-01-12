package base.app.util.ui

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup

fun ViewGroup.inflate(layoutId: Int): View {
    return LayoutInflater.from(context).inflate(
            layoutId, this, false)
}

fun View.visible() {
    visibility = VISIBLE
}

fun View.hide() {
    visibility = GONE
}