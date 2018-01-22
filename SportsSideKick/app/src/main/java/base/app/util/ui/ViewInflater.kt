package base.app.util.ui

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

fun View.setVisible(visible: Boolean) {
    visibility = if (visible) {
        VISIBLE
    } else {
        GONE
    }
}

inline fun <reified T : ViewModel> Fragment.inject(): T {
    return ViewModelProviders.of(this).get(T::class.java)
}

inline fun <reified T : ViewModel> FragmentActivity?.inject(): T {
    return ViewModelProviders.of(this!!).get(T::class.java)
}