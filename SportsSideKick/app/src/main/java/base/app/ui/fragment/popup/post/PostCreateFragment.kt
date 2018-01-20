package base.app.ui.fragment.popup.post

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import base.app.R
import base.app.data.content.news.PostsRepository
import base.app.util.commons.Utility.hideKeyboard
import base.app.util.ui.*
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
        viewModel.loadUser().observe(this, Observer {
            if (it != null) {
                authorName.text = "${it.firstName} ${it.lastName}"
                authorImage.showAvatar(it.avatar)
            }
        })
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
    return map { it.data().file }
}
