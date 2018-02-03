package base.app.ui.adapter.friends;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import base.app.R;
import base.app.util.events.FragmentEvent;
import base.app.ui.fragment.popup.FriendFragment;
import base.app.data.user.friends.FriendRequest;
import base.app.data.user.friends.FriendsManager;
import base.app.data.user.User;
import base.app.util.ui.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Djordje on 21/01/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.ViewHolder> {

    private static final String TAG = "Friend Requests Adapter";

    private List<FriendRequest> values;

    private Class initiatorFragment;

    public List<FriendRequest> getValues() {
        return values;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        @BindView(R.id.profile_image)
        ImageView profileImage;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.date_prefix)
        TextView datePrefix;

        @Nullable
        @BindView(R.id.deny_button)
        ImageButton denyButton;
        @Nullable
        @BindView(R.id.confirm_button)
        ImageButton confirmButton;


        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    SimpleDateFormat sdf;

    public FriendRequestsAdapter(Class initiatorFragment) {
        this.initiatorFragment = initiatorFragment;
        values = new ArrayList<>();
        sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        setupClickListeners(viewHolder);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final FriendRequest info = values.get(position);
        holder.name.setText(info.getSender().getNicName());
        if (info.getTimestamp() != null) {
            holder.datePrefix.setVisibility(View.VISIBLE);
            holder.date.setVisibility(View.VISIBLE);
            holder.date.setText(sdf.format(info.getTimestamp()));
        } else {
            holder.datePrefix.setVisibility(View.GONE);
            holder.date.setVisibility(View.GONE);
        }

        String avatarUrl = info.getSender().getAvatar();
        if (avatarUrl == null) {
            avatarUrl = info.getSender().getAvatarUrl();
        }
        ImageLoader.displayImage(avatarUrl, holder.profileImage, null);
    }

    @Override
    public int getItemCount() {
        if (values == null){
            return 0;
        }
        return values.size();
    }

    private void setupClickListeners(final ViewHolder viewHolder) {
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                values.get(viewHolder.getLayoutPosition());
                FragmentEvent fragmentEvent = new FragmentEvent(FriendFragment.class);
                fragmentEvent.setInitiatorFragment(initiatorFragment);
                int position = viewHolder.getLayoutPosition();
                fragmentEvent.setItemId(values.get(position).getSender().getUserId());
                EventBus.getDefault().post(fragmentEvent);
            }
        });

        if (viewHolder.confirmButton != null) {
            viewHolder.confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acceptFriendRequest(viewHolder.getLayoutPosition());
                }
            });
        }
        if (viewHolder.denyButton != null) {
            viewHolder.denyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rejectFriendRequest(viewHolder.getLayoutPosition());
                }
            });
        }
    }



    private void rejectFriendRequest(final int position) {
        Task<Boolean> requestTask = FriendsManager.getInstance().rejectFriendRequest(values.get(position).getId());
        requestTask.addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    getValues().remove(position);
                    notifyDataSetChanged();
                }
            }
        });

    }

    private void acceptFriendRequest(final int position) {
        Task<User> requestTask = FriendsManager.getInstance().acceptFriendRequest(values.get(position).getId());
        requestTask.addOnCompleteListener(new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {
                if (task.isSuccessful()) {
                    getValues().remove(position);
                    notifyDataSetChanged();
                }
            }
        });
    }


}




