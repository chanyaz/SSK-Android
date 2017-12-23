package base.app.adapter;

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

import base.app.R;
import base.app.model.user.UserInfo;
import base.app.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Djordje on 1/31/2017.
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
        DisplayImageOptions imageOptions = Utility.getImageOptionsForUsers();
        ImageLoader.getInstance().displayImage(info.getCircularAvatarUrl(),holder.avatar,imageOptions);
        if (info.getIsOnline())
        {
            holder.online.setVisibility(View.VISIBLE);
        }
        else {
            holder.online.setVisibility(View.GONE);
        }
        holder.name.setText(info.getLastName() + " " + info.getFirstName());
    }

    @Override
    public int getItemCount() {
        if (values==null)
            return 0;
        return values.size();
    }
}