package base.app.ui.fragment.content.wall

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import base.app.data.news.WallRepository
import base.app.data.user.LoginStateReceiver.LoginListener
import base.app.data.user.UserInfo
import base.app.data.wall.WallItem
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
}