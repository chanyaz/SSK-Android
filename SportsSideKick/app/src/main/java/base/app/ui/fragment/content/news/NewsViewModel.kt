package base.app.ui.fragment.content.news

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import base.app.data.news.NewsModel.NewsType
import base.app.data.news.NewsRepository
import base.app.data.wall.News

class NewsViewModel : ViewModel() {

    private lateinit var newsRepo: NewsRepository

    fun init(newsRepo: NewsRepository) {
        this.newsRepo = newsRepo
    }

    fun getNews(type: NewsType): LiveData<List<News>> {
        return newsRepo.getNews(type)
    }
}