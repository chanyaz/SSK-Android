package base.app.ui.fragment.content.wall

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import base.app.R
import base.app.ui.adapter.content.WallAdapter
import base.app.ui.fragment.content.wall.UserViewModel.SessionState.Anonymous
import base.app.util.ui.BaseFragment
import base.app.util.ui.inject
import base.app.util.ui.setVisible
import base.app.util.ui.show
import kotlinx.android.synthetic.main.fragment_wall.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class WallFragment : BaseFragment(R.layout.fragment_wall) {

    override fun onViewCreated(view: View, state: Bundle?) {
        val wallViewModel = inject<WallViewModel>()
        val userViewModel = activity.inject<UserViewModel>()

        headerImage.show(R.drawable.header_background)
        postButton.onClick { wallViewModel.onPostClicked() }

        userViewModel
                .getSession()
                .doOnNext { loginContainer.setVisible(it.state == Anonymous) }
                .repeatWhen { userViewModel.getFriendListChanges() }
                .subscribe()

        val adapter = WallAdapter(context)
        recyclerView.adapter = adapter
        wallViewModel.getItems().observe(this, Observer {
            adapter.clear()
            adapter.addAll(it)
        })
    }
}