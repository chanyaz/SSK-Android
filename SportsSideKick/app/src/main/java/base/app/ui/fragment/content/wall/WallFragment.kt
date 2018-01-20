package base.app.ui.fragment.content.wall

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

class WallFragmentNew : BaseFragment(R.layout.fragment_wall) {

    override fun onViewCreated(view: View, state: Bundle?) {
        headerImage.show(R.drawable.header_background)

        val feedViewModel = inject<FeedViewModel>()
        val userViewModel = activity.inject<UserViewModel>()

        val adapter = WallAdapter(context)
        recyclerView.adapter = adapter
        progressBar.setVisible(true)
        userViewModel
                .getSession()
                .doOnNext { loginContainer.setVisible(it.state == Anonymous) }
                .map { feedViewModel.getFeedFromCache() }
                .concatMap { feedViewModel.getFeedFromCache() }
                .doOnNext { feedViewModel.saveFeedToCache(it) }
                .repeatWhen { userViewModel.getFriendListChanges() }
                .subscribe {
                    adapter.clear()
                    adapter.addAll(it)
                    progressBar.setVisible(false)
                }


        postButton.onClick { feedViewModel.composePost() }
    }
}