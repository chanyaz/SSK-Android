package base.app.ui.fragment.content.wall

import android.arch.lifecycle.ViewModel
import android.os.SystemClock
import android.util.Log
import base.app.BuildConfig.DEBUG
import base.app.data.content.news.WallRepository
import base.app.data.content.wall.FeedItem
import base.app.data.content.wall.Post
import base.app.ui.fragment.popup.SignUpLoginFragment
import base.app.ui.fragment.popup.post.PostCreateFragment
import base.app.ui.fragment.user.auth.AuthApi
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
    fun getFeedFromServer(): Observable<List<FeedItem>> {
        if (DEBUG) {
            SystemClock.sleep(2000)
            return Observable.just(listOf<FeedItem>(Post("2")))
        }
        return wallRepo.getFeedFromCache()
    }

    fun saveFeedToCache(feedList: List<FeedItem>) {
        Log.d("tagx", "caching")
        // TODO: Save items to cache using Room database
    }

    fun composePost() {
        if (AuthApi.getInstance().isRealUser) {
            EventBus.getDefault().post(FragmentEvent(PostCreateFragment::class.java))
        } else {
            EventBus.getDefault().post(FragmentEvent(SignUpLoginFragment::class.java))
        }
    }
}