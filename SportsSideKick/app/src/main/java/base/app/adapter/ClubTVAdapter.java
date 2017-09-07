package base.app.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.youtube.model.Playlist;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import base.app.R;
import base.app.fragment.FragmentEvent;
import base.app.fragment.instance.ClubTvPlaylistFragment;
import base.app.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Filip on 1/17/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ClubTVAdapter extends RecyclerView.Adapter<ClubTVAdapter.ViewHolder> {

    private static final String TAG = "Club Adapter";
    private static final Double ITEM_HEIGHT = 0.15;

    private List<Playlist> values;
    private Context context;

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

    public ClubTVAdapter(Context context) {
        values = new ArrayList<>();
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tv_category_item, parent, false);
        viewHolder = new ViewHolder(view);
        if (Utility.isPhone(context)) {
            int height = Utility.getDisplayHeight(context);
            view.getLayoutParams().height = (int) (height * ITEM_HEIGHT);
        }
        //setup click listener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentEvent fragmentEvent = new FragmentEvent(ClubTvPlaylistFragment.class);
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
        final Playlist info = values.get(position);
        // setup caption - with count at the end in lighter color
        String originalCaption = info.getSnippet().getTitle();
        long count = info.getContentDetails().getItemCount();
        int startIndex = originalCaption.length();
        String countExtension = " (" + count + ")";
        int endIndex = startIndex + countExtension.length();
        String caption = originalCaption + countExtension;
        if (holder.caption != null) {
            holder.caption.setText(caption);
            Spannable spannable = (Spannable) holder.caption.getText();
            int color = ContextCompat.getColor(context, R.color.lightGrey);
            ForegroundColorSpan thinSpan = new ForegroundColorSpan(color);
            spannable.setSpan(thinSpan, startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }


        // display image
        DisplayImageOptions imageOptions = Utility.imageOptionsImageLoader();
        String imageUrl = info.getSnippet().getThumbnails().getHigh().getUrl();
        if (holder.image != null) {
            ImageLoader.getInstance().displayImage(imageUrl, holder.image, imageOptions);
        }
    }

    @Override
    public int getItemCount() {
        if (values == null)
            return 0;
        return values.size();
    }
}