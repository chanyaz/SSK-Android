package base.app.ui.adapter.friends;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import base.app.R;
import base.app.data.user.UserInfo;
import base.app.util.ui.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Djordje on 1/31/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class CallFriendsAdapter extends RecyclerView.Adapter<CallFriendsAdapter.ViewHolder> {

    private static final String TAG = "Call Friends Adapter";

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
        @BindView(R.id.profile_name)
        TextView name;
        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public CallFriendsAdapter() {
        values= new ArrayList<>();
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
        ImageLoader.displayRoundImage(info.getCircularAvatarUrl(), holder.avatar);
        if (info.getIsOnline())
        {
            holder.online.setVisibility(View.VISIBLE);
        }
        else {
            holder.online.setVisibility(View.GONE);
        }
        holder.name.setText(info.getFirstName() + " " + info.getLastName());
    }

    @Override
    public int getItemCount() {
        if (values==null)
            return 0;
        return values.size();
    }
}