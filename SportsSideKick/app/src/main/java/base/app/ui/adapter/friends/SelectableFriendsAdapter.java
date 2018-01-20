package base.app.ui.adapter.friends;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import base.app.R;
import base.app.data.user.User;
import base.app.util.events.AddFriendsEvent;
import base.app.util.commons.SoundEffects;
import base.app.util.commons.Utility;
import base.app.util.ui.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Filip on 12/14/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 * <p>
 * Selectable Users Adapter for use in chat and video chat fragments
 */


public class SelectableFriendsAdapter extends RecyclerView.Adapter<SelectableFriendsAdapter.ViewHolder> {
    private static final String TAG = "Selectable Friends Adapter";

    public List<User> getSelectedValues() {
        return selectedValues;
    }

    public void setValues(List<User> values) {
        this.selectedValues = values;
        notifyDataSetChanged();
    }

    private boolean isTablet = false;
    private List<User> selectedValues;
    int screenWidth;

    public void setSelectedUsers(List<User> selectedUsers) {
        this.selectedValues = selectedUsers;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        @Nullable
        @BindView(R.id.image)
        ImageView image;
        @Nullable
        @BindView(R.id.image_shadow)
        ImageView imageShadow;
        @Nullable
        @BindView(R.id.selected)
        ImageView selectedRingView;
        @Nullable
        @BindView(R.id.caption)
        TextView name;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public SelectableFriendsAdapter(Context context) {
        this.selectedValues = new ArrayList<>();
        if (context != null) {
            this.screenWidth = Utility.getDisplayWidth(context);
            isTablet=Utility.isTablet(context);
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
                SoundEffects.getDefault().playSound(SoundEffects.SUBTLE);
                int position = viewHolder.getLayoutPosition();
                updateUser(position);
                notifyItemChanged(position);
            }
        });
        return viewHolder;
    }

    private void updateUser(int position) {
        User info = values.get(position);
        boolean remove;
        if (selectedValues.contains(info)) {
            selectedValues.remove(info);
            remove = true;
        } else {
            selectedValues.add(info);
            remove = false;
        }
        EventBus.getDefault().post(new AddFriendsEvent(values.get(position), remove));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final User info = values.get(position);

        if (holder.image != null) {
            ImageLoader.displayImage(info.getAvatar(), holder.image, null);
            holder.name.setText(info.getFirstName() + " " + info.getLastName());
            if (selectedValues.contains(info)) {
                if (holder.selectedRingView != null) {
                    holder.selectedRingView.setVisibility(View.VISIBLE);
                    if (!isTablet && holder.imageShadow != null ) {
                        holder.imageShadow.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                if (holder.selectedRingView != null) {
                    holder.selectedRingView.setVisibility(View.GONE);
                    if (!isTablet && holder.imageShadow != null ) {
                        holder.imageShadow.setVisibility(View.GONE);
                    }
                }
            }
        }
        holder.view.setTag(position);
    }

    @Override
    public int getItemCount() {
        return values.size();
    }


    private static final Comparator<User> ALPHABETICAL_COMPARATOR = new Comparator<User>() {
        @Override
        public int compare(User a, User b) {
            return (a.getFirstName() + a.getLastName() + a.getNicName()).compareTo
                    (b.getFirstName() + b.getLastName() + b.getNicName());
        }
    };

    private final SortedList.Callback<User> callback = new SortedList.Callback<User>() {

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
        public int compare(User a, User b) {
            return ALPHABETICAL_COMPARATOR.compare(a, b);
        }

        @Override
        public boolean areContentsTheSame(User oldItem, User newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(User item1, User item2) {
            return item1.getUserId() == item2.getUserId();
        }
    };

    private final SortedList<User> values = new SortedList<>(User.class, callback);

    public void add(User model) {
        values.add(model);
    }

    public void remove(User model) {
        values.remove(model);
    }

    public void add(List<User> models) {
        values.addAll(models);
    }

    public void remove(List<User> models) {
        values.beginBatchedUpdates();
        for (User model : models) {
            values.remove(model);
        }
        values.endBatchedUpdates();
    }

    public void replaceAll(List<User> models) {
        values.beginBatchedUpdates();
        for (int i = values.size() - 1; i >= 0; i--) {
            final User model = values.get(i);
            if (!models.contains(model)) {
                values.remove(model);
            }
        }
        values.addAll(models);
        values.endBatchedUpdates();
    }
}