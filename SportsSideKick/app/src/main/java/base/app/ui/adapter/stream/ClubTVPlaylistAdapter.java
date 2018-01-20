package base.app.ui.adapter.stream;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.youtube.model.Video;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import base.app.R;
import base.app.util.events.FragmentEvent;
import base.app.ui.fragment.content.tv.VideoContainerFragment;
import base.app.util.ui.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Filip on 1/17/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ClubTVPlaylistAdapter extends RecyclerView.Adapter<ClubTVPlaylistAdapter.ViewHolder> {

    private static final String TAG = "Channel TV Adapter";

    private List<Video> values;
    private Context context;
    SimpleDateFormat sdf;

    public List<Video> getValues() {
        return values;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        @Nullable
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.caption)
        TextView caption;
        @BindView(R.id.date)
        TextView date;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public ClubTVPlaylistAdapter(Context context) {
        values= new ArrayList<>();
        this.context = context;
        sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tv_channel_item, parent, false);
        viewHolder = new ViewHolder(view);
        //setup click listener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentEvent fragmentEvent = new FragmentEvent(VideoContainerFragment.class);
                int position = viewHolder.getLayoutPosition();
                fragmentEvent.setId(values.get(position).getId());
                EventBus.getDefault().post(fragmentEvent);
            }
        });

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Video info = values.get(position);
        // setup caption
        holder.caption.setText(info.getSnippet().getTitle());
        holder.date.setText(sdf.format(new Date(info.getSnippet().getPublishedAt().getValue())));
        String imageUrl = info.getSnippet().getThumbnails().getHigh().getUrl();
        ImageLoader.displayImage(imageUrl, holder.image, null);
    }

    @Override
    public int getItemCount() {
        if (values==null)
            return 0;
        return values.size();
    }
}