package base.app.ui.fragment.popup.post

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.text.Html.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import base.app.R
import base.app.data.Model
import base.app.data.news.PostsRepository
import base.app.util.commons.Utility.hideKeyboard
import base.app.util.ui.hide
import base.app.util.ui.inflate
import base.app.util.ui.show
import base.app.util.ui.visible
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo.single
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData
import com.miguelbcr.ui.rx_paparazzo2.entities.Response
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_create_post.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.io.File

/**
 * Created by Filip on 11/21/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
class PostCreateFragment : Fragment(), IPostCreateView {

    private val viewModel by lazy {
        ViewModelProviders.of(this)
                .get(PostCreateViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        return container?.inflate(R.layout.fragment_create_post)
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        viewModel.postsRepo = PostsRepository()
        viewModel.view = this
        viewModel.onViewCreated()
        contentImage.show(R.drawable.background_post_create)
        tutorialBody.text = fromHtml(getString(R.string.post_create_tip_body), FROM_HTML_MODE_LEGACY)
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
        authorImage.show(info.avatarUrl, R.drawable.blank_profile_rounded)
    }

    override fun showPostImage(image: File) {
        contentImage.show(image)

        cameraButton.hide()
        galleryButton.hide()
        removeButton.visible()
    }

    override fun clearPostImage() {
        cameraButton.visible()
        galleryButton.visible()
        removeButton.hide()

        contentImage.show(R.drawable.background_post_create)
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
