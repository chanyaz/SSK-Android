package base.app.ui.fragment.content.wall

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import base.app.data.wall.WallBase

class WallViewModel : ViewModel() {

    val feed = MutableLiveData<List<WallBase>>()

    init {
        loadFeed()
    }

    private fun loadFeed() {
        feed.postValue(null)
    }
}