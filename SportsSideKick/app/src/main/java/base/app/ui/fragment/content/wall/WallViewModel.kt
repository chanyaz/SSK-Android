package base.app.ui.fragment.content.wall

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import base.app.data.content.news.WallRepository
import base.app.data.content.wall.BaseItem
import base.app.ui.fragment.popup.SignUpLoginFragment
import base.app.ui.fragment.popup.post.PostCreateFragment
import base.app.util.commons.Model
import base.app.util.events.FragmentEvent
import org.greenrobot.eventbus.EventBus

class WallViewModel : ViewModel() {

    internal val wallRepo: WallRepository by lazy { WallRepository() }

    fun getItems(): LiveData<List<BaseItem>> {
        return wallRepo.getItems()
    }

    fun onPostClicked() {
        if (Model.getInstance().isRealUser) {
            EventBus.getDefault().post(FragmentEvent(PostCreateFragment::class.java))
        } else {
            EventBus.getDefault().post(FragmentEvent(SignUpLoginFragment::class.java))
        }
    }
}