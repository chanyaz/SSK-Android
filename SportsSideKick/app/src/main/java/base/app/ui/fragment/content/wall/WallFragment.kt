package base.app.ui.fragment.content.wall

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import base.app.R
import base.app.data.content.news.WallRepository
import base.app.ui.adapter.content.WallAdapter
import base.app.util.ui.inflate
import base.app.util.ui.injectViewModel
import base.app.util.ui.show
import kotlinx.android.synthetic.main.fragment_wall.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class WallFragmentNew : Fragment() {

    private val adapter by lazy { WallAdapter(context) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        return container.inflate(R.layout.fragment_wall)
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        val wallViewModel = injectViewModel<WallViewModel>()
        val loginViewModel = activity.injectViewModel<LoginViewModel>()
        wallViewModel.wallRepo = WallRepository()

        headerImage.show(R.drawable.header_background)
        postButton.onClick { wallViewModel.onPostClicked() }

        // TODO: loginViewModel.getLoginState()
        // login
        // wallgetitems
        // listen for gs messages and add/update/remove

        recyclerView.adapter = adapter
        wallViewModel.getItems().observe(this, Observer {
            adapter.clear()
            adapter.addAll(it)
        })
    }
}