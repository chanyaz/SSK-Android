package base.app.ui.fragment.content.wall

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import base.app.data.Model
import base.app.data.news.WallRepository
import base.app.data.user.LoginStateReceiver.LoginListener
import base.app.data.user.UserInfo
import base.app.data.wall.WallItem
import base.app.ui.fragment.base.FragmentEvent
import base.app.ui.fragment.popup.post.PostCreateFragment
import base.app.ui.fragment.popup.SignUpLoginFragment
import org.greenrobot.eventbus.EventBus
import java.lang.Error

class WallViewModel(private val wallRepo: WallRepository) : ViewModel(), LoginListener {

    fun getItems(): LiveData<List<WallItem>> {
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