package base.app.ui.fragment.content.wall

import android.arch.lifecycle.ViewModel
import base.app.data.user.User
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class UserViewModel : ViewModel() {

    val currentUser: PublishSubject<User> = PublishSubject.create()

    fun getUser(): Observable<User> {
        return currentUser
    }

    fun getChangesInFriends(): Observable<Any> {
        // TODO: Listen for friend updates (FriendsListChangedEvent in event bus)
        return Observable.never()
    }
}