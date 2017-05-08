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

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.fragment.popup.MemberInfoFragment;
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

    public void setInitiatorFragment(Class initiatorFragment) {
        this.initiatorFragment = initiatorFragment;
    }

    private Class initiatorFragment;

    public List<UserInfo> getValues() {
        return values;
    }
    private int screenWidth;
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

    public FriendsAdapter(Class initiatorFragment){
        this.initiatorFragment = initiatorFragment;
        values = new ArrayList<>();
        this.screenWidth = 0;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        viewHolder = new ViewHolder(view);

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentEvent fragmentEvent = new FragmentEvent(MemberInfoFragment.class);
                fragmentEvent.setInitiatorFragment(initiatorFragment);
                int position = viewHolder.getLayoutPosition();
                fragmentEvent.setId(values.get(position).getUserId());
                EventBus.getDefault().post(fragmentEvent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final UserInfo info = values.get(position);
        DisplayImageOptions imageOptions = Utility.getImageOptionsForUsers();
        String avatarUrl = info.getCircularAvatarUrl();
        if (avatarUrl != null)
        {
            ImageLoader.getInstance().displayImage(avatarUrl, holder.avatar, imageOptions);
        }

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