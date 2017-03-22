package tv.sportssidekick.sportssidekick.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.model.friendship.FriendRequest;
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
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        DisplayImageOptions imageOptions = Utility.imageOptionsImageLoader();
        final FriendRequest info = values.get(position);
        holder.name.setText(info.getSender().getNicName());
        holder.date.setText(sdf.format(info.getTimestamp()));
        //ImageLoader.getInstance().displayImage(info.getImageUrl(),holder.profileImage,imageOptions);
    }

    @Override
    public int getItemCount() {
        if (values == null)
            return 0;
        return values.size();
    }
}
