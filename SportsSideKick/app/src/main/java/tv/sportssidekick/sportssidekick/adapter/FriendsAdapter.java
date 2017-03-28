package tv.sportssidekick.sportssidekick.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Filip on 1/17/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private static final String TAG = "Friends Adapter";

    private List<UserInfo> values;

    public List<UserInfo> getValues() {
        return values;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        @Nullable
        @BindView(R.id.avatar)
        ImageView avatar;
        @Nullable
        @BindView(R.id.online_status)
        ImageView online;
        @Nullable
        @BindView(R.id.profil_name)
        TextView name;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public FriendsAdapter() {
        values = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final UserInfo info = values.get(position);
        DisplayImageOptions imageOptions = Utility.getImageOptionsForUsers();
        ImageLoader.getInstance().displayImage(info.getCircularAvatarUrl(), holder.avatar, imageOptions);

        if (info.getIsOnline()) {
            holder.online.setVisibility(View.VISIBLE);
        } else {
            holder.online.setVisibility(View.GONE);
        }
        holder.name.setText(info.getLastName() + " " + info.getFirstName());
    }

    @Override
    public int getItemCount() {
        if (values == null)
            return 0;
        return values.size();
    }
}