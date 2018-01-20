package base.app.ui.adapter.friends;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import base.app.R;
import base.app.data.user.User;
import base.app.util.events.FragmentEvent;
import base.app.ui.fragment.popup.FriendFragment;
import base.app.data.user.friends.FriendsManager;
import base.app.util.ui.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Filip on 1/17/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private static final String TAG = "Friends Adapter";

    public void setValues(List<User> values) {
        this.values = values;
    }

    private List<User> values;

    public void setInitiatorFragment(Class initiatorFragment) {
        this.initiatorFragment = initiatorFragment;
    }

    private Class initiatorFragment;

    public List<User> getValues() {
        return values;
    }

    private int screenWidth;

    int layout = -1;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        @Nullable
        @BindView(R.id.avatar)
        ImageView avatar;
        @Nullable
        @BindView(R.id.online_status)
        ImageView online;
        @Nullable
        @BindView(R.id.profile_name)
        TextView name;
        @Nullable
        @BindView(R.id.friend_request_pending)
        TextView pendingRequestLabel;
        @Nullable
        @BindView(R.id.button_add_friend)
        ImageButton buttonAddFriend;


        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public FriendsAdapter(Class initiatorFragment) {
        this.initiatorFragment = initiatorFragment;
        values = new ArrayList<>();
        this.screenWidth = 0;
        layout = -1;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final ViewHolder viewHolder;
        View view;
        if (layout == -1) {
            // Set default layout
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        }
        viewHolder = new ViewHolder(view);
        if (screenWidth != 0) {
            view.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;
            view.getLayoutParams().width = screenWidth;
        }

        setupOpenUserProfileOnClick(viewHolder);
        if (layout == R.layout.row_add_friend_item && viewHolder.buttonAddFriend != null) {
            viewHolder.buttonAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final User user = values.get(viewHolder.getAdapterPosition());
                    if (user.isaFriend()) { // this user is my friend, remove it from friends
                        // TODO - Add dialog to confirm!
                        FriendsManager.getInstance().deleteFriend(user.getUserId())
                                .addOnCompleteListener(new OnCompleteListener<User>() {
                                    @Override
                                    public void onComplete(@NonNull Task<User> task) {
                                        if (task.isSuccessful()) {
                                            user.setaFriend(!user.isaFriend());
                                            updateButtons(viewHolder);
                                        }
                                    }
                                });
                    } else { // send friend request
                        FriendsManager.getInstance().sendFriendRequest(user.getUserId())
                                .addOnCompleteListener(new OnCompleteListener<User>() {
                            @Override
                            public void onComplete(@NonNull Task<User> task) {
                                if (task.isSuccessful()) {
                                    user.setFriendPendingRequest(true);
                                    updateButtons(viewHolder);
                                }
                            }
                        });
                    }
                }
            });
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final User info = values.get(position);

        String avatarUrl = info.getAvatar();

        if (avatarUrl != null) {
            ImageLoader.displayImage(avatarUrl, holder.avatar, null);
        }
        if (info.getIsOnline()) {
            if (holder.online != null) {
                holder.online.setVisibility(View.VISIBLE);
            }
        } else {
            if (holder.online != null) {
                holder.online.setVisibility(View.GONE);
            }
        }
        if (holder.name != null) {
            holder.name.setText(info.getFirstName() + " " + info.getLastName());
        }
        updateButtons(holder);
    }

    @Override
    public int getItemCount() {
        if (values == null)
            return 0;
        return values.size();
    }

    public void screenWidth(int width) {
        screenWidth = width;
    }

    private void setupOpenUserProfileOnClick(final ViewHolder viewHolder) {
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentEvent fragmentEvent = new FragmentEvent(FriendFragment.class);
                fragmentEvent.setInitiatorFragment(initiatorFragment);
                int position = viewHolder.getLayoutPosition();
                fragmentEvent.setItemId(values.get(position).getUserId());
                EventBus.getDefault().post(fragmentEvent);
            }
        });
    }

    private void updateButtons(ViewHolder holder){
        final User user = values.get(holder.getAdapterPosition());
        if (holder.pendingRequestLabel != null) {
            if (user.isFriendPendingRequest()) {
                holder.pendingRequestLabel.setVisibility(View.VISIBLE);
            } else {
                holder.pendingRequestLabel.setVisibility(View.GONE);
            }
        }
        if (holder.buttonAddFriend != null) {
            if (user.isFriendPendingRequest()) {
                holder.buttonAddFriend.setVisibility(View.GONE);
            } else {
                holder.buttonAddFriend.setVisibility(View.VISIBLE);
            }
            if (user.isaFriend()) {
                holder.buttonAddFriend.setSelected(true);
            } else {
                holder.buttonAddFriend.setSelected(false);
            }
        }
    }


}