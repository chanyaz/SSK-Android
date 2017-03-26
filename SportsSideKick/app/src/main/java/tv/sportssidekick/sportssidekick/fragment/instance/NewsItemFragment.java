package tv.sportssidekick.sportssidekick.fragment.instance;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.NewsAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.model.news.NewsItem;
import tv.sportssidekick.sportssidekick.model.news.NewsModel;
import tv.sportssidekick.sportssidekick.service.BusEvent;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Djordje Krutil on 30.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class NewsItemFragment extends BaseFragment{

    private static final String TAG = "NewsItemFragment";
    @BindView(R.id.content_image)
    ImageView imageHeader;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.strap)
    TextView strap;
    @BindView(R.id.content_text)
    TextView content;

    NewsItem news;

    public NewsItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news_item, container, false);
        ButterKnife.bind(this, view);

        NewsItem item = NewsModel.getInstance().getCachedItemById(getPrimaryArgument());

        DisplayImageOptions imageOptions = Utility.imageOptionsImageLoader();
        ImageLoader.getInstance().displayImage(item.getImage(), imageHeader, imageOptions);
        title.setText(item.getTitle());
        strap.setText(item.getStrap());
        content.setText(item.getContent());

        return view;
    }
}
