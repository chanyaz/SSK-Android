package base.app.ui.fragment.content.wall

import android.os.Bundle
import android.view.View
import base.app.R
import base.app.data.content.tv.inBackground
import base.app.ui.adapter.content.WallAdapter
import base.app.ui.fragment.content.wall.UserViewModel.SessionType.Anonymous
import base.app.util.ui.BaseFragment
import base.app.util.ui.inject
import base.app.util.ui.setVisible
import kotlinx.android.synthetic.main.fragment_wall.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class WallFragment : BaseFragment(R.layout.fragment_wall) {

    override fun onViewCreated(view: View, state: Bundle?) {
        val feedViewModel = inject<FeedViewModel>()
        val userViewModel = activity.inject<UserViewModel>()

        val adapter = WallAdapter(context)
        recyclerView.adapter = adapter
        progressBar.setVisible(true)

        disposables.add(userViewModel
                .getSession(context!!)
                .doOnNext { loginContainer.setVisible(it.state == Anonymous) }
                .flatMap { feedViewModel.getFeedFromServer(it.user) }
                .repeatWhen { userViewModel.getChangesInFriends() }
                .inBackground()
                .subscribe {
                    adapter.clear()
                    adapter.addAll(it)
                    progressBar.setVisible(false)
                })

        postButton.onClick { feedViewModel.composePost() }
    }
}