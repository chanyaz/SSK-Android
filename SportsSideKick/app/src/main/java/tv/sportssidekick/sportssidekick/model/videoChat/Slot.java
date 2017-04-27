package tv.sportssidekick.sportssidekick.model.videoChat;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.twilio.video.AudioTrack;
import com.twilio.video.Media;
import com.twilio.video.Participant;
import com.twilio.video.VideoTrack;
import com.twilio.video.VideoView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;

/**
 * Created by Filip on 4/2/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class Slot implements Media.Listener{



    View view;

    @BindView(R.id.video_view)
    VideoView video;
    @BindView(R.id.progress_bar)
    public View spinner;
    @BindView(R.id.user_name_text_view)
    public TextView label;
    @BindView(R.id.disabled_video_icon)
    public View disabled;


    private Participant participant;
    private String userId;
    private UserInfo userInfo;
    private VideoTrack videoTrack;

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
        setSpinner(this.participant!=null);
        if(this.participant==null){
            setUserId(null);
        } else {
            participant.getMedia().setListener(this);
            if(userId!=null && userId.equals(this.participant.getIdentity())){
                return;
            }
            setUserId(this.participant.getIdentity());
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        if(userId == null){
            setUserInfo(null);
            return;
        }
        Model.getInstance().getUserInfoById(userId).addOnCompleteListener(new OnCompleteListener<UserInfo>() {
            @Override
            public void onComplete(@NonNull Task<UserInfo> task) {
                if (task.isSuccessful()) {
                    setUserInfo(task.getResult());
                }
            }
        });
    }


    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        if(userInfo!=null){
            label.setText(userInfo.getNicName());
        } else {
            label.setText("...");
        }
    }


    public Slot(View view) {
        setView(view);
        view.setVisibility(View.GONE);
        ButterKnife.bind(this,view);
        setSpinner(true);
        setVideo(true);
    }

    private void setSpinner(boolean visible){
        spinner.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void disconnect(){
        if (this.participant != null){
            List<VideoTrack> tracks = this.participant.getMedia().getVideoTracks();
            if ( tracks!=null && tracks.size() > 0 ){
                for(VideoTrack track: tracks) {
                        track.removeRenderer(this.video);
                        participant.getMedia().setListener(null);
                }
            }
        }
        setParticipant(null);
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void reset(){
        setSpinner(true);
        setVideo(true);
    }

    public void setVideo(boolean visible){
        video.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        disabled.setVisibility(visible ? View.INVISIBLE : View.VISIBLE);
        spinner.setVisibility(visible ? View.GONE : View.GONE);
    }

    @Override
    public void onAudioTrackAdded(Media media, AudioTrack audioTrack) { }

    @Override
    public void onAudioTrackRemoved(Media media, AudioTrack audioTrack) { }

    @Override
    public void onVideoTrackAdded(Media media, VideoTrack videoTrack) {
        if(this.videoTrack !=null){
            videoTrack.removeRenderer(video);
            videoTrack = null;
        }
        this.videoTrack = videoTrack;
        if (videoTrack != null) {
            setVideo(true);
            videoTrack.addRenderer(video);
        }
    }

    @Override
    public void onVideoTrackRemoved(Media media, VideoTrack videoTrack) {
        if(this.videoTrack!=null){
            if(videoTrack.getTrackId().equals(this.videoTrack.getTrackId())){
                videoTrack.removeRenderer(video);
                this.videoTrack = null;
            }
        }
        setVideo(false);
    }

    @Override
    public void onAudioTrackEnabled(Media media, AudioTrack audioTrack) { }

    @Override
    public void onAudioTrackDisabled(Media media, AudioTrack audioTrack) { }

    @Override
    public void onVideoTrackEnabled(Media media, VideoTrack videoTrack) {
        setVideo(true);
    }

    @Override
    public void onVideoTrackDisabled(Media media, VideoTrack videoTrack) {
        setVideo(false);
    }
}
