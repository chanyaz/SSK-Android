package base.app.fragment.instance;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
import base.app.adapter.NewsAdapter;
import base.app.fragment.BaseFragment;
import base.app.model.news.NewsModel;
import base.app.model.news.NewsPageEvent;
import base.app.model.wall.WallNews;
import base.app.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Djordje on 12/29/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 * <p>
 * A simple {@link BaseFragment} subclass.
 */

public class NewsFragment extends BaseFragment {

    final NewsModel.NewsType type = NewsModel.NewsType.OFFICIAL;

    NewsAdapter adapter;
    @BindView(R.id.swipe_refresh_layout)
    SwipyRefreshLayout swipeRefreshLayout;
    @BindView(R.id.news_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    AVLoadingIndicatorView progressBar;

    @BindView(R.id.top_image)
    ImageView topImage;

    @BindView(R.id.top_caption)
    TextView topCaption;

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);

        if(Utility.isTablet(getContext())){
            topImage.setVisibility(View.GONE);
            topCaption.setVisibility(View.GONE);
        } else {
            Glide.with(getActivity()).load(R.drawable.image_wall_background).load(topImage);
        }

        adapter = new NewsAdapter();

        recyclerView.setAdapter(adapter);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        int space = (int) getResources().getDimension(R.dimen.padding_12);

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