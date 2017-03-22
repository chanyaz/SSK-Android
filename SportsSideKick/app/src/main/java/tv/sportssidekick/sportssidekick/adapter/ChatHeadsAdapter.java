package tv.sportssidekick.sportssidekick.adapter;

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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.fragment.popup.CreateChatFragment;
import tv.sportssidekick.sportssidekick.fragment.popup.ManageChatFragment;
import tv.sportssidekick.sportssidekick.model.im.ChatInfo;
import tv.sportssidekick.sportssidekick.service.UIEvent;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Filip on 12/14/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 *
 * Chat List Adapter for use in chat fragment
 */


public class ChatHeadsAdapter extends RecyclerView.Adapter<ChatHeadsAdapter.ViewHolder> {
    private static final int VIEW_TYPE_FOOTER = 1;
    private static final int VIEW_TYPE_CELL = 2;
    private static final String TAG = "Chat Heads Adapter";

    // Start with first item selected
    private int focusedItem = 0;
    private int focusedItemToEdit = -1;

    private boolean isInGrid;
    public void setInGrid(boolean inGrid) {
        isInGrid = inGrid;
    }

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
        @Nullable @BindView(R.id.image) ImageView imageView;
        @Nullable @BindView(R.id.people_icon) ImageView peopleIcon;
        @Nullable @BindView(R.id.notification_icon) ImageView notificationView;
        @Nullable @BindView(R.id.selected) View selectedRingView;
        @Nullable @BindView(R.id.caption) TextView chatCaption;
        @Nullable @BindView(R.id.edit_button) TextView editButton;
        @Nullable @BindView(R.id.people_count_value) TextView userCount;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public ChatHeadsAdapter() {
        values = new ArrayList<>();
        isInGrid = false;
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
                    if(isInGrid){
                        if(focusedItemToEdit>-1 && focusedItemToEdit == focusedItem){
                            FragmentEvent fragmentEvent = new FragmentEvent(ManageChatFragment.class);
                            int position = viewHolder.getLayoutPosition();
                            fragmentEvent.setId(values.get(position).getChatId());
                            EventBus.getDefault().post(fragmentEvent);
                        } else {
                            focusedItemToEdit = viewHolder.getLayoutPosition();
                            notifyItemChanged(focusedItem); // notify new item
                        }
                    } else {
                        notifyItemChanged(focusedItem);  // notify previous item!
                        focusedItem = viewHolder.getLayoutPosition();
                        notifyItemChanged(focusedItem); // notify new item
                        EventBus.getDefault().post(new UIEvent(focusedItem));
                    }

                }
            });
            return  viewHolder;
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_chat_button, parent, false);
            viewHolder = new ViewHolder(view);
            view.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            EventBus.getDefault().post(new FragmentEvent(CreateChatFragment.class));
                        }
                    });
            return viewHolder;
        }
    }




    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position < values.size()) { // don't take the last element!
            final ChatInfo info = values.get(position);
            int size = 1;
            if(info.getUsersIds()!=null){
                size =  info.getUsersIds().size();
            } else {
                Log.d(TAG, "Have no chatUsers yet!");
            }
            holder.userCount.setText(String.valueOf(size));
            DisplayImageOptions imageOptions = Utility.imageOptionsImageLoader();
            ImageLoader.getInstance().displayImage(info.getChatAvatarUrl(),holder.imageView,imageOptions);
            holder.chatCaption.setText(info.getChatTitle());
            holder.view.setTag(position);

            int unreadMessageCount = info.unreadMessageCount();
            Log.d(TAG, " *** Unread Message Count for chat" + info.getName() + " : " + unreadMessageCount);

            if(unreadMessageCount>0){
                holder.notificationView.setVisibility(View.VISIBLE);
            } else {
                holder.notificationView.setVisibility(View.GONE);
            }

            if(focusedItem==position){
               holder.selectedRingView.setVisibility(View.VISIBLE);
            } else {
               holder.selectedRingView.setVisibility(View.GONE);
            }
            int inGridVisibility = View.GONE;
            if(isInGrid){
                inGridVisibility = View.VISIBLE;
            }
            holder.userCount.setVisibility(inGridVisibility);
            holder.peopleIcon.setVisibility(inGridVisibility);
        } else {
            if(isInGrid){
                holder.chatCaption.setVisibility(View.VISIBLE);
            } else {
                holder.chatCaption.setVisibility(View.GONE);
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