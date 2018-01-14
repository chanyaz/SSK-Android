package base.app.data.news

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import base.app.data.wall.News

class NewsRepository {

    fun getNews(type: NewsModel.NewsType): LiveData<List<News>> {
        val data = MutableLiveData<List<News>>()

        val cached = NewsModel.getInstance().getAllCachedItems(type)
        if (cached.isNotEmpty()) {
            data.value = cached
        } else {
            NewsModel.getInstance().loadPage(type, data)
        }
        return data
    }
}