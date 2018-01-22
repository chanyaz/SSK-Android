package base.app.ui.fragment.content.wall

import android.arch.lifecycle.ViewModel
import base.app.data.content.tv.inBackground
import base.app.data.toUser
import base.app.data.user.User
import base.app.ui.fragment.content.wall.SessionState.Anonymous
import base.app.ui.fragment.user.auth.IUserView
import base.app.ui.fragment.user.auth.LoginApi
import base.app.ui.fragment.user.auth.LoginApi.LoggedInUserType
import io.reactivex.Observable

class UserViewModel : ViewModel() {

    internal lateinit var view: IUserView

    fun getSession(): Observable<Session> {
        val loginApi = LoginApi.getInstance()
        var state: SessionState = Anonymous

        return loginApi.sessionState
                .doOnNext { state = it }
                .notifyDeprecatedLogin(state)
                .flatMap { loginApi.startSession(state) }
                .flatMap { loginApi.profileData }
                .map { it.toUser() }
                .map { Session(it, state) }
    }

    fun onLoginSubmit(email: String, password: String) {
        view.showLoading(true)

        val loginApi = LoginApi.getInstance()
        loginApi.authorize(email, password)
                .flatMap { loginApi.profileData }
                .map { it.toUser() }
                .doOnNext { /* TODO: registerForPushNotifications() */ }
                .inBackground()
                .subscribe { view.navigateToFeed() }
    }

    fun getChangesInFriends(): Observable<Any> {
        return Observable.never()
    }

    // TODO: Remove after fully migrating app to login viewmodel
    private fun Observable<*>.notifyDeprecatedLogin(state: SessionState): Observable<*> {
        return doOnNext {
            if (state == Anonymous) {
                LoginApi.getInstance().loggedInUserType = LoggedInUserType.ANONYMOUS
            } else {
                LoginApi.getInstance().loggedInUserType = LoggedInUserType.REAL
            }
        }
    }
}

class Session(val user: User, val state: SessionState)
enum class SessionState { Anonymous, Authenticated }