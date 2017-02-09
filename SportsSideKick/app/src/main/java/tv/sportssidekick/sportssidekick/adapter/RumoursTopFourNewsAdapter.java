package tv.sportssidekick.sportssidekick.adapter;

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
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.fragment.instance.NewsItemFragment;
import tv.sportssidekick.sportssidekick.model.news.NewsItem;

/**
 * Created by Djordje on 04/01/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class RumoursTopFourNewsAdapter extends RecyclerView.Adapter<RumoursTopFourNewsAdapter.ViewHolder> {
    private static final String TAG = "RumoursTopFourNewsAdapter";

    private ArrayList<NewsItem> values;

    public ArrayList<NewsItem> getValues() {
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

    public RumoursTopFourNewsAdapter()
    {
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
        if (!values.isEmpty())
        {
            final NewsItem info = values.get(position);
            holder.rowInfo.setText(info.getStrap());
            String time = "" + DateUtils.getRelativeTimeSpanString(Long.valueOf(info.getPubDate()), System.currentTimeMillis() / 1000L, DateUtils.MINUTE_IN_MILLIS);
            holder.rowTime.setText(time);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentEvent fe = new FragmentEvent(NewsItemFragment.class);
                    fe.setId(info.getId());
                    EventBus.getDefault().post(fe);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return 4;
    }
}