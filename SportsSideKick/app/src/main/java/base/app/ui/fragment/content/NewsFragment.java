package base.app.ui.fragment.content;

import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.List;

import base.app.R;
import base.app.data.news.NewsModel.NewsType;
import base.app.ui.adapter.content.NewsAdapter;
import base.app.ui.fragment.base.BaseFragment;
import base.app.data.news.NewsModel;
import base.app.data.news.NewsPageEvent;
import base.app.data.wall.WallNews;
import butterknife.BindView;
import butterknife.ButterKnife;

import static base.app.data.news.NewsModel.NewsType.*;

/**
 * Created by Djordje on 12/29/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class NewsFragment extends BaseFragment {

    NewsType type = OFFICIAL;
    NewsAdapter adapter;
    @BindView(R.id.swipeRefreshLayout)
    SwipyRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    AVLoadingIndicatorView progressBar;
    @BindView(R.id.topImage)
    ImageView topImage;
    @BindView(R.id.topСaption)
    TextView topCaption;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);

        Glide.with(getContext()).load(R.drawable.image_wall_background).load(topImage);

        adapter = new NewsAdapter();

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);

        List<WallNews> existingItems = NewsModel.getInstance().getAllCachedItems(type);
        if (existingItems!=null && existingItems.size() > 0)
        {
            adapter.getValues().addAll(existingItems);
            progressBar.setVisibility(View.GONE);
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
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        adapter.getValues().addAll(event.getValues());
        adapter.notifyDataSetChanged();
    }
}