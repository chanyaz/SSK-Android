package tv.sportssidekick.sportssidekick.fragment.instance;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.model.news.NewsItem;
import tv.sportssidekick.sportssidekick.model.news.NewsModel;
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
    @BindView(R.id.close_news_button)
    Button close;
    @BindView(R.id.share_news_to_wall_button)
    Button share;

    public NewsItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news_item, container, false);
        ButterKnife.bind(this, view);

        String id = getPrimaryArgument();
        NewsItem.NewsType type = NewsItem.NewsType.OFFICIAL;
        if(id.contains("UNOFFICIAL$$$")){
            id = id.replace("UNOFFICIAL$$$","");
             type = NewsItem.NewsType.UNOFFICIAL;
        }
        NewsItem item = NewsModel.getInstance().getCachedItemById(id,type);

        DisplayImageOptions imageOptions = Utility.imageOptionsImageLoader();
        ImageLoader.getInstance().displayImage(item.getImage(), imageHeader, imageOptions);
        title.setText(item.getTitle());
        String time = "" + DateUtils.getRelativeTimeSpanString(item.getPubDate().longValue(), System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS);
        if (item.getStrap() != null)
        {
            strap.setText(item.getStrap() + " - " + time);
        }else {
            strap.setText(time);
        }

        content.setText(item.getContent());

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        return view;
    }
}
