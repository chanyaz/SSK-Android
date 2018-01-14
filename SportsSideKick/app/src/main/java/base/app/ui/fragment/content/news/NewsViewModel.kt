package base.app.ui.fragment.content.news

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import base.app.data.news.NewsModel.NewsType
import base.app.data.news.NewsRepository
import base.app.data.wall.News

class NewsViewModel(private val newsRepo: NewsRepository) : ViewModel() {

    fun getNews(type: NewsType): LiveData<List<News>> {
        return newsRepo.getNews(type)
    }
}