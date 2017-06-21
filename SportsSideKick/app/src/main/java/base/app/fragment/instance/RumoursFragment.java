package base.app.fragment.instance;


import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.Subscribe;

import base.app.R;
import base.app.activity.PhoneLoungeActivity;
import base.app.adapter.RumoursNewsListAdapter;
import base.app.adapter.RumoursTopFourNewsAdapter;
import base.app.fragment.BaseFragment;
import base.app.model.news.NewsModel;
import base.app.model.news.NewsPageEvent;
import base.app.util.Utility;
import base.app.util.ui.GridSpacingItemDecoration;
import base.app.util.ui.LinearItemDecoration;
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

    RumoursNewsListAdapter rumoursSmallAdapter;
    RumoursTopFourNewsAdapter topNewsAdapter;

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
    NestedScrollView fragmentContainer;
    int countOfTopRumours;

    RecyclerTouchListener onTouchListener;

    public RumoursFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(getActivity() instanceof PhoneLoungeActivity)
            ((PhoneLoungeActivity) getActivity()).setMarginTop(false);
        View view = inflater.inflate(R.layout.fragment_rumours, container, false);
        ButterKnife.bind(this, view);

        hideElements(true);
        GridLayoutManager layoutManager;
        if (Utility.isTablet(getActivity())) {
            layoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
            top4news.addItemDecoration(new GridSpacingItemDecoration(2, 16, true));
            top4news.setLayoutManager(layoutManager);
            countOfTopRumours = 4;
        } else {
            double space = Utility.getDisplayHeight(getActivity()) * 0.015;
            top4news.addItemDecoration(new LinearItemDecoration((int) space, false, true));
            top4news.setLayoutManager(new LinearLayoutManager(getContext()));
            countOfTopRumours = 2;
        }
        rumourRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        rumoursSmallAdapter = new RumoursNewsListAdapter();
        rumourRecyclerView.setNestedScrollingEnabled(false);
        rumourRecyclerView.setAdapter(rumoursSmallAdapter);


        topNewsAdapter = new RumoursTopFourNewsAdapter();
        top4news.setAdapter(topNewsAdapter);


        if (NewsModel.getInstance().getAllCachedItems(type).size() > 0) {
            NewsPageEvent event = new NewsPageEvent(NewsModel.getInstance().getAllCachedItems(type));
            if (event != null) {
                onNewsReceived(event);
            } else {
                NewsModel.getInstance().setLoading(false, type);
                NewsModel.getInstance().loadPage(type);
            }
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

        onTouchListener = new RecyclerTouchListener(getActivity(), rumourRecyclerView);

        if(!Utility.isTablet(getActivity())){ // On tablet there is no swipe so we need this only for Phone
        onTouchListener.setSwipeOptionViews(R.id.row_rumours_swipe_share)
                .setSwipeable(R.id.row_rumours_foreground_container, R.id.row_rumours_background_container, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        if (viewID == R.id.row_rumours_swipe_share) {
                            // Handle click on Share Button
                            Toast.makeText(getActivity(),"To be implemented !",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
        return view;
    }

    @Subscribe
    public void onNewsReceived(NewsPageEvent event) {
        swipeRefreshLayout.setRefreshing(false);
        if (!event.getValues().isEmpty() && event.getValues().size() > countOfTopRumours - 1) {
            if (topNewsAdapter.getValues().size() == 0)
                topNewsAdapter.getValues().addAll(event.getValues().subList(0, countOfTopRumours));
            rumoursSmallAdapter.getValues().addAll(event.getValues().subList(countOfTopRumours, event.getValues().size() - 1));

            topNewsAdapter.notifyDataSetChanged();
            rumoursSmallAdapter.notifyDataSetChanged();
        }
        hideElements(false);
    }

    private void hideElements(boolean hide) {
        if (hide) {
            fragmentContainer.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            fragmentContainer.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        rumourRecyclerView.addOnItemTouchListener(onTouchListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        rumourRecyclerView.removeOnItemTouchListener(onTouchListener);
    }
}