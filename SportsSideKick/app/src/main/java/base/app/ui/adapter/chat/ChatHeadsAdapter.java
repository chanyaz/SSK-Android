package base.app.ui.adapter.chat;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import base.app.R;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.ui.fragment.popup.CreateChatFragment;
import base.app.data.Model;
import base.app.data.im.ChatInfo;
import base.app.data.im.event.ChatNotificationsEvent;
import base.app.util.ui.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Filip on 12/14/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 * <p>
 * Chat List Adapter for use in chat fragment
 */

public class ChatHeadsAdapter extends RecyclerView.Adapter<ChatHeadsAdapter.ViewHolder> {
    private static final int VIEW_TYPE_FOOTER = 1;
    private static final int VIEW_TYPE_CELL = 2;
    private static final String TAG = "Chat Heads Adapter";

    // Start with first item selected
    private int focusedItem = 0;

    public List<ChatInfo> getValues() {
        return values;
    }

    public ChatHeadsAdapter setValues(List<ChatInfo> values) {
        this.values = values;
        return this;
    }

    private List<ChatInfo> values;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        @Nullable
        @BindView(R.id.chat_head_image_view)
        CircleImageView imageView;
        @Nullable
        @BindView(R.id.notification_icon)
        ImageView notificationView;
        @Nullable
        @BindView(R.id.selected)
        View selectedRingView;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }
    Context context;
    int selectedChatColor;

    public ChatHeadsAdapter(Context context) {
        values = new ArrayList<>();
        this.context = context;
        if(context!=null){
            selectedChatColor = ContextCompat.getColor(context,(R.color.colorAccent));
        }
    }


    @Override
    public int getItemViewType(int position) {
        return (position == values.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }

    @Override
    public ChatHeadsAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final ViewHolder viewHolder;
        if (viewType == VIEW_TYPE_CELL) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_head_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(values.size()>0){
                        selectChat(values.get(viewHolder.getLayoutPosition()), true);
                    }
                }
            });
            return viewHolder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_chat_button, parent, false);
            viewHolder = new ViewHolder(view);
            view.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(Model.getInstance().isRealUser()) {
                                EventBus.getDefault().post(new FragmentEvent(CreateChatFragment.class));
                            }
                        }
                    });
            return viewHolder;
        }
    }

    public void selectChat(ChatInfo chatInfo, boolean emitEvent){
        notifyItemChanged(focusedItem);  // notify previous item!
        int position = 0;
        for(ChatInfo chat : values){
            if(chat.getChatId().equals(chatInfo.getChatId())){
               break;
            }
            position++;
        }
        focusedItem = position;
        notifyItemChanged(focusedItem); // notify new item
        if(emitEvent){
            EventBus.getDefault().post(new ChatNotificationsEvent(chatInfo, ChatNotificationsEvent.Key.SET_CURRENT_CHAT));
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position < values.size()) { // don't take the last element!
            final ChatInfo info = values.get(position);

            if (holder.imageView != null) {
                ImageLoader.displayImage(info.getChatAvatarUrl(), holder.imageView, null);
            }
            holder.view.setTag(position);
            int unreadMessageCount = info.unreadMessageCount();
            //Log.d(TAG, " *** Unread Message Count for chat" + info.getName() + " : " + unreadMessageCount);

            if (holder.notificationView != null) {
                if (unreadMessageCount > 0) {
                    holder.notificationView.setVisibility(View.VISIBLE);
                } else {
                    holder.notificationView.setVisibility(View.GONE);
                }
            }
            if (focusedItem == position) {
                holder.imageView.setBorderColor(selectedChatColor);
            } else {
                holder.imageView.setBorderColor(Color.TRANSPARENT);
            }

        }
    }

    @Override
    public long getItemId(int position) {
        if (getItemViewType(position) == VIEW_TYPE_CELL) {
            return values.get(position).getChatId().hashCode();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return values.size() + 1;
    }
}