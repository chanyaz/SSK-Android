package tv.sportssidekick.sportssidekick.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.model.friendship.FriendRequest;
import tv.sportssidekick.sportssidekick.model.friendship.FriendsManager;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Djordje on 21/01/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.ViewHolder> {

    private static final String TAG = "Friend Requests Adapter";

    private List<FriendRequest> values;

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
        @BindView(R.id.deny_button)
        ImageButton denyButton;
        @BindView(R.id.confirm_button)
        ImageButton confirmButton;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    SimpleDateFormat sdf;

    public FriendRequestsAdapter() {
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

        viewHolder.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int position = viewHolder.getLayoutPosition();
                Task<UserInfo> requestTask = FriendsManager.getInstance().acceptFriendRequest(values.get(position).getId());
                requestTask.addOnCompleteListener(new OnCompleteListener<UserInfo>() {
                    @Override
                    public void onComplete(@NonNull Task<UserInfo> task) {
                        if(task.isSuccessful()){
                            getValues().remove(position);
                            notifyDataSetChanged();
                        } else {
                            // TODO Warn about error?
                        }
                    }
                });
            }
        });

        viewHolder.denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int position = viewHolder.getLayoutPosition();
                Task<Boolean> requestTask = FriendsManager.getInstance().rejectFriendRequest(values.get(position).getId());
                requestTask.addOnCompleteListener(new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if(task.isSuccessful()){
                            getValues().remove(position);
                            notifyDataSetChanged();
                        } else {
                            // TODO Warn about error?
                        }
                    }
                });
            }
        });


        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final FriendRequest info = values.get(position);
        holder.name.setText(info.getSender().getNicName());
        if(info.getTimestamp()!=null){
            holder.date.setText(sdf.format(info.getTimestamp()));
        }
        DisplayImageOptions imageOptions = Utility.getImageOptionsForUsers();
        String avatarUrl = info.getSender().getCircularAvatarUrl();
        if(avatarUrl==null){
            avatarUrl = info.getSender().getAvatarUrl();
        }
        ImageLoader.getInstance().displayImage(avatarUrl,holder.profileImage,imageOptions);
    }

    @Override
    public int getItemCount() {
        if (values == null)
            return 0;
        return values.size();
    }
}
