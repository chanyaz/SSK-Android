package base.app.ui.adapter.friends;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import base.app.R;
import base.app.data.user.User;
import base.app.util.commons.Utility;
import base.app.util.ui.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nemanja Jovanovic on 24/04/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class AddFriendsAdapter extends RecyclerView.Adapter<AddFriendsAdapter.ViewHolder> {
    private static final String TAG = "Add Friends Adapter";

    private Context context;

    int screenWidth;
    int itemWidth;
    public static final double GRID_PERCENT_CELL_WIDTH_PHONE = 0.2;
    class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        @Nullable
        @BindView(R.id.row_add_friend_image)
        ImageView image;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public AddFriendsAdapter(Context context) {
        this.context = context;
        itemWidth=0;
        if (context != null) {
            this.screenWidth = Utility.getDisplayWidth(context);


        }
    }

    public AddFriendsAdapter(Context context,Fragment initiator) {
        this.context = context;
        itemWidth=0;
        if (context != null) {
            this.screenWidth = Utility.getDisplayWidth(context);
            itemWidth = (int)(screenWidth * GRID_PERCENT_CELL_WIDTH_PHONE);

        }
    }


    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public AddFriendsAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final AddFriendsAdapter.ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_friend, parent, false);
        viewHolder = new AddFriendsAdapter.ViewHolder(view);
        if (itemWidth != 0) {
            view.getLayoutParams().width = itemWidth;
        }
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(AddFriendsAdapter.ViewHolder holder, final int position) {
        final User info = values.get(position);
        if (holder.image != null) {
            ImageLoader.displayImage(info.getAvatar(), holder.image, null);
        }
        holder.view.setTag(position);
    }

    List<User> values = new ArrayList<>();


    @Override
    public int getItemCount() {
        return values.size();
    }

    public void add(User model) {
        values.add(model);
        if (getItemCount() == 0) {
            notifyItemInserted(0);
        } else {
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void remove(User model) {
        int position = getItemPosition(model);
        values.remove(model);
        notifyItemRemoved(position);
    }



    public void add(List<User> models) {
        values.addAll(models);
    }

    private int getItemPosition(User info) {
        for (int position = 0; position < getItemCount(); position++)
            if (values.get(position).equals(info))
                return position;
        return 0;
    }


}