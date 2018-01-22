package base.app.ui.fragment.content.wall

import android.arch.lifecycle.ViewModel
import base.app.R.string.user
import base.app.data.content.tv.inBackground
import base.app.data.toUser
import base.app.data.user.User
import base.app.ui.fragment.content.wall.SessionType.Anonymous
import base.app.ui.fragment.content.wall.UserViewModel.SessionType
import base.app.ui.fragment.content.wall.UserViewModel.SessionType.Anonymous
import base.app.ui.fragment.content.wall.UserViewModel.SessionType.Authenticated
import base.app.ui.fragment.user.auth.IUserView
import base.app.ui.fragment.user.auth.LoginApi
import io.reactivex.Observable
import io.reactivex.Observable.just
import io.reactivex.functions.BiFunction

class UserViewModel : ViewModel() {

    internal lateinit var view: IUserView

    fun getSession(): Observable<Session> {
        val loginApi = LoginApi.getInstance()
        var state: SessionType

        return loginApi.sessionStatus
                .map {
                    if (it == Anonymous) {
                        loginApi.loginAnonymous()
                    } else {
                        just(true)
                    }
                }
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
                .doOnNext { /* TODO: Clear user cache, then save this new user to cache */ }
                .doOnNext { /* TODO: registerForPushNotifications() */ }
                .inBackground()
                .subscribe { view.navigateToFeed() }
    }

    fun getChangesInFriends(): Observable<Any> {
        // TODO: Listen for friend updates (FriendsListChangedEvent in event bus)
        return Observable.never()
    }

}

class Session(val user: User, val state: SessionType)
enum class SessionType { Anonymous, Authenticated }