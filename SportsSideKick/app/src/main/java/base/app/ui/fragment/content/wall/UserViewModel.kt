package base.app.ui.fragment.content.wall

import android.arch.lifecycle.ViewModel
import base.app.data.user.User
import base.app.ui.fragment.content.wall.UserViewModel.SessionState.Anonymous
import io.reactivex.Observable

class UserViewModel : ViewModel() {

    class Session(val state: SessionState, val user: User)
    enum class SessionState { Anonymous, LoggedIn }

    fun getSession(): Observable<Session> {
        return Observable.just(
                Session(Anonymous, User()))
    }

    fun getFriendListChanges(): Observable<Any> {
        // TODO: Listen for friend updates (FriendsListChangedEvent in event bus)
        return Observable.never()
    }
}