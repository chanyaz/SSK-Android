package base.app.ui.fragment.content.wall

import android.arch.lifecycle.ViewModel
import android.content.Context
import base.app.data.toUser
import base.app.data.user.User
import base.app.ui.fragment.content.wall.UserViewModel.SessionType.Anonymous
import base.app.ui.fragment.user.auth.LoginApi
import base.app.util.commons.GSAndroidPlatform
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class UserViewModel : ViewModel() {

    val currentUser: PublishSubject<User> = PublishSubject.create()

    fun getSession(context: Context): Observable<Session> {
        val loginApi = LoginApi.getInstance()
        return loginApi.initialize(context)
                .flatMap { Observable.just(GSAndroidPlatform.gs().isAuthenticated) }
                .flatMap { loginApi.loginAnonymous() }
                .flatMap { loginApi.profileData }
                .map { it.toUser() }
                .map { Session(it, Anonymous) }
    }

    fun getChangesInFriends(): Observable<Any> {
        // TODO: Listen for friend updates (FriendsListChangedEvent in event bus)
        return Observable.never()
    }

    class Session(val user: User, val state: SessionType)
    enum class SessionType { Anonymous, Authenticated }
}