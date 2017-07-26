package base.app.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import base.app.model.wall.WallBase;
import base.app.util.Utility;
import base.app.util.ui.GridSpacingItemDecoration;
import base.app.util.ui.LinearItemDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import base.app.R;
import base.app.fragment.FragmentEvent;
import base.app.fragment.instance.NewsItemFragment;
import base.app.model.wall.WallNews;

/**
 * Created by Djordje on 10/01/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class RumoursNewsListAdapter extends RecyclerView.Adapter<RumoursNewsListAdapter.ViewHolder> {

    private static final String TAG = "Rumours Small Adapter";
    private static final int IMAGE = 2;
    private static final int BIG_ITEM = 3;
    private static final int ITEM = 4;
    private List<WallNews> values = new ArrayList<>();

    Context context;
    private int countOfTopRumours;
    public List<WallNews> getRumours() {
        return values;
    }

    public void addRumours(List<WallNews> rumours,int countOfTopRumours){
        values.addAll(rumours);
        this.countOfTopRumours = countOfTopRumours;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        @Nullable
        @BindView(R.id.row_rumors_description)
        TextView description;
        @Nullable
        @BindView(R.id.row_rumors_time)
        TextView time;

        @Nullable
        @BindView(R.id.fragment_rumors_single_rumour_info_1)
        TextView rumourDescription1;
        @Nullable
        @BindView(R.id.fragment_rumors_single_rumour_info_2)
        TextView rumourDescription2;
        @Nullable
        @BindView(R.id.fragment_rumors_single_rumour_info_3)
        TextView rumourDescription3;
        @Nullable
        @BindView(R.id.fragment_rumors_single_rumour_info_4)
        TextView rumourDescription4;
        @Nullable
        @BindView(R.id.fragment_rumors_single_rumour_time_1)
        TextView rumourTime1;
        @Nullable
        @BindView(R.id.fragment_rumors_single_rumour_time_2)
        TextView rumourTime2;
        @Nullable
        @BindView(R.id.fragment_rumors_single_rumour_time_3)
        TextView rumourTime3;
        @Nullable
        @BindView(R.id.fragment_rumors_single_rumour_time_4)
        TextView rumourTime4;
        @Nullable
        @BindView(R.id.fragment_rumors_top_headline)
        TextView headline;
        @Nullable
        @BindView(R.id.fragment_rumors_top_image)
        ImageView image;
        @Nullable
        @BindView(R.id.fragment_rumors_all_big_list)
        RecyclerView top4news;

        @Nullable
        @BindView(R.id.fragment_rumors_single_rumour_info)
        TextView bigRumourInfo;

        @Nullable
        @BindView(R.id.fragment_rumors_single_rumour_time)
        TextView bigRumourTime;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public RumoursNewsListAdapter(Context context) {
        values = new ArrayList<>();
        this.context = context;
        WallNews image = new WallNews();
        values.add(0,image);
    }

    @Override
    public RumoursNewsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        View view;
        if (viewType == IMAGE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rumours_image, parent, false);
        } else if(viewType == BIG_ITEM){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rumours_big, parent, false);

        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rumours, parent, false);
        }
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (getItemViewType(position) == IMAGE) {
            //IMAGE ONLY
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {}
            });
        }else if (getItemViewType(position) == BIG_ITEM) {
            final WallNews bigInfo = values.get(position);
            if (bigInfo.getSubTitle() != null) {
                holder.bigRumourInfo.setText(bigInfo.getSubTitle());
            } else {
                holder.bigRumourInfo.setText(bigInfo.getTitle());
        }
            String time = "" + DateUtils.getRelativeTimeSpanString(bigInfo.getTimestamp().longValue(), System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS);
            holder.bigRumourTime.setText(time);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentEvent fe = new FragmentEvent(NewsItemFragment.class);
                    fe.setId("UNOFFICIAL$$$" + bigInfo.getPostId());
                    EventBus.getDefault().post(fe);
                }
            });
        }
        else if (getItemViewType(position) == ITEM) {
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

    }

    @Override
    public int getItemCount() {
        if (values != null) {
            return values.size();
        }
        return 1;

    }

    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        if (position == 0) {
            return IMAGE;
        } else if(countOfTopRumours == 2){
                if(position==1 || position ==2){
                    return BIG_ITEM;
                }else {
                    return ITEM;
                }
            }else if(countOfTopRumours == 4){
                if(position==1 || position ==2 || position ==3 || position ==4){
                    return BIG_ITEM;
                }else {
                    return ITEM;
                }
        }
        return ITEM;
    }
}