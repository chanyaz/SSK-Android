package tv.sportssidekick.sportssidekick.fragment.instance;


import android.Manifest;
import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.VideoView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nshmura.snappysmoothscroller.SnappyLinearLayoutManager;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.ChatHeadsAdapter;
import tv.sportssidekick.sportssidekick.adapter.MessageAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.fragment.IgnoreBackHandling;
import tv.sportssidekick.sportssidekick.fragment.popup.CreateChatFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.EditChatFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.JoinChatFragment;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.im.ChatInfo;
import tv.sportssidekick.sportssidekick.model.im.ImsManager;
import tv.sportssidekick.sportssidekick.model.im.ImsMessage;
import tv.sportssidekick.sportssidekick.model.im.event.ChatNotificationsEvent;
import tv.sportssidekick.sportssidekick.model.im.event.ChatsInfoUpdatesEvent;
import tv.sportssidekick.sportssidekick.model.im.event.CreateNewChatSuccessEvent;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.service.FullScreenImageEvent;
import tv.sportssidekick.sportssidekick.service.GameSparksEvent;
import tv.sportssidekick.sportssidekick.service.PlayVideoEvent;
import tv.sportssidekick.sportssidekick.util.Utility;

import static tv.sportssidekick.sportssidekick.Constant.REQUEST_CODE_CHAT_IMAGE_CAPTURE;
import static tv.sportssidekick.sportssidekick.Constant.REQUEST_CODE_CHAT_IMAGE_PICK;
import static tv.sportssidekick.sportssidekick.Constant.REQUEST_CODE_CHAT_VIDEO_CAPTURE;

/**
 * A simple {@link BaseFragment} subclass.
 */

@RuntimePermissions
@IgnoreBackHandling
public class ChatFragment extends BaseFragment {

    private static final String TAG = "CHAT Fragment";
    @BindView(R.id.message_container)
    RecyclerView messageListView;
    @BindView(R.id.chat_heads_view)
    RecyclerView chatHeadsView;
    @BindView(R.id.progress_bar)
    AVLoadingIndicatorView progressBar;

    @BindView(R.id.info_message)
    TextView infoMessage;
    @BindView(R.id.down_arrow)
    ImageView downArrow;
    @BindView(R.id.chat_info_line)
    View chatInfoLine;
    @BindView(R.id.chat_info_line_text)
    TextView infoLineTextView;

    @BindView(R.id.mic_button)
    ImageButton micButton;
    @BindView(R.id.video_view_container)
    RelativeLayout videoViewContainer;
    @BindView(R.id.video_view)
    VideoView videoView;
    @BindView(R.id.image_fullscreen)
    ImageView imageViewFullScreen;
    @BindView(R.id.full_screen_container)

    RelativeLayout fullScreenContainer;
    @BindView(R.id.input_edit_text)
    EditText inputEditText;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.chat_buttons_container)
    RelativeLayout chatButtonsContainer;

    @BindView(R.id.menu_button)
    ImageButton chatButtonsMenu;
    @BindView(R.id.chat_menu_dots_container)
    LinearLayout chatMenuDotsContainer;
    @BindView(R.id.chat_menu_dots)
    ImageView chatMenuDotsImageView;

    @BindView(R.id.chat_menu_edit)
    TextView chatMenuEditButton;

    Drawable chatRightArrowDrawable;
    Drawable chatDotsDrawable;

    ChatHeadsAdapter chatHeadsAdapter;
    MessageAdapter messageAdapter;
    ChatInfo currentlyActiveChat;

    private MediaRecorder recorder = null;
    private String audioFilepath = null;
    String currentPath;
    String videoDownloadUrl;



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
        chatHeadsAdapter = new ChatHeadsAdapter(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        chatHeadsView.setAdapter(chatHeadsAdapter);

        progressBar.setVisibility(View.VISIBLE);
        infoMessage.setVisibility(View.GONE);

        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);

        messageAdapter = new MessageAdapter(getContext());
        messageListView.setAdapter(messageAdapter);

        SnappyLinearLayoutManager snappyLinearLayoutManager = new SnappyLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        snappyLinearLayoutManager.setSnapInterpolator(new AccelerateDecelerateInterpolator());
        snappyLinearLayoutManager.setSnapDuration(1000);
        snappyLinearLayoutManager.setSeekDuration(1000);
        messageListView.setLayoutManager(snappyLinearLayoutManager);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (currentlyActiveChat != null) {
                    currentlyActiveChat.loadPreviousMessagesPage();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        inputEditText.setImeActionLabel("SEND",EditorInfo.IME_ACTION_SEND);
        inputEditText.setImeOptions(EditorInfo.IME_ACTION_SEND);

        /* input Listeners */
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (currentlyActiveChat != null) {
                    currentlyActiveChat.setUserIsTyping(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
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
                if(currentlyActiveChat!=null) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            ChatFragmentPermissionsDispatcher.startRecordingWithCheck(ChatFragment.this);
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
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //! Update active chat first
        findActiveChat();
        //! Update UI
        updateAllViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }

    private void updateTopChatsView(){
        // Setup chat heads
        chatHeadsAdapter.setValues(ImsManager.getInstance().getUserChatsList());
        chatHeadsAdapter.notifyDataSetChanged();
        if(currentlyActiveChat!=null){
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

    private void updateChatTitleText(){
        if (currentlyActiveChat != null) {
            // Setup Chat label
            StringBuilder chatLabel = new StringBuilder(currentlyActiveChat.getChatTitle());
            chatLabel.append(": ");
            int size = currentlyActiveChat.getUsersIds().size();
            int count = 0;
            for (String userId : currentlyActiveChat.getUsersIds()) {
                count++;
                String chatName = Model.getInstance().getCachedUserInfoById(userId).getNicName();
                if (!TextUtils.isEmpty(chatName)) {
                    chatLabel.append(chatName);
                    if (count < size) {
                        chatLabel.append(", ");
                    }
                }
            }
            infoLineTextView.setText(chatLabel.toString());
        }
    }

    private void setupEditChatButton(){
        if(currentlyActiveChat !=null){
            UserInfo user = Model.getInstance().getUserInfo();
            if(user!=null){
                if(user.getUserId().equals(currentlyActiveChat.getOwner())){
                    chatMenuEditButton.setText("Edit"); // TODO Extract strings...
                } else {
                    chatMenuEditButton.setText("Leave");
                }
            } else {
                chatMenuEditButton.setText("Leave");
            }
        }
    }

    /** ** ** ** ** ** ** ** ** ** ** ** ** **
                Click listeners
     ** ** ** ** ** ** ** ** ** ** ** ** ** **/

    @OnClick(R.id.menu_button)
    public void chatButtonsMenuOnClick(View v) {
        if(currentlyActiveChat!=null){
            chatButtonsMenu.setVisibility(View.GONE);
            chatButtonsContainer.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.chat_menu_dots)
    public void chatMenuDotsContainerOnClick() {
        if(currentlyActiveChat!=null) {
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
    }

    Animation animation;

    private void animate(View view, int visibility, int anim) {
        animation = AnimationUtils.loadAnimation(getActivity(), anim);
        view.startAnimation(animation);
        view.setVisibility(visibility);
    }

    @OnClick(R.id.close_image_button)
    public void imageCloseButtonOnClick() {
        fullScreenContainer.setVisibility(View.GONE);
    }

    @OnClick(R.id.download_image_button)
    public void imageDownloadButtonOnClick() {
        Toast.makeText(getContext(), "Image is downloaded.", Toast.LENGTH_SHORT).show();
    }

    public void sendButtonOnClick() {
        if(currentlyActiveChat!=null){
            ImsMessage message = ImsMessage.getDefaultMessage();
            message.setText(inputEditText.getText().toString().trim());
            currentlyActiveChat.sendMessage(message);
            currentlyActiveChat.setUserIsTyping(false);
        }
        inputEditText.setText("");
        Utility.hideKeyboard(getActivity());
    }


    @OnClick(R.id.chat_menu_create)
    public void chatMenuCreateOnClick() {
        EventBus.getDefault().post(new FragmentEvent(CreateChatFragment.class));
    }

    @OnClick(R.id.chat_menu_edit)
    public void chatMenuEditOnClick() {
        UserInfo user = Model.getInstance().getUserInfo();
        if(currentlyActiveChat!=null && user!=null){
            if(Model.getInstance().getUserInfo().getUserId().equals(currentlyActiveChat.getOwner())){
                FragmentEvent fe = new FragmentEvent(EditChatFragment.class);
                fe.setId(currentlyActiveChat.getChatId());
                EventBus.getDefault().post(fe);
            } else {
                currentlyActiveChat.deleteChat(); // TODO - Display confirmation dialog!
                chatMenuEditButton.setText("Leave");
            }
        }
    }

    @OnClick(R.id.chat_menu_search)
    public void chatMenuSearchOnClick() {
        EventBus.getDefault().post(new FragmentEvent(JoinChatFragment.class));
    }

    @OnClick(R.id.image_button)
    public void selectImageOnClick(){
        if(currentlyActiveChat!=null) {
            ChatFragmentPermissionsDispatcher.invokeImageSelectionWithCheck(this);
        }
    }

    @OnClick(R.id.camera_button)
    public void cameraButtonOnClick(){
        if(currentlyActiveChat!=null) {
            ChatFragmentPermissionsDispatcher.invokeCameraCaptureWithCheck(this);
        }
    }

    /** ** ** ** ** ** ** ** ** ** ** ** ** **
                 Event listeners
     ** ** ** ** ** ** ** ** ** ** ** ** ** **/
    @Subscribe
    @SuppressWarnings("Unchecked cast")
    public void onChatEventDetected(GameSparksEvent event) {
        Log.d(TAG, "event received in Chat Fragment: " + event.getEventType());
        switch (event.getEventType()) {
            case MESSAGE_IMAGE_FILE_UPLOADED:
            case AUDIO_FILE_UPLOADED:
                String downloadUrl = (String) event.getData();
                ImsMessage message = ImsMessage.getDefaultMessage();
                message.setImageUrl(downloadUrl);
                currentlyActiveChat.sendMessage(message);
                break;
            case VIDEO_FILE_UPLOADED:
                videoDownloadUrl = (String) event.getData();
                Model.getInstance().uploadVideoRecordingThumbnail(currentPath, getActivity().getFilesDir());
                break;
            case VIDEO_IMAGE_FILE_UPLOADED:
                String videoThumbnailDownloadUrl = (String) event.getData();
                ImsMessage messageVideo = ImsMessage.getDefaultMessage();
                messageVideo.setVidUrl(videoDownloadUrl);
                messageVideo.setImageUrl(videoThumbnailDownloadUrl);
                currentlyActiveChat.sendMessage(messageVideo);
                break;
        }
    }

    @Subscribe
    public void onEvent(ChatsInfoUpdatesEvent event){
        findActiveChat();
        //updateAllViews(); // TOTALY NOT NEEDED!
        checkPushNotification();
    }

    @Subscribe
    public void onEvent(CreateNewChatSuccessEvent event){
        ChatInfo chatInfo = event.getChatInfo();
        setCurrentChatNotification(chatInfo);
    }

    @Subscribe
    public void onEvent(ChatNotificationsEvent event){
        Log.d(TAG,"Received ChatNotificationsEvent: " + event.getKey().name());
        ChatInfo chatInfo = event.getChatInfo();
        switch (event.getKey()){
            case UPDATED_CHAT_USERS:
                handleUpdatedChatUsers(chatInfo);
                break;
            case CHANGED_CHAT_MESSAGE:
                break;
            case UPDATED_CHAT_MESSAGES:
                String chatId = null;
                chatInfo = event.getChatInfo();
                if(chatInfo!=null){
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

    private void handleUpdatedChatUsers(ChatInfo chatInfo){
        if(currentlyActiveChat !=null){
            setCurrentlyActiveChat(ImsManager.getInstance().getChatInfoById(currentlyActiveChat.getChatId()));
        }
        updateAllViews();
        if(currentlyActiveChat!=null){
            if(currentlyActiveChat.getChatId().equals(chatInfo.getChatId())){
                // TODO - Update chat data?
            }
        }
    }

    private void handleUpdatedChatMessages(String chatId){
        updateTopChatsView();
        if(currentlyActiveChat!=null) {// If current chat was updated
            if(currentlyActiveChat.getChatId().equals(chatId)){
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                messageAdapter.notifyDataSetChanged();
                messageListView.smoothScrollToPosition(0); // Scroll to top
            }
        }
    }

    private void setCurrentChatNotification(ChatInfo chatInfo){
        if(currentlyActiveChat !=null){
            if(currentlyActiveChat.getChatId().equals(chatInfo.getChatId())) { // Its the same chat - hide edit buttons
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

    private void checkPushNotification() { /* TODO */ }

    private void findActiveChat() {
        String chatId;
        if(currentlyActiveChat!=null){
            chatId = this.currentlyActiveChat.getChatId();
            ChatInfo chat = ImsManager.getInstance().getChatInfoById(chatId);
            if(chat!=null){
                setCurrentlyActiveChat(chat);
                return;
            }
        }
        //! Set first one if chat was not selected
        setFirstChatAsActive();
    }

    private void setFirstChatAsActive(){
        if(ImsManager.getInstance().getUserChatsList()!=null){
            List<ChatInfo> chats = ImsManager.getInstance().getUserChatsList();
            if(chats!=null && chats.size()>0){
                setCurrentlyActiveChat(chats.get(0));
                return;
            }
        }
        setCurrentlyActiveChat(null);
    }

    @Subscribe
    public void playVideo(PlayVideoEvent event) {
        videoViewContainer.setVisibility(View.VISIBLE);
        videoView.setVideoURI(Uri.parse(event.getId()));
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoViewContainer.setVisibility(View.GONE);
            }
        });
    }

    @Subscribe
    public void showImageFullScreen(FullScreenImageEvent event) {
        fullScreenContainer.setVisibility(View.VISIBLE);
        String uri = event.getId();
        ImageLoader.getInstance().displayImage(uri, imageViewFullScreen);
    }

    /** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
                    CAMERA, MIC, IMAGES...
     ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **/


    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void invokeImageSelection(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_CODE_CHAT_IMAGE_PICK);//one can be replaced with any action code
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void invokeCameraCapture(){
        AlertDialog.Builder chooseDialog = new AlertDialog.Builder(getActivity());
        chooseDialog.setTitle("Choose");
        chooseDialog.setMessage("Take photo or record video ?");
        chooseDialog.setNegativeButton("Video", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_CODE_CHAT_VIDEO_CAPTURE);
                };
            }
        });
        chooseDialog.setPositiveButton("Image", new DialogInterface.OnClickListener() {
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
                        Uri photoURI = FileProvider.getUriForFile(getActivity(), "tv.sportssidekick.sportssidekick.fileprovider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    }
                    startActivityForResult(takePictureIntent, REQUEST_CODE_CHAT_IMAGE_CAPTURE);
                }
            }
        });
        chooseDialog.show();
    }

    @NeedsPermission({Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void startRecording() {
        Toast.makeText(getContext(), "Hold down to record!", Toast.LENGTH_SHORT).show();
        final Handler handler = new Handler();
        handler.postDelayed(new TimerTask() {
                                @Override
                                public void run() {
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
                            }
                , 250);
    }

    private void stopRecording() {
        if (recorder != null) {
            try {
                recorder.stop();
                recorder.release();
                Toast.makeText(getContext(), "Recording stopped.", Toast.LENGTH_SHORT).show();
                Model.getInstance().uploadAudioRecording(audioFilepath);
                recorder = null;
            } catch (Exception e) {
                Log.e(TAG, "Stop recording failed!");
            }
        }
    }

    @OnShowRationale({Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showRationaleForMicrophone(final PermissionRequest request) {
        new AlertDialog.Builder(getContext())
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

    @OnNeverAskAgain({Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showNeverAskForMicrophone() {
        Toast.makeText(getContext(), R.string.permission_microphone_neverask, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ChatFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CHAT_IMAGE_CAPTURE:
                    Model.getInstance().uploadImageForMessage(currentPath);
                    break;
                case REQUEST_CODE_CHAT_IMAGE_PICK:
                    Uri selectedImageURI = intent.getData();
                    String realPath = Model.getRealPathFromURI(getContext(), selectedImageURI);
                    Model.getInstance().uploadImageForMessage(realPath);
                    break;
                case REQUEST_CODE_CHAT_VIDEO_CAPTURE:
                    Uri videoUri = intent.getData();
                    currentPath = Model.getRealPathFromURI(getContext(), videoUri);
                    Model.getInstance().uploadVideoRecording(currentPath);
                    break;
            }
        }
    }
}
