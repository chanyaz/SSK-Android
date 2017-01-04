package tv.sportssidekick.sportssidekick.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.model.UserInfo;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Filip on 12/14/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 *
 * Chat List Adapter for use in chat fragment
 */


public class ChatFriendsAdapter extends RecyclerView.Adapter<ChatFriendsAdapter.ViewHolder> {
    private static final int VIEW_TYPE_FOOTER = 1;
    private static final int VIEW_TYPE_CELL = 2;
    private static final String TAG = "Chat Friends Adapter";

    private Context context;

    public List<UserInfo> getSelectedValues() {
        return selectedValues;
    }

    private List<UserInfo> selectedValues;

    class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        @Nullable @BindView(R.id.profile_image) ImageView image;
        @Nullable @BindView(R.id.selected) View selectedRingView;
        @Nullable @BindView(R.id.caption) TextView chatCaption;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public ChatFriendsAdapter(Context context) {
        selectedValues = new ArrayList<>();
        this.context = context;
    }


    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_CELL;
    }

    @Override
    public ChatFriendsAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
       final ViewHolder viewHolder;
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_friend_item, parent, false);
        viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int focusedItem = viewHolder.getLayoutPosition();
                UserInfo info = values.get(focusedItem);
                if(selectedValues.contains(info)){
                    selectedValues.remove(info);
                } else {
                    selectedValues.add(info);
                }
                notifyItemChanged(focusedItem);
            }
        });
        return  viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position < values.size()) { // don't take the last element!
            final UserInfo info = values.get(position);
                    DisplayImageOptions imageOptions = Utility.imageOptionsImageLoader();
                    ImageLoader.getInstance().displayImage(info.getCircularAvatarUrl(),holder.image,imageOptions);
            holder.chatCaption.setText(info.getFirstName()  + " " + info.getLastName());

            if(selectedValues.contains(info)){
                holder.image.setColorFilter(ContextCompat.getColor(context,R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
            } else {
                holder.image.setColorFilter(null);
            }

            holder.view.setTag(position);
        }
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

    private final SortedList.Callback<UserInfo> mCallback = new SortedList.Callback<UserInfo>() {

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

    final SortedList<UserInfo> values = new SortedList<>(UserInfo.class, mCallback);

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