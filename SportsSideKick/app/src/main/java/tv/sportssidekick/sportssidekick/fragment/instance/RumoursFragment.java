package tv.sportssidekick.sportssidekick.fragment.instance;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.RumoursNewsListAdapter;
import tv.sportssidekick.sportssidekick.adapter.RumoursTopFourNewsAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.model.news.NewsItem;
import tv.sportssidekick.sportssidekick.model.news.NewsModel;
import tv.sportssidekick.sportssidekick.model.news.NewsPageEvent;
import tv.sportssidekick.sportssidekick.util.GridSpacingItemDecoration;

/**
 * Created by Djordje on 01/03/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 *
 * A simple {@link BaseFragment} subclass.
 */

public class RumoursFragment extends BaseFragment {

    RumoursNewsListAdapter rumoursSmallAdapter;
    RumoursTopFourNewsAdapter top4newsAdapter;

    @BindView(R.id.fragment_rumors_all_single_rumours_container)
    RelativeLayout singleRumoursContainer;
    @BindView(R.id.fragment_rumors_recycler_view)
    RecyclerView rumourRecyclerView;
    @BindView(R.id.fragment_rumors_single_rumour_info_1)
    TextView rumourDescription1;
    @BindView(R.id.fragment_rumors_single_rumour_info_2)
    TextView rumourDescription2;
    @BindView(R.id.fragment_rumors_single_rumour_info_3)
    TextView rumourDescription3;
    @BindView(R.id.fragment_rumors_single_rumour_info_4)
    TextView rumourDescription4;
    @BindView(R.id.fragment_rumors_single_rumour_time_1)
    TextView rumourTime1;
    @BindView(R.id.fragment_rumors_single_rumour_time_2)
    TextView rumourTime2;
    @BindView(R.id.fragment_rumors_single_rumour_time_3)
    TextView rumourTime3;
    @BindView(R.id.fragment_rumors_single_rumour_time_4)
    TextView rumourTime4;
    @BindView(R.id.fragment_rumors_top_headline)
    TextView headline;
    @BindView(R.id.fragment_rumors_top_image)
    ImageView image;
    @BindView(R.id.fragment_rumors_all_big_list)
    RecyclerView top4news;
    @BindView(R.id.rumours_swipe_refresh_layout)
    SwipyRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progress_bar)
    AVLoadingIndicatorView progressBar;
    @BindView(R.id.fragment_rumors_root)
    ScrollView fragmentContainer;

    public RumoursFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rumours, container, false);
        ButterKnife.bind(this, view);

        hideElements(true);

        NewsModel.getDefault().initialze(NewsItem.NewsType.UNOFFICIAL, 15).loadPage();  // TODO This will delete news model - we need two singletons?

        rumourRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        rumoursSmallAdapter = new RumoursNewsListAdapter();
        rumourRecyclerView.setAdapter(rumoursSmallAdapter);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false);
        top4news.addItemDecoration(new GridSpacingItemDecoration(2,16,true));
        top4news.setLayoutManager(layoutManager);

        top4newsAdapter = new RumoursTopFourNewsAdapter();
        top4news.setAdapter(top4newsAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                NewsModel.getDefault().initialze(NewsItem.NewsType.UNOFFICIAL, 15).loadPage();
            }
        });

        return view;
    }

    @Subscribe
    public void onNewsReceived(NewsPageEvent event) {
        swipeRefreshLayout.setRefreshing(false);
        if (!event.getValues().isEmpty() && event.getValues().size()>3)
        {
            top4newsAdapter.getValues().addAll(event.getValues().subList(0,4));
            rumoursSmallAdapter.getValues().addAll(event.getValues().subList(4, event.getValues().size()-1));

            top4newsAdapter.notifyDataSetChanged();
            rumoursSmallAdapter.notifyDataSetChanged();
        }
        hideElements(false);
    }

    private void hideElements(boolean hide)
    {
        if (hide)
        {
            fragmentContainer.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
        else
        {
            fragmentContainer.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }
}