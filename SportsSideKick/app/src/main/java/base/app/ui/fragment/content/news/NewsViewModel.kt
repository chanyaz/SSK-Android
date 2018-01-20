package base.app.ui.fragment.content.news

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import base.app.data.content.news.NewsModel.NewsType
import base.app.data.content.news.NewsRepository
import base.app.data.content.wall.News

class NewsViewModel : ViewModel() {

    lateinit var newsRepo: NewsRepository

    fun loadNews(type: NewsType): LiveData<List<News>> {
        return newsRepo.getNews(type)
    }
}