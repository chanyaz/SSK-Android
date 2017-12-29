package base.app.ui.adapter.chat;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import base.app.R;
import base.app.data.user.UserInfo;
import base.app.util.Utility;
import base.app.util.ui.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nemanja Jovanovic on 12/04/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ChatExpandedItemAdapter extends RecyclerView.Adapter<ChatExpandedItemAdapter.ViewHolder> {
    private static final double IMAGE_SIZE = 0.06;
    private static final int VIEW_TYPE_FOOTER = 1;
    private static final int VIEW_TYPE_CELL = 2;
    private static final String TAG = "FriendsInChatAdapter";

    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        @Nullable
        @BindView(R.id.row_chat_expanded_item_image)
        CircleImageView rowImage;
        @Nullable
        @BindView(R.id.row_chat_expanded_item_name)
        TextView rowName;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    private List<UserInfo> values;

    public ChatExpandedItemAdapter(Context context) {
        values = new ArrayList<>();
        this.context = context;
    }

    public void setValues(List<UserInfo> values) {
        this.values = values;
        notifyDataSetChanged();
    }

    public void addValue(UserInfo info){
        values.add(info);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == values.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }

    private int image_size;

    @Override
    public ChatExpandedItemAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final ChatExpandedItemAdapter.ViewHolder viewHolder;
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_expanded_item, parent, false);
        viewHolder = new ChatExpandedItemAdapter.ViewHolder(view);
        int screenHeight = Utility.getDisplayHeight(context);
        image_size = (int) (screenHeight * IMAGE_SIZE);
        return viewHolder;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ChatExpandedItemAdapter.ViewHolder holder, final int position) {
        UserInfo info =values.get(position);
        if (holder.rowImage != null) {
            holder.rowImage.getLayoutParams().height = image_size;
            holder.rowImage.getLayoutParams().width = image_size;
            ImageLoader.displayImage(info.getAvatarUrl(), holder.rowImage);
        }
        if (holder.rowName != null) {
            holder.rowName.setText(info.getNicName());
        }
    }

    @Override
    public long getItemId(int position) {
        return values.size();
    }

    @Override
    public int getItemCount() {
        return values.size();
    }
}