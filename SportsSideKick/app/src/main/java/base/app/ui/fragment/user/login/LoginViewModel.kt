package base.app.ui.fragment.user.login

import android.arch.lifecycle.ViewModel
import base.app.data.content.tv.inBackground
import base.app.data.toUser
import base.app.data.user.User

class LoginViewModel : ViewModel() {

    lateinit var view: ILoginView
    lateinit var currentUser: User

    fun onLoginSubmit(email: String, password: String) {
        view.showLoading(true)

        val loginApi = LoginApi.getInstance()
        loginApi.authorize(email, password)
                .flatMap { loginApi.profileData }
                .map { it.toUser() }
                .doOnNext { currentUser = it }
                .doOnNext { /* TODO: Clear user cache, then save this new user to cache */ }
                .doOnNext { /* TODO: registerForPushNotifications() */ }
                .inBackground()
                .subscribe { view.navigateToFeed() }
    }
}