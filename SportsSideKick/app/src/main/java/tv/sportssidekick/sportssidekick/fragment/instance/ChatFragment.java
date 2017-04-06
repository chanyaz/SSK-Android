package tv.sportssidekick.sportssidekick.fragment.instance;


import android.Manifest;
import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
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
import tv.sportssidekick.sportssidekick.fragment.IgnoreBackHandling;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.model.im.ChatInfo;
import tv.sportssidekick.sportssidekick.model.im.ImsManager;
import tv.sportssidekick.sportssidekick.model.im.ImsMessage;
import tv.sportssidekick.sportssidekick.service.GameSparksEvent;
import tv.sportssidekick.sportssidekick.service.FullScreenImageEvent;
import tv.sportssidekick.sportssidekick.service.PlayVideoEvent;
import tv.sportssidekick.sportssidekick.service.UIEvent;
import tv.sportssidekick.sportssidekick.util.OnSwipeTouchListener;

/**
 * A simple {@link BaseFragment} subclass.
 */

@RuntimePermissions
@IgnoreBackHandling
public class ChatFragment extends BaseFragment {

    private static final String TAG = "CHAT Fragment";
    public static final int REQUEST_CODE_IMAGE_CAPTURE = 201;
    public static final int REQUEST_CODE_IMAGE_PICK = 301;
    public static final int REQUEST_CODE_VIDEO_CAPTURE = 401;
    @BindView(R.id.message_container) RecyclerView messageListView;
    @BindView(R.id.chat_heads_view) RecyclerView chatHeadsView;
    @BindView(R.id.progress_bar) AVLoadingIndicatorView progressBar;
    @BindView(R.id.input_container) View inputContainer;
    @BindView(R.id.bottom_create_chat_container) RelativeLayout bottomCreateChatContainer;
    @BindView(R.id.info_message) TextView infoMessage;
    @BindView(R.id.down_arrow) ImageView downArrow;
    @BindView(R.id.chat_info_line) View chatInfoLine;
    @BindView(R.id.chat_info_line_text) TextView infoLineTextView;

    @BindView(R.id.messenger_send_button) Button sendButton;
    @BindView(R.id.mic_button) Button micButton;
    @BindView(R.id.cam_button) Button camButton;
    @BindView(R.id.pic_button) Button picButton;
    @BindView(R.id.vid_button) Button vidButton;
    @BindView(R.id.video_view) VideoView videoView;
    @BindView(R.id.image_fullscreen) ImageView imageViewFullScreen;
    @BindView(R.id.full_screen_container) RelativeLayout fullScreenContainer;
    @BindView(R.id.close_image_button) ImageView imageCloseButton;
    @BindView(R.id.input_edit_text) EditText inputEditText;
    @BindView(R.id.download_image_button) ImageView imageDownloadButton;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    ChatHeadsAdapter chatHeadsAdapter;
    ChatInfo activeChatInfo;

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
        chatHeadsAdapter = new ChatHeadsAdapter();
        chatHeadsView.setAdapter(chatHeadsAdapter);
        inputContainer.setVisibility(View.GONE);
        messageListView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        infoMessage.setVisibility(View.GONE);
        sendButton.setVisibility(View.GONE);

        imageCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullScreenContainer.setVisibility(View.GONE);
            }
        });
        imageDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Image is downloaded.", Toast.LENGTH_SHORT).show();
            }
        });
        initializeUI();

        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
//        chatHeadsContainer.setLayoutTransition(layoutTransition);

//        chatInfoLine.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
//            public void onSwipeBottom() {
//                showGridChats();
//            }
//        });
//
//        bottomCreateChatContainer.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
//            public void onSwipeTop() {
//                hideGridChats();
//            }
//        });

         //Add textWatcher to notify the user
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==0) {
                    sendButton.setVisibility(View.GONE);
                } else {
                    sendButton.setVisibility(View.VISIBLE);
                }
                if(activeChatInfo!=null){
                    activeChatInfo.setUserIsTyping(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImsMessage message = ImsMessage.getDefaultMessage();
                message.setText(inputEditText.getText().toString().trim());
                activeChatInfo.sendMessage(message);
                inputEditText.setText("");
                activeChatInfo.setUserIsTyping(false);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(activeChatInfo!=null){
                    activeChatInfo.loadPreviousMessagesPage();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ChatFragmentPermissionsDispatcher.startRecordingWithCheck(ChatFragment.this);
                        break;
                    case MotionEvent.ACTION_UP:
                        stopRecording();
                        break;
                }
                return false;
            }
        } );

        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    startActivityForResult(takePictureIntent, REQUEST_CODE_IMAGE_CAPTURE);
                }
            }
        });
        picButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, REQUEST_CODE_IMAGE_PICK);//one can be replaced with any action code
            }
        });

        vidButton .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_CODE_VIDEO_CAPTURE);
                }
            }
        });

       /* downArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGridChats();
            }
        });*/
        return view;
    }

    private MediaRecorder recorder = null;
    private String audioFilepath = null;
    String currentPath;
    String videoDownloadUrl;

/*    private void hideGridChats(){
        swipeRefreshLayout.setEnabled(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        chatHeadsView.setLayoutManager(layoutManager);
        bottomCreateChatContainer.setVisibility(View.GONE);
        inputContainer.setVisibility(View.VISIBLE);
        displayChat(activeChatInfo);
        chatHeadsAdapter.setInGrid(false);
        chatHeadsAdapter.notifyDataSetChanged();
    }

    private void showGridChats(){
        swipeRefreshLayout.setEnabled(false);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 6);
        chatHeadsView.setLayoutManager(layoutManager);
        bottomCreateChatContainer.setVisibility(View.VISIBLE);
        infoMessage.setVisibility(View.GONE);
        inputContainer.setVisibility(View.GONE);
        chatHeadsAdapter.setInGrid(true);
        chatHeadsAdapter.notifyDataSetChanged();
    }*/

    @Subscribe
    @SuppressWarnings("Unchecked cast")
    public void onChatEventDetected(GameSparksEvent event){
        //Log.d(TAG, "event received: " + event.getEventType());
        switch (event.getEventType()){
            case TYPING:
                    if ((event.getData() != null)) {
                        List<UserInfo> usersTyping = (List<UserInfo>) event.getData();
                        //Log.d(TAG, "Count of usersTyping: " + usersTyping.size());
                    }
                break;
            case USER_CHAT_DETECTED:
                initializeUI();
                break;
            case PUBLIC_CHAT_DETECTED:
                initializeUI();
                break;
            case GLOBAL_CHAT_DETECTED: // TODO Check on this - same logic as for public chats?
                initializeUI();
                break;
            case NEW_MESSAGE_ADDED:
                messageListView.getAdapter().notifyDataSetChanged();
                int lastMessagePosition = messageListView.getAdapter().getItemCount() == 0 ? 0 : messageListView.getAdapter().getItemCount();
                messageListView.smoothScrollToPosition(lastMessagePosition);
                break;
            case NEXT_PAGE_LOADED:
                swipeRefreshLayout.setRefreshing(false);
                messageListView.getAdapter().notifyDataSetChanged();
                messageListView.smoothScrollToPosition(0);
                break;
            case MESSAGE_IMAGE_FILE_UPLOADED:
            case AUDIO_FILE_UPLOADED:
                String downloadUrl = (String) event.getData();
                ImsMessage message = ImsMessage.getDefaultMessage();
                message.setImageUrl(downloadUrl);
                activeChatInfo.sendMessage(message);
                break;
            case VIDEO_FILE_UPLOADED:
                videoDownloadUrl = (String) event.getData();
                Model.getInstance().uploadVideoRecordingThumbnail(currentPath,getActivity().getFilesDir());
                break;
            case VIDEO_IMAGE_FILE_UPLOADED:
                String videoThumbnailDownloadUrl = (String) event.getData();
                ImsMessage messageVideo = ImsMessage.getDefaultMessage();
                messageVideo.setVidUrl(videoDownloadUrl);
                messageVideo.setImageUrl(videoThumbnailDownloadUrl);
                activeChatInfo.sendMessage(messageVideo);
                break;
        }
    }

    @Subscribe
    public void onUIChatEventDetected(UIEvent event){
        //Log.d(TAG, "event received: " + event.getId());
        int currentPosition = event.getPosition();
        List<ChatInfo> infos = ImsManager.getInstance().getUserChatsList();
        displayChat(infos.get(currentPosition));

    }

    @Subscribe
    public void playVideo(PlayVideoEvent event){
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoURI(Uri.parse(event.getId()));
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.setVisibility(View.GONE);
            }
        });
    }

    @Subscribe
    public void showImageFullScreen(FullScreenImageEvent event){
        fullScreenContainer.setVisibility(View.VISIBLE);
        String uri = event.getId();
        ImageLoader.getInstance().displayImage(uri,imageViewFullScreen);
    }

    public void initializeUI(){
        //Log.d(TAG, "Initialize Chat UI");
        List<ChatInfo> allUserChats = ImsManager.getInstance().getUserChatsList();

        StringBuilder chatNames = new StringBuilder("");
        for(ChatInfo chatInfo : allUserChats){
            String chatName = chatInfo.getName();
            if(!TextUtils.isEmpty(chatName)){
                chatNames.append(chatInfo.getName()).append(", ");
            }
        }
        infoLineTextView.setText(chatNames);

        chatHeadsAdapter.setValues(allUserChats);
        chatHeadsAdapter.notifyDataSetChanged();

        inputContainer.setVisibility(View.VISIBLE);
//        chatHeadsContainer.setVisibility(View.VISIBLE);
        messageListView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void displayChat(ChatInfo info){
        activeChatInfo = info;
        if(activeChatInfo!=null && info.getMessages()!=null){
            // Message container initialization
            if(info.getMessages().size()>0){
                swipeRefreshLayout.setEnabled(true);
                messageListView.setVisibility(View.VISIBLE);
                infoMessage.setVisibility(View.GONE);
                //Log.d(TAG, "Displaying Chat - message count: " + info.getMessages().size());

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                messageListView.setLayoutManager(layoutManager);

                MessageAdapter messageAdapter = new MessageAdapter(getContext(),info);
                messageListView.setAdapter(messageAdapter);
                messageListView.invalidate();
                return;
            } else {
                Log.e(TAG, "Message array size is 0!");
            }
        } else {
            Log.e(TAG, "Message array is null!");
        }
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
        infoMessage.setVisibility(View.VISIBLE);
        messageListView.setVisibility(View.INVISIBLE);
    }

    @NeedsPermission({Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void startRecording() {
        Toast.makeText(getContext(),"Hold down to record!", Toast.LENGTH_SHORT).show();
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
        if(recorder!=null){
            try {
                recorder.stop();
                recorder.release();
                Toast.makeText(getContext(),"Recording stopped.", Toast.LENGTH_SHORT).show();
                Model.getInstance().uploadAudioRecording(audioFilepath);
                recorder = null;
            } catch (Exception e) {
                Log.e(TAG, "Stop recording failed!");
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

    @OnShowRationale({Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE})
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

    @OnPermissionDenied({Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showDeniedForMicrophone() {
        Toast.makeText(getContext(), R.string.permission_microphone_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE})
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
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_IMAGE_CAPTURE:
                    //Log.d(TAG, "CAPTURED IMAGE PATH IS: " + currentPath);
                    Model.getInstance().uploadImageForMessage(currentPath);
                    break;
                case REQUEST_CODE_IMAGE_PICK:
                    Uri selectedImageURI = intent.getData();
                    //Log.d(TAG, "SELECTED IMAGE URI IS: " + selectedImageURI.toString());
                    String realPath = Model.getRealPathFromURI(getContext(),selectedImageURI);
                    //Log.d(TAG, "SELECTED IMAGE REAL PATH IS: " + realPath);
                    Model.getInstance().uploadImageForMessage(realPath);
                    break;
                case REQUEST_CODE_VIDEO_CAPTURE:
                    Uri videoUri = intent.getData();
                    //Log.d(TAG, "VIDEO URI IS: " + videoUri.toString());
                    currentPath = Model.getRealPathFromURI(getContext(),videoUri);
                    Model.getInstance().uploadVideoRecording(currentPath);
                    break;
            }
        }
    }
}
