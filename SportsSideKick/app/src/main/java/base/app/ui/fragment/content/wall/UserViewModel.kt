package base.app.ui.fragment.content.wall

import android.arch.lifecycle.ViewModel
import base.app.data.user.UserInfo
import base.app.ui.fragment.content.wall.UserViewModel.SessionState.Anonymous
import io.reactivex.Observable

class UserViewModel : ViewModel() {

    class Session(val state: SessionState, val user: UserInfo)
    enum class SessionState { Anonymous, LoggedIn }

    fun getSession(): Observable<Session> {
        return Observable.just(
                Session(Anonymous, UserInfo()))
    }

    fun getFriendListChanges(): Observable<Any> {
        // TODO: Listen for friend updates (FriendsListChangedEvent in event bus)
        return Observable.never()
    }
}