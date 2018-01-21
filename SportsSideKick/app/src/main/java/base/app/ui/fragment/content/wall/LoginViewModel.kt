package base.app.ui.fragment.content.wall

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import base.app.data.user.LoginStateReceiver

class LoginViewModel(val app: Application) : AndroidViewModel(app) {

    init {
        loginStateReceiver = LoginStateReceiver(this)
    }
}