package base.app.ui.fragment.content.wall

import android.os.Bundle
import android.view.View
import base.app.R
import base.app.data.content.news.NewsModel
import base.app.data.content.news.NewsModel.NewsType
import base.app.data.content.tv.inBackground
import base.app.ui.adapter.content.WallAdapter
import base.app.ui.fragment.content.wall.SessionState.Anonymous
import base.app.ui.fragment.popup.RegisterFragment
import base.app.ui.fragment.user.auth.LoginApi
import base.app.ui.fragment.user.auth.LoginFragment
import base.app.util.events.FragmentEvent
import base.app.util.events.ItemUpdateEvent
import base.app.util.ui.BaseFragment
import base.app.util.ui.inject
import base.app.util.ui.setVisible
import kotlinx.android.synthetic.main.fragment_wall.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.sdk25.coroutines.onClick

class WallFragment : BaseFragment(R.layout.fragment_wall) {

    val adapter by lazy { WallAdapter(context) }

    override fun onViewCreated(view: View, state: Bundle?) {
        val feedViewModel = inject<FeedViewModel>()
        val userViewModel = activity.inject<UserViewModel>()

        recyclerView.adapter = adapter
        progressBar.setVisible(true)

        LoginApi.getInstance().initialize(context)
        disposables.add(userViewModel.getSession()
                .doOnNext { loginContainer.setVisible(it.state == Anonymous) }
                .flatMap { feedViewModel.getFeedFromServer(it.user) }
                .repeatWhen { userViewModel.getChangesInFriends() }
                .doOnNext {
                    NewsModel.getInstance().loadPage(NewsType.OFFICIAL)
                    NewsModel.getInstance().loadPage(NewsType.UNOFFICIAL)
                }
                .inBackground()
                .subscribe {
                    adapter.clear()
                    adapter.addAll(it)
                    progressBar.setVisible(false)
                })

        setClickListeners(feedViewModel)

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    private fun setClickListeners(feedViewModel: FeedViewModel) {
        registerButton.onClick { EventBus.getDefault().post(FragmentEvent(RegisterFragment::class.java)) }
        loginButton.onClick { EventBus.getDefault().post(FragmentEvent(LoginFragment::class.java)) }
        postButton.onClick { feedViewModel.composePost() }
    }

    @Subscribe
    fun onItemUpdate(event: ItemUpdateEvent) {
        val newItem = event.item

        val oldItem = adapter.values.firstOrNull { newItem.id == it.id }
        if (oldItem != null) adapter.remove(oldItem)
        adapter.add(newItem)
        adapter.notifyDataSetChanged()
    }
}