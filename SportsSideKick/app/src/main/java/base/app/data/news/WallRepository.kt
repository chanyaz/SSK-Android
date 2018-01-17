package base.app.data.news

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import base.app.data.wall.WallItem

class WallRepository {

    fun getItems(): LiveData<List<WallItem>> {
        val data = MutableLiveData<List<WallItem>>()

        data.value = WallItem.cache.values.toList().sortedBy { it.timestamp }

        return data
    }
}