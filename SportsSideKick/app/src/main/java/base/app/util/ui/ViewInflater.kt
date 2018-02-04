package base.app.util.ui

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
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

inline fun <reified T : ViewModel> Fragment.inject(): T {
    return ViewModelProviders.of(this).get(T::class.java)
}

inline fun <reified T : ViewModel> FragmentActivity?.inject(): T {
    return ViewModelProviders.of(this!!).get(T::class.java)
}