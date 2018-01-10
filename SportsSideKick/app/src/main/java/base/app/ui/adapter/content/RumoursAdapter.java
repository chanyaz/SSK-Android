package base.app.ui.adapter.content;

import android.support.annotation.NonNull;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import base.app.R;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.ui.fragment.content.NewsItemFragment;
import base.app.data.wall.WallNews;

import static android.view.View.*;

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

        WallNews rumour = values.get(position);
        boolean hasImage = rumour.getCoverImageUrl() != null && !rumour.getCoverImageUrl().isEmpty();
        holder.view.findViewById(R.id.spacer).setVisibility(hasImage ? GONE : VISIBLE);
    }

    @NonNull
    protected OnClickListener getClickListener(final WallNews item) {
        return new OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentEvent fe = new FragmentEvent(NewsItemFragment.class);
                fe.setId("UNOFFICIAL$$$" + item.getPostId());
                EventBus.getDefault().post(fe);
            }
        };
    }
}