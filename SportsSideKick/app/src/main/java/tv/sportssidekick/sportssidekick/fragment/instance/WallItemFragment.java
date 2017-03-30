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
import tv.sportssidekick.sportssidekick.adapter.WallAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.model.news.NewsItem;
import tv.sportssidekick.sportssidekick.model.news.NewsModel;
import tv.sportssidekick.sportssidekick.model.wall.WallBase;
import tv.sportssidekick.sportssidekick.model.wall.WallModel;
import tv.sportssidekick.sportssidekick.model.wall.WallNews;
import tv.sportssidekick.sportssidekick.model.wall.WallPost;
import tv.sportssidekick.sportssidekick.model.wall.WallStoreItem;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Djordje Krutil on 30.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class WallItemFragment extends BaseFragment{

    public static final int VIEW_TYPE_POST_IMAGE = 0;
    public static final int VIEW_TYPE_SMALL_CELL = 1;
    public static final int VIEW_TYPE_SMALL_CELL_WITH_CIRCLE_PROGRESS = 2;
    public static final int VIEW_TYPE_SHOP = 3;
    public static final int VIEW_TYPE_COMMENT = 4;

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

    // HACK! HACK! HACK!HACK! HACK! HACK! HACK! HACK! HACK!

    public WallItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news_item, container, false);
        ButterKnife.bind(this, view);

        String id = getPrimaryArgument();

        WallBase item = WallModel.getInstance().getItemById(id);
        DisplayImageOptions imageOptions = Utility.imageOptionsImageLoader();

        if (item.getType() == VIEW_TYPE_POST_IMAGE) {
            WallPost post = (WallPost)item;
            ImageLoader.getInstance().displayImage(post.getCoverImageUrl(), imageHeader, imageOptions);
            title.setText(post.getTitle());
            content.setText(post.getBodyText());
        }  else if (item.getType() == VIEW_TYPE_SMALL_CELL) {
            WallNews news = (WallNews)item;
            ImageLoader.getInstance().displayImage(news.getCoverImageUrl(), imageHeader, imageOptions);
            title.setText(news.getTitle());
            content.setText(news.getBodyText());
        } else if (item.getType() == VIEW_TYPE_SMALL_CELL_WITH_CIRCLE_PROGRESS) {
            //ne znam sta je
        } else if (item.getType() == VIEW_TYPE_SHOP) {
            WallStoreItem storeItem = (WallStoreItem) item;
            ImageLoader.getInstance().displayImage(storeItem.getCoverImage(), imageHeader, imageOptions);
            title.setText(storeItem.getTitle());
        } else if (item.getType() == VIEW_TYPE_COMMENT) {
            WallNews news = (WallNews)item;
            ImageLoader.getInstance().displayImage(news.getCoverImageUrl(), imageHeader, imageOptions);
            title.setText(news.getTitle());
            content.setText(news.getBodyText());
        } else {
            return null;
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        return view;
    }
}
