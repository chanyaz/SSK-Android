package base.app.ui.fragment.content.wall

import android.arch.lifecycle.ViewModel
import base.app.data.content.wall.FeedItem
import base.app.data.content.wall.WallModel
import base.app.data.user.User
import base.app.ui.fragment.popup.SignUpLoginFragment
import base.app.ui.fragment.popup.post.PostCreateFragment
import base.app.ui.fragment.user.auth.LoginApi
import base.app.util.events.FragmentEvent
import io.reactivex.Observable
import org.greenrobot.eventbus.EventBus

class FeedViewModel : ViewModel() {

    fun getFeedFromServer(user: User): Observable<List<FeedItem>> {
        return WallModel.getInstance().loadFeed(user)
    }

    fun composePost() {
        if (LoginApi.getInstance().isLoggedIn) {
            EventBus.getDefault().post(FragmentEvent(PostCreateFragment::class.java))
        } else {
            EventBus.getDefault().post(FragmentEvent(SignUpLoginFragment::class.java))
        }
    }
}