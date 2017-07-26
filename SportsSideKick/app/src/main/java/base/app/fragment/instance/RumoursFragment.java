package base.app.fragment.instance;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import base.app.R;
import base.app.adapter.RumoursNewsListAdapter;
import base.app.fragment.BaseFragment;
import base.app.model.AlertDialogManager;
import base.app.model.Model;
import base.app.model.news.NewsModel;
import base.app.model.news.NewsPageEvent;
import base.app.model.wall.WallModel;
import base.app.model.wall.WallNews;
import base.app.util.SoundEffects;
import base.app.util.Utility;
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

    @Nullable
    @BindView(R.id.fragment_rumors_all_single_rumours_container)
    RelativeLayout singleRumoursContainer;
    @BindView(R.id.fragment_rumors_recycler_view)
    RecyclerView rumourRecyclerView;

    @Nullable
    @BindView(R.id.rumours_swipe_refresh_layout)
    SwipyRefreshLayout swipeRefreshLayout;
    @Nullable
    @BindView(R.id.progress_bar)
    AVLoadingIndicatorView progressBar;

    RecyclerTouchListener onTouchListener;

    public RumoursFragment() {
        // Required empty public constructor
    }
    int spanSize = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setMarginTop(false);
        View view = inflater.inflate(R.layout.fragment_rumours, container, false);
        ButterKnife.bind(this, view);

        hideElements(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),spanSize);
        rumourRecyclerView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(Utility.isTablet(getActivity())){
                    switch (position){
                        case 0:
                            return 2;
                        case 1:
                            return 1;
                        case 2:
                            return 1;
                        case 3:
                            return 1;
                        case 4:
                            return 1;
                        default:
                            return 2;
                    }
                }else {
                    return 1;
                }

            }
        });
        rumoursSmallAdapter = new RumoursNewsListAdapter(getActivity());
        rumourRecyclerView.setAdapter(rumoursSmallAdapter);

        onTouchListener = new RecyclerTouchListener(getActivity(), rumourRecyclerView);

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

        onTouchListener
                .setSwipeOptionViews(R.id.row_rumours_swipe_share)
                .setSwipeable(
                        R.id.row_rumours_foreground_container,
                        R.id.row_rumours_background_container,
                        swipeListener
                );

        return view;
    }

    RecyclerTouchListener.OnSwipeOptionsClickListener swipeListener = new RecyclerTouchListener.OnSwipeOptionsClickListener() {
        @Override
        public void onSwipeOptionClicked(int viewID, int position) {
            if (viewID == R.id.row_rumours_swipe_share) {
                // Handle click on Share Button
                pinToWall(rumoursSmallAdapter.getRumours().get(position));
            }

        }
    };


    int countOfTopRumours;
    @Subscribe
    public void onNewsReceived(NewsPageEvent event) {
        if (Utility.isTablet(getActivity())) {
            countOfTopRumours = 4;
        } else {
            countOfTopRumours = 2;
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (!event.getValues().isEmpty() && event.getValues().size() > countOfTopRumours - 1) {
            rumoursSmallAdapter.addRumours(event.getValues(), countOfTopRumours);
            rumoursSmallAdapter.notifyDataSetChanged();
        }
        hideElements(false);

        setUnswipeable();

    }

    private void setUnswipeable() {
        List<Integer> nonSwipePositions = new ArrayList<>();
        if (Utility.isTablet(getActivity())) {
            nonSwipePositions.clear();
            nonSwipePositions.add(0);
            nonSwipePositions.add(1);
            nonSwipePositions.add(2);
            nonSwipePositions.add(3);
            nonSwipePositions.add(4);
        } else {
            nonSwipePositions.clear();
            nonSwipePositions.add(0);
            nonSwipePositions.add(1);
            nonSwipePositions.add(2);
        }
        onTouchListener.setUnSwipeableRows(nonSwipePositions.toArray(new Integer[nonSwipePositions.size()]));
    }

    private void hideElements(boolean hide) {
        if (hide) {
            //  fragmentContainer.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            //  fragmentContainer.setVisibility(View.VISIBLE);
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

    public void pinToWall(final WallNews item) {
        if (Model.getInstance().isRealUser()) {
            AlertDialogManager.getInstance().showAlertDialog(getContext().getResources().getString(R.string.news_post_to_wall_title), getContext().getResources().getString(R.string.news_post_to_wall_message),
                    new View.OnClickListener() {// Cancel
                        @Override
                        public void onClick(View v) {
                            getActivity().onBackPressed();
                        }
                    }, new View.OnClickListener() { // Confirm
                        @Override
                        public void onClick(View v) {
                            WallNews itemToPost = new WallNews();
                            itemToPost.setBodyText(item.getBodyText());
                            itemToPost.setTitle(item.getTitle());

                            if (item.getSource() != null) {
                                itemToPost.setSubTitle(item.getSource());
                            } else {
                                itemToPost.setSubTitle("");
                            }

                            itemToPost.setTimestamp((double) System.currentTimeMillis());
                            itemToPost.setCoverAspectRatio(0.666666f);
                            if (item.getCoverImageUrl() != null) {
                                itemToPost.setCoverImageUrl(item.getCoverImageUrl());
                            }
                            WallModel.getInstance().mbPost(itemToPost);
                            getActivity().onBackPressed();
                        }
                    });
        } else {

        }
        SoundEffects.getDefault().playSound(SoundEffects.ROLL_OVER);
    }


}