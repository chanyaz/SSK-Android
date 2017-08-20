package base.app.adapter;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import base.app.R;
import base.app.events.FullScreenImageEvent;
import base.app.events.PlayVideoEvent;
import base.app.model.GSConstants;
import base.app.model.Model;
import base.app.model.im.ChatInfo;
import base.app.model.im.ImsMessage;
import base.app.model.user.UserInfo;
import base.app.util.Utility;
import base.app.util.ui.ThemeManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static base.app.model.GSConstants.UPLOADING;

/**
 * Created by Filip on 12/14/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 *
 * Message Adapter for use in chat fragment
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final String TAG = "Message Adapter";

    public void setChatInfo(ChatInfo chatInfo) {
        this.chatInfo = chatInfo;
    }

    private ChatInfo chatInfo;
    private LayoutInflater inflater = null;
    private static final int VIEW_TYPE_MESSAGE_THIS_USER = 0;
    private static final int VIEW_TYPE_MESSAGE_OTHER_USERS = 1;




    static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        @BindView(R.id.text) TextView textView;
        @BindView(R.id.timestamp) TextView timeTextView;
        @BindView(R.id.sender) TextView senderTextView;
        @Nullable @BindView(R.id.profile_image) CircleImageView senderImageView;
        @BindView(R.id.content_image) ImageView contentImage;
        @BindView(R.id.play_button) ImageView playButton;
        @Nullable @BindView(R.id.progress_bar) AVLoadingIndicatorView progressBar;
        ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }

    private int textColor;
    private int lightTextColorLeft;
    private int lightTextColorRight;
    public MessageAdapter(Context context) {
        audioPlayer = new MediaPlayer();
        if (context != null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            textColor = ContextCompat.getColor(context,R.color.light_chat_content_right_color);
            lightTextColorLeft = ContextCompat.getColor(context,R.color.light_chat_content_left_color);
            lightTextColorRight = ContextCompat.getColor(context,R.color.light_chat_content_right_color);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_THIS_USER) {
            view = inflater.inflate(R.layout.message_item_user, parent, false);
        } else {
            view = inflater.inflate(R.layout.message_item, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImsMessage message = chatInfo.getMessages().get(position);
        if(!message.getReadFlag()){
            chatInfo.markMessageAsRead(message);
        }
        int selectedTextColor;
        if(holder.getItemViewType() == VIEW_TYPE_MESSAGE_OTHER_USERS){
            setupAvatarFromRemoteUser(message,holder);
            selectedTextColor = lightTextColorLeft;
        }else {
            selectedTextColor = lightTextColorRight;
        }

        holder.textView.setTextColor(ThemeManager.getInstance().isLightTheme() ?  selectedTextColor : textColor );
        holder.timeTextView.setText(message.getTimeAgo());

        final String imageUrl = message.getImageUrl();
        final String videoUrl = message.getVidUrl();

        if(message.getType()!=null){
            switch (message.getType()){
                case GSConstants.UPLOAD_TYPE_IMAGE:
                    holder.textView.setVisibility(View.GONE);
                    holder.contentImage.setVisibility(View.VISIBLE);
                    ImageLoader.getInstance().displayImage(imageUrl,holder.contentImage,Utility.imageOptionsImageLoader());
                    holder.playButton.setVisibility(View.GONE);
                    holder.contentImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            EventBus.getDefault().post(new FullScreenImageEvent(imageUrl));
                        }
                    });
                    break;
                case GSConstants.UPLOAD_TYPE_VIDEO:
                    holder.textView.setVisibility(View.GONE);
                    holder.contentImage.setVisibility(View.VISIBLE);
                    ImageLoader.getInstance().displayImage(imageUrl,holder.contentImage,Utility.imageOptionsImageLoader());
                    holder.playButton.setVisibility(View.VISIBLE);
                    holder.contentImage.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    EventBus.getDefault().post(new PlayVideoEvent(videoUrl));
                                }
                            });
                    holder.playButton.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    EventBus.getDefault().post(new PlayVideoEvent(videoUrl));
                                }
                            });
                    break;
                case GSConstants.UPLOAD_TYPE_AUDIO:
                    holder.textView.setVisibility(View.GONE);
                    holder.playButton.setVisibility(View.VISIBLE);
                    holder.contentImage.setVisibility(View.GONE);
                    final ImageView contentView = holder.playButton;
                    holder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setupAudioPlayButton(imageUrl,contentView);
                        }
                    });
                    break;
                case GSConstants.UPLOAD_TYPE_TEXT:
                    holder.playButton.setVisibility(View.GONE);
                    holder.contentImage.setVisibility(View.GONE);
                    holder.textView.setVisibility(View.VISIBLE);
                    holder.textView.setText(message.getText());
                    break;
            }
        } else {
            Log.e(TAG,"Message type is null: " + message.getId());
        }

        if (holder.progressBar != null) {
            if(UPLOADING.equals(message.getUploadStatus())){
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.playButton.setVisibility(View.GONE);
            } else {
                holder.progressBar.setVisibility(View.GONE);
            }
        }
    }


    private void setupAvatarFromRemoteUser(ImsMessage message,final ViewHolder holder){
        holder.senderTextView.setText("New User");
        Model.getInstance().getUserInfoById(message.getSenderId()).addOnSuccessListener(new OnSuccessListener<UserInfo>() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                String senderImageUrl=null;
                String nicName="New User";
                if(userInfo!=null){
                    senderImageUrl = userInfo.getCircularAvatarUrl();
                    nicName = userInfo.getNicName();
                }
                if (holder.senderImageView != null) {
                    ImageLoader.getInstance().displayImage(senderImageUrl,holder.senderImageView,Utility.getImageOptionsForUsers());
                }
                holder.senderTextView.setText(nicName);
            }
        });
    }

    private MediaPlayer audioPlayer;
    private void setupAudioPlayButton(String url, final ImageView button){
        try {
            if(audioPlayer!=null){
                if(audioPlayer.isPlaying()){
                    audioPlayer.stop();
                    audioPlayer.release();
                }
                audioPlayer = new MediaPlayer();
                audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                audioPlayer.setDataSource(url);
                audioPlayer.prepareAsync();
                audioPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        audioPlayer.start();
                        button.setClickable(false);
                    }
                });
                button.setImageResource(R.drawable.pause_button_icon);
                audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        button.setImageResource(R.drawable.play_button_icon);
                        button.setClickable(true);
                    }
                });
            }

        } catch (Exception e) {
           Log.e(TAG,"Audio file can't be played!");
        }
    }

    @Override
    public long getItemId(int i) {
        if(chatInfo!=null) {
            return chatInfo.getMessages().get(i).getTimestampEpoch();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        if(chatInfo!=null){
            List messages = chatInfo.getMessages();
            if(messages!=null){
                return messages.size();
            } else {
                Log.d(TAG, "List of messages is null for chat: " + chatInfo.getChatId());
                return 0;
            }
        } else {
            return 0;
        }

    }

    @Override
    public int getItemViewType(int position) {
        UserInfo info = Model.getInstance().getUserInfo();
        if(chatInfo!=null && info!=null){
            ImsMessage message = chatInfo.getMessages().get(position);
            String userId = info.getUserId();
            if(userId!=null){
                if(info.getUserId().equals(message.getSenderId())) {
                    return VIEW_TYPE_MESSAGE_THIS_USER;
                }
            }
            return VIEW_TYPE_MESSAGE_OTHER_USERS;
        }
        return VIEW_TYPE_MESSAGE_OTHER_USERS;
    }

}
