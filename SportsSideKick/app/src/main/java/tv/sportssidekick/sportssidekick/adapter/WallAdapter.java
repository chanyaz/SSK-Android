package tv.sportssidekick.sportssidekick.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.fragment.instance.WallItemFragment;
import tv.sportssidekick.sportssidekick.model.wall.WallBase;
import tv.sportssidekick.sportssidekick.model.wall.WallNews;
import tv.sportssidekick.sportssidekick.model.wall.WallPost;
import tv.sportssidekick.sportssidekick.model.wall.WallStoreItem;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Djordje Krutil on 06/01/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class WallAdapter extends RecyclerView.Adapter<WallAdapter.ViewHolder> {
    private static final String TAG = "WallAdapter";

    private List<WallBase> items;

    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;



        @Nullable
        @BindView(R.id.caption)
        TextView captionTextView;
        @Nullable
        @BindView(R.id.description)
        TextView descriptionTextView;
        @Nullable
        @BindView(R.id.play_button)
        ImageView playButton;
        @Nullable
        @BindView(R.id.image)
        ImageView imageView;


        @Nullable
        @BindView(R.id.row_wall_post_likes)
        TextView rowPostLikes;
        @Nullable
        @BindView(R.id.row_wall_post_comments)
        TextView rowPostComments;


        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);


        }
    }

    WallBase.PostType[] postTypeValues;
    public WallAdapter(Context context, List<WallBase> fakeModelList) {
        this.context = context;
        this.items = fakeModelList;
        postTypeValues = WallBase.PostType.values();
    }

    @Override
    public WallAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        int viewResourceId;
        switch (postTypeValues[viewType]){
            case post:
                viewResourceId = R.layout.wall_item_user_post;
                break;
            case newsShare:
                viewResourceId = R.layout.wall_item_news;
                break;
            case rumor:
                viewResourceId = R.layout.wall_item_rumour;
                break;
            case wallStoreItem:
                viewResourceId = R.layout.wall_item_shop;
               break;
            default: // Somehow, we got unknown type as viewType argument, return default cell
                viewResourceId = R.layout.wall_item_rumour;
                break;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(viewResourceId, parent, false);
        return new WallAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)

    @Override
    public void onBindViewHolder(WallAdapter.ViewHolder holder, final int position) {
        int screenHeight = Utility.getDisplayHeight(context);
        switch (items.get(position).getType()){
            case post:
                WallPost post = (WallPost)items.get(position);
                if (holder.imageView != null) {
                    if (!TextUtils.isEmpty(post.getCoverImageUrl())) {
                        holder.imageView.getLayoutParams().height = (int) (screenHeight * 0.2);
                        ImageLoader.getInstance().displayImage(post.getCoverImageUrl(), holder.imageView, Utility.imageOptionsImageLoader());
                        holder.imageView.setVisibility(View.VISIBLE);
                    } else {
                        holder.imageView.setVisibility(View.GONE);
                    }
                }
                if (holder.playButton != null) {
                    holder.playButton.setVisibility(TextUtils.isEmpty(post.getVidUrl()) ? View.GONE : View.VISIBLE);
                }
                if (holder.captionTextView != null) {
                    holder.captionTextView.setText(post.getTitle());
                }
                if (holder.descriptionTextView != null) {
                    holder.descriptionTextView.setText(post.getSubTitle());
                }
                break;
            case newsShare:
                WallNews news = (WallNews)items.get(position);
                if (holder.imageView != null) {
                    if (!TextUtils.isEmpty(news.getCoverImageUrl())) {
                        holder.imageView.getLayoutParams().height = (int) (screenHeight * 0.25);
                        ImageLoader.getInstance().displayImage(news.getCoverImageUrl(), holder.imageView, Utility.getImageOptionsForWallItem());
                        holder.imageView.setVisibility(View.VISIBLE);
                    } else {
                        holder.imageView.setVisibility(View.GONE);
                    }
                }
                if (holder.playButton != null) {
                    holder.playButton.setVisibility(TextUtils.isEmpty(news.getVidUrl()) ? View.GONE : View.VISIBLE);
                }
                if (holder.captionTextView != null) {
                    holder.captionTextView.setText(news.getTitle());
                }
                if (holder.descriptionTextView != null) {
                    holder.descriptionTextView.setText(news.getSubTitle());
                }
                if (holder.rowPostLikes != null) {
                    holder.rowPostLikes.setText(String.valueOf(news.getLikeCount()));
                }
                if (holder.rowPostComments != null) {
                    holder.rowPostComments.setText(String.valueOf(news.getCommentsCount()));
                }
                break;
            case betting:
                // No items of this type yet!
                break;
            case stats:
                // No items of this type yet!
                break;
            case rumor:
                WallNews rumour = (WallNews)items.get(position);
                if (holder.descriptionTextView != null) {
                    holder.descriptionTextView.setText(rumour.getTitle());
                }
                break;
            case wallStoreItem:
                WallStoreItem storeItem = (WallStoreItem) items.get(position);
                if (holder.imageView != null) {
                    String coverImageUrl = storeItem.getCoverImageUrl();
                    if (!TextUtils.isEmpty(coverImageUrl)) {
                        holder.imageView.getLayoutParams().height = (int) (screenHeight * 0.25);
                        ImageLoader.getInstance().displayImage(coverImageUrl, holder.imageView, Utility.getImageOptionsForWallItem());
                        holder.imageView.setVisibility(View.VISIBLE);
                    } else {
                        holder.imageView.setVisibility(View.GONE);
                    }
                }
                if (holder.captionTextView != null) {
                    holder.captionTextView.setText(storeItem.getTitle());
                }
                if (holder.descriptionTextView != null) {
                    holder.descriptionTextView.setText(storeItem.getSubTitle());
                }
                break;
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentEvent fe = new FragmentEvent(WallItemFragment.class);
                fe.setId(items.get(position).getPostId());
                EventBus.getDefault().post(fe);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType().ordinal();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}