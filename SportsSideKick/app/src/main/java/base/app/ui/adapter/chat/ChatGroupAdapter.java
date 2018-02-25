package base.app.ui.adapter.chat;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import base.app.R;
import base.app.data.chat.ChatInfo;
import base.app.util.ui.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aleksandar Marinkovic on 02/06/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ChatGroupAdapter extends RecyclerView.Adapter<ChatGroupAdapter.ViewHolder> {


    // Start with first item selected
    int screenWidth = 0;
    private List<ChatInfo> values;

    public List<ChatInfo> getValues() {
        return values;
    }

    public ChatGroupAdapter setValues(List<ChatInfo> values) {
        this.values = values;
        notifyDataSetChanged();
        return this;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        @Nullable
        @BindView(R.id.chat_head_image_view)
        ImageView imageView;
        @Nullable
        @BindView(R.id.chat_name)
        TextView chatName;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    Context context;

    public ChatGroupAdapter(Context context, int screenWidth) {
        values = new ArrayList<>();
        this.context = context;
        this.screenWidth = screenWidth;
    }


    @Override
    public ChatGroupAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final ViewHolder viewHolder;

        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_head_item, parent, false);
        viewHolder = new ViewHolder(view);
        if (screenWidth != 0) {
            view.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;

            view.getLayoutParams().width = screenWidth;
        }
        return viewHolder;

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        ChatInfo info = values.get(position);
        if (holder.imageView != null) {
            ImageLoader.displayRoundImage(info.getChatAvatarUrl(), holder.imageView);
        }
        assert holder.chatName != null;
        holder.chatName.setText(info.getName());
    }


    @Override
    public int getItemCount() {
        if(values==null)
            return 0;
        return values.size();
    }
}