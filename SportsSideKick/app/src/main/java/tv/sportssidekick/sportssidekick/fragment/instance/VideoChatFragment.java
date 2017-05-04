package tv.sportssidekick.sportssidekick.fragment.instance;


import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.twilio.video.CameraCapturer;
import com.twilio.video.ConnectOptions;
import com.twilio.video.LocalAudioTrack;
import com.twilio.video.LocalMedia;
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
import tv.sportssidekick.sportssidekick.activity.LoungeActivity;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.fragment.popup.AlertDialogFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.StartingNewCallFragment;
import tv.sportssidekick.sportssidekick.model.AlertDialogManager;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.model.videoChat.Slot;
import tv.sportssidekick.sportssidekick.model.videoChat.VideoChatEvent;
import tv.sportssidekick.sportssidekick.model.videoChat.VideoChatItem;
import tv.sportssidekick.sportssidekick.model.videoChat.VideoChatModel;
import tv.sportssidekick.sportssidekick.events.AddUsersToCallEvent;
import tv.sportssidekick.sportssidekick.events.StartCallEvent;

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

    String roomId;
    String pendingRoomId;
    VideoChatModel model;

    LocalMedia localMedia;
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
            // TODO - toggle camera button image
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
            Toast.makeText(getContext(), "Sorry you can only video call with a maximum of 3 friends, and this call is already full!", Toast.LENGTH_LONG).show();
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
        View view = inflater.inflate(R.layout.fragment_video_chat, container, false);
        ButterKnife.bind(this, view);

        model = VideoChatModel.getInstance();
        localMedia = LocalMedia.create(getContext());
        name.setText("You");
        slots = new ArrayList<>();
        slots.add(new Slot(ButterKnife.findById(view, R.id.slot_2)));
        slots.add(new Slot(ButterKnife.findById(view, R.id.slot_3)));
        slots.add(new Slot(ButterKnife.findById(view, R.id.slot_4)));

        VideoChatEvent event = VideoChatModel.getInstance().getVideoChatEvent();
        if (event != null)
        {
            onVideoChatEvent(event);
            VideoChatModel.getInstance().setVideoChatEvent(null);
        }

        return view;
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
                            AlertDialogManager.getInstance().showAlertDialog("Receiving a call from \'" + usersName + " \'?", "Accept the call?",
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
                if (((LoungeActivity)getActivity()).getFragmentOrganizer().getOpenFragment() instanceof AlertDialogFragment) {
                    getActivity().onBackPressed();
                }
                break;
            case onChatClosed:
                if (((LoungeActivity)getActivity()).getFragmentOrganizer().getOpenFragment() instanceof AlertDialogFragment) {
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
        AlertDialogManager.getInstance().showAlertDialog("Permissions", getActivity().getResources().getString(R.string.video_chat_permission_microphone_and_camera_rationale),
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
        if (localAudioTrack == null) {
            localAudioTrack = localMedia.addAudioTrack(true);
        }
        if (localMedia.getVideoTracks().size() == 0) {
            startPreview();
        }
        // Connect to a room
        ConnectOptions connectOptions = new ConnectOptions.Builder(token)
                .roomName(roomId)
                .localMedia(localMedia)
                .build();

        room = Video.connect(getContext(), connectOptions, this);
    }

    private void startPreview() {
        camera = new CameraCapturer(getContext(), CameraCapturer.CameraSource.FRONT_CAMERA);
        localVideoTrack = localMedia.addVideoTrack(true, camera);
        if (localVideoTrack == null) {
            Log.e(TAG, "Failed to add video track");
        } else {
            localVideoTrack.addRenderer(previewView);
        }
    }

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
//      self.appDelegate.loungeViewController?.wallTabs.tabItemJumpTo("Video Chat") TODO ?
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
            for (Map.Entry<String, Participant> remoteParticipant : room.getParticipants().entrySet()) {

                Slot slot = getSlotBy(remoteParticipant.getValue().getIdentity());
                // You started the call, but other people have managed to connect before you, so we already
                // have a slot for them
                if (slot != null) {
                    slot.setParticipant(remoteParticipant.getValue());
                } else { // Otherwise, create a new free slot if one is avaiable
                    slot = getNextFreeSlot();
                    if (slot != null) {
                        slot.setParticipant(remoteParticipant.getValue());
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

    @Override
    public void onResume() {
        if(!EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().register(this);
        }
        super.onResume();
    }

    @Override
    public void onStop() {
        disconnect();
        super.onStop();
    }
}
