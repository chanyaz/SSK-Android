package base.app.fragment.instance;


import android.Manifest;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.twilio.video.CameraCapturer;
import com.twilio.video.ConnectOptions;
import com.twilio.video.LocalAudioTrack;
import com.twilio.video.LocalVideoTrack;
import com.twilio.video.Participant;
import com.twilio.video.Room;
import com.twilio.video.TwilioException;
import com.twilio.video.Video;
import com.twilio.video.VideoView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import base.app.activity.PhoneLoungeActivity;
import base.app.fragment.popup.LoginFragment;
import base.app.fragment.popup.SignUpFragment;
import base.app.model.user.LoginStateReceiver;
import base.app.model.user.UserEvent;
import base.app.util.Utility;
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
import base.app.R;
import base.app.activity.LoungeActivity;
import base.app.fragment.BaseFragment;
import base.app.fragment.FragmentEvent;
import base.app.fragment.popup.AlertDialogFragment;
import base.app.fragment.popup.StartingNewCallFragment;
import base.app.model.AlertDialogManager;
import base.app.model.Model;
import base.app.model.user.UserInfo;
import base.app.model.videoChat.Slot;
import base.app.model.videoChat.VideoChatEvent;
import base.app.model.videoChat.VideoChatItem;
import base.app.model.videoChat.VideoChatModel;
import base.app.events.AddUsersToCallEvent;
import base.app.events.StartCallEvent;
import base.app.util.ui.ThemeManager;

/**
 * Created by Djordje on 01/31/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 * <p>
 * A simple {@link BaseFragment} subclass.
 */
@RuntimePermissions
public class VideoChatFragment extends BaseFragment implements Room.Listener {

    private static final String TAG = "VIDEO CHAT FRAGMENT";
    // The 3 main view state containers
    @BindView(R.id.progress_bar)
    public View loadingView;
    @BindView(R.id.active_container)
    public View activeChatView;
    @BindView(R.id.inactive_container)
    public View inactiveChatView;

    // local user feed
    @BindView(R.id.preview_video_view)
    public VideoView previewView;
    @BindView(R.id.preview_user_name_text_view)
    public TextView name;
    @BindView(R.id.disabled_video_icon)
    public View disabled;


    @BindView(R.id.disconnect_button)
    public ImageButton hangupButton;
    @BindView(R.id.toggle_mic_button)
    public ImageButton muteButton;
    @BindView(R.id.toggle_camera_button)
    public ImageButton flipCameraButton;
    @BindView(R.id.toggle_video_button)
    public ImageButton videoButton;
    @BindView(R.id.add_users_button)
    public ImageButton addUserButton;
    @Nullable
    @BindView(R.id.camera_logo)
    ImageView cameraLogo;

    @BindView(R.id.begin_your_call)
    Button beginYourCall;


    @BindView(R.id.logo)
    ImageView Logo;
    @Nullable
    @BindView(R.id.login_container)
    LinearLayout loginContainer;

    @BindView(R.id.text1)
    TextView text;

    String roomId;
    String pendingRoomId;
    VideoChatModel model;

    //    LocalMedia localMedia;
    LocalAudioTrack localAudioTrack;
    LocalVideoTrack localVideoTrack;
    CameraCapturer camera;
    String token = "TWILIO_ACCESS_TOKEN";
    Room room;

    boolean isMicrophoneEnabled;
    boolean isFrontCamera;
    boolean isVideoEnabled;

    ArrayList<Slot> slots;

    public void setMicrophoneEnabled(boolean microphoneEnabled) {
        isMicrophoneEnabled = microphoneEnabled;
        localAudioTrack.enable(microphoneEnabled);
        if (microphoneEnabled) {
            muteButton.setSelected(false);
        } else {
            muteButton.setSelected(true);
        }
    }

    public void setFrontCamera(boolean frontCamera) {
        isFrontCamera = frontCamera;
        if (camera != null) {
            camera.switchCamera();
        }
    }

    public void setVideoEnabled(boolean videoEnabled) {
        isVideoEnabled = videoEnabled;
        previewView.setVisibility(isVideoEnabled ? View.VISIBLE : View.INVISIBLE);
        disabled.setVisibility(isVideoEnabled ? View.INVISIBLE : View.VISIBLE);
        localVideoTrack.enable(isVideoEnabled);
        if (videoEnabled) {
            videoButton.setSelected(false);
        } else {
            videoButton.setSelected(true);
        }
    }

    @OnClick(R.id.begin_your_call)
    public void onConnect() {
        VideoChatFragmentPermissionsDispatcher.onConnectClickedWithCheck(this, getView());
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    public void onConnectClicked(View view) {
        EventBus.getDefault().post(new FragmentEvent(StartingNewCallFragment.class));
    }

    @OnClick(R.id.disconnect_button)
    public void onDisconnect(View view) {
        disconnect();
    }

    @OnClick(R.id.toggle_camera_button)
    public void onToggleCamera(View view) {
        setFrontCamera(!isFrontCamera);
    }

    @OnClick(R.id.toggle_mic_button)
    public void onToggleMicrophone(View view) {
        if (localAudioTrack != null) {
            setMicrophoneEnabled(!isMicrophoneEnabled);
        }
    }

    @OnClick(R.id.toggle_video_button)
    public void onToggleVideo(View view) {
        if (localVideoTrack != null) {
            setVideoEnabled(!isVideoEnabled);
        }
    }

    @OnClick(R.id.add_users_button)
    public void onAddUsers(View view) {
        Slot slot = getNextFreeSlot();
        if (slot == null) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.video_chat_maximum), Toast.LENGTH_LONG).show();
            return;
        }
        FragmentEvent fe = new FragmentEvent(StartingNewCallFragment.class);
        fe.setStringArrayList(getUserIdsFromSlots());
        EventBus.getDefault().post(fe);
    }

    public VideoChatFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (getActivity() instanceof PhoneLoungeActivity)
            ((PhoneLoungeActivity) getActivity()).setMarginTop(true);
        View view = inflater.inflate(R.layout.fragment_video_chat, container, false);
        ButterKnife.bind(this, view);

        model = VideoChatModel.getInstance();
//        localMedia = LocalMedia.create(getContext());
        name.setText(getContext().getResources().getString(R.string.video_chat_you));
        slots = new ArrayList<>();
        slots.add(new Slot(ButterKnife.findById(view, R.id.slot_2)));
        slots.add(new Slot(ButterKnife.findById(view, R.id.slot_3)));
        slots.add(new Slot(ButterKnife.findById(view, R.id.slot_4)));
//TODO @Djordje deprecated
        if (Utility.isTablet(getActivity())) {
            text.setText(Html.fromHtml(getString(R.string.video_chat_text_1)));
        }
      else  {
            onLoginStateChange();
        }

        VideoChatEvent event = VideoChatModel.getInstance().getVideoChatEvent();
        if (event != null) {
            onVideoChatEvent(event);
            VideoChatModel.getInstance().setVideoChatEvent(null);
        }
        updateIconsColor();
        return view;
    }

    public void updateIconsColor() {
        if (ThemeManager.getInstance().isLightTheme()) {
            hangupButton.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            muteButton.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            flipCameraButton.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            videoButton.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            addUserButton.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        } else {
            hangupButton.clearColorFilter();
            muteButton.clearColorFilter();
            flipCameraButton.clearColorFilter();
            videoButton.clearColorFilter();
            addUserButton.clearColorFilter();
        }

    }

    private void setViewState(boolean active, boolean loading) {
        activeChatView.setVisibility(active && !loading ? View.VISIBLE : View.GONE);
        inactiveChatView.setVisibility(!active && !loading ? View.VISIBLE : View.GONE);
        loadingView.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    // -> setListeners
    @Subscribe
    public void onVideoChatEvent(VideoChatEvent event) {
        VideoChatFragmentPermissionsDispatcher.onVideoChatEventReceivedWithCheck(this, event);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    public void onVideoChatEventReceived(VideoChatEvent event) {
        switch (event.getType()) {
            case onUserInvited:
                ArrayList<String> users = new ArrayList<>();
                users.add(event.getId());
                addUsersToView(users);
                break;
            case onUserInvitationRejected:
                removeAndCheckStateBy(event.getId());
                break;
            case onSelfInvited:
                VideoChatItem videoChatItem = event.getItem();
                final String roomId = videoChatItem.getId().getOid();
                Task<UserInfo> userInfoTask = Model.getInstance().getUserInfoById(videoChatItem.getOwnerId());
                userInfoTask.addOnCompleteListener(new OnCompleteListener<UserInfo>() {
                    @Override
                    public void onComplete(@NonNull Task<UserInfo> task) {
                        if (task.isSuccessful()) {
                            UserInfo userInfo = task.getResult();
                            String usersName = "";
                            if (userInfo.getFirstName() != null && userInfo.getLastName() != null) {
                                usersName = userInfo.getFirstName() + userInfo.getLastName();
                            } else if (userInfo.getNicName() != null) {
                                usersName = userInfo.getNicName();
                            }
                            AlertDialogManager.getInstance().showAlertDialog(getContext().getResources().getString(R.string.video_chat_receive_call) + " \'" + usersName + " \'?", getContext().getResources().getString(R.string.video_chat_accpet_call),
                                    new View.OnClickListener() {// Cancel
                                        @Override
                                        public void onClick(View v) {
                                            rejectInvitation(roomId);
                                            getActivity().onBackPressed();
                                        }
                                    }, new View.OnClickListener() { // Confirm
                                        @Override
                                        public void onClick(View v) {
                                            acceptInvitation(roomId);
                                            getActivity().onBackPressed();
                                        }
                                    });
                        }
                    }
                });
                break;
            case onInvitationRevoked:
                if (((LoungeActivity) getActivity()).getFragmentOrganizer().getOpenFragment() instanceof AlertDialogFragment) {
                    getActivity().onBackPressed();
                }
                break;
            case onChatClosed:
                if (((LoungeActivity) getActivity()).getFragmentOrganizer().getOpenFragment() instanceof AlertDialogFragment) {
                    getActivity().onBackPressed();
                }
                if (room != null && room.getName().equals(event.getId())) {
                    disconnect();
                }
                break;
        }
    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    void showRationaleForMicrophoneAndCamera(final PermissionRequest request) {
        AlertDialogManager.getInstance().showAlertDialog(getContext().getResources().getString(R.string.permissions), getActivity().getResources().getString(R.string.video_chat_permission_microphone_and_camera_rationale),
                new View.OnClickListener() {// Cancel
                    @Override
                    public void onClick(View v) {
                        request.cancel();
                        getActivity().onBackPressed();
                    }
                }, new View.OnClickListener() { // Confirm
                    @Override
                    public void onClick(View v) {
                        request.proceed();
                        getActivity().onBackPressed();
                    }
                });
    }

    @OnNeverAskAgain({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    void showNeverAskForMicrophoneAndCamera() {
        Toast.makeText(getContext(), R.string.video_chat_permission_microphone_and_camera_denied, Toast.LENGTH_SHORT).show();
    }

    @OnPermissionDenied({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    void showDeniedForMicrophoneAnCamera() {
        Toast.makeText(getContext(), R.string.video_chat_permission_microphone_and_camera_denied, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        VideoChatFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private void acceptCallFromPushNotification(String roomId) {
        acceptInvitation(roomId);
    }

    @Subscribe
    public void startCallWithUsers(StartCallEvent event) {
        List<UserInfo> users = event.getUsers();
        final ArrayList<String> opponentIds = new ArrayList<>();
        for (UserInfo user : users) {
            if (!user.getUserId().equals(Model.getInstance().getUserInfo().getUserId())) {
                opponentIds.add(user.getUserId());
            }
        }

        Task<VideoChatItem> createCallTask = model.create(opponentIds);
        createCallTask.addOnCompleteListener(new OnCompleteListener<VideoChatItem>() {
            @Override
            public void onComplete(@NonNull Task<VideoChatItem> task) {
                if (task.isSuccessful()) {
                    addUsersToView(opponentIds);
                    roomId = task.getResult().getId().getOid();
                    connect();
                } else {
                    Log.e(TAG, "Couldn't create conference on Database!!");
                }
            }
        });
    }

    @Subscribe
    public void addUsersToCall(AddUsersToCallEvent event) {
        List<UserInfo> users = event.getUsers();
        final ArrayList<String> opponentIds = new ArrayList<>();
        for (UserInfo user : users) {
            if (!user.getUserId().equals(Model.getInstance().getUserInfo().getUserId())) {
                opponentIds.add(user.getUserId());
            }
        }
        Task<VideoChatItem> inviteUsersTask = model.invite(roomId, opponentIds);
        inviteUsersTask.addOnCompleteListener(new OnCompleteListener<VideoChatItem>() {
            @Override
            public void onComplete(@NonNull Task<VideoChatItem> task) {
                if (task.isSuccessful()) {
                    addUsersToView(opponentIds);
                }
            }
        });
    }

    private void addUsersToView(List<String> users) {
        if (users != null) {
            for (String userId : users) {
                if (!userId.equals(Model.getInstance().getUserInfo().getUserId())) {
                    Slot slot = getSlotBy(userId);
                    if (slot == null) {
                        slot = getNextFreeSlot();
                        if (slot != null) {
                            slot.setUserId(userId);
                            addSlotToLayout(slot);
                        }
                    }
                }
            }
        }

    }

    private void acceptInvitation(String roomId) {
        if (room != null) {
            pendingRoomId = roomId;
            disconnect();
        } else {
            preConnect(roomId);
        }
    }

    private void rejectInvitation(String roomId) {
        model.reject(roomId);
    }

    private void disconnect() {
        if (room != null) {
            setViewState(false, true);
            room.disconnect();
        }
    }

    private void onDisconnected() {
        if (room != null) {
            model.leave(room.getName());
        }
        clearListeners();
        cleanupRemoteParticipants();
        room = null;
        setViewState(false, false);
        if (pendingRoomId != null) {
            preConnect(pendingRoomId);
        }
    }

    private void connect() {
        setViewState(true, true);
        pendingRoomId = null;
        model.getToken(roomId).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "VideoChat.connect() -> AuthToken not available!");
                    // Present failure / re-login notice to user
                    model.leave(roomId);
                    onDisconnected();
                } else {
                    token = task.getResult();
                    completeConnect();
                }
            }
        });
    }

    private void completeConnect() {
        localAudioTrack = LocalAudioTrack.create(getContext(), true);
        camera = new CameraCapturer(getContext(), CameraCapturer.CameraSource.FRONT_CAMERA);
        localVideoTrack = LocalVideoTrack.create(getContext(), true, camera);
        List<LocalAudioTrack> localAudioTracks = new ArrayList<LocalAudioTrack>() {{
            add(localAudioTrack);
        }};
        List<LocalVideoTrack> localVideoTracks = new ArrayList<LocalVideoTrack>() {{
            add(localVideoTrack);
        }};

        if (localAudioTracks.size() != 0) {
            startPreview();
        }
        // Connect to a room
        ConnectOptions connectOptions = new ConnectOptions.Builder(token)
                .roomName(roomId)
                .audioTracks(localAudioTracks)
                .videoTracks(localVideoTracks)
                .build();

        room = Video.connect(getContext(), connectOptions, this);
    }

//    private void completeConnect() {
//        if (localAudioTrack == null) {
//            localAudioTrack = localMedia.addAudioTrack(true);
//        }
//        if (localMedia.getVideoTracks().size() == 0) {
//            startPreview();
//        }
//        // Connect to a room
//        ConnectOptions connectOptions = new ConnectOptions.Builder(token)
//                .roomName(roomId)
//                .localMedia(localMedia)
//                .build();
//
//        room = Video.connect(getContext(), connectOptions, this);
//    }

    private void startPreview() {
        localVideoTrack.addRenderer(previewView);
    }

//    private void startPreview() {
//        camera = new CameraCapturer(getContext(), CameraCapturer.CameraSource.FRONT_CAMERA);
//        localVideoTrack = localMedia.addVideoTrack(true, camera);
//        if (localVideoTrack == null) {
//            Log.e(TAG, "Failed to add video track");
//        } else {
//            localVideoTrack.addRenderer(previewView);
//        }
//    }


    private void cleanupRemoteParticipants() {
        for (Slot slot : slots) {
            removeSlotFromLayout(slot);
            slot.disconnect();
        }
    }

    private Slot getNextFreeSlot() {
        for (Slot slot : slots) {
            if (slot.getParticipant() == null && slot.getUserId() == null) {
                return slot;
            }
        }
        return null;
    }

    private Slot getSlotBy(String userId) {
        for (Slot slot : slots) {
            if (slot.getUserId() != null) {
                if (slot.getUserId().equals(userId)) {
                    return slot;
                }
            }
        }
        return null;
    }

    private ArrayList<String> getUserIdsFromSlots() {
        ArrayList<String> users = new ArrayList<>();
        for (Slot slot : slots) {
            if (slot.getUserId() != null) {
                users.add(slot.getUserId());
            }
        }
        return users;
    }

    private List<UserInfo> getUsersFromSlots() {
        List<UserInfo> users = new ArrayList<>();
        for (Slot slot : slots) {
            if (slot.getUserInfo() != null) {
                users.add(slot.getUserInfo());
            }
        }
        return users;
    }

    private void removeAndCheckStateBy(String userId) {
        final Slot slot = getSlotBy(userId);
        if (slot != null) {
            removeSlotFromLayout(slot);
            slot.disconnect();
            ArrayList<String> users = getUserIdsFromSlots();
            if (users.size() < 1) {
                disconnect();
            }
        }
    }


    private void addSlotToLayout(Slot slot) {
        slot.getView().setVisibility(View.VISIBLE);
    }

    private void removeSlotFromLayout(Slot slot) {
        slot.reset();
        slot.getView().setVisibility(View.GONE);
    }


    private void preConnect(String id) {
        model.join(id).addOnCompleteListener(new OnCompleteListener<VideoChatItem>() {
            @Override
            public void onComplete(@NonNull Task<VideoChatItem> task) {
                if (task.isSuccessful()) {
                    roomId = task.getResult().getId().getOid();
                    connect();
                }
            }
        });

    }


    private void clearListeners() {
        clearListeners(false);
    }

    private void clearListeners(boolean destroy) {
//        if destroy == true {
//            self.onInvited              = nil
//            self.onInvitationRevoked    = nil
//            self.onRoomClosed           = nil
//        }
//
//        self.onRemoteUserInvitationRejected = nil
//        self.onRemoteUserInvited            = nil
    }


    @Override
    public void onConnected(Room room) {
        Log.d(TAG, "Connected to room " + room.getName() + " as " + room.getLocalParticipant().getIdentity());
        if (room.getParticipants().size() > 0) {
            for (Participant remoteParticipant : room.getParticipants()) {

                Slot slot = getSlotBy(remoteParticipant.getIdentity());
                // You started the call, but other people have managed to connect before you, so we already
                // have a slot for them
                if (slot != null) {
                    slot.setParticipant(remoteParticipant);
                } else { // Otherwise, create a new free slot if one is avaiable
                    slot = getNextFreeSlot();
                    if (slot != null) {
                        slot.setParticipant(remoteParticipant);
                        addSlotToLayout(slot);
                    } else {
                        Log.e(TAG, "No free slots left!");
                        break;
                    }
                }
            }
        }
        addUsersToView(model.getInvitedUsers());
        setViewState(true, false);
    }

    @Override
    public void onConnectFailure(Room room, TwilioException e) {
        onDisconnected();
    }

    @Override
    public void onDisconnected(Room room, TwilioException e) {
        onDisconnected();
    }

    @Override
    public void onParticipantConnected(Room room, Participant participant) {
        Log.d(TAG, "Room " + room.getName() + ", Participant " + participant.getIdentity());
        Slot slot = getSlotBy(participant.getIdentity());
        if (slot != null) {
            // You started the call and the user has connected in response
            slot.setParticipant(participant);
        } else { // Maybe someone else added a new user and they've joined
            slot = getNextFreeSlot();
            if (slot != null) {
                slot.setParticipant(participant);
                addSlotToLayout(slot);
            } else {
                Log.e(TAG, "No free slots left!");
            }
        }
    }

    @Override
    public void onParticipantDisconnected(Room room, Participant participant) {
        removeAndCheckStateBy(participant.getIdentity());
    }

    @Override
    public void onRecordingStarted(Room room) {
    }

    @Override
    public void onRecordingStopped(Room room) {
    }

    @Subscribe
    public void updateUserName(UserEvent event) {
        if (!Utility.isTablet(getActivity())) {
            onLoginStateChange();
        }

    }

    @Override
    public void onResume() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        super.onResume();
    }

    @Override
    public void onStop() {
        disconnect();
        super.onStop();
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


    private void onLoginStateChange(){

        if (Model.getInstance().getLoggedInUserType() == Model.LoggedInUserType.REAL) {
            cameraLogo.setVisibility(View.GONE);
            loginContainer.setVisibility(View.GONE);
            Logo.setVisibility(View.VISIBLE);
            beginYourCall.setVisibility(View.VISIBLE);
        } else {
            cameraLogo.setVisibility(View.VISIBLE);
            loginContainer.setVisibility(View.VISIBLE);
            Logo.setVisibility(View.GONE);
            beginYourCall.setVisibility(View.GONE);
        }
    }

}
