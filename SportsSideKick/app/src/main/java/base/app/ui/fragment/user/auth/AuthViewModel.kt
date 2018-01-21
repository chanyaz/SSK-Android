package base.app.ui.fragment.user.auth

import android.arch.lifecycle.ViewModel
import base.app.data.content.tv.inBackground
import base.app.data.toUser
import base.app.data.user.User
import base.app.ui.fragment.user.auth.AuthViewModel.SessionState.LoggedIn
import io.reactivex.Observable

class AuthViewModel : ViewModel() {

    internal lateinit var view: IAuthView
    private lateinit var currentSession: Observable<Session>

    fun getSession(): Session {
        return currentSession
    }

    fun onLoginSubmit(email: String, password: String) {
        view.showLoading(true)

        val loginApi = AuthApi.getInstance()
        loginApi.authorize(email, password)
                .flatMap { loginApi.profileData }
                .map { it.toUser() }
                .map { Session(it, LoggedIn) }
                .doOnNext { /* TODO: Clear user cache, then save this new user to cache */ }
                .doOnNext { /* TODO: registerForPushNotifications() */ }
                .inBackground()
                .subscribe { view.navigateToFeed(it) }
    }

    // TODO: onLoginAnonymously()

    class Session(val user: User, val state: SessionState)
    enum class SessionState { Anonymous, LoggedIn }
}