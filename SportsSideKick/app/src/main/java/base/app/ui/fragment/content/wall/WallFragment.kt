package base.app.ui.fragment.content.wall

import android.os.Bundle
import android.util.Log
import android.view.View
import base.app.R
import base.app.data.content.tv.inBackground
import base.app.ui.adapter.content.WallAdapter
import base.app.ui.fragment.user.auth.AuthViewModel.SessionState.Anonymous
import base.app.util.ui.BaseFragment
import base.app.util.ui.inject
import base.app.util.ui.setVisible
import base.app.util.ui.show
import kotlinx.android.synthetic.main.fragment_wall.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class WallFragment : BaseFragment(R.layout.fragment_wall) {

    override fun onViewCreated(view: View, state: Bundle?) {
        headerImage.show(R.drawable.header_background)

        val feedViewModel = inject<FeedViewModel>()
        val userViewModel = activity.inject<UserViewModel>()

        // TODO: Add/fix caching
        val adapter = WallAdapter(context)
        recyclerView.adapter = adapter
        userViewModel
                .getUser()
                .doOnNext { loginContainer.setVisible(it.state == Anonymous) }
                .flatMap { feedViewModel.getFeedFromCache() }
                .mergeWith { feedViewModel.getFeedFromServer() }
                .doOnNext { feedViewModel.saveFeedToCache(it) }
                .repeatWhen { userViewModel.getChangesInFriends() }
                .inBackground()
                .subscribe {
                    Log.d("tagx", "onNext")
                    adapter.clear()
                    adapter.addAll(it)
                    progressBar.setVisible(false)
                }
        progressBar.setVisible(true)

        postButton.onClick { feedViewModel.composePost() }
    }
}