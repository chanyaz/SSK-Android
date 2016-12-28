package tv.sportssidekick.sportssidekick.adapter;

import android.support.annotation.Nullable;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
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

    // Start with first item selected
    private int focusedItem = 0;

    public class ViewHolder extends RecyclerView.ViewHolder {
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

    public ChatFriendsAdapter() {
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        // Handle key up and key down and attempt to move selection
        recyclerView.setOnKeyListener((v, keyCode, event) -> {
            RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();

            // Return false if scrolled to the bounds and allow focus to move off the list
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    return tryMoveSelection(lm, 1);
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    return tryMoveSelection(lm, -1);
                }
            }
            return false;
        });
    }

    private boolean tryMoveSelection(RecyclerView.LayoutManager lm, int direction) {
        int tryFocusItem = focusedItem + direction;
        // If still within valid bounds, move the selection, notify to redraw, and scroll
        if (tryFocusItem >= 0 && tryFocusItem < getItemCount()) {
            notifyItemChanged(focusedItem);
            focusedItem = tryFocusItem;
            notifyItemChanged(focusedItem);
            lm.scrollToPosition(focusedItem);
            return true;
        }

        return false;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == values.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }

    @Override
    public ChatFriendsAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        if (viewType == VIEW_TYPE_CELL) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_friend_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setOnClickListener(v -> {
//                notifyItemChanged(focusedItem);
//                focusedItem = viewHolder.getLayoutPosition();
//                notifyItemChanged(focusedItem);
//                EventBus.getDefault().post(new UIEvent(focusedItem));
            });
            return  viewHolder;
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_chat_button, parent, false);
            viewHolder = new ViewHolder(view);
//            view.setOnClickListener( view1 -> EventBus.getDefault().post(new FragmentEvent(CreateChatFragment.class)));

            return viewHolder;
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position < values.size()) { // don't take the last element!
            final UserInfo info = values.get(position);
                    DisplayImageOptions imageOptions = Utility.imageOptionsImageLoader();
                    ImageLoader.getInstance().displayImage(info.getCircularAvatarUrl(),holder.image,imageOptions);
            holder.chatCaption.setText(info.getFirstName()  + " " + info.getLastName());
            holder.view.setTag(position);
        }
    }

    @Override
    public int getItemCount() {
        return values.size() + 1;
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