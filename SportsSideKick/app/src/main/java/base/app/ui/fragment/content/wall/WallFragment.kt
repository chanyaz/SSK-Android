package base.app.ui.fragment.content.wall

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import base.app.R
import base.app.ui.adapter.content.WallAdapter
import base.app.util.ui.BaseFragment
import base.app.util.ui.injectViewModel
import base.app.util.ui.show
import kotlinx.android.synthetic.main.fragment_wall.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class WallFragmentNew : BaseFragment(R.layout.fragment_wall) {

    override fun onViewCreated(view: View, state: Bundle?) {
        val wallViewModel = injectViewModel<WallViewModel>()
        val loginViewModel = activity.injectViewModel<LoginViewModel>()

        headerImage.show(R.drawable.header_background)
        postButton.onClick { wallViewModel.onPostClicked() }

        // TODO: loginViewModel.getLoginState()
        // login
        // wallgetitems
        // listen for gs messages and add/update/remove

        val adapter = WallAdapter(context)
        recyclerView.adapter = adapter
        wallViewModel.getItems().observe(this, Observer {
            adapter.clear()
            adapter.addAll(it)
        })
    }
}