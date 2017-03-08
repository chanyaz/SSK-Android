package tv.sportssidekick.sportssidekick.fragment.instance;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twilio.video.AudioTrack;
import com.twilio.video.CameraCapturer;
import com.twilio.video.ConnectOptions;
import com.twilio.video.LocalAudioTrack;
import com.twilio.video.LocalMedia;
import com.twilio.video.LocalVideoTrack;
import com.twilio.video.Media;
import com.twilio.video.Participant;
import com.twilio.video.Room;
import com.twilio.video.TwilioException;
import com.twilio.video.VideoClient;
import com.twilio.video.VideoTrack;
import com.twilio.video.VideoView;

import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;

/**
 * Created by Djordje Krutil on 7.2.2017..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class VideoChatRoomFragment  extends BaseFragment {

    private static final String TWILIO_ACCESS_TOKEN = "TWILIO_ACCESS_TOKEN";
    private VideoClient videoClient;
    private Room room;
    private VideoView primaryVideoView;
    private VideoView thumbnailVideoView;
    private static final int CAMERA_MIC_PERMISSION_REQUEST_CODE = 1;
    private CameraCapturer cameraCapturer;
    private LocalMedia localMedia;
    private LocalAudioTrack localAudioTrack;
    private LocalVideoTrack localVideoTrack;
    private AudioManager audioManager;

    public VideoChatRoomFragment()
    {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_chat, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    // TODO: ADAPT TO DESIGN - EVERYTHING

    private void retrieveAccessTokenfromServer() {
//        Ion.with(this)
//                .load("https://ssk-vc-tokengen-development.herokuapp.com/")
//                .asJsonObject()
//                .setCallback(new FutureCallback<JsonObject>() {
//                    @Override
//                    public void onCompleted(Exception e, JsonObject result) {
//                        if (e == null) {
//                            String accessToken = result.get("token").getAsString();
//
//                            videoClient = new VideoClient(getContext(), accessToken);
//                        } else {
//                        //Error TODO: Handling error
//                        }
//                    }
//                });
    }

    private boolean checkPermissionForCameraAndMicrophone(){
//        int resultCamera = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
//        int resultMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
//        return resultCamera == PackageManager.PERMISSION_GRANTED &&
//                resultMic == PackageManager.PERMISSION_GRANTED;
        return true;
    }

    private void requestPermissionForCameraAndMicrophone(){

    }

    private void createLocalMedia() {
        localMedia = LocalMedia.create(getContext());

        // Share your microphone
        localAudioTrack = localMedia.addAudioTrack(true);

        // Share your camera
        cameraCapturer = new CameraCapturer(getContext(), CameraCapturer.CameraSource.FRONT_CAMERA);
        localVideoTrack = localMedia.addVideoTrack(true, cameraCapturer);
        primaryVideoView.setMirror(true);
        localVideoTrack.addRenderer(primaryVideoView);
    }


    private void createVideoClient() {
        videoClient = new VideoClient(getContext(), TWILIO_ACCESS_TOKEN);
    }

    private void connectToRoom(String roomName) {
        setAudioFocus(true);
        ConnectOptions connectOptions = new ConnectOptions.Builder()
                .roomName(roomName)
                .localMedia(localMedia)
                .build();
        room = videoClient.connect(connectOptions, roomListener());
        setDisconnectAction();
    }

    private void setDisconnectAction() {
//        connectActionFab.setImageDrawable(ContextCompat.getDrawable(this,
//                R.drawable.ic_call_end_white_24px));
//        connectActionFab.show();
//        connectActionFab.setOnClickListener(disconnectClickListener());
    }

    private void showConnectDialog() {
//        EditText roomEditText = new EditText(this);
//        alertDialog = Dialog.createConnectDialog(roomEditText,
//                connectClickListener(roomEditText), cancelConnectDialogClickListener(), this);
//        alertDialog.show();
    }

    private void addParticipant(Participant participant) {
        /*
         * This app only displays video for one additional participant per Room
         */
    }

    private void addParticipantVideo(VideoTrack videoTrack) {
        moveLocalVideoToThumbnailView();
        primaryVideoView.setMirror(false);
        videoTrack.addRenderer(primaryVideoView);
    }

    private void moveLocalVideoToThumbnailView() {
        if (thumbnailVideoView.getVisibility() == View.GONE) {
            thumbnailVideoView.setVisibility(View.VISIBLE);
            localVideoTrack.removeRenderer(primaryVideoView);
            localVideoTrack.addRenderer(thumbnailVideoView);
            thumbnailVideoView.setMirror(cameraCapturer.getCameraSource() ==
                    CameraCapturer.CameraSource.FRONT_CAMERA);
        }
    }

    private void removeParticipant(Participant participant) {
//        videoStatusTextView.setText("Participant "+participant.getIdentity()+ " left.");
//        if (!participant.getIdentity().equals(participantIdentity)) {
//            return;
//        }

        /*
         * Remove participant renderer
         */
        if (participant.getMedia().getVideoTracks().size() > 0) {
            removeParticipantVideo(participant.getMedia().getVideoTracks().get(0));
        }
        participant.getMedia().setListener(null);
        moveLocalVideoToPrimaryView();
    }

    private void removeParticipantVideo(VideoTrack videoTrack) {
        videoTrack.removeRenderer(primaryVideoView);
    }

    private void moveLocalVideoToPrimaryView() {
        if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
            localVideoTrack.removeRenderer(thumbnailVideoView);
            thumbnailVideoView.setVisibility(View.GONE);
            localVideoTrack.addRenderer(primaryVideoView);
            primaryVideoView.setMirror(cameraCapturer.getCameraSource() ==
                    CameraCapturer.CameraSource.FRONT_CAMERA);
        }
    }

    /*
 * Room events listener
 */
    private Room.Listener roomListener() {
        return new Room.Listener() {
            @Override
            public void onConnected(Room room) {
//                videoStatusTextView.setText("Connected to " + room.getName());
//                setTitle(room.getName());
//
//                for (Map.Entry<String, Participant> entry : room.getParticipants().entrySet()) {
//                    addParticipant(entry.getValue());
//                    break;
//                }
            }

            @Override
            public void onConnectFailure(Room room, TwilioException e) {
//                videoStatusTextView.setText("Failed to connect");
            }

            @Override
            public void onDisconnected(Room room, TwilioException e) {
//                videoStatusTextView.setText("Disconnected from " + room.getName());
//                VideoActivity.this.room = null;
//                setAudioFocus(false);
//                intializeUI();
                moveLocalVideoToPrimaryView();
            }

            @Override
            public void onParticipantConnected(Room room, Participant participant) {
                addParticipant(participant);

            }

            @Override
            public void onParticipantDisconnected(Room room, Participant participant) {
                removeParticipant(participant);
            }
        };
    }

    private Media.Listener mediaListener() {
        return new Media.Listener() {

            @Override
            public void onAudioTrackAdded(Media media, AudioTrack audioTrack) {
//                videoStatusTextView.setText("onAudioTrackAdded");
            }

            @Override
            public void onAudioTrackRemoved(Media media, AudioTrack audioTrack) {
//                videoStatusTextView.setText("onAudioTrackRemoved");
            }

            @Override
            public void onVideoTrackAdded(Media media, VideoTrack videoTrack) {
//                videoStatusTextView.setText("onVideoTrackAdded");
                addParticipantVideo(videoTrack);
            }

            @Override
            public void onVideoTrackRemoved(Media media, VideoTrack videoTrack) {
//                videoStatusTextView.setText("onVideoTrackRemoved");
                removeParticipantVideo(videoTrack);
            }

            @Override
            public void onAudioTrackEnabled(Media media, AudioTrack audioTrack) {

            }

            @Override
            public void onAudioTrackDisabled(Media media, AudioTrack audioTrack) {

            }

            @Override
            public void onVideoTrackEnabled(Media media, VideoTrack videoTrack) {

            }

            @Override
            public void onVideoTrackDisabled(Media media, VideoTrack videoTrack) {

            }
        };
    }

    private void setAudioFocus(boolean focus) {
//        if (focus) {
//            previousAudioMode = audioManager.getMode();
//            // Request audio focus before making any device switch.
//            audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL,
//                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
//            /*
//             * Use MODE_IN_COMMUNICATION as the default audio mode. It is required
//             * to be in this mode when playout and/or recording starts for the best
//             * possible VoIP performance. Some devices have difficulties with
//             * speaker mode if this is not set.
//             */
//            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
//        } else {
//            audioManager.setMode(previousAudioMode);
//            audioManager.abandonAudioFocus(null);
//        }
    }
}
