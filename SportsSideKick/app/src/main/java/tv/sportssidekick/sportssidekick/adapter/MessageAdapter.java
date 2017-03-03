package tv.sportssidekick.sportssidekick.adapter;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.UserInfo;
import tv.sportssidekick.sportssidekick.model.im.ChatInfo;
import tv.sportssidekick.sportssidekick.model.im.ImsMessage;
import tv.sportssidekick.sportssidekick.service.FullScreenImageEvent;
import tv.sportssidekick.sportssidekick.service.PlayVideoEvent;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Filip on 12/14/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 *
 * Message Adapter for use in chat fragment
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final String TAG = "Message Adapter";
    private ChatInfo chatInfo;
    private Context context;
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

        ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }


    public MessageAdapter(Context context, ChatInfo chatInfo) {
        this.chatInfo = chatInfo;
        this.context = context;
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        DisplayImageOptions imageOptions = Utility.imageOptionsImageLoader();
        ImsMessage message = chatInfo.getMessages().get(position);

        chatInfo.markMessageAsRead(message);

        if(holder.getItemViewType() == VIEW_TYPE_MESSAGE_OTHER_USERS){
            UserInfo info = Model.getInstance().getCachedUserInfoById(message.getSenderId());
            String senderImageUrl = info.getCircularAvatarUrl();
            ImageLoader.getInstance().displayImage(senderImageUrl,holder.senderImageView,imageOptions);
            holder.senderTextView.setText(info.getNicName());
        }
        holder.timeTextView.setText(message.getTimeAgo());

        final String imageUrl = message.getImageUrl();
        String videoUrl = message.getVidUrl();
        if(imageUrl!=null){
            holder.textView.setVisibility(View.GONE);
            if(imageUrl.contains("jpg") || imageUrl.contains("png")){
                holder.contentImage.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(imageUrl,holder.contentImage,imageOptions);
                if(videoUrl!=null){ // its video, show play button and prepare player
                    holder.playButton.setVisibility(View.VISIBLE);
                    final String fVideoUrl = videoUrl;
                    holder.contentImage.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    EventBus.getDefault().post(new PlayVideoEvent(fVideoUrl));
                                }
                            });
                } else {
                    holder.playButton.setVisibility(View.GONE);
                    holder.contentImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            EventBus.getDefault().post(new FullScreenImageEvent(imageUrl));
                        }
                    });
                }
            } else { // its audio file
                holder.playButton.setVisibility(View.VISIBLE);
                holder.contentImage.setVisibility(View.GONE);
                final ImageView contentView = holder.playButton;
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setupAudioPlayButton(imageUrl,contentView);
                    }
                });
            }
        } else {
            holder.playButton.setVisibility(View.GONE);
            holder.contentImage.setVisibility(View.GONE);
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText(message.getText());
        }
    }


    private void setupAudioPlayButton(String url, final ImageView button){
        try {
            MediaPlayer player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(url);
            player.prepare();
            player.start();
            button.setImageResource(R.drawable.pause_button_icon);

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    button.setImageResource(R.drawable.play_button_icon);
                }
            });
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public long getItemId(int i) {
        return chatInfo.getMessages().get(i).getTimestampEpoch();
    }

    @Override
    public int getItemCount() {
        List messages = chatInfo.getMessages();
        if(messages!=null){
            return messages.size();
        } else {
            Log.d(TAG, "List of messages is null for chat: " + chatInfo.getChatId());
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        ImsMessage message = chatInfo.getMessages().get(position);
        UserInfo info = Model.getInstance().getUserInfo();
        if(info.getUserId().equals(message.getSenderId())){
           return VIEW_TYPE_MESSAGE_THIS_USER;
        } else {
            return VIEW_TYPE_MESSAGE_OTHER_USERS;
        }
    }

}
