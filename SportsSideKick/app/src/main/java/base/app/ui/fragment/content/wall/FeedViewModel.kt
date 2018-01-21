package base.app.ui.fragment.content.wall

import android.arch.lifecycle.ViewModel
import android.util.Log
import base.app.BuildConfig.DEBUG
import base.app.data.content.news.WallRepository
import base.app.data.content.wall.FeedItem
import base.app.data.content.wall.Post
import base.app.data.content.wall.WallModel
import base.app.data.user.User
import base.app.ui.fragment.popup.SignUpLoginFragment
import base.app.ui.fragment.popup.post.PostCreateFragment
import base.app.ui.fragment.user.auth.LoginApi
import base.app.util.events.FragmentEvent
import io.reactivex.Observable
import org.greenrobot.eventbus.EventBus

class FeedViewModel : ViewModel() {

    private val wallRepo = WallRepository()

    fun getFeedFromCache(): Observable<List<FeedItem>> {
        if (DEBUG) {
            return Observable.just(listOf<FeedItem>(Post("1")))
        }
        return wallRepo.getFeedFromCache()
    }

    fun getFeedFromServer(user: User): Observable<List<FeedItem>> {
        return WallModel.getInstance().loadFeed(user)
    }

    fun saveFeedToCache(feedList: List<FeedItem>) {
        Log.d("tagx", "caching")
        // TODO: Save items to cache using Room database
    }

    fun composePost() {
        if (LoginApi.getInstance().isRealUser) {
            EventBus.getDefault().post(FragmentEvent(PostCreateFragment::class.java))
        } else {
            EventBus.getDefault().post(FragmentEvent(SignUpLoginFragment::class.java))
        }
    }
}