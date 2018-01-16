package base.app.ui.fragment.popup.post

import android.arch.lifecycle.ViewModel
import base.app.data.news.PostsRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import java.io.File

class PostCreateViewModel : ViewModel() {

    lateinit var postsRepo: PostsRepository
    lateinit var view: IPostCreateView

    private var selectedImage: File? = null
    private val disposables = CompositeDisposable()

    fun onViewCreated() {
        view.showUser()
    }

    fun attachImage(fileObservable: Observable<File>) {
        disposables.add(fileObservable
                .doOnNext { view.showPostImage(it) }
                .subscribe { selectedImage = it })
    }

    fun publishPost(title: String, bodyText: String) {
        disposables.add(postsRepo.uploadImage(selectedImage)
                .flatMap { postsRepo.composePost(title, bodyText, imageUrl = it) }
                .map { postsRepo.savePost(it).blockingSingle() }
                .subscribeOn(io())
                .observeOn(mainThread())
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