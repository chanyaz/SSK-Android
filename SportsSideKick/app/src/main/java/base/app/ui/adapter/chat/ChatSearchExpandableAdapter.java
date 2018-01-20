package base.app.ui.adapter.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.List;

import base.app.R;
import base.app.data.user.User;
import base.app.ui.fragment.popup.JoinChatFragment;
import base.app.util.commons.Model;
import base.app.data.chat.ChatInfo;
import base.app.util.commons.Utility;
import base.app.util.ui.AnimatedExpandableListView;
import base.app.util.ui.ImageLoader;
import base.app.util.ui.LinearItemSpacing;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nemanja Jovanovic on 12/04/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ChatSearchExpandableAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private double IMAGE_SIZE = 0.06;
    protected Context context;
    private LayoutInflater inflater;
    private List<ChatInfo> parentItems;
    private HashMap<String, ChatExpandedItemAdapter> expandedAdaptersMap;

    private int background;
    private int backgroundExpanded;
    private int screenHeight;
    JoinChatFragment parentFragment;

    public ChatSearchExpandableAdapter(Context context, JoinChatFragment parentFragment, List<ChatInfo> parents) {
        this.context = context;
        expandedAdaptersMap = new HashMap<>();
        if (context != null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (Utility.isTablet(context)) {
                IMAGE_SIZE = 0.06f;
            } else {
                IMAGE_SIZE = 0.09f;
            }

            background =  ContextCompat.getColor(context, R.color.colorPrimaryMediumLight);
            backgroundExpanded =  ContextCompat.getColor(context, R.color.colorPrimary);
            screenHeight = Utility.getDisplayHeight(context);
        }
        this.parentItems = parents;
        this.parentFragment = parentFragment;
    }

    static class ParentViewHolder {
        @BindView(R.id.row_join_chat_search_image)
        CircleImageView rowImage;
        @BindView(R.id.row_join_chat_search_name)
        TextView rowName;
        @BindView(R.id.row_join_chat_search_members_count)
        TextView rowMemberCount;
        @Nullable
        @BindView(R.id.row_last_chat_label)
        TextView rowLastChatLabel;
        @Nullable
        @BindView(R.id.row_join_chat_search_friends_count)
        TextView rowFriendsCount;
        @BindView(R.id.row_join_chat_search_container)
        RelativeLayout rowParentRoot;


        ParentViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ChildViewHolder {
        @BindView(R.id.row_join_chat_search_list)
        RecyclerView memberList;
        @BindView(R.id.row_join_chat_search_join)
        Button joinButton;

        ChildViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder;
        final ChatInfo info = parentItems.get(groupPosition);
        if (convertView != null) {
            holder = (ChildViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.row_chat_search_expandable_child, parent, false);
            holder = new ChildViewHolder(convertView);
            int space = context.getResources().getDimensionPixelOffset(R.dimen.margin_20);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            holder.memberList.setLayoutManager(linearLayoutManager);
            holder.memberList.addItemDecoration(new LinearItemSpacing(space, true, true));
            convertView.setTag(holder);
        }
        if (!info.getUsersIds().contains(Model.getInstance().getUserInfo().getUserId())) {
            holder.joinButton.setVisibility(View.VISIBLE);
            holder.joinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    info.joinChat();
                    parentFragment.closeFragment();
                }
            });
        } else {
            holder.joinButton.setVisibility(View.GONE);
        }
        holder.memberList.setAdapter(getHorizontalAdapter(info));
        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return 1;
    }

    private ChatExpandedItemAdapter getHorizontalAdapter(ChatInfo chat) {

        final ChatExpandedItemAdapter chatExpandedItemAdapter;

        if (expandedAdaptersMap.containsKey(chat.getChatId())) { //Need unique id
            return expandedAdaptersMap.get(chat.getChatId());//Need unique id
        } else { // its brand new view, so create new adapter and store it in hash map for recycling
            chatExpandedItemAdapter = new ChatExpandedItemAdapter(context);
            expandedAdaptersMap.put(chat.getChatId(), chatExpandedItemAdapter);
            for (String uid : chat.getUsersIds()) {
                Task<User> task = Model.getInstance().getUserInfoById(uid);
                task.addOnCompleteListener(new OnCompleteListener<User>() {
                    @Override
                    public void onComplete(@NonNull Task<User> task) {
                        if (task.isSuccessful()) {
                            chatExpandedItemAdapter.addValue(task.getResult());
                        }
                    }
                });
            }
        }
        return chatExpandedItemAdapter;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentViewHolder holder;

        ChatInfo info = parentItems.get(groupPosition);

        if (convertView != null) {
            holder = (ParentViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.row_chat_search_expandable_parent, parent, false);
            holder = new ParentViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.rowName.setText(info.getChatTitle());
        if (Utility.isTablet(context)) {
            holder.rowMemberCount.setText(info.getUsersIds().size() + " " + context.getResources().getString(R.string.members));
        } else {
            holder.rowMemberCount.setText(" - " + info.getUsersIds().size() + " " + context.getResources().getString(R.string.members));
        }
        ImageLoader.displayImage(info.getChatAvatarUrl(), holder.rowImage, null);
        holder.rowImage.getLayoutParams().height = (int) (screenHeight * IMAGE_SIZE);
        holder.rowImage.getLayoutParams().width = (int) (screenHeight * IMAGE_SIZE);

        if (isExpanded) {
            holder.rowParentRoot.setBackgroundColor(backgroundExpanded);
        } else {
            holder.rowParentRoot.setBackgroundColor(background);
        }

        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
        // return items.get(groupPosition).items.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public int getGroupCount() {
        return parentItems.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

}