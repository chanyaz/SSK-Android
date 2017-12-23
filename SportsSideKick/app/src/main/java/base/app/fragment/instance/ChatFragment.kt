package base.app.fragment.instance

import android.Manifest
import android.animation.LayoutTransition
import android.app.Activity
import android.app.DownloadManager
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.nostra13.universalimageloader.core.ImageLoader
import com.nshmura.snappysmoothscroller.SnappyLinearLayoutManager
import com.universalvideoview.UniversalMediaController
import com.universalvideoview.UniversalVideoView
import com.wang.avi.AVLoadingIndicatorView

import org.apache.commons.io.FilenameUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

import java.io.File
import java.io.IOException
import java.util.TimerTask

import base.app.BuildConfig
import base.app.R
import base.app.adapter.ChatHeadsAdapter
import base.app.adapter.MessageAdapter
import base.app.events.FullScreenImageEvent
import base.app.events.MessageSelectedEvent
import base.app.events.OpenChatEvent
import base.app.events.PlayVideoEvent
import base.app.fragment.BaseFragment
import base.app.fragment.FragmentEvent
import base.app.fragment.IgnoreBackHandling
import base.app.fragment.popup.CreateChatFragment
import base.app.fragment.popup.EditChatFragment
import base.app.fragment.popup.JoinChatFragment
import base.app.fragment.popup.LoginFragment
import base.app.fragment.popup.SignUpFragment
import base.app.model.AlertDialogManager
import base.app.model.GSConstants
import base.app.model.Model
import base.app.model.im.ChatInfo
import base.app.model.im.ImsManager
import base.app.model.im.ImsMessage
import base.app.model.im.event.ChatNotificationsEvent
import base.app.model.im.event.ChatsInfoUpdatesEvent
import base.app.model.im.event.CreateNewChatSuccessEvent
import base.app.model.im.event.UserIsTypingEvent
import base.app.model.user.UserInfo
import base.app.util.Utility
import base.app.util.ui.TranslationView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Optional
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions

import android.content.Context.DOWNLOAD_SERVICE
import base.app.Constant.REQUEST_CODE_CHAT_IMAGE_CAPTURE
import base.app.Constant.REQUEST_CODE_CHAT_IMAGE_PICK
import base.app.Constant.REQUEST_CODE_CHAT_VIDEO_CAPTURE
import kotlinx.android.synthetic.main.fragment_chat.*

@RuntimePermissions
@IgnoreBackHandling
class ChatFragment : BaseFragment() {

    internal var chatRightArrowDrawable: Drawable? = null
    internal var chatDotsDrawable: Drawable? = null

    internal var chatHeadsAdapter: ChatHeadsAdapter
    internal var messageAdapter: MessageAdapter
    internal var currentlyActiveChat: ChatInfo? = null

    private var recorder: MediaRecorder? = null

    private var audioFilepath: String? = null
    internal var currentPath: String

    internal var messageForEdit: ImsMessage? = null

    internal var activeAnimation: Animation

    private var urlInFullscreen: String? = null
    internal var audioRecordHandler: Handler


    internal var audioRecordHandlerTask: TimerTask = object : TimerTask() {
        override fun run() {
            if (!micButton.isPressed()) {
                return
            }
            recorder = MediaRecorder()
            recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            audioFilepath = Model.getAudioFileName()
            recorder!!.setOutputFile(audioFilepath)
            try {
                recorder!!.prepare()
                recorder!!.start()
            } catch (e: Exception) {
                Log.e(TAG, "Start of recording failed!")
            }

        }
    }

    private var resultFromPicker: Int = 0

    internal var disabledClick: View.OnClickListener = View.OnClickListener { }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        chatHeadsView.setLayoutManager(layoutManager)
        chatHeadsAdapter = ChatHeadsAdapter(activity)
        chatHeadsView.setAdapter(chatHeadsAdapter)

        progressBar.setVisibility(View.VISIBLE)
        infoMessage.setVisibility(View.GONE)

        val layoutTransition = LayoutTransition()
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        messageAdapter = MessageAdapter(context)
        messageListView.setAdapter(messageAdapter)

        messageAdapter.setTranslationView(translationView)
        translationView.setParentView(view)

        val snappyLinearLayoutManager = SnappyLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        snappyLinearLayoutManager.setSnapInterpolator(AccelerateDecelerateInterpolator())
        snappyLinearLayoutManager.setSnapDuration(1000)
        snappyLinearLayoutManager.setSeekDuration(1000)
        messageListView.setLayoutManager(snappyLinearLayoutManager)
        if (Utility.isPhone(activity)!! && ImsManager.getInstance().userChatsList.size == 0) {
            ImsManager.getInstance().reload()
        }
        swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            if (currentlyActiveChat != null) {
                val refreshingChat = currentlyActiveChat
                val nextPageTask = currentlyActiveChat!!.loadPreviousMessagesPage()
                nextPageTask.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val messages = task.result
                        if (messages != null) {
                            if (messages.size > 0) {
                                refreshingChat!!.addReceivedMessage(messages)
                                if (currentlyActiveChat != null) {
                                    if (currentlyActiveChat == refreshingChat) {
                                        messageAdapter.notifyDataSetChanged()
                                        messageListView.smoothScrollToPosition(0)
                                    }
                                }
                            }
                        }
                        swipeRefreshLayout.setRefreshing(false)
                    }
                }
            }
        })

        inputEditText.setImeActionLabel(context!!.resources.getString(R.string.chat_send), EditorInfo.IME_ACTION_SEND)
        inputEditText.setImeOptions(EditorInfo.IME_ACTION_SEND)

        /* input Listeners */
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (currentlyActiveChat != null) {
                    currentlyActiveChat!!.setUserIsTyping(true)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        inputEditText.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEND || actionId == R.id.action_send_message_id) {
                sendButtonOnClick()
                handled = true
            }
            handled
        })

        micButton.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            if (currentlyActiveChat != null) {
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        ChatFragmentPermissionsDispatcher.startRecordingWithPermissionCheck(this@ChatFragment)
                        micButton.setSelected(true)
                    }
                    MotionEvent.ACTION_UP -> {
                        stopRecording()
                        micButton.setSelected(false)
                    }
                }
            }
            false
        })

        chatRightArrowDrawable = ContextCompat.getDrawable(activity!!, R.drawable.chat_right_arrow)
        chatDotsDrawable = ContextCompat.getDrawable(activity!!, R.drawable.chat_menu_button)
        inputEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                chatButtonsMenu.setVisibility(View.VISIBLE)
                chatButtonsContainer.setVisibility(View.GONE)
                // slideToLeft(chatButtonsContainer);
            }
        })
        updateAllViews()

        if (Utility.isPhone(activity)!!) {
            onLoginStateChange()
        }

        if (fullScreenContainer != null) {
            fullScreenContainer.setOnClickListener(disabledClick)
        }
        videoViewContainer.setOnClickListener(disabledClick)

        return view
    }


    private fun onLoginStateChange() {
        if (Model.getInstance().isRealUser) {
            if (inactiveContainer != null) {
                inactiveContainer.setVisibility(View.GONE)
            }
        }
    }

    @Optional
    @OnClick(R.id.join_now_button)
    fun joinOnClick() {
        EventBus.getDefault().post(FragmentEvent(SignUpFragment::class.java))
    }

    @Optional
    @OnClick(R.id.login_button)
    fun loginOnClick() {
        EventBus.getDefault().post(FragmentEvent(LoginFragment::class.java))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //! Update active chat first
        findActiveChat()
        //! Update UI
        updateAllViews()
    }

    override fun onResume() {
        super.onResume()
        if (inactiveContainer != null) {
            if (Model.getInstance().isRealUser) {
                inactiveContainer.setVisibility(View.GONE)
            } else {
                inactiveContainer.setVisibility(View.VISIBLE)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (recorder != null) {
            recorder!!.release()
            recorder = null
        }
    }

    private fun updateTopChatsView() {
        // Setup chat heads
        chatHeadsAdapter.values = ImsManager.getInstance().userChatsList
        chatHeadsAdapter.notifyDataSetChanged()
        if (currentlyActiveChat != null) {
            chatHeadsAdapter.selectChat(currentlyActiveChat, false)
        }
    }

    fun updateAllViews() {
        updateTopChatsView()
        updateChatTitleText()
        messageAdapter.notifyDataSetChanged()
        setupEditChatButton()
        swipeRefreshLayout.setRefreshing(false)
        progressBar.setVisibility(View.GONE)
    }

    private fun setCurrentlyActiveChat(info: ChatInfo?) {
        currentlyActiveChat = info
        updateAllViews()
        if (currentlyActiveChat != null) {
            messageAdapter.setChatInfo(currentlyActiveChat)
            messageListView.scrollToPosition(messageAdapter.itemCount) // Scroll to bottom!
        } else {
            messageAdapter.setChatInfo(null)
        }
    }

    private fun updateChatTitleText() {
        if (currentlyActiveChat != null) {
            // Setup Chat label
            val chatLabel = StringBuilder(currentlyActiveChat!!.chatTitle)
            chatLabel.append(": ")
            val size = currentlyActiveChat!!.usersIds.size
            var count = 0
            for (userId in currentlyActiveChat!!.usersIds) {
                count++
                val info = Model.getInstance().getCachedUserInfoById(userId)
                if (info != null) {
                    val chatName = info.nicName
                    if (!TextUtils.isEmpty(chatName)) {
                        chatLabel.append(chatName)
                        if (count < size) {
                            chatLabel.append(", ")
                        }
                    }
                }
            }
            infoLineTextView.setText(chatLabel.toString())
        }
    }

    private fun setupEditChatButton() {
        if (currentlyActiveChat != null) {
            val user = Model.getInstance().userInfo
            if (user != null) {
                if (messageForEdit != null) {
                    chatMenuDeleteButton.setVisibility(View.VISIBLE)
                    chatMenuSearchButton.setVisibility(View.GONE)
                    chatMenuCreateButton.setVisibility(View.GONE)
                    chatMenuAlertsButton.setVisibility(View.GONE)
                    chatMenuEditButton.setText(context!!.resources.getText(R.string.chat_edit))
                } else {
                    chatMenuDeleteButton.setVisibility(View.GONE)
                    chatMenuAlertsButton.setVisibility(View.VISIBLE)
                    if (currentlyActiveChat!!.isMuted) {
                        chatMenuAlertsButton.setText(R.string.alerts_off)
                    } else {
                        chatMenuAlertsButton.setText(R.string.alerts_on)
                    }
                    chatMenuSearchButton.setVisibility(View.VISIBLE)
                    chatMenuCreateButton.setVisibility(View.VISIBLE)
                    if (user.userId == currentlyActiveChat!!.owner) {
                        chatMenuEditButton.setText(context!!.resources.getText(R.string.chat_edit))
                    } else {
                        chatMenuEditButton.setText(context!!.resources.getString(R.string.chat_Leave))
                    }
                }
            } else {
                chatMenuEditButton.setText(context!!.resources.getString(R.string.chat_Leave))
            }
        }
    }

    /**
     * * ** ** ** ** ** ** ** ** ** ** ** **
     * Click listeners
     * * ** ** ** ** ** ** ** ** ** ** ** **
     */

    @OnClick(R.id.menu_button)
    fun chatButtonsMenuOnClick() {
        if (!Model.getInstance().isRealUser) {
            if (inactiveContainer != null) {
                inactiveContainer.setVisibility(View.VISIBLE)
            }
            return
        }
        if (currentlyActiveChat != null) {
            chatButtonsMenu.setVisibility(View.GONE)
            chatButtonsContainer.setVisibility(View.VISIBLE)
        }
    }

    @OnClick(R.id.chat_menu_dots)
    fun chatMenuDotsContainerOnClick() {
        if (!Model.getInstance().isRealUser) {
            if (inactiveContainer != null) {
                inactiveContainer.setVisibility(View.VISIBLE)
            }
            return
        }
        if (currentlyActiveChat != null) {
            messageForEdit = null
            animateChatMenu()
        }
    }

    private fun animateChatMenu() {
        if (chatMenuDotsContainer.getVisibility() === View.GONE) {
            animate(chatMenuDotsContainer, View.VISIBLE, R.anim.slide_in_left)
            chatMenuDotsContainer.setVisibility(View.VISIBLE)
            chatMenuDotsImageView.setImageDrawable(chatRightArrowDrawable)
            setupEditChatButton()
        } else {
            chatMenuDotsContainer.setVisibility(View.GONE)
            animate(chatMenuDotsContainer, View.GONE, R.anim.slide_in_right)
            chatMenuDotsImageView.setImageDrawable(chatDotsDrawable)
            if (chatButtonsMenu.getVisibility() === View.GONE) {
                chatButtonsMenu.setVisibility(View.VISIBLE)
                chatButtonsContainer.setVisibility(View.GONE)
            }
        }
    }

    @Subscribe
    fun setMessageForEdit(event: MessageSelectedEvent) {
        this.messageForEdit = event.selectedMessage
        animateChatMenu()
        //       HANDLE THIS BUTTONS
        //       R.id.chat_menu_edit
        //       R.id.chat_menu_delete

        //       @BindView(R.id.chat_menu_delete)
        //       TextView chatMenuDeleteButton;

        //       @BindView(R.id.chat_menu_edit)
        //       TextView chatMenuEditButton;
    }

    private fun animate(view: View, visibility: Int, anim: Int) {
        activeAnimation = AnimationUtils.loadAnimation(activity, anim)
        view.startAnimation(activeAnimation)
        view.visibility = visibility
    }

    @Optional
    @OnClick(R.id.close_image_button)
    fun imageCloseButtonOnClick() {
        if (fullScreenContainer != null) {
            fullScreenContainer.setVisibility(View.GONE)
        }
    }

    @Optional
    @OnClick(R.id.download_image_button)
    fun imageDownloadButtonOnClick() {
        ChatFragmentPermissionsDispatcher.invokeDownloadButtonWithPermissionCheck(this)
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun invokeDownloadButton() {
        val downloadManager = activity!!.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(urlInFullscreen)
        val fileName = FilenameUtils.getName(uri.path)
        val downloadRequest = DownloadManager.Request(uri)
        downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        downloadRequest.setVisibleInDownloadsUi(true)
        downloadRequest.setTitle("SSK - Downloading a chat image")
        downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        downloadManager?.enqueue(downloadRequest)
    }

    @OnClick(R.id.video_close_image_button)
    fun videoCloseButtonOnClick() {
        videoViewContainer.setVisibility(View.GONE)
        if (videoView != null) {
            videoView.stopPlayback()
            videoView.closePlayer()
            if (mediaController != null) {
                mediaController.reset()
            }
        }
    }

    @Optional
    @OnClick(R.id.sticker_button)
    fun stickerButtonOnClick() {
        //TODO open sticker
    }

    @Optional
    @OnClick(R.id.vide_download_image_button)
    fun videoDownloadButtonOnClick() {
        Toast.makeText(context, context!!.resources.getString(R.string.chat_video_downloaded), Toast.LENGTH_SHORT).show()
    }

    fun sendButtonOnClick() {
        if (!Model.getInstance().isRealUser) {
            if (inactiveContainer != null) {
                inactiveContainer.setVisibility(View.VISIBLE)
            }
            return
        }
        if (Model.getInstance().isRealUser && currentlyActiveChat != null) {
            val textMessage = inputEditText.getText().toString().trim()
            sendTextMessage(textMessage)
        } else {
            if (inactiveContainer != null) {
                inactiveContainer.setVisibility(View.VISIBLE)
            }
        }
        inputEditText.setText("")
        Utility.hideKeyboard(activity)
    }

    private fun sendTextMessage(text: String) {
        if (messageForEdit != null) {
            messageForEdit!!.text = text
            currentlyActiveChat!!.updateMessage(messageForEdit, null)
            messageForEdit = null
        } else {
            val message = ImsMessage.getDefaultMessage()
            message.type = GSConstants.UPLOAD_TYPE_TEXT
            message.text = text
            currentlyActiveChat!!.sendMessage(message)
            currentlyActiveChat!!.setUserIsTyping(false)
        }
    }


    @OnClick(R.id.chat_menu_create)
    fun chatMenuCreateOnClick() {
        if (Model.getInstance().isRealUser) {
            EventBus.getDefault().post(FragmentEvent(CreateChatFragment::class.java))
        } else {
            if (inactiveContainer != null) {
                inactiveContainer.setVisibility(View.VISIBLE)
            }
        }
    }

    @OnClick(R.id.chat_menu_delete)
    fun chatMenuDeleteOnClick() {
        if (messageForEdit != null) {
            if (currentlyActiveChat != null) {
                currentlyActiveChat!!.deleteMessage(messageForEdit, null)
                animateChatMenu()
            }
        }
    }

    @OnClick(R.id.chat_menu_alerts)
    fun chatMenuAlertsOnClick() {
        currentlyActiveChat!!.setMuteChat(!currentlyActiveChat!!.isMuted)
        if (currentlyActiveChat!!.isMuted) {
            chatMenuAlertsButton.setText(R.string.alerts_off)
        } else {
            chatMenuAlertsButton.setText(R.string.alerts_on)
        }
    }


    @OnClick(R.id.chat_menu_edit)
    fun chatMenuEditOnClick() {
        val user = Model.getInstance().userInfo
        if (currentlyActiveChat != null && user != null) {
            if (messageForEdit != null) {
                Toast.makeText(context, "Edit mode for chat message activated", Toast.LENGTH_SHORT).show()
                animateChatMenu()
                inputEditText.setText(messageForEdit!!.text)
            } else {
                if (Model.getInstance().userInfo!!.userId == currentlyActiveChat!!.owner) {
                    val fe = FragmentEvent(EditChatFragment::class.java)
                    fe.id = currentlyActiveChat!!.chatId
                    EventBus.getDefault().post(fe)
                } else {
                    AlertDialogManager.getInstance().showAlertDialog(context!!.resources.getString(R.string.are_you_sure), context!!.resources.getString(R.string.chat_leave_chat),
                                // Cancel listener
                    {
                        activity!!.onBackPressed()
                    }) // Confirm listener
                    {
                        activity!!.onBackPressed()
                        currentlyActiveChat!!.deleteChat()
                        if (chatMenuDotsContainer.getVisibility() === View.VISIBLE) {
                            chatMenuDotsContainerOnClick()
                        }
                    }

                    chatMenuEditButton.setText(context!!.resources.getString(R.string.chat_Leave))
                }
            }
        }
    }

    @OnClick(R.id.chat_menu_search)
    fun chatMenuSearchOnClick() {
        EventBus.getDefault().post(FragmentEvent(JoinChatFragment::class.java))
    }

    @OnClick(R.id.image_button)
    fun selectImageOnClick() {
        if (currentlyActiveChat != null) {
            ChatFragmentPermissionsDispatcher.invokeImageSelectionWithPermissionCheck(this)
        }
    }

    @OnClick(R.id.camera_button)
    fun cameraButtonOnClick() {
        if (currentlyActiveChat != null) {
            ChatFragmentPermissionsDispatcher.invokeCameraCaptureWithPermissionCheck(this)
        }
    }

    private fun sendAudioMessage(path: String?) {
        val messageAudio = ImsMessage.getDefaultMessage()
        messageAudio.type = GSConstants.UPLOAD_TYPE_AUDIO
        messageAudio.uploadStatus = GSConstants.UPLOADING
        messageAudio.imageUrl = ""
        messageAudio.localPath = path
        messageAudio.imageAspectRatio = 1f
        currentlyActiveChat!!.sendMessage(messageAudio).addOnCompleteListener {
            val source = TaskCompletionSource<String>()
            source.task.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    messageAudio.localPath = null
                    messageAudio.imageUrl = task.result
                    messageAudio.uploadStatus = GSConstants.UPLOADED
                    currentlyActiveChat!!.updateMessage(messageAudio, null)
                    // TODO @Filip Hide waiting dialog ?
                } else {
                    messageAudio.imageUrl = ""
                    messageAudio.uploadStatus = GSConstants.FAILED
                    currentlyActiveChat!!.updateMessage(messageAudio, null)
                    // TODO @Filip Hide waiting dialog ? - show error?
                }
            }
            Model.getInstance().uploadAudioRecordingForChat(path, activity!!.filesDir, source)
        }
    }

    private fun sendImageMessage(path: String) {
        val preppingImsObject = ImsMessage.getDefaultMessage()
        preppingImsObject.type = GSConstants.UPLOAD_TYPE_IMAGE
        preppingImsObject.uploadStatus = GSConstants.UPLOADING
        preppingImsObject.text = null
        preppingImsObject.imageUrl = null
        preppingImsObject.localPath = path

        currentlyActiveChat!!.sendMessage(preppingImsObject).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val source = TaskCompletionSource<String>()
                source.task.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        preppingImsObject.imageUrl = task.result
                        preppingImsObject.uploadStatus = GSConstants.UPLOADED
                        preppingImsObject.localPath = null
                        currentlyActiveChat!!.updateMessage(preppingImsObject, null)
                        // TODO @Filip Update UI with image
                    } else {
                        preppingImsObject.imageUrl = ""
                        preppingImsObject.uploadStatus = GSConstants.FAILED
                        currentlyActiveChat!!.updateMessage(preppingImsObject, null)
                        // TODO @Filip Hide waiting dialog ? - show error?
                    }
                }
                Model.getInstance().uploadImageForChatMessage(path, activity!!.filesDir, source)
            }
        }
    }

    private fun sendVideoMessage(path: String) {

        val preppingImsObject = ImsMessage.getDefaultMessage()
        preppingImsObject.type = GSConstants.UPLOAD_TYPE_VIDEO
        preppingImsObject.uploadStatus = GSConstants.UPLOADING
        preppingImsObject.text = null
        preppingImsObject.imageUrl = null
        preppingImsObject.localPath = path


        currentlyActiveChat!!.sendMessage(preppingImsObject).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val source = TaskCompletionSource<String>()
                source.task.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        preppingImsObject.imageUrl = task.result
                        val source = TaskCompletionSource<String>()
                        source.task.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                preppingImsObject.vidUrl = task.result
                                preppingImsObject.uploadStatus = GSConstants.UPLOADED
                                preppingImsObject.localPath = null
                                currentlyActiveChat!!.updateMessage(preppingImsObject, null)
                            } else {
                                preppingImsObject.vidUrl = null
                                preppingImsObject.uploadStatus = GSConstants.FAILED
                                currentlyActiveChat!!.updateMessage(preppingImsObject, null)
                            }
                        }
                        Model.getInstance().uploadChatVideoRecording(currentPath, activity!!.filesDir, source)
                    } else {
                        preppingImsObject.imageUrl = null
                        preppingImsObject.vidUrl = null
                        preppingImsObject.uploadStatus = GSConstants.FAILED
                        currentlyActiveChat!!.updateMessage(preppingImsObject, null)
                    }
                }
                Model.getInstance().uploadChatVideoRecordingThumbnail(path, activity!!.filesDir, source)
            }
        }
    }

    /**
     * * ** ** ** ** ** ** ** ** ** ** ** **
     * Event listeners
     * * ** ** ** ** ** ** ** ** ** ** ** **
     */

    @Subscribe
    fun onEvent(event: ChatsInfoUpdatesEvent) {
        findActiveChat()
        //updateAllViews(); // TOTALLY NOT NEEDED!
        checkPushNotification()
    }

    @Subscribe
    fun onEvent(event: CreateNewChatSuccessEvent) {
        val chatInfo = event.chatInfo
        setCurrentChatNotification(chatInfo)
    }

    @Subscribe
    fun onEvent(event: ChatNotificationsEvent) {
        Log.d(TAG, "Received ChatNotificationsEvent: " + event.key.name)
        var chatInfo: ChatInfo? = event.chatInfo
        when (event.key) {
            ChatNotificationsEvent.Key.UPDATED_CHAT_USERS -> handleUpdatedChatUsers(chatInfo)
            ChatNotificationsEvent.Key.DELETED_CHAT_MESSAGE -> messageAdapter.notifyDataSetChanged()
            ChatNotificationsEvent.Key.CHANGED_CHAT_MESSAGE -> if (currentlyActiveChat != null) {
                val messages = currentlyActiveChat!!.messages
                val messageToUpdate = event.message
                if (messages != null && messageToUpdate != null) {
                    if (messages.contains(messageToUpdate)) {
                        messageAdapter.notifyItemChanged(messages.indexOf(messageToUpdate))
                    } else {
                        Log.e(TAG, "Active chat does not contain this message!")
                    }
                } else {
                    Log.e(TAG, "Message to be updated is not in active chat!")
                }
            }
            ChatNotificationsEvent.Key.UPDATED_CHAT_MESSAGES -> {
                var chatId: String? = null
                chatInfo = event.chatInfo
                if (chatInfo != null) {
                    chatId = chatInfo.chatId
                }
                handleUpdatedChatMessages(chatId)
            }
            ChatNotificationsEvent.Key.SET_CURRENT_CHAT -> {
                chatInfo = event.chatInfo
                setCurrentChatNotification(chatInfo)
            }
        }
    }

    private fun handleUpdatedChatUsers(chatInfo: ChatInfo?) {
        if (currentlyActiveChat != null) {
            setCurrentlyActiveChat(ImsManager.getInstance().getChatInfoById(currentlyActiveChat!!.chatId))
        }
        updateAllViews()
        if (currentlyActiveChat != null) {
            if (currentlyActiveChat!!.chatId == chatInfo!!.chatId) {
                Log.d(TAG, "Single chat should be updated!")
            }
        }
    }

    private fun handleUpdatedChatMessages(chatId: String?) {
        updateTopChatsView()
        if (currentlyActiveChat != null) {// If current chat was updated
            if (currentlyActiveChat!!.chatId == chatId) {
                swipeRefreshLayout.setRefreshing(false)
                progressBar.setVisibility(View.GONE)
                messageAdapter.notifyDataSetChanged()
                messageListView.smoothScrollToPosition(messageAdapter.itemCount) // Scroll to bottom!
            }
        }
    }

    private fun setCurrentChatNotification(chatInfo: ChatInfo?) {
        messageForEdit = null
        if (currentlyActiveChat != null) {
            if (currentlyActiveChat!!.chatId == chatInfo!!.chatId) { // Its the same chat - hide or show edit buttons
                chatMenuDotsContainerOnClick()
            } else { // its not the same chat, so hide edit buttons if those are visible
                if (chatMenuDotsContainer.getVisibility() === View.VISIBLE) {
                    chatMenuDotsContainerOnClick()
                }
                setCurrentlyActiveChat(chatInfo)
            }
        } else {
            setCurrentlyActiveChat(chatInfo)
        }
        messageListView.smoothScrollToPosition(messageAdapter.itemCount) // Scroll to bottom!
    }

    private fun checkPushNotification() {
        Log.d(TAG, "Missing implementation for checkPushNotification method!")
    }

    private fun findActiveChat() {
        val chatId: String
        if (currentlyActiveChat != null) {
            chatId = this.currentlyActiveChat!!.chatId
            val chat = ImsManager.getInstance().getChatInfoById(chatId)
            if (chat != null) {
                setCurrentlyActiveChat(chat)
                return
            }
        }
        //! Set first one if chat was not selected
        setFirstChatAsActive()
    }

    private fun setFirstChatAsActive() {
        if (ImsManager.getInstance().userChatsList != null) {
            val chats = ImsManager.getInstance().userChatsList
            if (chats != null && chats.size > 0) {
                setCurrentlyActiveChat(chats[0])
                return
            }
        }
        setCurrentlyActiveChat(null)
    }

    @Subscribe
    fun playVideo(event: PlayVideoEvent) {
        if (videoView != null) {
            videoView.setMediaController(mediaController)
        }
        if (mediaController != null) {
            mediaController.setOnLoadingView(R.layout.loading)
            mediaController.setOnErrorView(R.layout.error)
            mediaController.reset()
        }
        videoViewContainer.setVisibility(View.VISIBLE)
        videoView.setVideoURI(Uri.parse(event.id))
        videoView.setOnPreparedListener(MediaPlayer.OnPreparedListener { videoView.start() })
        mediaController.setOnErrorViewClick(View.OnClickListener { videoCloseButtonOnClick() })
        videoView.setOnCompletionListener(MediaPlayer.OnCompletionListener {
            videoView.seekTo(0)
            videoView.pause()
            mediaController.reset()
        })
    }

    @Subscribe
    fun showImageFullScreen(event: FullScreenImageEvent) {
        if (fullScreenContainer != null) {
            fullScreenContainer.setVisibility(View.VISIBLE)
        }
        urlInFullscreen = event.id
        if (imageViewFullScreen != null) {
            ImageLoader.getInstance().displayImage(urlInFullscreen, imageViewFullScreen)
        }
    }

    /**
     * * ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
     * CAMERA, MIC, IMAGES...
     * * ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
     */


    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun invokeImageSelection() {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, REQUEST_CODE_CHAT_IMAGE_PICK)//one can be replaced with any action code
    }

    @NeedsPermission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun invokeCameraCapture() {
        val chooseDialog = AlertDialog.Builder(activity!!, R.style.AlertDialog)
        chooseDialog.setTitle(context!!.resources.getString(R.string.choose))
        chooseDialog.setMessage(context!!.resources.getString(R.string.chat_image_or_video))
        chooseDialog.setNegativeButton(context!!.resources.getString(R.string.record_video)) { dialog, which ->
            val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            if (takeVideoIntent.resolveActivity(activity!!.packageManager) != null) {
                startActivityForResult(takeVideoIntent, REQUEST_CODE_CHAT_VIDEO_CAPTURE)
            }
        }
        chooseDialog.setPositiveButton(context!!.resources.getString(R.string.chat_image)) { dialog, which ->
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
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
                startActivityForResult(takePictureIntent, REQUEST_CODE_CHAT_IMAGE_CAPTURE)
            }
        }
        chooseDialog.show()
    }

    @NeedsPermission(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun startRecording() {
        Toast.makeText(context, context!!.resources.getString(R.string.chat_hold), Toast.LENGTH_SHORT).show()
        audioRecordHandler = Handler()
        audioRecordHandler.postDelayed(audioRecordHandlerTask, 250)
    }

    private fun stopRecording() {
        if (recorder != null) {
            try {
                audioRecordHandler.removeCallbacks(audioRecordHandlerTask)
                recorder!!.stop()
                recorder!!.release()
                Toast.makeText(context, context!!.resources.getString(R.string.chat_recording_stop), Toast.LENGTH_SHORT).show()
                sendAudioMessage(audioFilepath)
                recorder = null
            } catch (e: Exception) {
                Log.e(TAG, "Stop recording failed!")
            }

        }
    }

    @OnShowRationale(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    internal fun showRationaleForCamera(request: PermissionRequest) {
        AlertDialog.Builder(context!!, R.style.AlertDialog)
                .setMessage(R.string.permission_camera_rationale)
                .setPositiveButton(R.string.button_allow, DialogInterface.OnClickListener { dialogInterface, i -> request.proceed() })
                .setNegativeButton(R.string.button_deny, DialogInterface.OnClickListener { dialogInterface, i -> request.cancel() })
                .show()
    }

    @OnShowRationale(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    internal fun showRationaleForMicrophone(request: PermissionRequest) {
        AlertDialog.Builder(context!!, R.style.AlertDialog)
                .setMessage(R.string.permission_microphone_rationale)
                .setPositiveButton(R.string.button_allow, DialogInterface.OnClickListener { dialogInterface, i -> request.proceed() })
                .setNegativeButton(R.string.button_deny, DialogInterface.OnClickListener { dialogInterface, i -> request.cancel() })
                .show()
    }

    @OnPermissionDenied(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    internal fun showDeniedForMicrophone() {
        Toast.makeText(context, R.string.permission_microphone_denied, Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    internal fun showNeverAskForMicrophone() {
        Toast.makeText(context, R.string.permission_microphone_never_ask, Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        ChatFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults)
    }


    /**
     * Since onStart is called after onActivity result, we have to store the code and delay
     * uploading and sending of the message in order to make sure fragment is subscribed to EvenBus
     */
    override fun onStart() {
        super.onStart()
        when (resultFromPicker) {
            REQUEST_CODE_CHAT_IMAGE_CAPTURE, REQUEST_CODE_CHAT_IMAGE_PICK -> sendImageMessage(currentPath)
            REQUEST_CODE_CHAT_VIDEO_CAPTURE -> sendVideoMessage(currentPath)
        }
        resultFromPicker = -1 // Reset this value to prevent resending after onStart is triggered
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        resultFromPicker = -1
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CHAT_IMAGE_PICK, REQUEST_CODE_CHAT_VIDEO_CAPTURE -> {
                    val uri = intent!!.data
                    currentPath = Model.getRealPathFromURI(context, uri)
                    resultFromPicker = requestCode
                }
                REQUEST_CODE_CHAT_IMAGE_CAPTURE -> resultFromPicker = requestCode
            }
        }
    }


    @Subscribe
    fun handleUserIsTypingEvent(event: UserIsTypingEvent) {
        if (currentlyActiveChat != null) {
            if (event.chatId == currentlyActiveChat!!.chatId) {
                val users = event.users
                if (users.size == 1) {
                    val userName = users[0].nicName
                    infoLineTextView.setText(getString(R.string.single_user_is_typing, userName))
                } else if (users.size > 1) {
                    infoLineTextView.setText(R.string.multiple_users_are_typing)
                } else {
                    updateChatTitleText()
                }
            }
        }
    }

    @Subscribe
    fun openChatEvent(event: OpenChatEvent) {
        chatHeadsAdapter.notifyDataSetChanged()
    }

    override fun onStop() {
        super.onStop()
        translationView.setVisibility(View.GONE)
    }

    companion object {

        private val TAG = "CHAT Fragment"
    }
}// Required empty public constructor
