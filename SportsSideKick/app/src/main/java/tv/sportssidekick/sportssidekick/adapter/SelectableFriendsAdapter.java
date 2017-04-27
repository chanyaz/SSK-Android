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

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.model.user.AddFriendsEvent;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Filip on 12/14/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 *
 * Selectable Users Adapter for use in chat and video chat fragments
 */


public class SelectableFriendsAdapter extends RecyclerView.Adapter<SelectableFriendsAdapter.ViewHolder> {
    private static final String TAG = "Selectable Friends Adapter";

    private Context context;

    public List<UserInfo> getSelectedValues() {
        return selectedValues;
    }

    private List<UserInfo> selectedValues;
    int screenWidth;
    class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        @Nullable @BindView(R.id.image) ImageView image;
        @Nullable @BindView(R.id.selected) ImageView selectedRingView;
        @Nullable @BindView(R.id.caption) TextView name;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public SelectableFriendsAdapter(Context context) {
        this.selectedValues = new ArrayList<>();
        this.context = context;
        if(context!=null){
            this.screenWidth = Utility.getDisplayWidth(context);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public SelectableFriendsAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
       final ViewHolder viewHolder;
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selectable_friend_item, parent, false);
        viewHolder = new ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getLayoutPosition();
                updateUser(position);
                notifyItemChanged(position);
            }
        });
        return  viewHolder;
    }

    private void updateUser(int position){
        UserInfo info = values.get(position);
        boolean remove;
        if(selectedValues.contains(info)){
            selectedValues.remove(info);
            remove = true;
        } else {
            selectedValues.add(info);
            remove = false;
        }
        EventBus.getDefault().post(new AddFriendsEvent(values.get(position),remove));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final UserInfo info = values.get(position);
        DisplayImageOptions imageOptions = Utility.getImageOptionsForUsers();
        if(holder.image!=null){
            ImageLoader.getInstance().displayImage(info.getCircularAvatarUrl(),holder.image,imageOptions);
            holder.name.setText(info.getFirstName()  + " " + info.getLastName());
            if(selectedValues.contains(info)){
                if (holder.selectedRingView != null) {
                    holder.selectedRingView.setVisibility(View.VISIBLE);
                }
            } else {
                if (holder.selectedRingView != null) {
                    holder.selectedRingView.setVisibility(View.GONE);
                }
            }
        }
        holder.view.setTag(position);
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