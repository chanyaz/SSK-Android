package tv.sportssidekick.sportssidekick.fragment.instance;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.NewsAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.model.news.NewsItem;
import tv.sportssidekick.sportssidekick.model.news.NewsModel;

/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 * <p>
 * A simple {@link BaseFragment} subclass.
 */

public class NewsFragment extends BaseFragment {

    NewsAdapter adapter;
    @BindView(R.id.swipe_refresh_layout)
    SwipyRefreshLayout swipeRefreshLayout;
    @BindView(R.id.news_recycler_view)
    RecyclerView recyclerView;

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);

        NewsModel.getDefault().initialze(NewsItem.NewsType.OFFICIAL, 15).loadPage();
        adapter = new NewsAdapter();

        recyclerView.setAdapter(adapter);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                NewsModel.getDefault().initialze(NewsItem.NewsType.OFFICIAL, 15).loadPage();
            }
        });
        return view;

    }

    @Subscribe
    public void onNewsReceived(List<NewsItem> list) {
        swipeRefreshLayout.setRefreshing(false);
        adapter.getValues().addAll(list);
        adapter.notifyDataSetChanged();
    }

}
