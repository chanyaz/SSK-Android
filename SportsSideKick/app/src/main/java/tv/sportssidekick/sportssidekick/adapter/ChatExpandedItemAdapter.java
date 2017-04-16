package tv.sportssidekick.sportssidekick.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Nemanja Jovanovic on 12/04/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ChatExpandedItemAdapter extends RecyclerView.Adapter<ChatExpandedItemAdapter.ViewHolder> {
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

    private List<String> values;

    public ChatExpandedItemAdapter(Context context) {
        values = new ArrayList<>();
        this.context = context;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == values.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }

    private static final double IMAGE_SIZE = 0.1;
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
        DisplayImageOptions imageOptions = Utility.getImageOptionsForUsers();
        if (holder.rowImage != null) {
            holder.rowImage.getLayoutParams().height = image_size;
            holder.rowImage.getLayoutParams().width = image_size;
            ImageLoader.getInstance().displayImage(values.get(position), holder.rowImage, imageOptions);
        }
        if (holder.rowName != null) {
            holder.rowName.setText("Dummy test");
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