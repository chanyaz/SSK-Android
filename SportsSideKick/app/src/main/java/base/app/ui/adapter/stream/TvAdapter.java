package base.app.ui.adapter.stream;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.youtube.model.Playlist;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import base.app.R;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.ui.fragment.content.tv.TvPlaylistFragment;
import base.app.util.ui.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Filip on 1/17/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class TvAdapter extends RecyclerView.Adapter<TvAdapter.ViewHolder> {

    private List<Playlist> values;

    public List<Playlist> getValues() {
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

    public TvAdapter() {
        values = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.tv_category_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        //setup click listener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentEvent fragmentEvent = new FragmentEvent(TvPlaylistFragment.class);
                int position = viewHolder.getLayoutPosition();
                fragmentEvent.setId(values.get(position).getId());
                EventBus.getDefault().post(fragmentEvent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Playlist info = values.get(position);
        // setup caption - with count at the end in lighter color
        String originalCaption = info.getSnippet().getTitle();
        long count = info.getContentDetails().getItemCount();
        int startIndex = originalCaption.length();
        String countExtension = " (" + count + ")";
        int endIndex = startIndex + countExtension.length();
        String caption = originalCaption + countExtension;
            holder.caption.setText(caption);
        Spannable spannable = (Spannable) holder.caption.getText();
        int color = Color.parseColor("#9ba1a3");
        ForegroundColorSpan thinSpan = new ForegroundColorSpan(color);
        spannable.setSpan(thinSpan, startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        String imageUrl = info.getSnippet().getThumbnails().getHigh().getUrl();
        if (holder.image != null) {
            ImageLoader.displayImage(imageUrl, holder.image, null);
        }
    }

    public void addAll(List<Playlist> items) {
        values.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return values.size();
    }
}