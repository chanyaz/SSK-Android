package tv.sportssidekick.sportssidekick.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.util.ui.AnimatedExpandableListView;
import tv.sportssidekick.sportssidekick.util.ui.LinearItemSpacing;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Nemanja Jovanovic on 12/04/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ChatSearchExpandableAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private static final double IMAGE_SIZE = 0.1;
    protected Context context;
    private LayoutInflater inflater;
    private List<String> childtems;
    private List<String> parentItems;
    private HashMap<Integer, ChatExpandedItemAdapter> expandedAdapters;

    private int background;
    private int backgroundExpanded;
    private int screenHeight;

    public ChatSearchExpandableAdapter(Context context, List<String> parents, List<String> childern) {
        this.context = context;
        if (context != null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            background = context.getResources().getColor(R.color.green_dark_1);
            backgroundExpanded = context.getResources().getColor(R.color.colorPrimary);
            screenHeight = Utility.getDisplayHeight(context);
        }
        this.parentItems = parents;
        this.childtems = childern;
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
        RecyclerView rowSearchGroupMemeberList;
        @BindView(R.id.row_join_chat_search_join)
        Button rowJoinGroup;

        ChildViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder;
        if (convertView != null) {
            holder = (ChildViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.row_chat_search_expandable_child, parent, false);
            holder = new ChildViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.rowJoinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO get selected chats and join group
            }
        });

        createHorizontalList(holder.rowSearchGroupMemeberList,groupPosition);

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return childtems.size();
        // return items.get(groupPosition).items.size();
    }

    public void createHorizontalList(RecyclerView recyclerView,int position) {
        ChatExpandedItemAdapter chatExpandedItemAdapter;
        //TODO UNCOMMENT TO PREVENT FROM CREATING NEW ADAPTERS - NEED UNIQUE ID
//        if (expandedAdapters.containsKey(1)) { //Need unique id
//            chatExpandedItemAdapter = expandedAdapters.get(1);//Need unique id
//        } else { // its brand new view, so create new adapter and store it in hash map for recycling

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        int space = context.getResources().getDimensionPixelOffset(R.dimen.margin_20);
        recyclerView.addItemDecoration(new LinearItemSpacing(space,true,true));
        chatExpandedItemAdapter = new ChatExpandedItemAdapter(context);
        List<String> values = new ArrayList<>();
        values.add("Test 1");
        values.add("Test 2");
        values.add("Test 3");
        values.add("Test 4");
        chatExpandedItemAdapter.setValues(values);
        recyclerView.setAdapter(chatExpandedItemAdapter);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentViewHolder holder;
        if (convertView != null) {
            holder = (ParentViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.row_chat_search_expandable_parent, parent, false);
            holder = new ParentViewHolder(convertView);
            convertView.setTag(holder);
        }

        DisplayImageOptions displayImageOptions = Utility.getImageOptionsForUsers();
        holder.rowName.setText("Chat254");
        holder.rowMemberCount.setText("3 Members");
        ImageLoader.getInstance().displayImage("", holder.rowImage, displayImageOptions);
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