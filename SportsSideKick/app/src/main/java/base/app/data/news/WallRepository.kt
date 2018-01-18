package base.app.data.news

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import base.app.data.TypeMapper
import base.app.data.wall.BaseItem

class WallRepository {

    fun getItems(): LiveData<List<BaseItem>> {
        val data = MutableLiveData<List<BaseItem>>()

        data.value = TypeMapper.cache.values.toList().sortedBy { it.timestamp }

        return data
    }
}