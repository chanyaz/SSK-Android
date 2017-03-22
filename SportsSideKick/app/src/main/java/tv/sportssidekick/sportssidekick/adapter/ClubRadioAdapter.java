package tv.sportssidekick.sportssidekick.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.api.services.youtube.model.Playlist;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.fragment.instance.ClubRadioStationFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.ClubTvPlaylistFragment;
import tv.sportssidekick.sportssidekick.model.club.Station;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Filip on 1/30/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ClubRadioAdapter extends RecyclerView.Adapter<ClubRadioAdapter.ViewHolder> {

    private static final String TAG = "Club Adapter";

    private List<Station> values;
    private Context context;

    public List<Station> getValues() {
        return values;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        @Nullable
        @BindView(R.id.image)
        ImageView image;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public ClubRadioAdapter(Context context) {
        values= new ArrayList<>();
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ClubRadioAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final ClubRadioAdapter.ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.club_radio_item, parent, false);
        viewHolder = new ClubRadioAdapter.ViewHolder(view);
        //setup click listener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentEvent fragmentEvent = new FragmentEvent(ClubRadioStationFragment.class);
                int position = viewHolder.getLayoutPosition();
                fragmentEvent.setId(values.get(position).getName());
                EventBus.getDefault().post(fragmentEvent);
            }
        });

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ClubRadioAdapter.ViewHolder holder, final int position) {
        final Station info = values.get(position);
        // display image
        DisplayImageOptions imageOptions = Utility.imageOptionsImageLoader();
        String imageUrl = info.getCoverImageUrl();
        ImageLoader.getInstance().displayImage(imageUrl, holder.image, imageOptions);
    }

    @Override
    public int getItemCount() {
        if (values==null)
            return 0;
        return values.size();
    }
}