package base.app.ui.fragment.other;


import android.Manifest;
import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import base.app.util.ui.ImageLoader;
import com.nshmura.snappysmoothscroller.SnappyLinearLayoutManager;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.commons.io.FilenameUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TimerTask;

import base.app.BuildConfig;
import base.app.R;
import base.app.ui.adapter.chat.ChatHeadsAdapter;
import base.app.ui.adapter.chat.MessageAdapter;
import base.app.util.events.chat.FullScreenImageEvent;
import base.app.util.events.chat.MessageSelectedEvent;
import base.app.util.events.chat.OpenChatEvent;
import base.app.util.events.stream.PlayVideoEvent;
import base.app.ui.fragment.base.BaseFragment;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.ui.fragment.base.IgnoreBackHandling;
import base.app.ui.fragment.popup.CreateChatFragment;
import base.app.ui.fragment.popup.EditChatFragment;
import base.app.ui.fragment.popup.JoinChatFragment;
import base.app.ui.fragment.popup.LoginFragment;
import base.app.ui.fragment.popup.SignUpFragment;
import base.app.data.AlertDialogManager;
import base.app.data.GSConstants;
import base.app.data.Model;
import base.app.data.im.ChatInfo;
import base.app.data.im.ImsManager;
import base.app.data.im.ImsMessage;
import base.app.data.im.event.ChatNotificationsEvent;
import base.app.data.im.event.ChatsInfoUpdatesEvent;
import base.app.data.im.event.CreateNewChatSuccessEvent;
import base.app.data.im.event.UserIsTypingEvent;
import base.app.data.user.UserInfo;
import base.app.util.commons.Utility;
import base.app.util.ui.TranslationView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static android.content.Context.DOWNLOAD_SERVICE;
import static base.app.util.commons.Constant.REQUEST_CODE_CHAT_IMAGE_CAPTURE;
import static base.app.util.commons.Constant.REQUEST_CODE_CHAT_IMAGE_PICK;
import static base.app.util.commons.Constant.REQUEST_CODE_CHAT_VIDEO_CAPTURE;

/**

 */

@RuntimePermissions
@IgnoreBackHandling
public class ChatFragment extends BaseFragment {

    private static final String TAG = "CHAT Fragment";
    @BindView(R.id.message_container)
    RecyclerView messageListView;
    @BindView(R.id.chat_heads_view)
    RecyclerView chatHeadsView;
    @BindView(R.id.progressBar)
    AVLoadingIndicatorView progressBar;

    @BindView(R.id.info_message)
    TextView infoMessage;
    @BindView(R.id.chat_info_line)
    View chatInfoLine;
    @BindView(R.id.chat_info_line_text)
    TextView infoLineTextView;

    @BindView(R.id.mic_button)
    ImageButton micButton;
    @BindView(R.id.video_view_container)
    RelativeLayout videoViewContainer;

    @Nullable
    @BindView(R.id.mediaController)
    UniversalMediaController mediaController;

    @Nullable
    @BindView(R.id.videoView)
    UniversalVideoView videoView;

    @Nullable
    @BindView(R.id.image_fullscreen)
    ImageView imageViewFullScreen;
    @BindView(R.id.full_screen_container)
    @Nullable
    RelativeLayout fullScreenContainer;
    @BindView(R.id.input_edit_text)
    EditText inputEditText;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.chat_buttons_container)
    RelativeLayout chatButtonsContainer;

    @BindView(R.id.menu_button)
    ImageButton chatButtonsMenu;
    @BindView(R.id.chat_menu_dots_container)
    LinearLayout chatMenuDotsContainer;
    @BindView(R.id.chat_menu_dots)
    ImageView chatMenuDotsImageView;


    @BindView(R.id.chat_menu_alerts)
    TextView chatMenuAlertsButton;

    @BindView(R.id.chat_menu_delete)
    TextView chatMenuDeleteButton;

    @BindView(R.id.chat_menu_edit)
    TextView chatMenuEditButton;

    @BindView(R.id.chat_menu_search)
    TextView chatMenuSearchButton;

    @BindView(R.id.chat_menu_create)
    TextView chatMenuCreateButton;

    @Nullable
    @BindView(R.id.logo)
    ImageView Logo;
    @BindView(R.id.backgroundImage)
    ImageView backgroundImage;
    @Nullable
    @BindView(R.id.login_container)
    LinearLayout loginContainer;

    @Nullable
    @BindView(R.id.inactive_container)
    RelativeLayout inactiveContainer;

    Drawable chatRightArrowDrawable;
    Drawable chatDotsDrawable;

    ChatHeadsAdapter chatHeadsAdapter;
    MessageAdapter messageAdapter;
    ChatInfo currentlyActiveChat;

    private MediaRecorder recorder = null;

    private String audioFilepath = null;
    String currentPath;

    @BindView(R.id.translation_view)
    TranslationView translationView;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        chatHeadsView.setLayoutManager(layoutManager);
        chatHeadsAdapter = new ChatHeadsAdapter(getActivity());
        chatHeadsView.setAdapter(chatHeadsAdapter);

        progressBar.setVisibility(View.VISIBLE);
        infoMessage.setVisibility(View.GONE);

        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);

        messageAdapter = new MessageAdapter(getContext());
        messageListView.setAdapter(messageAdapter);

        messageAdapter.setTranslationView(translationView);
        translationView.setParentView(view);

        SnappyLinearLayoutManager snappyLinearLayoutManager = new SnappyLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        snappyLinearLayoutManager.setSnapInterpolator(new AccelerateDecelerateInterpolator());
        snappyLinearLayoutManager.setSnapDuration(1000);
        snappyLinearLayoutManager.setSeekDuration(1000);
        messageListView.setLayoutManager(snappyLinearLayoutManager);
        if (Utility.isPhone(getActivity()) && ImsManager.getInstance().getUserChatsList().size() == 0) {
            ImsManager.getInstance().reload();
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (currentlyActiveChat != null) {
                    final ChatInfo refreshingChat = currentlyActiveChat;
                    Task<List<ImsMessage>> nextPageTask = currentlyActiveChat.loadPreviousMessagesPage();
                    nextPageTask.addOnCompleteListener(new OnCompleteListener<List<ImsMessage>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<ImsMessage>> task) {
                            if(task.isSuccessful()){
                                List<ImsMessage> messages = task.getResult();
                                if(messages!=null){
                                    if(messages.size()>0){
                                        refreshingChat.addReceivedMessage(messages);
                                        if(currentlyActiveChat!=null){
                                            if(currentlyActiveChat.equals(refreshingChat)){
                                                messageAdapter.notifyDataSetChanged();
                                                messageListView.smoothScrollToPosition(0);
                                            }
                                        }
                                    }
                                }
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    });
                }
            }
        });

        inputEditText.setImeActionLabel(getContext().getResources().getString(R.string.chat_send), EditorInfo.IME_ACTION_SEND);
        inputEditText.setImeOptions(EditorInfo.IME_ACTION_SEND);

        /* input Listeners */
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (currentlyActiveChat != null) {
                    currentlyActiveChat.setUserIsTyping(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        inputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == R.id.action_send_message_id) {
                    sendButtonOnClick();
                    handled = true;
                }
                return handled;
            }
        });

        micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (currentlyActiveChat != null) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            ChatFragmentPermissionsDispatcher.startRecordingWithPermissionCheck(ChatFragment.this);
                            micButton.setSelected(true);
                            break;
                        case MotionEvent.ACTION_UP:
                            stopRecording();
                            micButton.setSelected(false);
                            break;
                    }
                }
                return false;
            }
        });

        chatRightArrowDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.chat_right_arrow);
        chatDotsDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.chat_menu_button);
        inputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    chatButtonsMenu.setVisibility(View.VISIBLE);
                    chatButtonsContainer.setVisibility(View.GONE);
                    // slideToLeft(chatButtonsContainer);
                }
            }
        });
        updateAllViews();

        if (Utility.isPhone(getActivity()))
        {
            onLoginStateChange();
        }

        if (fullScreenContainer != null) {
            fullScreenContainer.setOnClickListener(disabledClick);
        }
        videoViewContainer.setOnClickListener(disabledClick);

        Glide.with(getContext())
                .load(R.drawable.phone_chat_background)
                .into(backgroundImage);

        return view;
    }


    private void onLoginStateChange() {
        if (Model.getInstance().isRealUser()) {
            if (inactiveContainer != null) {
                inactiveContainer.setVisibility(View.GONE);
            }
        }
    }

    @Optional
    @OnClick(R.id.join_now_button)
    public void joinOnClick() {
        EventBus.getDefault().post(new FragmentEvent(SignUpFragment.class));
    }

    @Optional
    @OnClick(R.id.login_button)
    public void loginOnClick() {
        EventBus.getDefault().post(new FragmentEvent(LoginFragment.class));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //! Update active chat first
        findActiveChat();
        //! Update UI
        updateAllViews();
    }

    public void onResume(){
        super.onResume();
        if (inactiveContainer != null) {
            if (Model.getInstance().isRealUser()) {
                inactiveContainer.setVisibility(View.GONE);
            } else {
                inactiveContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }

    private void updateTopChatsView() {
        // Setup chat heads
        chatHeadsAdapter.setValues(ImsManager.getInstance().getUserChatsList());
        chatHeadsAdapter.notifyDataSetChanged();
        if (currentlyActiveChat != null) {
            chatHeadsAdapter.selectChat(currentlyActiveChat, false);
        }
    }

    public void updateAllViews() {
        updateTopChatsView();
        updateChatTitleText();
        messageAdapter.notifyDataSetChanged();
        setupEditChatButton();
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
    }

    private void setCurrentlyActiveChat(ChatInfo info) {
        currentlyActiveChat = info;
        updateAllViews();
        if (currentlyActiveChat != null) {
            messageAdapter.setChatInfo(currentlyActiveChat);
            messageListView.scrollToPosition(messageAdapter.getItemCount()); // Scroll to bottom!
        } else {
            messageAdapter.setChatInfo(null);
        }
    }

    private void updateChatTitleText() {
        if (currentlyActiveChat != null) {
            // Setup Chat label
            StringBuilder chatLabel = new StringBuilder(currentlyActiveChat.getChatTitle());
            chatLabel.append(": ");
            int size = currentlyActiveChat.getUsersIds().size();
            int count = 0;
            for (String userId : currentlyActiveChat.getUsersIds()) {
                count++;
                UserInfo info = Model.getInstance().getCachedUserInfoById(userId);
                if (info != null) {
                    String chatName = info.getNicName();
                    if (!TextUtils.isEmpty(chatName)) {
                        chatLabel.append(chatName);
                        if (count < size) {
                            chatLabel.append(", ");
                        }
                    }
                }
            }
            infoLineTextView.setText(chatLabel.toString());
        }
    }

    private void setupEditChatButton() {
        if (currentlyActiveChat != null) {
            UserInfo user = Model.getInstance().getUserInfo();
            if (user != null) {
                if(messageForEdit !=null){
                    chatMenuDeleteButton.setVisibility(View.VISIBLE);
                    chatMenuSearchButton.setVisibility(View.GONE);
                    chatMenuCreateButton.setVisibility(View.GONE);
                    chatMenuAlertsButton.setVisibility(View.GONE);
                    chatMenuEditButton.setText(getContext().getResources().getText(R.string.chat_edit));
                } else {
                    chatMenuDeleteButton.setVisibility(View.GONE);
                    chatMenuAlertsButton.setVisibility(View.VISIBLE);
                    if(currentlyActiveChat.getIsMuted()){
                        chatMenuAlertsButton.setText(R.string.alerts_off);
                    } else {
                        chatMenuAlertsButton.setText(R.string.alerts_on);
                    }
                    chatMenuSearchButton.setVisibility(View.VISIBLE);
                    chatMenuCreateButton.setVisibility(View.VISIBLE);
                    if (user.getUserId().equals(currentlyActiveChat.getOwner())) {
                        chatMenuEditButton.setText(getContext().getResources().getText(R.string.chat_edit));
                    } else {
                        chatMenuEditButton.setText(getContext().getResources().getString(R.string.chat_Leave));
                    }
                }
            } else {
                chatMenuEditButton.setText(getContext().getResources().getString(R.string.chat_Leave));
            }
        }
    }

    /**
     * * ** ** ** ** ** ** ** ** ** ** ** **
     * Click listeners
     * * ** ** ** ** ** ** ** ** ** ** ** **
     **/

    @OnClick(R.id.menu_button)
    public void chatButtonsMenuOnClick() {
        if(!Model.getInstance().isRealUser()){
            if (inactiveContainer != null) {
                inactiveContainer.setVisibility(View.VISIBLE);
            }
            return;
        }
        if (currentlyActiveChat != null) {
            chatButtonsMenu.setVisibility(View.GONE);
            chatButtonsContainer.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.chat_menu_dots)
    public void chatMenuDotsContainerOnClick() {
        if(!Model.getInstance().isRealUser()){
            if (inactiveContainer != null) {
                inactiveContainer.setVisibility(View.VISIBLE);
            }
            return;
        }
        if (currentlyActiveChat != null) {
            messageForEdit = null;
            animateChatMenu();
        }
    }

    private void animateChatMenu(){
        if (chatMenuDotsContainer.getVisibility() == View.GONE) {
            animate(chatMenuDotsContainer, View.VISIBLE, R.anim.slide_in_left);
            chatMenuDotsContainer.setVisibility(View.VISIBLE);
            chatMenuDotsImageView.setImageDrawable(chatRightArrowDrawable);
            setupEditChatButton();
        } else {
            chatMenuDotsContainer.setVisibility(View.GONE);
            animate(chatMenuDotsContainer, View.GONE, R.anim.slide_in_right);
            chatMenuDotsImageView.setImageDrawable(chatDotsDrawable);
            if (chatButtonsMenu.getVisibility() == View.GONE) {
                chatButtonsMenu.setVisibility(View.VISIBLE);
                chatButtonsContainer.setVisibility(View.GONE);
            }
        }
    }

    ImsMessage messageForEdit;

    @Subscribe
    public void setMessageForEdit(MessageSelectedEvent event){
        this.messageForEdit = event.getSelectedMessage();
        animateChatMenu();
//       HANDLE THIS BUTTONS
//       R.id.chat_menu_edit
//       R.id.chat_menu_delete

//       @BindView(R.id.chat_menu_delete)
//       TextView chatMenuDeleteButton;

//       @BindView(R.id.chat_menu_edit)
//       TextView chatMenuEditButton;
    }

    Animation activeAnimation;

    private void animate(View view, int visibility, int anim) {
        activeAnimation = AnimationUtils.loadAnimation(getActivity(), anim);
        view.startAnimation(activeAnimation);
        view.setVisibility(visibility);
    }

    @Optional
    @OnClick(R.id.close_image_button)
    public void imageCloseButtonOnClick() {
        if (fullScreenContainer != null) {
            fullScreenContainer.setVisibility(View.GONE);
        }
    }
    @Optional
    @OnClick(R.id.download_image_button)
    public void imageDownloadButtonOnClick() {
        ChatFragmentPermissionsDispatcher.invokeDownloadButtonWithPermissionCheck(this);
    }

    @NeedsPermission( {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void invokeDownloadButton() {
        DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(urlInFullscreen);
        String fileName = FilenameUtils.getName(uri.getPath());
        DownloadManager.Request downloadRequest = new DownloadManager.Request(uri);
        downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadRequest.setVisibleInDownloadsUi(true);
        downloadRequest.setTitle("SSK - Downloading a chat image");
        downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName);
        if (downloadManager != null) {
            downloadManager.enqueue(downloadRequest);
        }
    }

    @OnClick(R.id.video_close_image_button)
    public void videoCloseButtonOnClick() {
        videoViewContainer.setVisibility(View.GONE);
        if (videoView != null) {
            videoView.stopPlayback();
            videoView.closePlayer();
            if (mediaController != null) {
                mediaController.reset();
            }
        }
    }

    @Optional
    @OnClick(R.id.sticker_button)
    public void stickerButtonOnClick() {
        //TODO open sticker
    }

    @Optional
    @OnClick(R.id.vide_download_image_button)
    public void videoDownloadButtonOnClick() {
        Toast.makeText(getContext(), getContext().getResources().getString(R.string.chat_video_downloaded), Toast.LENGTH_SHORT).show();
    }

    public void sendButtonOnClick() {
        if(!Model.getInstance().isRealUser()){
            if (inactiveContainer != null) {
                inactiveContainer.setVisibility(View.VISIBLE);
            }
            return;
        }
        if(Model.getInstance().isRealUser() && currentlyActiveChat != null) {
            String textMessage = inputEditText.getText().toString().trim();
            sendTextMessage(textMessage);
        } else {
            if (inactiveContainer != null) {
                inactiveContainer.setVisibility(View.VISIBLE);
            }
        }
        inputEditText.setText("");
        Utility.hideKeyboard(getActivity());
    }

    private void sendTextMessage(String text){
        if(messageForEdit!=null){
            messageForEdit.setText(text);
            currentlyActiveChat.updateMessage(messageForEdit,null);
            messageForEdit = null;
        } else {
            ImsMessage message = ImsMessage.getDefaultMessage();
            message.setType(GSConstants.UPLOAD_TYPE_TEXT);
            message.setText(text);
            currentlyActiveChat.sendMessage(message);
            currentlyActiveChat.setUserIsTyping(false);
        }
    }


    @OnClick(R.id.chat_menu_create)
    public void chatMenuCreateOnClick() {
        if(Model.getInstance().isRealUser()){
            EventBus.getDefault().post(new FragmentEvent(CreateChatFragment.class));
        } else {
            if (inactiveContainer != null) {
                inactiveContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnClick(R.id.chat_menu_delete)
    public void chatMenuDeleteOnClick() {
        if(messageForEdit !=null){
            if(currentlyActiveChat!=null){
                currentlyActiveChat.deleteMessage(messageForEdit,null);
                animateChatMenu();
            }
        }
    }

    @OnClick(R.id.chat_menu_alerts)
    public void chatMenuAlertsOnClick() {
        currentlyActiveChat.setMuteChat(!currentlyActiveChat.getIsMuted());
        if(currentlyActiveChat.getIsMuted()){
            chatMenuAlertsButton.setText(R.string.alerts_off);
        } else {
            chatMenuAlertsButton.setText(R.string.alerts_on);
        }
    }


    @OnClick(R.id.chat_menu_edit)
    public void chatMenuEditOnClick() {
        UserInfo user = Model.getInstance().getUserInfo();
        if (currentlyActiveChat != null && user != null) {
            if(messageForEdit !=null){
                Toast.makeText(getContext(),"Edit mode for chat message activated",Toast.LENGTH_SHORT).show();
                animateChatMenu();
                inputEditText.setText(messageForEdit.getText());
            } else {
                if (Model.getInstance().getUserInfo().getUserId().equals(currentlyActiveChat.getOwner())) {
                    FragmentEvent fe = new FragmentEvent(EditChatFragment.class);
                    fe.setId(currentlyActiveChat.getChatId());
                    EventBus.getDefault().post(fe);
                } else {
                    AlertDialogManager.getInstance().showAlertDialog(getContext().getResources().getString(R.string.are_you_sure), getContext().getResources().getString(R.string.chat_leave_chat),
                            new View.OnClickListener() {// Cancel listener
                                @Override
                                public void onClick(View v) {
                                    getActivity().onBackPressed();
                                }
                            }, new View.OnClickListener() {// Confirm listener
                                @Override
                                public void onClick(View v) {
                                    getActivity().onBackPressed();
                                    currentlyActiveChat.deleteChat();
                                    if (chatMenuDotsContainer.getVisibility() == View.VISIBLE) {
                                        chatMenuDotsContainerOnClick();
                                    }
                                }
                            });

                    chatMenuEditButton.setText(getContext().getResources().getString(R.string.chat_Leave));
                }
            }
        }
    }

    @OnClick(R.id.chat_menu_search)
    public void chatMenuSearchOnClick() {
        EventBus.getDefault().post(new FragmentEvent(JoinChatFragment.class));
    }

    @OnClick(R.id.image_button)
    public void selectImageOnClick() {
        if (currentlyActiveChat != null) {
            ChatFragmentPermissionsDispatcher.invokeImageSelectionWithPermissionCheck(this);
        }
    }

    @OnClick(R.id.camera_button)
    public void cameraButtonOnClick() {
        if (currentlyActiveChat != null) {
            ChatFragmentPermissionsDispatcher.invokeCameraCaptureWithPermissionCheck(this);
        }
    }

    private void sendAudioMessage(final String path){
        final ImsMessage messageAudio = ImsMessage.getDefaultMessage();
        messageAudio.setType(GSConstants.UPLOAD_TYPE_AUDIO);
        messageAudio.setUploadStatus(GSConstants.UPLOADING);
        messageAudio.setImageUrl("");
        messageAudio.setLocalPath(path);
        messageAudio.setImageAspectRatio(1);
        currentlyActiveChat.sendMessage(messageAudio).addOnCompleteListener(new OnCompleteListener<ChatInfo>() {
            @Override
            public void onComplete(@NonNull Task<ChatInfo> task) {
                final TaskCompletionSource<String> source = new TaskCompletionSource<>();
                source.getTask().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(task.isSuccessful()){
                            messageAudio.setLocalPath(null);
                            messageAudio.setImageUrl(task.getResult());
                            messageAudio.setUploadStatus(GSConstants.UPLOADED);
                            currentlyActiveChat.updateMessage(messageAudio,null);
                            // TODO @Filip Hide waiting dialog ?
                        } else {
                            messageAudio.setImageUrl("");
                            messageAudio.setUploadStatus(GSConstants.FAILED);
                            currentlyActiveChat.updateMessage(messageAudio,null);
                            // TODO @Filip Hide waiting dialog ? - show error?
                        }
                    }
                });
                Model.getInstance().uploadAudioRecordingForChat(path, getActivity().getFilesDir(), source);
            }
        });
    }

    private void sendImageMessage(final String path){
        final ImsMessage preppingImsObject = ImsMessage.getDefaultMessage();
        preppingImsObject.setType(GSConstants.UPLOAD_TYPE_IMAGE);
        preppingImsObject.setUploadStatus(GSConstants.UPLOADING);
        preppingImsObject.setText(null);
        preppingImsObject.setImageUrl(null);
        preppingImsObject.setLocalPath(path);

        currentlyActiveChat.sendMessage(preppingImsObject).addOnCompleteListener(new OnCompleteListener<ChatInfo>() {
            @Override
            public void onComplete(@NonNull Task<ChatInfo> task) {
                if(task.isSuccessful()){
                    final TaskCompletionSource<String> source = new TaskCompletionSource<>();
                    source.getTask().addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if(task.isSuccessful()){
                                preppingImsObject.setImageUrl(task.getResult());
                                preppingImsObject.setUploadStatus(GSConstants.UPLOADED);
                                preppingImsObject.setLocalPath(null);
                                currentlyActiveChat.updateMessage(preppingImsObject,null);
                                // TODO @Filip Update UI with image
                            } else {
                                preppingImsObject.setImageUrl("");
                                preppingImsObject.setUploadStatus(GSConstants.FAILED);
                                currentlyActiveChat.updateMessage(preppingImsObject,null);
                                // TODO @Filip Hide waiting dialog ? - show error?
                            }
                        }
                    });
                    Model.getInstance().uploadImageForChatMessage(path, getActivity().getFilesDir(),source);
                }
            }
        });
    }

    private void sendVideoMessage(final String path){

        final ImsMessage preppingImsObject = ImsMessage.getDefaultMessage();
        preppingImsObject.setType(GSConstants.UPLOAD_TYPE_VIDEO);
        preppingImsObject.setUploadStatus(GSConstants.UPLOADING);
        preppingImsObject.setText(null);
        preppingImsObject.setImageUrl(null);
        preppingImsObject.setLocalPath(path);


        currentlyActiveChat.sendMessage(preppingImsObject).addOnCompleteListener(new OnCompleteListener<ChatInfo>() {
            @Override
            public void onComplete(@NonNull Task<ChatInfo> task) {
                if(task.isSuccessful()){
                    final TaskCompletionSource<String> source = new TaskCompletionSource<>();
                    source.getTask().addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if(task.isSuccessful()){
                                preppingImsObject.setImageUrl(task.getResult());
                                final TaskCompletionSource<String> source = new TaskCompletionSource<>();
                                source.getTask().addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        if (task.isSuccessful()) {
                                            preppingImsObject.setVidUrl(task.getResult());
                                            preppingImsObject.setUploadStatus(GSConstants.UPLOADED);
                                            preppingImsObject.setLocalPath(null);
                                            currentlyActiveChat.updateMessage(preppingImsObject,null);
                                        } else {
                                            preppingImsObject.setVidUrl(null);
                                            preppingImsObject.setUploadStatus(GSConstants.FAILED);
                                            currentlyActiveChat.updateMessage(preppingImsObject,null);
                                        }
                                    }
                                });
                                Model.getInstance().uploadChatVideoRecording(currentPath,getActivity().getFilesDir(), source);
                            } else {
                                preppingImsObject.setImageUrl(null);
                                preppingImsObject.setVidUrl(null);
                                preppingImsObject.setUploadStatus(GSConstants.FAILED);
                                currentlyActiveChat.updateMessage(preppingImsObject,null);
                            }
                        }
                    });
                    Model.getInstance().uploadChatVideoRecordingThumbnail(path,getActivity().getFilesDir(),source);
                }
            }
        });
    }

    /**
     * * ** ** ** ** ** ** ** ** ** ** ** **
     * Event listeners
     * * ** ** ** ** ** ** ** ** ** ** ** **
     **/

    @Subscribe
    public void onEvent(ChatsInfoUpdatesEvent event) {
        findActiveChat();
        //updateAllViews(); // TOTALLY NOT NEEDED!
        checkPushNotification();
    }

    @Subscribe
    public void onEvent(CreateNewChatSuccessEvent event) {
        ChatInfo chatInfo = event.getChatInfo();
        setCurrentChatNotification(chatInfo);
    }

    @Subscribe
    public void onEvent(ChatNotificationsEvent event) {
        Log.d(TAG, "Received ChatNotificationsEvent: " + event.getKey().name());
        ChatInfo chatInfo = event.getChatInfo();
        switch (event.getKey()) {
            case UPDATED_CHAT_USERS:
                handleUpdatedChatUsers(chatInfo);
                break;
            case DELETED_CHAT_MESSAGE:
                messageAdapter.notifyDataSetChanged();
                break;
            case CHANGED_CHAT_MESSAGE:
                if(currentlyActiveChat!=null){
                   List<ImsMessage> messages = currentlyActiveChat.getMessages();
                    ImsMessage messageToUpdate = event.getMessage();
                    if(messages!=null && messageToUpdate!=null){
                        if(messages.contains(messageToUpdate)){
                            messageAdapter.notifyItemChanged(messages.indexOf(messageToUpdate));
                        } else {
                            Log.e(TAG,"Active chat does not contain this message!");
                        }
                    } else {
                        Log.e(TAG,"Message to be updated is not in active chat!");
                    }
                }
                break;
            case UPDATED_CHAT_MESSAGES:
                String chatId = null;
                chatInfo = event.getChatInfo();
                if (chatInfo != null) {
                    chatId = chatInfo.getChatId();
                }
                handleUpdatedChatMessages(chatId);
                break;
            case SET_CURRENT_CHAT:
                chatInfo = event.getChatInfo();
                setCurrentChatNotification(chatInfo);
                break;
        }
    }

    private void handleUpdatedChatUsers(ChatInfo chatInfo) {
        if (currentlyActiveChat != null) {
            setCurrentlyActiveChat(ImsManager.getInstance().getChatInfoById(currentlyActiveChat.getChatId()));
        }
        updateAllViews();
        if (currentlyActiveChat != null) {
            if (currentlyActiveChat.getChatId().equals(chatInfo.getChatId())) {
                Log.d(TAG, "Single chat should be updated!");
            }
        }
    }

    private void handleUpdatedChatMessages(String chatId) {
        updateTopChatsView();
        if (currentlyActiveChat != null) {// If current chat was updated
            if (currentlyActiveChat.getChatId().equals(chatId)) {
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                messageAdapter.notifyDataSetChanged();
                messageListView.smoothScrollToPosition(messageAdapter.getItemCount()); // Scroll to bottom!
            }
        }
    }

    private void setCurrentChatNotification(ChatInfo chatInfo) {
        messageForEdit = null;
        if (currentlyActiveChat != null) {
            if (currentlyActiveChat.getChatId().equals(chatInfo.getChatId())) { // Its the same chat - hide or show edit buttons
                chatMenuDotsContainerOnClick();
            } else { // its not the same chat, so hide edit buttons if those are visible
                if (chatMenuDotsContainer.getVisibility() == View.VISIBLE) {
                    chatMenuDotsContainerOnClick();
                }
                setCurrentlyActiveChat(chatInfo);
            }
        } else {
            setCurrentlyActiveChat(chatInfo);
        }
        messageListView.smoothScrollToPosition(messageAdapter.getItemCount()); // Scroll to bottom!
    }

    private void checkPushNotification() {
        Log.d(TAG, "Missing implementation for checkPushNotification method!");
    }

    private void findActiveChat() {
        String chatId;
        if (currentlyActiveChat != null) {
            chatId = this.currentlyActiveChat.getChatId();
            ChatInfo chat = ImsManager.getInstance().getChatInfoById(chatId);
            if (chat != null) {
                setCurrentlyActiveChat(chat);
                return;
            }
        }
        //! Set first one if chat was not selected
        setFirstChatAsActive();
    }

    private void setFirstChatAsActive() {
        if (ImsManager.getInstance().getUserChatsList() != null) {
            List<ChatInfo> chats = ImsManager.getInstance().getUserChatsList();
            if (chats != null && chats.size() > 0) {
                setCurrentlyActiveChat(chats.get(0));
                return;
            }
        }
        setCurrentlyActiveChat(null);
    }

    @Subscribe
    public void playVideo(PlayVideoEvent event) {
        if (videoView != null) {
            videoView.setMediaController(mediaController);
        }
        if (mediaController != null) {
            mediaController.setOnLoadingView(R.layout.loading);
            mediaController.setOnErrorView(R.layout.error);
            mediaController.reset();
        }
        videoViewContainer.setVisibility(View.VISIBLE);
        videoView.setVideoURI(Uri.parse(event.getId()));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });
        mediaController.setOnErrorViewClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoCloseButtonOnClick();
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.seekTo(0);
                videoView.pause();
                mediaController.reset();
            }
        });
    }

    private String urlInFullscreen;

    @Subscribe
    public void showImageFullScreen(FullScreenImageEvent event) {
        if (fullScreenContainer != null) {
            fullScreenContainer.setVisibility(View.VISIBLE);
        }
        urlInFullscreen = event.getId();
        if (imageViewFullScreen != null) {
            ImageLoader.displayImage(urlInFullscreen, imageViewFullScreen);
        }
    }

    /**
     * * ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
     * CAMERA, MIC, IMAGES...
     * * ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
     **/


    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void invokeImageSelection() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_CODE_CHAT_IMAGE_PICK);//one can be replaced with any action code
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void invokeCameraCapture() {
        AlertDialog.Builder chooseDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialog);
        chooseDialog.setTitle(getContext().getResources().getString(R.string.choose));
        chooseDialog.setMessage(getContext().getResources().getString(R.string.chat_image_or_video));
        chooseDialog.setNegativeButton(getContext().getResources().getString(R.string.record_video), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_CODE_CHAT_VIDEO_CAPTURE);
                }
            }
        });
        chooseDialog.setPositiveButton(getContext().getResources().getString(R.string.chat_image), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = Model.createImageFile(getContext());
                        currentPath = photoFile.getAbsolutePath();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    if (photoFile != null) {
                        if(Utility.isKitKat()){
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        }
                        if(Utility.isLollipopAndUp()){
                            Uri photoURI = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        }
                    }
                    startActivityForResult(takePictureIntent, REQUEST_CODE_CHAT_IMAGE_CAPTURE);
                }
            }
        });
        chooseDialog.show();
    }
    Handler audioRecordHandler;
    @NeedsPermission({Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void startRecording() {
        Toast.makeText(getContext(), getContext().getResources().getString(R.string.chat_hold), Toast.LENGTH_SHORT).show();
        audioRecordHandler  = new Handler();
        audioRecordHandler.postDelayed(audioRecordHandlerTask, 250);
    }




    TimerTask audioRecordHandlerTask = new TimerTask() {
        @Override
        public void run() {
            if(!micButton.isPressed()){
                return;
            }
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            audioFilepath = Model.getAudioFileName();
            recorder.setOutputFile(audioFilepath);
            try {
                recorder.prepare();
                recorder.start();
            } catch (Exception e) {
                Log.e(TAG, "Start of recording failed!");
            }
        }
    };

    private void stopRecording() {
        if (recorder != null) {
            try {
                audioRecordHandler.removeCallbacks(audioRecordHandlerTask);
                recorder.stop();
                recorder.release();
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.chat_recording_stop), Toast.LENGTH_SHORT).show();
                sendAudioMessage(audioFilepath);
                recorder = null;
            } catch (Exception e) {
                Log.e(TAG, "Stop recording failed!");
            }
        }
    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showRationaleForCamera(final PermissionRequest request) {
        new AlertDialog.Builder(getContext(), R.style.AlertDialog)
                .setMessage(R.string.permission_camera_rationale)
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnShowRationale({Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showRationaleForMicrophone(final PermissionRequest request) {
        new AlertDialog.Builder(getContext(), R.style.AlertDialog)
                .setMessage(R.string.permission_microphone_rationale)
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showDeniedForMicrophone() {
        Toast.makeText(getContext(), R.string.permission_microphone_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showNeverAskForMicrophone() {
        Toast.makeText(getContext(), R.string.permission_microphone_never_ask, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ChatFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    /**
     * Since onStart is called after onActivity result, we have to store the code and delay
     * uploading and sending of the message in order to make sure fragment is subscribed to EvenBus
     */
    @Override
    public void onStart(){
        super.onStart();
        switch (resultFromPicker){
            case REQUEST_CODE_CHAT_IMAGE_CAPTURE:
            case REQUEST_CODE_CHAT_IMAGE_PICK:
                sendImageMessage(currentPath);
                break;
            case REQUEST_CODE_CHAT_VIDEO_CAPTURE:
                sendVideoMessage(currentPath);
                break;
        }
        resultFromPicker = -1; // Reset this value to prevent resending after onStart is triggered
    }

    private int resultFromPicker;

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        resultFromPicker = -1;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CHAT_IMAGE_PICK:
                case REQUEST_CODE_CHAT_VIDEO_CAPTURE:
                    Uri uri = intent.getData();
                    currentPath = Model.getRealPathFromURI(getContext(), uri);
                case REQUEST_CODE_CHAT_IMAGE_CAPTURE:
                    resultFromPicker = requestCode;
                    break;
            }
        }
    }


    @Subscribe
    public void handleUserIsTypingEvent(UserIsTypingEvent event){
        if(currentlyActiveChat!=null){
            if(event.getChatId().equals(currentlyActiveChat.getChatId())){
                List<UserInfo> users = event.getUsers();
                if(users.size()==1){
                    String userName = users.get(0).getNicName();
                    infoLineTextView.setText(getString(R.string.single_user_is_typing,userName));
                } else if (users.size()>1) {
                    infoLineTextView.setText(R.string.multiple_users_are_typing);
                } else {
                    updateChatTitleText();
                }
            }
        }
    }

    @Subscribe
    public void openChatEvent(OpenChatEvent event){
        chatHeadsAdapter.notifyDataSetChanged();
    }

    View.OnClickListener disabledClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {}
    };

    @Override
    public void onStop() {
        super.onStop();
        translationView.setVisibility(View.GONE);
    }
}
