package tv.sportssidekick.sportssidekick.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.model.im.ChatInfo;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Filip on 12/14/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 *
 * Chat List Adapter for use in chat fragment
 */


public class ChatMembersAdapter extends RecyclerView.Adapter<ChatMembersAdapter.ViewHolder> {
    private static final String TAG = "Chat Members Adapter";

    private Context context;
    private ChatInfo chatInfo;
    List<UserInfo> membersList;

    public void setChatInfo(ChatInfo chatInfo) {
        this.chatInfo = chatInfo;
    }

    private void reloadChatMembers(){
        membersList = Model.getInstance().getCachedUserInfoById(chatInfo.getUsersIds());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        @Nullable @BindView(R.id.profile_image) ImageView image;
        @Nullable @BindView(R.id.remove_icon) ImageView removeIcon;
        @Nullable @BindView(R.id.caption) TextView name;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public ChatMembersAdapter(Context context) {
        this.context = context;
    }


    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ChatMembersAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
       final ViewHolder viewHolder;
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_member_item, parent, false);
        viewHolder = new ViewHolder(view);
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getLayoutPosition();
                updateUser(position);
                notifyItemChanged(position);
            }
        });
        viewHolder.removeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getLayoutPosition();
                blockUser(position);
                notifyItemChanged(position);
            }
        });
        return  viewHolder;
    }

    private void updateUser(int position){
        UserInfo info = values.get(position);
        if(isMemberOfChat(info)) {
            // remove from chat
            chatInfo.removeUserFromChat(info.getUserId());
        } else {
            // add to chat
            chatInfo.addUser(info);
        }
    }

    private void blockUser(int position){
        UserInfo info = values.get(position);
        if(isBlocked(info)) {
            chatInfo.unblockUserInThisChat(info.getUserId());
            // remove from chat
        } else {
            // block
            chatInfo.blockUserFromJoinningThisChat(info.getUserId());

        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final UserInfo info = values.get(position);
        DisplayImageOptions imageOptions = Utility.imageOptionsImageLoader();
        ImageLoader.getInstance().displayImage(info.getCircularAvatarUrl(),holder.image,imageOptions);
        holder.name.setText(info.getFirstName()  + " " + info.getLastName());

        // TODO Set user appearance
        if(isMemberOfChat(info)){ // this is member of a chat
            holder.image.setAlpha(1.0f);
            holder.name.setAlpha(1.0f);
        } else { // this one is not yet member of a chat
            holder.image.setAlpha(0.5f);
            holder.name.setAlpha(0.5f);
        }
//        if(selectedValues.contains(info)){
//            holder.image.setColorFilter(ContextCompat.getColor(context,R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
//        } else {
//            holder.image.setColorFilter(null);
//        }

        holder.view.setTag(position);
    }

    private boolean isMemberOfChat(UserInfo info){
       return chatInfo.getUsersIds().contains(info.getUserId());
    }

    private boolean isBlocked(UserInfo info){
        return chatInfo.isUserBlockedFromThisChat(info.getUserId());
    }

    @Override
    public int getItemCount() {
        return values.size();
    }


    private static final Comparator<UserInfo> ALPHABETICAL_COMPARATOR = new Comparator<UserInfo>() {
        @Override
        public int compare(UserInfo a, UserInfo b) {
            return (a.getFirstName()+a.getLastName()+a.getNicName()).compareTo
                (b.getFirstName()+b.getLastName()+b.getNicName());
        }
    };

    private final SortedList.Callback<UserInfo> callback = new SortedList.Callback<UserInfo>() {

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public int compare(UserInfo a, UserInfo b) {
            return ALPHABETICAL_COMPARATOR.compare(a, b);
        }

        @Override
        public boolean areContentsTheSame(UserInfo oldItem, UserInfo newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(UserInfo item1, UserInfo item2) {
            return item1.getUserId() == item2.getUserId();
        }
    };

    private final SortedList<UserInfo> values = new SortedList<>(UserInfo.class, callback);

    public void add(UserInfo model) {
        values.add(model);
    }

    public void remove(UserInfo model) {
        values.remove(model);
    }

    public void add(List<UserInfo> models) {
        values.addAll(models);
    }

    public void remove(List<UserInfo> models) {
        values.beginBatchedUpdates();
        for (UserInfo model : models) {
            values.remove(model);
        }
        values.endBatchedUpdates();
    }

    public void replaceAll(List<UserInfo> models) {
        values.beginBatchedUpdates();
        for (int i = values.size() - 1; i >= 0; i--) {
            final UserInfo model = values.get(i);
            if (!models.contains(model)) {
                values.remove(model);
            }
        }
        values.addAll(models);
        values.endBatchedUpdates();
    }
}