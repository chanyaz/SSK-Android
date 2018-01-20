package base.app.ui.fragment.content.wall

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import base.app.data.content.news.WallRepository
import base.app.data.content.wall.BaseItem
import base.app.data.user.LoginStateReceiver.LoginListener
import base.app.data.user.UserInfo
import base.app.ui.fragment.popup.SignUpLoginFragment
import base.app.ui.fragment.popup.post.PostCreateFragment
import base.app.util.commons.Model
import base.app.util.events.FragmentEvent
import org.greenrobot.eventbus.EventBus
import java.lang.Error

class WallViewModel : ViewModel(), LoginListener {

    internal val wallRepo: WallRepository by lazy { WallRepository() }

    fun getItems(): LiveData<List<BaseItem>> {
        return wallRepo.getItems()
    }

    override fun onLogin(user: UserInfo?) {
    }

    override fun onLoginAnonymously() {
    }

    override fun onLoginError(error: Error?) {
    }

    override fun onLogout() {
    }

    fun onPostClicked() {
        if (Model.getInstance().isRealUser) {
            EventBus.getDefault().post(FragmentEvent(PostCreateFragment::class.java))
        } else {
            EventBus.getDefault().post(FragmentEvent(SignUpLoginFragment::class.java))
        }
    }
}