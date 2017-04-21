package tv.sportssidekick.sportssidekick.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.model.im.ChatInfo;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Nemanja Jovanovic on 10/04/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class FriendsInChatAdapter extends RecyclerView.Adapter<FriendsInChatAdapter.ViewHolder> {
    private static final int VIEW_TYPE_FOOTER = 1;
    private static final int VIEW_TYPE_CELL = 2;
    private static final String TAG = "FriendsInChatAdapter";

    Context context;
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        @Nullable
        @BindView(R.id.row_friends_in_chat_image)
        CircleImageView rowImage;
        @Nullable
        @BindView(R.id.row_friends_in_chat_notification_count)
        TextView rowFriendsCount;
        @Nullable
        @BindView(R.id.row_friends_in_chat_name)
        TextView rowName;
        @Nullable
        @BindView(R.id.row_friends_in_chat_notification_container)
        RelativeLayout rowFriendsCountContainer;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    private List<ChatInfo> values;
    private int cellSize;
    private int bubbleSize;

    public FriendsInChatAdapter(Context context) {
        values = new ArrayList<>();
        this.context = context;
        cellSize =  (int) (Utility.getDisplayHeight(context)*0.14);
        bubbleSize = (int) (cellSize/3);
    }
    public void setValues(List<ChatInfo> values) {
        this.values = values;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == values.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }



    @Override
    public FriendsInChatAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final ViewHolder viewHolder;
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_friends_in_chat, parent, false);
        view.getLayoutParams().height = cellSize;
        view.getLayoutParams().width = cellSize;
        viewHolder = new ViewHolder(view);



        return viewHolder;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        DisplayImageOptions imageOptions = Utility.getImageOptionsForUsers();
        ChatInfo chat = values.get(position);
        if (holder.rowImage != null) {
            ImageLoader.getInstance().displayImage(chat.getChatAvatarUrl(), holder.rowImage, imageOptions);
        }
        if (holder.rowName != null) {
            holder.rowName.setText(chat.getName());
        }

        if(holder.rowFriendsCountContainer!=null){
            holder.rowFriendsCountContainer.getLayoutParams().height = bubbleSize;
            holder.rowFriendsCountContainer.getLayoutParams().width = bubbleSize;
            holder.rowFriendsCount.setVisibility(View.VISIBLE);
        }else {
            holder.rowFriendsCount.setVisibility(View.INVISIBLE);
        }

        if (holder.rowFriendsCount != null) {
            int count = 0;
            if(chat.getUsersIds()!=null){
                count = chat.getUsersIds().size();
            }
            holder.rowFriendsCount.setText(String.valueOf(count));
        }
    }

    @Override
    public long getItemId(int position) {
        return values.size();
    }

    @Override
    public int getItemCount() {
        return values.size();
    }
}