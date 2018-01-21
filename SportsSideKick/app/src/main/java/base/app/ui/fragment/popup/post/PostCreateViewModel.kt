package base.app.ui.fragment.popup.post

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import base.app.data.content.news.PostsRepository
import base.app.data.content.tv.inBackground
import base.app.data.user.User
import base.app.ui.fragment.user.login.LoginApi
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.io.File

class PostCreateViewModel : ViewModel() {

    val postsRepo = PostsRepository()
    lateinit var view: IPostCreateView

    private var selectedImage: File? = null
    private val disposables = CompositeDisposable()

    fun loadUser() : LiveData<User> {
        val user = LoginApi.getInstance().user
        return MutableLiveData<User>().just(user)
    }

    fun attachImage(fileObservable: Observable<File>) {
        disposables.add(fileObservable
                .doOnNext { view.showPostImage(it) }
                .subscribe { selectedImage = it })
    }

    fun publishPost(title: String, bodyText: String) {
        view.showLoading(true)

        disposables.add(postsRepo.uploadImage(selectedImage)
                .flatMap { postsRepo.composePost(title, bodyText, imageUrl = it) }
                .flatMap { postsRepo.savePost(it) }
                .inBackground()
                .subscribe { view.exit() })
    }

    fun onRemoveImageClicked() {
        selectedImage = null
        view.clearPostImage()
    }

    fun onDestroy() {
        disposables.clear()
    }
}

fun <T> MutableLiveData<T>.just(obj: T): LiveData<T> {
    value = obj
    return this
}
