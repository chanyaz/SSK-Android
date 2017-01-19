package tv.sportssidekick.sportssidekick.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Filip on 1/17/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class StashAdapter extends RecyclerView.Adapter<StashAdapter.ViewHolder> {

    private static final String TAG = "Stash Adapter";

    private List<Pair<String,String>> values;

    public List<Pair<String,String>> getValues() {
        return values;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        @Nullable
        @BindView(R.id.icon)
        ImageView icon;
        @Nullable
        @BindView(R.id.award_name)
        TextView awardName;
        @BindView(R.id.profile_frame)
        TextView profileFrame;
        @BindView(R.id.received_from)
        TextView receivedFrom;
        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public StashAdapter() {
        values= new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stash_item, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        DisplayImageOptions imageOptions = Utility.imageOptionsImageLoader();
        final Pair<String,String> info = values.get(position);
        holder.awardName.setText(info.first);
        holder.profileFrame.setText(info.second);
    }

    @Override
    public int getItemCount() {
        if (values==null)
            return 0;
        return values.size();
    }
}