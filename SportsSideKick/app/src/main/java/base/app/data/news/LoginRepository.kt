package base.app.data.news

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import base.app.data.wall.WallBase

class LoginRepository {

    fun getItems(): LiveData<List<WallBase>> {
        val data = MutableLiveData<List<WallBase>>()

        data.value = WallBase.cache.values.toList().sortedBy { it.timestamp }

        return data
    }
}