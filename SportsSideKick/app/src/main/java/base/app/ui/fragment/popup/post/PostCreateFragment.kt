package base.app.ui.fragment.popup.post

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import base.app.R
import base.app.data.Model
import base.app.data.news.PostsRepository
import base.app.util.commons.Utility.hideKeyboard
import base.app.util.ui.gone
import base.app.util.ui.inflate
import base.app.util.ui.show
import base.app.util.ui.visible
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo.single
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData
import com.miguelbcr.ui.rx_paparazzo2.entities.Response
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_post_create.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.io.File

class PostCreateFragment : Fragment(), IPostCreateView {

    private val viewModel by lazy {
        ViewModelProviders.of(this)
                .get(PostCreateViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        return container.inflate(R.layout.fragment_post_create)
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        viewModel.postsRepo = PostsRepository()
        viewModel.view = this
        viewModel.onViewCreated()
        contentImage.show(R.drawable.image_rumours_background)
        setClickListeners()
    }

    private fun setClickListeners() {
        cameraButton.onClick { viewModel.attachImage(single(activity).usingCamera().getFile()) }
        galleryButton.onClick { viewModel.attachImage(single(activity).usingGallery().getFile()) }
        postButton.onClick { viewModel.publishPost(
                titleField.text.toString(), bodyTextField.text.toString()) }
        removeButton.onClick { viewModel.onRemoveImageClicked() }
        backButton.onClick { exit() }
    }

    @SuppressLint("SetTextI18n")
    override fun showUser() {
        val info = Model.getInstance().userInfo
        authorName.text = "${info.firstName} ${info.lastName}"
        authorImage.show(info.avatar, R.drawable.avatar_placeholder)
    }

    override fun showPostImage(image: File) {
        contentImage.show(image)

        cameraButton.gone()
        galleryButton.gone()
        removeButton.visible()
    }

    override fun clearPostImage() {
        cameraButton.visible()
        galleryButton.visible()
        removeButton.gone()

        contentImage.show(R.drawable.image_rumours_background)
    }

    override fun showLoading(loading: Boolean) {
        if (loading) progressBar.visible() else progressBar.gone()
    }

    override fun exit() {
        hideKeyboard(activity)
        activity?.onBackPressed()
    }

    override fun onDestroy() {
        viewModel.onDestroy()
        super.onDestroy()
    }
}

private fun Observable<Response<FragmentActivity?, FileData>>.getFile(): Observable<File> {
    return this.map { it.data().file }
}
