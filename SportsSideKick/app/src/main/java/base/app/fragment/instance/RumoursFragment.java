package base.app.fragment.instance;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.Subscribe;

import base.app.R;
import base.app.adapter.RumoursAdapter;
import base.app.fragment.BaseFragment;
import base.app.model.news.NewsModel;
import base.app.model.news.NewsPageEvent;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Djordje on 01/03/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 * <p>
 * A simple {@link BaseFragment} subclass.
 */

public class RumoursFragment extends BaseFragment {

    final NewsModel.NewsType type = NewsModel.NewsType.UNOFFICIAL;

    RumoursAdapter rumoursAdapter;

    @BindView(R.id.news_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipyRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progress_bar)
    AVLoadingIndicatorView progressBar;

    @BindView(R.id.top_image)
    ImageView topImage;

    @BindView(R.id.top_caption)
    TextView topCaption;

    public RumoursFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);

        Glide.with(getContext()).load(R.drawable.image_wall_background).into(topImage);
        topCaption.setText(R.string.rumours_caption);

        hideElements(true);

        rumoursAdapter = new RumoursAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(rumoursAdapter);

        if (NewsModel.getInstance().getAllCachedItems(type).size() > 0) {
            NewsPageEvent event = new NewsPageEvent(NewsModel.getInstance().getAllCachedItems(type));
            onNewsReceived(event);
        } else {
            NewsModel.getInstance().loadPage(type);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                NewsModel.getInstance().setLoading(false, type);
                NewsModel.getInstance().loadPage(type);
            }
        });
        return view;
    }

    @Subscribe
    public void onNewsReceived(NewsPageEvent event) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (!event.getValues().isEmpty() && event.getValues().size() > 0) {
            rumoursAdapter.getValues().addAll(event.getValues());
            rumoursAdapter.notifyDataSetChanged();
        }
        hideElements(false);
    }

    private void hideElements(boolean hide) {
        if (progressBar != null) {
            progressBar.setVisibility(hide ? View.VISIBLE : View.GONE);
        }
    }
}