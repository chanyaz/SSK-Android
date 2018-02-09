package base.app.ui.adapter.chat;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import base.app.R;
import base.app.data.DateUtils;
import base.app.data.GSConstants;
import base.app.data.Model;
import base.app.data.Translator;
import base.app.data.im.ChatInfo;
import base.app.data.im.ImsMessage;
import base.app.data.user.UserInfo;
import base.app.util.events.chat.FullScreenImageEvent;
import base.app.util.events.chat.MessageSelectedEvent;
import base.app.util.events.stream.PlayVideoEvent;
import base.app.util.ui.ImageLoader;
import base.app.util.ui.TranslationView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static base.app.data.GSConstants.UPLOADING;
import static base.app.ui.fragment.popup.ProfileFragment.isAutoTranslateEnabled;

/**
 * Created by Filip on 12/14/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 *
 * Message Adapter for use in chat fragment
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final String TAG = "Message Adapter";

    private TranslationView translationView;
    private List<ImsMessage> translatedMessages;
    private Map<ImsMessage, Boolean> translatedMap = new HashMap<>();

    public void setChatInfo(ChatInfo chatInfo) {
        translatedMessages.clear();
        this.chatInfo = chatInfo;
    }

    private ChatInfo chatInfo;
    private LayoutInflater inflater = null;
    private static final int VIEW_TYPE_MESSAGE_THIS_USER = 0;
    private static final int VIEW_TYPE_MESSAGE_OTHER_USERS = 1;


    public void setTranslationView(TranslationView translationView) {
        this.translationView = translationView;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        @BindView(R.id.text) TextView textView;
        @BindView(R.id.timestamp) TextView timeTextView;
        @BindView(R.id.sender) TextView senderTextView;
        @Nullable @BindView(R.id.profile_image) ImageView senderImageView;
        @BindView(R.id.contentImage) ImageView contentImage;
        @BindView(R.id.play_button) ImageView playButton;
        @BindView(R.id.content_container) View contentContainer;
        @Nullable @BindView(R.id.progressBar) View progressBar;
        ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }

    public MessageAdapter(Context context) {
        translatedMessages = new ArrayList<>();
        audioPlayer = new MediaPlayer();
        if (context != null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ImsMessage message = chatInfo.getMessages().get(holder.getAdapterPosition());
        if(!message.getReadFlag()){
            chatInfo.markMessageAsRead(message);
        }

        if(holder.getItemViewType() == VIEW_TYPE_MESSAGE_OTHER_USERS) {
            setupAvatarFromRemoteUser(message, holder);
        }

        holder.timeTextView.setText(DateUtils.getTimeAgo(message.getTimestampEpoch()));

        final String imageUrl = message.getImageUrl();
        final String videoUrl = message.getVidUrl();

        holder.view.setTag(message.getId());

        if(message.getType()!=null){
            switch (message.getType()){
                case GSConstants.UPLOAD_TYPE_IMAGE:
                    holder.textView.setVisibility(View.GONE);
                    holder.contentImage.setVisibility(View.VISIBLE);
                    ImageLoader.displayImage(imageUrl,holder.contentImage, null);
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
                    ImageLoader.displayImage(imageUrl,holder.contentImage);
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
                    holder.view.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {
                            String messageId = (String) view.getTag();

                            TaskCompletionSource<ImsMessage> source = new TaskCompletionSource<>();
                            source.getTask().addOnCompleteListener(new OnCompleteListener<ImsMessage>() {
                                @Override
                                public void onComplete(@NonNull Task<ImsMessage> task) {
                                if(task.isSuccessful()){
                                    ImsMessage translatedMessage = task.getResult();
                                    updateWithTranslatedMessage(translatedMessage,holder.getAdapterPosition());
                                }
                                }
                            });
                            translationView.showTranslationPopup(holder.contentContainer,messageId, source, TranslationView.TranslationType.TRANSLATE_IMS);
                        }
                    });

                    if(holder.getItemViewType() == VIEW_TYPE_MESSAGE_THIS_USER) {
                        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                EventBus.getDefault().post(new MessageSelectedEvent(message));
                                return true;
                            }
                        });
                    }

                    holder.playButton.setVisibility(View.GONE);
                    holder.contentImage.setVisibility(View.GONE);
                    holder.textView.setVisibility(View.VISIBLE);

                    String translatedValue = null;
                    for(ImsMessage translated : translatedMessages){
                        if(message.getId().equals(translated.getId())){
                            translatedValue = translated.getText();
                        }
                    }
                    if(translatedValue!=null){
                        holder.textView.setText(translatedValue);
                    } else {
                        holder.textView.setText(message.getText());
                    }

                    if (isAutoTranslateEnabled() && translatedValue == null) {
                        String messageId = (String) holder.view.getTag();

                        TaskCompletionSource<ImsMessage> source = new TaskCompletionSource<>();
                        source.getTask().addOnCompleteListener(new OnCompleteListener<ImsMessage>() {
                            @Override
                            public void onComplete(@NonNull Task<ImsMessage> task) {
                                if(task.isSuccessful()){
                                    int position = holder.getAdapterPosition();
                                    ImsMessage translatedMessage = task.getResult();
                                    translatedMap.put(translatedMessage, true);
                                    updateWithTranslatedMessage(translatedMessage,position);
                                }
                            }
                        });
                        Translator.getInstance().translateMessage(messageId,source);
                    }
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
                    ImageLoader.displayRoundImage(senderImageUrl,holder.senderImageView);
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
        }
        return VIEW_TYPE_MESSAGE_OTHER_USERS;
    }

    public void updateWithTranslatedMessage(ImsMessage message, int position){
        translatedMessages.add(message);
        notifyItemChanged(position);
    }
}
