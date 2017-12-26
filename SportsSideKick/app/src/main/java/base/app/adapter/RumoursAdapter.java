package base.app.adapter;

import android.support.annotation.NonNull;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import base.app.R;
import base.app.fragment.FragmentEvent;
import base.app.fragment.instance.NewsItemFragment;
import base.app.model.wall.WallNews;

/**
 * Created by Djordje on 10/01/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class RumoursAdapter extends NewsAdapter {

    @Override
    protected int getItemLayoutId() {
        return R.layout.wall_item_rumours;
    }

    @Override
    public void onBindViewHolder(WallAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        final WallNews rumour = values.get(position);
        boolean hasImage = rumour.getCoverImageUrl() != null && !rumour.getCoverImageUrl().isEmpty();
        if (hasImage) {
            holder.view.findViewById(R.id.spacer).setVisibility(View.GONE);
        } else {
            holder.view.findViewById(R.id.spacer).setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    protected View.OnClickListener getClickListener(final WallNews item) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentEvent fe = new FragmentEvent(NewsItemFragment.class);
                fe.setId("UNOFFICIAL$$$" + item.getPostId());
                EventBus.getDefault().post(fe);
            }
        };
    }
}