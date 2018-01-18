package base.app.data.news

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import base.app.data.wall.BaseItem

class LoginRepository {

    fun getItems(): LiveData<List<BaseItem>> {
        val data = MutableLiveData<List<BaseItem>>()

        data.value = BaseItem.cache.values.toList().sortedBy { it.timestamp }

        return data
    }
}