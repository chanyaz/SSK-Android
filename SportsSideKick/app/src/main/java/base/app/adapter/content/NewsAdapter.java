package base.app.adapter.content;

import android.support.annotation.NonNull;
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

/**
 * Created by Djordje on 12/29/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */


public class NewsAdapter extends RecyclerView.Adapter<WallAdapter.ViewHolder> {

    private static final int VIEW_TYPE_ROW = 1;

    protected List<WallNews> values;

    public List<WallNews> getValues() {
        return values;
    }

    public NewsAdapter() {
        values = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_ROW;
    }

    @Override
    public WallAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        WallAdapter.ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(getItemLayoutId(), parent, false);
        viewHolder = new WallAdapter.ViewHolder(view);
        return viewHolder;
    }

    protected int getItemLayoutId() {
        return R.layout.wall_item_news;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(WallAdapter.ViewHolder holder, final int position) {
        final WallNews news = values.get(position);
        WallAdapter.displayUserInfo(news, holder);
        WallAdapter.displayCaption(news.getTitle(), holder);
        WallAdapter.displayPostImage(news, holder);
        WallAdapter.displayCommentsAndLikes(news, holder);
        holder.view.setOnClickListener(getClickListener(news));
    }

    @NonNull
    protected View.OnClickListener getClickListener(final WallNews item) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentEvent fe = new FragmentEvent(NewsItemFragment.class);
                fe.setId(item.getPostId());
                EventBus.getDefault().post(fe);
            }
        };
    }

    @Override
    public int getItemCount() {
        if (values == null) {
            return 0;
        }
        return values.size();
    }
}