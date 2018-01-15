package base.app.ui.fragment.popup

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import base.app.BuildConfig
import base.app.R
import base.app.data.Model
import base.app.data.wall.Post
import base.app.data.wall.WallItem
import base.app.data.wall.WallModel
import base.app.util.commons.Constant.REQUEST_CODE_POST_IMAGE_CAPTURE
import base.app.util.commons.Constant.REQUEST_CODE_POST_IMAGE_PICK
import base.app.util.commons.Utility
import base.app.util.events.post.PostCompleteEvent
import base.app.util.ui.ImageLoader
import base.app.util.ui.inflate
import base.app.util.ui.show
import butterknife.OnClick
import com.google.android.gms.tasks.TaskCompletionSource
import com.jakewharton.rxbinding2.view.RxView.clicks
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo.single
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import kotlinx.android.synthetic.main.fragment_create_post.*
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.io.IOException

class CreatePostFragment : Fragment() {

    private lateinit var currentPath: String
    private var uploadedImageUrl: String? = null
    private val disposables = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        return container.inflate(R.layout.fragment_create_post)
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        contentImage.show(R.drawable.image_rumours_background)
        setupUserInfo()
        setClickListeners()
    }

    private fun setClickListeners() {
        disposables.add(clicks(cameraButton)
                .flatMap { single(this).usingCamera() }
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribe { contentImage.show(it.data().file) })
    }

    @SuppressLint("SetTextI18n")
    private fun setupUserInfo() {
        val info = Model.getInstance().userInfo

        authorName.text = "${info.firstName} ${info.lastName}"
        authorImage.show(info.circularAvatarUrl, R.drawable.blank_profile_rounded)
    }

    @OnClick(R.id.galleryButton)
    fun selectImageOnClick() {
        // CreatePostFragmentPermissionsDispatcher.invokeImageSelectionWithPermissionCheck(this)
    }

    @OnClick(R.id.contentImage, R.id.removeButton)
    fun removeUploadedContent() {
        uploadedImageUrl = null
        removeButton!!.visibility = View.GONE
        progressBar!!.visibility = View.INVISIBLE
        cameraButton!!.visibility = View.VISIBLE
        galleryButton!!.visibility = View.VISIBLE

        contentImage!!.alpha = 0.5f
        contentImage.show(R.drawable.image_rumours_background)
    }

    @OnClick(R.id.backButton)
    fun onBackButton() {
        // TODO Close fragment
        activity!!.onBackPressed()
    }

    @OnClick(R.id.post)
    fun postPost() {
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

    // @NeedsPermission(WRITE_EXTERNAL_STORAGE)
    fun invokeImageSelection() {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, REQUEST_CODE_POST_IMAGE_PICK)
    }

    // @NeedsPermission(CAMERA, WRITE_EXTERNAL_STORAGE)
    fun invokeCameraCapture() {
        val takePictureIntent = Intent(ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(activity!!.packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = Model.createImageFile(context)
                currentPath = photoFile!!.absolutePath
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                if (Utility.isKitKat()) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                }
                if (Utility.isLollipopAndUp()) {
                    val photoURI = FileProvider.getUriForFile(activity!!, BuildConfig.APPLICATION_ID + ".fileprovider", photoFile)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                }
            }
            startActivityForResult(takePictureIntent, REQUEST_CODE_POST_IMAGE_CAPTURE)
        }
    }

    @Subscribe
    fun onPostCreated(event: PostCompleteEvent) {
        activity!!.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_POST_IMAGE_CAPTURE -> {
                    cameraButton!!.visibility = View.GONE
                    galleryButton!!.visibility = View.GONE
                    progressBar!!.visibility = View.VISIBLE
                    removeButton!!.visibility = View.VISIBLE
                    uploadImagePost(currentPath)
                }
                REQUEST_CODE_POST_IMAGE_PICK -> {
                    // TODO: Fix crash on some devices (LG G4s on Android 6 for example)
                    val selectedImageURI = intent!!.data
                    val realPath = Model.getRealPathFromURI(context, selectedImageURI)
                    uploadImagePost(realPath)
                    cameraButton!!.visibility = View.GONE
                    galleryButton!!.visibility = View.GONE
                    progressBar!!.visibility = View.VISIBLE
                    removeButton!!.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun uploadImagePost(path: String) {
        val source = TaskCompletionSource<String>()
        source.task.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                uploadedImageUrl = task.result
                ImageLoader.displayImage(uploadedImageUrl, contentImage!!)
                progressBar!!.visibility = View.INVISIBLE
                contentImage!!.alpha = 1.0f
            } else {
                // TODO @Filip Handle error!
            }
        }
        Model.getInstance().uploadImageForWallPost(path, activity!!.filesDir, source)
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }
}
