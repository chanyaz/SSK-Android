package base.app.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import base.app.R;
import base.app.fragment.FragmentEvent;
import base.app.fragment.instance.NewsItemFragment;
import base.app.model.wall.WallNews;
import base.app.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Djordje on 10/01/2017.
 */

public class RumoursNewsListAdapter extends RecyclerView.Adapter<RumoursNewsListAdapter.ViewHolder> {

    private static final int BIG_ITEM = 1;
    private static final int ITEM = 2;
    private List<WallNews> values = new ArrayList<>();

    Context context;
    public List<WallNews> getRumours() {
        return values;
    }

    public void addRumours(List<WallNews> rumours){
        values.addAll(rumours);
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
    }

    @Override
    public RumoursNewsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        View view;
        int layoutResource;

        if(viewType == BIG_ITEM){
            layoutResource = R.layout.row_rumours_big;
        }else{
            layoutResource = R.layout.row_rumours;
        }
        view = LayoutInflater.from(parent.getContext()).inflate(layoutResource, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final WallNews info = values.get(position);
        if (getItemViewType(position) == BIG_ITEM) {
            if (info.getSubTitle() != null) {
                holder.bigRumourInfo.setText(info.getSubTitle());
            } else {
                holder.bigRumourInfo.setText(info.getTitle());
            }
            String time = DateUtils.getRelativeTimeSpanString(
                info.getTimestamp().longValue(),
                Utility.getCurrentTime(),
                DateUtils.HOUR_IN_MILLIS
            ).toString();
            holder.bigRumourTime.setText(time);

        } else if (getItemViewType(position) == ITEM) {
            holder.description.setText(info.getTitle());
            String time = "Recent";
            holder.time.setText(time);
        }
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
        if (values != null) {
            return values.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if(Utility.isPhone(context) ){ // on phone, first 2 items are different
            if(position<2) {
                return BIG_ITEM;
            }
        } else if (position <=3){ // on tablet, first 4 items different from others
            return BIG_ITEM;
        }
        return ITEM;
    }
}