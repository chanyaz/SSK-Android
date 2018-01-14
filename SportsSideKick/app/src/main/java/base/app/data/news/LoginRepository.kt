package base.app.data.news

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import base.app.data.wall.WallItem

class LoginRepository {

    fun getItems(): LiveData<List<WallItem>> {
        val data = MutableLiveData<List<WallItem>>()

        data.value = WallItem.getCache().values.toList().sortedBy { it.timestamp }

        return data
    }
}