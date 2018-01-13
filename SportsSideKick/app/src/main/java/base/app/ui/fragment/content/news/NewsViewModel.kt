package base.app.ui.fragment.content.news

import android.arch.lifecycle.ViewModel
import base.app.data.wall.WallNews
import io.reactivex.Observable

class NewsViewModel : ViewModel() {

    fun getNews(): Observable<List<WallNews>> {
        val observableFromCache

        val observableFromNetwork

        return
    }
}