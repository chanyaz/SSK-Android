package tv.sportssidekick.sportssidekick.fragment.instance;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.fragment.popup.StartingNewCallFragment;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.model.videoChat.VideoChatEvent;
import tv.sportssidekick.sportssidekick.model.videoChat.VideoChatItem;
import tv.sportssidekick.sportssidekick.model.videoChat.VideoChatModel;

/**
 * Created by Djordje on 01/31/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 *
 * A simple {@link BaseFragment} subclass.
 */

public class VideoChatFragment extends BaseFragment implements Room.Listener {

    private static final String TAG = "VIDEO CHAT FRAGMENT";
    @BindView(R.id.being_your_call)
    Button startCallButton;

    // The 3 main view state containers
    private View loadingView;
    private View activeChatView;
    private View inactiveChatView;

    // chat active state view stack rows
    private View bottom;
    private View top;

    // local user feed
    private VideoView previewView;
    private TextView name;
    private View disabled;


    @BindView(R.id.disconnect_button)
    private Button hangupButton;
    @BindView(R.id.toggle_mic_button)
    private Button muteButton;
    @BindView(R.id.toggle_camera_button)
    private Button flipCameraButton;
    @BindView(R.id.toggle_video_button)
    private Button videoButton;


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

    public void setMicrophoneEnabled(boolean microphoneEnabled) {
        isMicrophoneEnabled = microphoneEnabled;
        localAudioTrack.enable(microphoneEnabled);
        // TODO - toggle mic image
    }

    public void setFrontCamera(boolean frontCamera) {
        isFrontCamera = frontCamera;
        camera.switchCamera();
        // TODO - toggle camera image
        // camera.getCameraSource();
    }

    public void setVideoEnabled(boolean videoEnabled) {
        isVideoEnabled = videoEnabled;
        previewView.setVisibility(isVideoEnabled ? View.VISIBLE : View.GONE);
        disabled.setVisibility(isVideoEnabled ? View.GONE : View.VISIBLE);
        localVideoTrack.enable(isVideoEnabled);
    }

    @OnClick(R.id.being_your_call)
    public void onConnect(View view){
        EventBus.getDefault().post(new FragmentEvent(StartingNewCallFragment.class));
    }

    @OnClick(R.id.disconnect_button)
    public void onDisconnect(View view){
        disconnect();
    }

    @OnClick(R.id.toggle_camera_button)
    public void onToggleCamera(View view){
        setFrontCamera(!isFrontCamera);
    }

    @OnClick(R.id.toggle_mic_button)
    public void onToggleMicrophone(View view){
        if(localAudioTrack!=null){
            setMicrophoneEnabled(!isMicrophoneEnabled);
        }
    }
    @OnClick(R.id.toggle_video_button)
    public void onToggleVideo(View view){
        if(localVideoTrack != null) {
            setVideoEnabled(!isVideoEnabled);
        }
    }
    @OnClick(R.id.add_users_button)
    public void onAddUsers(View view){

    }

    public VideoChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_chat, container, false);
        ButterKnife.bind(this, view);
        model = VideoChatModel.getInstance();
        localMedia = LocalMedia.create(getContext());
        return view;
    }




    private void setViewState(boolean active, boolean loading) {
        // TODO Implement UI
//        self.activeChatView.isUserInteractionEnabled      = active && !loading
//        self.inactiveChatView.isUserInteractionEnabled    = !active || loading
//        self.loadingView.isUserInteractionEnabled         = loading
//
//        UIView.animate(withDuration: 0.25, animations: {
//            self.activeChatView.alpha       = (active && !loading) ? 1.0 : 0.0
//            self.inactiveChatView.alpha     = (!active && !loading) ? 1.0 : 0.0
//            self.loadingView.alpha          = loading ? 1.0 : 0.0
//        })
    }

    // -> setListeners
    @Subscribe
    public void onVideoChatEvent(VideoChatEvent event){
        switch(event.getType()){
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
                final String roomId = videoChatItem.getId();
                Task<UserInfo> userInfoTask = Model.getInstance().getUserInfoById(videoChatItem.getOwnerId());
                userInfoTask.addOnCompleteListener(new OnCompleteListener<UserInfo>() {
                    @Override
                    public void onComplete(@NonNull Task<UserInfo> task) {
                        if(task.isSuccessful()){
                            UserInfo userInfo = task.getResult();
                            String usersName = "";
                            if(userInfo.getFirstName()!=null && userInfo.getLastName()!=null){
                                usersName = userInfo.getFirstName() +  userInfo.getLastName();
                            } else if(userInfo.getNicName() != null) {
                                usersName = userInfo.getNicName();
                            }
                            // TODO (1) Show dialog to accept/reject video call - and store it to be dismissed if invitation revoked is triggered (call being canceled)
                            acceptInvitation(roomId);
                            //rejectInvitation(roomId);
                        }
                    }
                });
                break;
            case onInvitationRevoked:
                // TODO Dismiss dialog created in (1)
                break;
            case onChatClosed:
                // TODO Dismiss dialog created in (1)
                if(room!=null && room.getName().equals(event.getId())){
                    disconnect();
                }
              break;
        }
    }

    private void acceptCallFromPushNotification(String roomId) {
        acceptInvitation(roomId);
    }

    private void startCallWithUsers(List<UserInfo> users) {
        final ArrayList<String> opponentIds = new ArrayList<>();
        for(UserInfo user : users){
            if(!user.getUserId().equals(Model.getInstance().getUserInfo().getUserId())){
                opponentIds.add(user.getUserId());
            }
        }

        Task<VideoChatItem> createCallTask = model.create(opponentIds);
        createCallTask.addOnCompleteListener(new OnCompleteListener<VideoChatItem>() {
            @Override
            public void onComplete(@NonNull Task<VideoChatItem> task) {
                if(task.isSuccessful()){
                    addUsersToView(opponentIds);
                    roomId = task.getResult().getId();
                    connect();
                } else {
                    Log.e(TAG, "Couldn't create conference on Database!!");
                }
            }
        });
    }

    private void addUsersToCall(List<UserInfo> users){
        final ArrayList<String> opponentIds = new ArrayList<>();
        for(UserInfo user : users){
            if(!user.getUserId().equals(Model.getInstance().getUserInfo().getUserId())){
                opponentIds.add(user.getUserId());
            }
        }
        Task<VideoChatItem> inviteUsersTask = model.invite(roomId, opponentIds);
        inviteUsersTask.addOnCompleteListener(new OnCompleteListener<VideoChatItem>() {
            @Override
            public void onComplete(@NonNull Task<VideoChatItem> task) {
                if(task.isSuccessful()){
                    addUsersToView(opponentIds);
                }
            }
        });
    }

    private void addUsersToView(List<String> users){
        for(String userId : users){
            if(!userId.equals(Model.getInstance().getUserInfo().getUserId())){
                // TODO - Implement slots
//                if getSlotBy(userId: userId) == nil {
//                    if let slot:Slot = self.getNextFreeSlot() {
//                        slot.userId = userId
//                        layout!.add(slot: slot)
//                    }
//                }
            }
        }
    }

    private void acceptInvitation(String roomId){
        // TODO Dismiss popup - create video chat
        if(room != null){
            pendingRoomId = roomId;
            disconnect();
        } else {
            preConnect(roomId);
        }
    }

    private void rejectInvitation(String roomId){
       model.reject(roomId);
    }

    private void disconnect(){
        if(room != null){
            setViewState(false,true);
            room.disconnect();
        }
    }

    private void onDisconnected(){
        if(room != null) {
            model.leave(room.getName());
        }
        clearListeners();
        cleanupRemoteParticipants();
        room = null;
        setViewState(false,false);
        if (pendingRoomId != null) {
            preConnect(pendingRoomId);
        }
    }

    private void connect(){
        setViewState(true,true);
        pendingRoomId = null;
        model.getToken(roomId).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()){
                    Log.d(TAG,"VideoChat.connect() -> AuthToken not available!");
                    // TODO: Present failure / re-login notice to user
                    model.leave(roomId);
                    onDisconnected();
                } else {
                    token = task.getResult();
                    completeConnect();
                }
            }
        });
    }

    private void completeConnect(){
        if(localAudioTrack==null){
            localAudioTrack = localMedia.addAudioTrack(true);
        }
        if(localMedia.getVideoTracks().size()==0){
            startPreview();
        }
        // Connect to a room
        ConnectOptions connectOptions = new ConnectOptions.Builder(token)
                .roomName("my-room")
                .localMedia(localMedia)
                .build();

        room = Video.connect(getContext(), connectOptions, this);
    }

    private void startPreview() {
        camera = new CameraCapturer(getContext(), CameraCapturer.CameraSource.FRONT_CAMERA);
        localVideoTrack = localMedia.addVideoTrack(true, camera);
        if(localVideoTrack==null){
            Log.e(TAG,"Failed to add video track");
        } else {
            localVideoTrack.addRenderer(previewView);
        }
    }

    private void cleanupRemoteParticipants() {
        // TODO - Implement slots
//        for slot:Slot in self.slots {
//            layout!.remove(slot:slot)
//            slot.disconnect()
//        }
    }

    private void removeAndCheckStateBy(String userId){
        // TODO - Implement slots
//        if let slot:Slot = self.getSlotBy(userId: userId) {
//            self.layout!.remove(slot: slot) {
//                void in
//
//                slot.disconnect()
//
//                let users:[String] = self.getUserIdsFromSlots()
//
//                if users.count == 0 {
//                    self.disconnect()
//                }
//            }
//        }
    }

    private void preConnect(String id) {
//      self.appDelegate.loungeViewController?.wallTabs.tabItemJumpTo("Video Chat") TODO ?
        model.join(id).addOnCompleteListener(new OnCompleteListener<VideoChatItem>() {
            @Override
            public void onComplete(@NonNull Task<VideoChatItem> task) {
                if(task.isSuccessful()){
                    roomId = task.getResult().getId();
                    connect();
                }
            }
        });

    }



    private void clearListeners(){
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
        Log.d(TAG,"Connected to room "+ room.getName() + " as "+ room.getLocalParticipant().getIdentity());
        if(room.getParticipants().size()>0){
            for(Map.Entry<String,Participant> remoteParticipant : room.getParticipants().entrySet()){
                // TODO - Implement slots
//                // You started the call, but other people have managed to connect before you, so we already
//                // have a slot for them
//                if let slot:Slot = self.getSlotBy(userId: remoteParticipant.identity) {
//                    slot.participant = remoteParticipant
//                    // Otherwise, create a new free slot if one is avaiable
//                } else if let slot:Slot = self.getNextFreeSlot() {
//                    slot.participant = remoteParticipant
//                    layout!.add(slot: slot)
//                } else {
//                    print("No free slots left!")
//                    break
//                }
            }
        }
        addUsersToView(model.getInvitedUsers());
        setViewState(true,false);
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
        Log.d(TAG,"Room "+ room.getName() + ", Participant "+ participant.getIdentity());

        // TODO - Implement slots
//        if let slot:Slot = self.getSlotBy(userId: participant.identity) {
//            // You started the call and the user has connected in response
//            slot.participant = participant
//        } else if let slot:Slot = self.getNextFreeSlot() {
//            // Maybe someone else added a new user and they've joined
//            slot.participant = participant
//            layout!.add(slot: slot)
//        }  else {
//            print("No free slots left!")
//        }
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
}
