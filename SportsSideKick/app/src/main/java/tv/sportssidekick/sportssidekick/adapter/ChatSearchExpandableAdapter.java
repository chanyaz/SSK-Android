package tv.sportssidekick.sportssidekick.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.im.ChatInfo;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.util.Utility;
import tv.sportssidekick.sportssidekick.util.ui.AnimatedExpandableListView;
import tv.sportssidekick.sportssidekick.util.ui.LinearItemSpacing;

/**
 * Created by Nemanja Jovanovic on 12/04/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ChatSearchExpandableAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private static final double IMAGE_SIZE = 0.1;
    protected Context context;
    private LayoutInflater inflater;
    private List<ChatInfo> parentItems;
    private HashMap<String, ChatExpandedItemAdapter> expandedAdaptersMap;

    private int background;
    private int backgroundExpanded;
    private int screenHeight;

    public ChatSearchExpandableAdapter(Context context, List<ChatInfo> parents) {
        this.context = context;
        expandedAdaptersMap = new HashMap<>();
        if (context != null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            background = context.getResources().getColor(R.color.green_dark_1);
            backgroundExpanded = context.getResources().getColor(R.color.colorPrimary);
            screenHeight = Utility.getDisplayHeight(context);
        }
        this.parentItems = parents;
    }

    static class ParentViewHolder {
        @BindView(R.id.row_join_chat_search_image)
        CircleImageView rowImage;
        @BindView(R.id.row_join_chat_search_name)
        TextView rowName;
        @BindView(R.id.row_join_chat_search_members_count)
        TextView rowMemberCount;
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
            holder.memberList.addItemDecoration(new LinearItemSpacing(space,true,true));
            convertView.setTag(holder);
        }
        if(!info.getUsersIds().contains(Model.getInstance().getUserInfo().getUserId())){
            holder.joinButton.setAlpha(1.f);
            holder.joinButton.setText("Join Group");
            holder.joinButton.setClickable(true);
            holder.joinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    info.joinChat();
                }
            });
        } else {
            holder.joinButton.setClickable(false);
            holder.joinButton.setText("You are already in group");
            holder.joinButton.setAlpha(0.5f);
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
            expandedAdaptersMap.put(chat.getChatId(),chatExpandedItemAdapter);
            for(String uid : chat.getUsersIds()){
                Task<UserInfo> task = Model.getInstance().getUserInfoById(uid);
                task.addOnCompleteListener(new OnCompleteListener<UserInfo>() {
                    @Override
                    public void onComplete(@NonNull Task<UserInfo> task) {
                        if(task.isSuccessful()){
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

        DisplayImageOptions displayImageOptions = Utility.getImageOptionsForUsers();
        holder.rowName.setText(info.getChatTitle());
        holder.rowMemberCount.setText(info.getUsersIds().size() + " Members"); // TODO USE RESOURCE AND PLACEHOLDER
        ImageLoader.getInstance().displayImage(info.getChatAvatarUrl(), holder.rowImage, displayImageOptions);
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