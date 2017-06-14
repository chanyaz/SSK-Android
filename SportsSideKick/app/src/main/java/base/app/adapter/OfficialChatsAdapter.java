package base.app.adapter;

import android.support.annotation.Nullable;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Comparator;
import java.util.List;

import base.app.fragment.popup.JoinChatFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import base.app.R;
import base.app.model.im.ChatInfo;
import base.app.util.Utility;

/**
 * Created by Filip on 12/14/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 *
 * Chat List Adapter for use in chat fragment
 */


public class OfficialChatsAdapter extends RecyclerView.Adapter<OfficialChatsAdapter.ViewHolder> {
    private static final int VIEW_TYPE_CELL = 1;
    private static final String TAG = "Chat Friends Adapter";


    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        @Nullable @BindView(R.id.row_public_chat_image) ImageView image;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }
    JoinChatFragment parentFragment;
    private int cellHeight;
    public OfficialChatsAdapter(int cellHeight, JoinChatFragment parentFragment) {
        this.cellHeight = cellHeight;
        this.parentFragment = parentFragment;
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_CELL;
    }

    @Override
    public OfficialChatsAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_public, parent, false);
        view.getLayoutParams().height = cellHeight;
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ChatInfo info = values.get(position);
        DisplayImageOptions imageOptions = Utility.getImageOptionsForUsers();
        if(holder.image!=null){
            ImageLoader.getInstance().displayImage(info.getChatAvatarUrl(),holder.image);
        }
        holder.view.setTag(position);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info.joinChat();
                parentFragment.closeFragment();
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }


    private static final Comparator<ChatInfo> ALPHABETICAL_COMPARATOR = new Comparator<ChatInfo>() {
        @Override
        public int compare(ChatInfo a, ChatInfo b) {
            return (a.getChatTitle()).compareTo
                (b.getChatTitle());
        }
    };

    private final SortedList.Callback<ChatInfo> mCallback = new SortedList.Callback<ChatInfo>() {

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
        public int compare(ChatInfo a, ChatInfo b) {
            return ALPHABETICAL_COMPARATOR.compare(a, b);
        }

        @Override
        public boolean areContentsTheSame(ChatInfo oldItem, ChatInfo newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(ChatInfo item1, ChatInfo item2) {
            return item1.getChatId().equals(item2.getChatId());
        }
    };

    final SortedList<ChatInfo> values = new SortedList<>(ChatInfo.class, mCallback);

    public void add(ChatInfo model) {
        values.add(model);
    }

    public void remove(ChatInfo model) {
        values.remove(model);
    }

    public void add(List<ChatInfo> models) {
        values.addAll(models);
    }

    public void remove(List<ChatInfo> models) {
        values.beginBatchedUpdates();
        for (ChatInfo model : models) {
            values.remove(model);
        }
        values.endBatchedUpdates();
    }

    public void replaceAll(List<ChatInfo> models) {
        values.beginBatchedUpdates();
        for (int i = values.size() - 1; i >= 0; i--) {
            final ChatInfo model = values.get(i);
            if (!models.contains(model)) {
                values.remove(model);
            }
        }
        values.addAll(models);
        values.endBatchedUpdates();
    }
}