package base.app.ui.fragment.popup.post

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import base.app.R
import base.app.data.Model
import base.app.data.wall.Post
import base.app.data.wall.WallItem
import base.app.data.wall.WallModel
import base.app.util.commons.Utility
import base.app.util.events.post.PostCompleteEvent
import base.app.util.ui.inflate
import base.app.util.ui.show
import butterknife.OnClick
import com.google.android.gms.tasks.TaskCompletionSource
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo.single
import kotlinx.android.synthetic.main.fragment_post_create.*
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.io.File

class PostCreateFragment : Fragment(), IPostCreateView {

    private val viewModel by lazy {
        ViewModelProviders.of(this)
                .get(PostCreateViewModel::class.java)
    }
    private var uploadedImageUrl: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        return container.inflate(R.layout.fragment_post_create)
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        viewModel.init()
        contentImage.show(R.drawable.image_rumours_background)
        setClickListeners()
    }

    private fun setClickListeners() {
        cameraButton.onClick {
            val cameraObservable = single(activity).usingCamera()
            viewModel.attachImage(cameraObservable)
        }
        galleryButton.onClick {
            val galleryObservable = single(activity).usingGallery()
            viewModel.attachImage(galleryObservable)
        }
        backButton.onClick { activity?.onBackPressed() }
        removeButton.onClick { clearPostImage() }
    }

    @SuppressLint("SetTextI18n")
    override fun showUser() {
        val info = Model.getInstance().userInfo
        authorName.text = "${info.firstName} ${info.lastName}"
        authorImage.show(info.circularAvatarUrl, R.drawable.blank_profile_rounded)
    }

    override fun showPostImage(image: File) {
        contentImage.show(image)

        cameraButton.visibility = View.GONE
        galleryButton.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        removeButton.visibility = View.VISIBLE
    }

    override fun clearPostImage() {
        uploadedImageUrl = null
        cameraButton!!.visibility = View.VISIBLE
        galleryButton!!.visibility = View.VISIBLE
        removeButton!!.visibility = View.GONE
        progressBar!!.visibility = View.GONE

        contentImage.show(R.drawable.image_rumours_background)
    }

    @OnClick(R.id.post)
    fun publishPost() {
        val captionContent = caption!!.text.toString()
        val postContent = content!!.text.toString()
        if (progressBar!!.visibility == View.VISIBLE) {
            Utility.toast(activity, getString(R.string.uploading))
            return
        }

        val newPost = Post()
        newPost.title = captionContent
        newPost.bodyText = postContent
        newPost.type = WallItem.PostType.post
        newPost.timestamp = Utility.getCurrentTime().toDouble()

        if (uploadedImageUrl != null) {
            newPost.coverImageUrl = uploadedImageUrl
        }
        WallModel.getInstance().createPost(newPost)
        Utility.hideKeyboard(activity)

        if (activity != null) {
            activity!!.onBackPressed()
        }
    }

    @Subscribe
    fun onPostCreated(event: PostCompleteEvent) {
        activity!!.onBackPressed()
    }

    private fun uploadImage(file: File) {
        val source = TaskCompletionSource<String>()
        source.task.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                uploadedImageUrl = task.result
            } else {
                // TODO @Filip Handle error!
            }
        }
        Model.getInstance().uploadImageForWallPost(file.absolutePath, activity!!.filesDir, source)
    }

    override fun onDestroy() {
        viewModel.onDestroy()
        super.onDestroy()
    }
}