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
import tv.sportssidekick.sportssidekick.fragment.instance.NewsItemFragment;
import tv.sportssidekick.sportssidekick.model.news.NewsItem;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Djordje on 12/29/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private static final int VIEW_TYPE_CELL = 2;
    private static final String TAG = "News Adapter";

    private List<NewsItem> values;

    public List<NewsItem> getValues() {
        return values;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        @Nullable
        @BindView(R.id.image)
        ImageView image;
        @Nullable
        @BindView(R.id.caption)
        TextView caption;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public NewsAdapter() {
        values= new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_CELL;
    }

    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_item, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        DisplayImageOptions imageOptions = Utility.imageOptionsImageLoader();
        final NewsItem info = values.get(position);
        ImageLoader.getInstance().displayImage(info.getImage(), holder.image, imageOptions);
        holder.caption.setText(info.getTitle());
        holder.view.setOnClickListener(v -> {
            FragmentEvent fe = new FragmentEvent(NewsItemFragment.class);
            fe.setId(info.getId());
            EventBus.getDefault().post(fe);
        });
    }

    @Override
    public int getItemCount() {
        if (values==null)
            return 0;
        return values.size();
    }
}