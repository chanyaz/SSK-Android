package base.app.ui.fragment.content.wall

import android.arch.lifecycle.BuildConfig
import android.arch.lifecycle.ViewModel
import base.app.data.content.news.WallRepository
import base.app.data.content.wall.FeedItem
import base.app.ui.fragment.popup.SignUpLoginFragment
import base.app.ui.fragment.popup.post.PostCreateFragment
import base.app.util.commons.Model
import base.app.util.events.FragmentEvent
import io.reactivex.Observable
import org.greenrobot.eventbus.EventBus

class FeedViewModel : ViewModel() {

    private val wallRepo: WallRepository by lazy { WallRepository() }

    fun getFeedFromCache(): Observable<List<FeedItem>> {
        if (BuildConfig.DEBUG) {
//            return listOf<FeedItem>()
        }
        return wallRepo.getFeedFromCache()
    }

    fun saveFeedToCache(feedList: List<FeedItem>) {
        // TODO: Save items to cache using Room database
    }

    fun composePost() {
        if (Model.getInstance().isRealUser) {
            EventBus.getDefault().post(FragmentEvent(PostCreateFragment::class.java))
        } else {
            EventBus.getDefault().post(FragmentEvent(SignUpLoginFragment::class.java))
        }
    }
}