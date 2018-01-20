package base.app.data.content.news

import base.app.data.TypeConverter
import base.app.data.content.wall.FeedItem
import io.reactivex.Observable

class WallRepository {

    fun getFeedFromCache(): Observable<List<FeedItem>> {
        return Observable.create<List<FeedItem>> {
            val feedList = TypeConverter.cache.values.toList().sortedBy { it.timestamp }
            it.onNext(feedList)
            it.onComplete()
        }
    }
}