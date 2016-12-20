package tv.sportssidekick.sportssidekick.adapter;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

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

    private ChatInfo chatInfo;
    private Context context;
    private static LayoutInflater inflater = null;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        @BindView(R.id.text) public TextView textView;
        @BindView(R.id.timestamp) public TextView timeTextView;
        @BindView(R.id.sender) public TextView senderTextView;
        @BindView(R.id.profile_image) public CircleImageView senderImageView;
        @BindView(R.id.content_image) public ImageView contentImage;
        @BindView(R.id.play_button) public ImageView playButton;

        public ViewHolder(View v) {
            super(v);
            view = v;
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
        View view = inflater.inflate(R.layout.message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DisplayImageOptions imageOptions = Utility.imageOptionsImageLoader();
        ImsMessage message = chatInfo.getMessages().get(position);

        UserInfo info = Model.getInstance().getCachedUserInfoById(message.getSenderId());
        String senderImageUrl = info.getCircularAvatarUrl();
        ImageLoader.getInstance().displayImage(senderImageUrl,holder.senderImageView,imageOptions);
        holder.senderTextView.setText(info.getNicName());
        String timeago = DateUtils.getRelativeTimeSpanString(Long.valueOf(message.getTimestamp().replace(".",""))/1000).toString();
        holder.timeTextView.setText(timeago);

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
                    holder.contentImage.setOnClickListener(new View.OnClickListener() {
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
            button.setImageResource(R.mipmap.pause_button_icon);

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    button.setImageResource(R.mipmap.play_button_icon);
                }
            });
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return chatInfo.getMessages().size();
    }

}
