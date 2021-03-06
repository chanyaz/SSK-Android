package base.app.ui.adapter.profile;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import base.app.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Filip on 1/17/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class UserStatsAdapter extends RecyclerView.Adapter<UserStatsAdapter.ViewHolder> {

    private static final String TAG = "UserStats Adapter";

    private List<Pair<String,String>> values;

    public List<Pair<String,String>> getValues() {
        return values;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        @Nullable
        @BindView(R.id.caption)
        TextView caption;
        @Nullable
        @BindView(R.id.value)
        TextView value;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public UserStatsAdapter() {
        values= new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_stats_item, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Pair<String,String> info = values.get(position);
        holder.caption.setText(info.first);
        holder.value.setText(info.second);
    }

    @Override
    public int getItemCount() {
        if (values==null)
            return 0;
        return values.size();
    }
}