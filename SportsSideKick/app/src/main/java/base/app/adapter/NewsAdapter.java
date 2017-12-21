package base.app.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import base.app.R;
import base.app.fragment.FragmentEvent;
import base.app.fragment.instance.NewsItemFragment;
import base.app.model.wall.WallNews;

import static base.app.adapter.WallAdapter.ViewHolder;
import static base.app.adapter.WallAdapter.displayCaption;
import static base.app.adapter.WallAdapter.displayCommentsAndLikes;
import static base.app.adapter.WallAdapter.displayPostImage;
import static base.app.adapter.WallAdapter.displayUserInfo;

/**
 * Created by Djordje on 12/29/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */


public class NewsAdapter extends RecyclerView.Adapter<WallAdapter.ViewHolder> {
    private static final int VIEW_TYPE_CELL = 2;
    private static final String TAG = "News Adapter";

    private List<WallNews> values;

    public List<WallNews> getValues() {
        return values;
    }

    public NewsAdapter() {
        values = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_CELL;
    }

    @Override
    public WallAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        WallAdapter.ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wall_item_news, parent, false);
        viewHolder = new WallAdapter.ViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final WallNews news = values.get(position);
        displayUserInfo(news, holder);
        displayCaption(news.getTitle(), holder);
        displayPostImage(news, holder);
        displayCommentsAndLikes(news, holder);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentEvent fe = new FragmentEvent(NewsItemFragment.class);
                fe.setId(news.getPostId());
                EventBus.getDefault().post(fe);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (values == null) {
            return 0;
        }
        return values.size();
    }
}