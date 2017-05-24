package base.app.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import base.app.R;
import base.app.fragment.FragmentEvent;
import base.app.fragment.instance.NewsItemFragment;
import base.app.model.wall.WallNews;

/**
 * Created by Djordje on 04/01/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class RumoursTopFourNewsAdapter extends RecyclerView.Adapter<RumoursTopFourNewsAdapter.ViewHolder> {
    private static final String TAG = "RumoursTopFourNewsAdapter";

    private ArrayList<WallNews> values=null;

    public ArrayList<WallNews> getValues() {
        return values;
    }

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

    public RumoursTopFourNewsAdapter() {
        values = new ArrayList<>();
    }

    @Override
    public RumoursTopFourNewsAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        RumoursTopFourNewsAdapter.ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rumours_big, parent, false);
        viewHolder = new RumoursTopFourNewsAdapter.ViewHolder(view);
        // view.setLayoutParams(new RecyclerView.LayoutParams((recyclerViewWidth - R.dimen.padding_16)/2, RecyclerView.LayoutParams.WRAP_CONTENT));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!values.isEmpty()) {
            final WallNews info = values.get(position);
            if (info.getSubTitle() != null) {
                holder.rowInfo.setText(info.getSubTitle());
            } else {
                holder.rowInfo.setText(info.getTitle());
            }
            String time = "" + DateUtils.getRelativeTimeSpanString(info.getTimestamp().longValue(), System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS);
            holder.rowTime.setText(time);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentEvent fe = new FragmentEvent(NewsItemFragment.class);
                    fe.setId("UNOFFICIAL$$$" + info.getPostId());
                    EventBus.getDefault().post(fe);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        if (values != null)
            return values.size();
        else
            return 0;
    }
}