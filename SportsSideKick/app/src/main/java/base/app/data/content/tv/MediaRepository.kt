package base.app.data.content.tv

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import base.app.Keys.YOUTUBE_API_KEY
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.Playlist
import com.google.api.services.youtube.model.Video
import io.reactivex.Observable
import io.reactivex.Observable.fromCallable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io

class MediaRepository(private val youtubeClient: YouTube) {

    fun getPlaylists(
            channelId: String
    ): LiveData<List<Playlist>> {
        val playlistsKey = "contentDetails,snippet"
        val fieldsKey = "pageInfo,nextPageToken,items(id,snippet(title,publishedAt,thumbnails/high),contentDetails)"

        val data = MutableLiveData<List<Playlist>>()

        fromCallable {
            youtubeClient.playlists()
                    .list(playlistsKey)
                    .setChannelId(channelId)
                    .setFields(fieldsKey)
                    .setMaxResults(50)
                    .setKey(YOUTUBE_API_KEY)
                    .execute()
        }
                .map { it.items }
                .inBackground()
                .subscribe { data.postValue(it) }
        return data
    }

    fun getVideos(playlistId: String): Single<List<Video>> {
        val requestKey = "snippet,contentDetails,statistics"
        val fieldsKey = "items(id,snippet(title,publishedAt,thumbnails/high),contentDetails/duration,statistics)"

        return getVideoIds(playlistId)
                .map { it.joinToString(",") }
                .map {
                    youtubeClient.videos()
                            .list(requestKey)
                            .setFields(fieldsKey)
                            .setMaxResults(50)
                            .setKey(YOUTUBE_API_KEY)
                            .setId(it)
                            .execute()
                }
                .map { it.items }
    }

    private fun getVideoIds(playlistId: String): Single<List<String>> {
        val requestKey = "snippet"
        val fieldsKey = "pageInfo,nextPageToken,items(playlistId,snippet(resourceId/videoId))"

        return fromCallable {
            youtubeClient.playlistItems()
                    .list(requestKey)
                    .setPlaylistId(playlistId)
                    .setFields(fieldsKey)
                    .setKey(YOUTUBE_API_KEY)
                    .execute()
        }
                .flatMapIterable { it.items }
                .map { it.snippet.resourceId.videoId }
                .toList()
    }
}

fun <T> Observable<T>.inBackground(): Observable<T> {
    return subscribeOn(io())
            .observeOn(mainThread())
}

fun <T> Single<T>.inBackground(): Single<T> {
    return subscribeOn(io())
            .observeOn(mainThread())
}