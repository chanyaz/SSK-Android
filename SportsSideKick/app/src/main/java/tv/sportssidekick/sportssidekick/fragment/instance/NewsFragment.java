package tv.sportssidekick.sportssidekick.fragment.instance;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.NewsAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.model.news.NewsItem;
import tv.sportssidekick.sportssidekick.model.news.NewsModel;
import tv.sportssidekick.sportssidekick.model.news.NewsPageEvent;

/**
 * Created by Djordje on 12/29/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 * <p>
 * A simple {@link BaseFragment} subclass.
 */

public class NewsFragment extends BaseFragment {

    final NewsItem.NewsType type = NewsItem.NewsType.OFFICIAL;

    NewsAdapter adapter;
    @BindView(R.id.swipe_refresh_layout)
    SwipyRefreshLayout swipeRefreshLayout;
    @BindView(R.id.news_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    AVLoadingIndicatorView progres;

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);

        adapter = new NewsAdapter();

        recyclerView.setAdapter(adapter);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        List<NewsItem> existingItems = NewsModel.getInstance().getAllCachedItems(type);
        if (existingItems!=null && existingItems.size() > 0)
        {
            adapter.getValues().addAll(existingItems);
            progres.setVisibility(View.GONE);
        }
        else {
            NewsModel.getInstance().loadPage(type);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                NewsModel.getInstance().setLoading(false,type);
                NewsModel.getInstance().loadPage(type);
            }
        });
        return view;

    }

    @Subscribe
    public void onNewsReceived(NewsPageEvent event) {
        progres.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        adapter.getValues().addAll(event.getValues());
        adapter.notifyDataSetChanged();
    }
}
