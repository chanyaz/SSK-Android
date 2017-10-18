package base.app.fragment.instance;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.Subscribe;

import base.app.R;
import base.app.adapter.RumoursNewsListAdapter;
import base.app.fragment.BaseFragment;
import base.app.model.news.NewsModel;
import base.app.model.news.NewsPageEvent;
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

    RumoursNewsListAdapter rumoursNewsListAdapter;

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

        // On both phone and tablet, Rumours page looks similar to News, so we are going to reuse the layout for both.
        // Note that image for header background is different on tablet and phone for SCP, for Brugge there are duplicated resources.
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);

        if(Utility.isPhone(getContext())){
            topImage.setImageResource(R.drawable.image_rumours_background);
        } else {
            topImage.setImageResource(R.drawable.image_rumours_background_tablet);
        }
        topCaption.setText(R.string.rumours_caption);

        hideElements(true);

        // On tablet, span size is 2 (first 4 items are half width of screen)
        int spanSize = Utility.isTablet(getContext()) ? 2 : 1;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), spanSize);

        recyclerView.setLayoutManager(gridLayoutManager);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (Utility.isTablet(getActivity())) {
                    if(position<4){
                        return 1; // first 4 items are half width
                    } else {
                        return 2; // other items are full width
                    }
                } else {
                    return 1;
                }
            }
        });
        rumoursNewsListAdapter = new RumoursNewsListAdapter(getActivity());
        recyclerView.setAdapter(rumoursNewsListAdapter);

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
            rumoursNewsListAdapter.addRumours(event.getValues());
            rumoursNewsListAdapter.notifyDataSetChanged();
        }
        hideElements(false);
    }

    private void hideElements(boolean hide) {
        if(progressBar!=null){
            progressBar.setVisibility(hide ? View.VISIBLE : View.GONE);
        }
    }
}