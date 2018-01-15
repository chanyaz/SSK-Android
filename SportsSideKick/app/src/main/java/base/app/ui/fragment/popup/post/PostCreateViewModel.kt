package base.app.ui.fragment.popup.post

import android.arch.lifecycle.ViewModel
import android.support.v4.app.FragmentActivity
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData
import com.miguelbcr.ui.rx_paparazzo2.entities.Response
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class PostCreateViewModel : ViewModel() {

    private val disposables = CompositeDisposable()
    private val view = IPostCreateView()

    fun init() {
        view.showUser()
    }

    fun attachImage(observable: Observable<Response<FragmentActivity?, FileData>>) {
        disposables.add(observable
                .map { it.data().file }
                .doOnNext { view.showPostImage(it) }
                .doOnNext {
                    /*
                    uploadImage(it)
                    */
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { })
    }

    fun onDestroy() {
        disposables.clear()
    }
}