package base.app.ui.fragment.user.auth

import android.arch.lifecycle.ViewModel
import base.app.data.content.tv.inBackground
import base.app.data.toUser

class LoginViewModel : ViewModel() {

    internal lateinit var view: ILoginView

    fun onLoginSubmit(email: String, password: String) {
        view.showLoading(true)

        val loginApi = LoginApi.getInstance()
        loginApi.authorize(email, password)
                .flatMap { loginApi.profileData }
                .map { it.toUser() }
                .doOnNext { /* TODO: Clear user cache, then save this new user to cache */ }
                .doOnNext { /* TODO: registerForPushNotifications() */ }
                .inBackground()
                .subscribe { view.navigateToFeed(it) }
    }
}