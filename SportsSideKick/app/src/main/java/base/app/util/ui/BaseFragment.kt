package base.app.util.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable

// TODO: After converting all fragments to Kotlin, make layoutId parameter not nullable and remove super() call in onCreateView
abstract class BaseFragment (private val layoutId: Int? = null) : Fragment() {

    val disposables: CompositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        if (layoutId == null) {
            super.onCreateView(inflater, container, state)
            return null
        }
        return container.inflate(layoutId)
    }

    fun ViewGroup?.inflate(layoutId: Int): View? {
        if (this == null) return null
        return LayoutInflater.from(context).inflate(
                layoutId, this, false)
    }

    var data: Any? = null

    protected val primaryArgument: String?
        get() = arguments!!.getString(PRIMARY_ARG_TAG)

    protected val secondaryArgument: String?
        get() = arguments!!.getString(SECONDARY_ARG_TAG)

    protected val stringArrayArgument: List<String>?
        get() = if (arguments!!.containsKey(STRING_ARRAY_ARG_TAG)) {
            arguments!!.getStringArrayList(STRING_ARRAY_ARG_TAG)
        } else null

    companion object {
        const val PRIMARY_ARG_TAG = "PRIMARY_ARG_TAG"
        const val SECONDARY_ARG_TAG = "SECONDARY_ARG_TAG"
        const val ITEM_ARG_TAG = "ITEM_ARG_TAG"
        const val STRING_ARRAY_ARG_TAG = "STRING_ARRAY_ARG_TAG"
        const val INITIATOR = "INITIATOR_ARG_TAG"
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }
}