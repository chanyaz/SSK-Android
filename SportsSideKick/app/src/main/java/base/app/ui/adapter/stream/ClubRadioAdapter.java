package base.app.ui.adapter.stream;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import base.app.R;
import base.app.data.FragmentEvent;
import base.app.ui.fragment.stream.RadioStationFragment;
import base.app.data.tv.Station;
import base.app.util.ui.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;

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
                FragmentEvent fragmentEvent = new FragmentEvent(RadioStationFragment.class);
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
        String imageUrl = info.getCoverImageUrl();
        ImageLoader.displayImage(imageUrl, holder.image, null);
    }

    @Override
    public int getItemCount() {
        if (values==null)
            return 0;
        return values.size();
    }
}
