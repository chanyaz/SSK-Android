package tv.sportssidekick.sportssidekick.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.fragment.instance.NewsItemFragment;
import tv.sportssidekick.sportssidekick.model.wall.WallNews;

/**
 * Created by Djordje on 10/01/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class RumoursNewsListAdapter extends RecyclerView.Adapter<RumoursNewsListAdapter.ViewHolder> {

    private static final String TAG = "Rumours Small Adapter";

    private List<WallNews> values;

    public List<WallNews> getValues() {
        return values;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        @Nullable
        @BindView(R.id.row_rumors_description)
        TextView description;
        @Nullable
        @BindView(R.id.row_rumors_time)
        TextView time;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public RumoursNewsListAdapter()
    {
        values = new ArrayList<>();
    }

    @Override
    public RumoursNewsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rumours, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final WallNews info = values.get(position);
        holder.description.setText(info.getTitle());
        String time = "Recent";
        holder.time.setText(time);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentEvent fe = new FragmentEvent(NewsItemFragment.class);
                fe.setId("UNOFFICIAL$$$" + info.getPostId());
                EventBus.getDefault().post(fe);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (values ==null)
            return 0;
        return values.size();
    }
}