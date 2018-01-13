package base.app.ui.fragment.content.news

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import base.app.data.news.NewsModel
import base.app.data.news.NewsModel.NewsType
import base.app.data.wall.News

class NewsViewModel : ViewModel() {

    fun getNews(type: NewsType): LiveData<List<News>> {
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