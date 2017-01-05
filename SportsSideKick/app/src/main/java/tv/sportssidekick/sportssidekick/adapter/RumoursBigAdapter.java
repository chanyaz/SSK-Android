package tv.sportssidekick.sportssidekick.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;

/**
 * Created by Djordje on 04/01/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class RumoursBigAdapter extends RecyclerView.Adapter<RumoursBigAdapter.ViewHolder> {
    private static final String TAG = "RumoursBigAdapter";

    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        @Nullable
        @BindView(R.id.fragment_rumors_single_rumour_info)
        TextView rowInfo;
        @Nullable
        @BindView(R.id.fragment_rumors_single_rumour_time)
        TextView rowTime;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public RumoursBigAdapter(Context context) {
    }

    @Override
    public RumoursBigAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        RumoursBigAdapter.ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rumours_big, parent, false);
        viewHolder = new RumoursBigAdapter.ViewHolder(view);
        // view.setLayoutParams(new RecyclerView.LayoutParams((recyclerViewWidth - R.dimen.padding_16)/2, RecyclerView.LayoutParams.WRAP_CONTENT));
        return  viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {
        return 4;
    }
}