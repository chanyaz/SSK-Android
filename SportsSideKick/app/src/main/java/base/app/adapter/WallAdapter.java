package base.app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import base.app.R;
import base.app.fragment.FragmentEvent;
import base.app.fragment.instance.WallItemFragment;
import base.app.model.Model;
import base.app.model.tutorial.WallTip;
import base.app.model.user.UserInfo;
import base.app.model.wall.WallBase;
import base.app.model.wall.WallNews;
import base.app.model.wall.WallNewsShare;
import base.app.model.wall.WallPost;
import base.app.model.wall.WallStoreItem;
import base.app.util.Utility;

/**
 * Created by Djordje Krutil on 06/01/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class WallAdapter extends RecyclerView.Adapter<WallAdapter.ViewHolder> {
    private static final String TAG = "WallAdapter";
    private static final int ADS_INTERVAL = 5;
    private static final int ADS_COUNT = 10;
    private static final int WALL_ADVERT_VIEW_TYPE = 10005;

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
        @BindView(R.id.user_image)
        CircleImageView userImage;
        @Nullable
        @BindView(R.id.author)
        TextView author;
        @Nullable
        @BindView(R.id.likes_count)
        TextView likesCount;
        @Nullable
        @BindView(R.id.likes_icon)
        ImageView likesIcon;
        @Nullable
        @BindView(R.id.liked_icon)
        ImageView likedIcon;
        @Nullable
        @BindView(R.id.comments_count)
        TextView commentsCount;

        @Nullable
        @BindView(R.id.wall_native_ad_media_view)
        MediaView nativeAdMediaView;
        @Nullable
        @BindView(R.id.wall_native_ad_body)
        TextView nativeAdBody;
        @Nullable
        @BindView(R.id.wall_native_ad_social_context)
        TextView nativeAdSocialContext;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    private WallBase.PostType[] postTypeValues;

    private int currentAdInterval;

    public WallAdapter(Context context) {
        this.context = context;
        postTypeValues = WallBase.PostType.values();
        initializeNativeAdManagerAndRequestAds(ADS_COUNT);
        currentAdInterval = 0;
    }

    private NativeAdsManager manager;

    private void initializeNativeAdManagerAndRequestAds(int adsFrequency) {
        // Initialize a NativeAdsManager and request a number of ads
        AdSettings.addTestDevice("1669b3492b83373dc025ed1cc9943c63"); //samsung tablet
        AdSettings.addTestDevice("9d381cd4828b3659af83f6c494b452e8"); //kindle tablet
        manager = new NativeAdsManager(context, "102782376855276_240749436391902", ADS_COUNT);
        manager.setListener(new NativeAdsManager.Listener() {
            @Override
            public void onAdsLoaded() {
                //Ads Loaded callback
                if(manager.getUniqueNativeAdCount()>0){
                    currentAdInterval = ADS_INTERVAL;
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onAdError(AdError adError) {
                // Ad error callback
                Log.d("onAdsLoaded", adError.getErrorMessage());
            }
        });
        manager.loadAds(NativeAd.MediaCacheFlag.ALL);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        int viewResourceId;
        if(viewType == WALL_ADVERT_VIEW_TYPE){
            viewResourceId = R.layout.wall_native_ad;
        }else {
            switch (postTypeValues[viewType]) {
                case post:
                    viewResourceId = R.layout.wall_item_user_post_new;
                    break;
                case newsShare:
                    viewResourceId = R.layout.wall_item_news_new;
                    break;
                case rumor:
                    viewResourceId = R.layout.wall_item_rumour_new;
                    break;
                case tip:
                    viewResourceId = R.layout.wall_item_tip;
                    break;
                case wallStoreItem:
                    viewResourceId = R.layout.wall_item_shop_new;
                    break;
                default: // Somehow, we got unknown type as viewType argument, return default cell
                    viewResourceId = R.layout.wall_item_rumour_new;
                    break;
            }
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(viewResourceId, parent, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(isAdvert(position)){
            // this is advert, show it!
            final NativeAd advert = manager.nextNativeAd();
            if(advert!=null){
                holder.nativeAdMediaView.setNativeAd(advert);
                holder.nativeAdBody.setText(advert.getAdTitle());
                holder.nativeAdSocialContext.setText(advert.getAdBody());
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.nativeAdMediaView.performClick();
                    }
                });
            } else {
                // Hide view?
            }
        } else {
            // this is wall item
            int index = position;
            if(currentAdInterval>0){
                index =  position - (position/currentAdInterval);

            }
            switch (values.get(index).getType()) {
                case post:
                    WallPost post = (WallPost) values.get(index);
                    if (holder.imageView != null) {
                        if (!TextUtils.isEmpty(post.getCoverImageUrl())) {
                            ImageLoader.getInstance().displayImage(post.getCoverImageUrl(), holder.imageView, Utility.getRoundedImageOptions());
                            holder.imageView.setVisibility(View.VISIBLE);
                        } else {
                            holder.imageView.setVisibility(View.GONE);
                        }
                    }
                    if (holder.playButton != null) {
                        holder.playButton.setVisibility(TextUtils.isEmpty(post.getVidUrl()) ? View.GONE : View.VISIBLE);
                    }
                    if (holder.captionTextView != null) {
                        holder.captionTextView.setText(post.getBodyText());
                    }
                    if (holder.descriptionTextView != null) {
                        holder.descriptionTextView.setText(post.getSubTitle());
                    }
                    Task<UserInfo> getUserTask = Model.getInstance().getUserInfoById(post.getWallId());
                    getUserTask.addOnCompleteListener(new OnCompleteListener<UserInfo>() {
                        @Override
                        public void onComplete(@NonNull Task<UserInfo> task) {
                            if (task.isSuccessful()) {
                                UserInfo user = task.getResult();
                                if (user != null) {
                                    if (user.getCircularAvatarUrl() != null && holder.userImage != null) {
                                        ImageLoader.getInstance().displayImage(user.getCircularAvatarUrl(), holder.userImage, Utility.imageOptionsImageLoader());
                                    }
                                    if (user.getNicName() != null) {
                                        holder.author.setText(user.getFirstName() + " " + user.getLastName());
                                    }
                                }
                            }
                        }
                    });
                    if (holder.playButton != null) {
                        holder.playButton.setVisibility(TextUtils.isEmpty(post.getVidUrl()) ? View.GONE : View.VISIBLE);
                    }

                    holder.commentsCount.setText(String.valueOf(post.getCommentsCount()));
                    holder.likesCount.setText(String.valueOf(post.getLikeCount()));


                    if (post.isLikedByUser()) {
                        holder.likedIcon.setVisibility(View.VISIBLE);
                        holder.likesIcon.setVisibility(View.GONE);
                    } else {
                        holder.likedIcon.setVisibility(View.GONE);
                        holder.likesIcon.setVisibility(View.VISIBLE);
                    }

                    break;
                case newsShare:
                    WallNewsShare news = (WallNewsShare) values.get(index);
                    if (holder.imageView != null) {
                        if (!TextUtils.isEmpty(news.getCoverImageUrl()) || news.getCoverImageUrl() != null) {
                            ImageLoader.getInstance().displayImage(news.getCoverImageUrl(), holder.imageView, Utility.getImageOptionsForWallItem());
                            holder.imageView.setVisibility(View.VISIBLE);
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


                    Task<UserInfo> getUserTaskNews = Model.getInstance().getUserInfoById(news.getWallId());
                    getUserTaskNews.addOnCompleteListener(new OnCompleteListener<UserInfo>() {
                        @Override
                        public void onComplete(@NonNull Task<UserInfo> task) {
                            if (task.isSuccessful()) {
                                UserInfo user = task.getResult();
                                if (user != null) {
                                    if (user.getCircularAvatarUrl() != null && holder.userImage != null) {
                                        ImageLoader.getInstance().displayImage(user.getCircularAvatarUrl(), holder.userImage, Utility.imageOptionsImageLoader());
                                    }
                                    if (user.getNicName() != null) {
                                        holder.author.setText(user.getFirstName() + " " + user.getLastName());
                                    }
                                }
                            }
                        }
                    });

                    holder.commentsCount.setText(String.valueOf(news.getCommentsCount()));
                    holder.likesCount.setText(String.valueOf(news.getLikeCount()));

                    break;
                case betting:
                    // No items of this type yet!
                    break;
                case stats:
                    // No items of this type yet!
                    break;
                case rumor:
                    WallNews rumour = (WallNews) values.get(index);
                    if (holder.captionTextView != null) {
                        holder.captionTextView.setText(rumour.getTitle());
                    }
                    break;
                case tip:
                    WallTip tip = (WallTip) values.get(index);
                    if (holder.descriptionTextView != null) {
                        holder.descriptionTextView.setText(tip.getTipDescription());
                    }
                    break;
                case wallStoreItem:
                    WallStoreItem storeItem = (WallStoreItem) values.get(index);
                    if (holder.imageView != null) {
                        String coverImageUrl = storeItem.getCoverImageUrl();
                        if (!TextUtils.isEmpty(coverImageUrl) && coverImageUrl != null) {
                            ImageLoader.getInstance().displayImage(coverImageUrl, holder.imageView, Utility.getImageOptionsForWallItem());
                            holder.imageView.setVisibility(View.VISIBLE);
                        }
                    }
                    if (holder.captionTextView != null) {
                        holder.captionTextView.setText(storeItem.getTitle());
                    }
                    if (holder.descriptionTextView != null) {
                        holder.descriptionTextView.setText(storeItem.getSubTitle());
                    }
                    Task<UserInfo> getStoreUserTask = Model.getInstance().getUserInfoById(storeItem.getWallId());
                    getStoreUserTask.addOnCompleteListener(new OnCompleteListener<UserInfo>() {
                        @Override
                        public void onComplete(@NonNull Task<UserInfo> task) {
                            if (task.isSuccessful()) {
                                UserInfo user = task.getResult();
                                if (user != null) {
                                    if (user.getCircularAvatarUrl() != null && holder.userImage != null) {
                                        ImageLoader.getInstance().displayImage(user.getCircularAvatarUrl(), holder.userImage, Utility.imageOptionsImageLoader());
                                    }
                                    if (user.getNicName() != null) {
                                        holder.author.setText(user.getFirstName() + " " + user.getLastName());
                                    }
                                }
                            }
                        }
                    });
                    break;
            }
            final int finalIndex = index;
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentEvent fe = new FragmentEvent(WallItemFragment.class);
                    fe.setId(values.get(finalIndex).getPostId());
                    EventBus.getDefault().post(fe);
                }
            });
        }
    }

    private boolean isAdvert(int position) {
        if(currentAdInterval>0){
            return position%currentAdInterval == 0 && position>0;
        } else {
            return false;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(isAdvert(position)){
            return WALL_ADVERT_VIEW_TYPE;
        } else {
            int index = position;
            if(currentAdInterval>0) {
                index = position - (position / currentAdInterval);
            }
            return values.get(index).getType().ordinal();
        }
    }

    @Override
    public int getItemCount() {
        if(currentAdInterval>0){
            return values.size() + values.size()/currentAdInterval;
        } else {
            return values.size();
        }
    }

    public void clear() {
        if (values != null) {
            clear();
        }
    }

    private static final Comparator<WallBase> WALL_BASE_TIMESTAMP_COMPARATOR = new Comparator<WallBase>() {
        @Override
        public int compare(WallBase a, WallBase b) {
            return (b.getTimestamp()).compareTo
                    (a.getTimestamp());
        }
    };

    private final SortedList.Callback<WallBase> mCallback = new SortedList.Callback<WallBase>() {

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public int compare(WallBase a, WallBase b) {
            return WALL_BASE_TIMESTAMP_COMPARATOR.compare(a, b);
        }

        @Override
        public boolean areContentsTheSame(WallBase oldItem, WallBase newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(WallBase item1, WallBase item2) {
            return item1.getPostId().equals(item2.getPostId());
        }
    };

    final SortedList<WallBase> values = new SortedList<>(WallBase.class, mCallback);

    public void add(WallBase model) {
        values.add(model);
    }

    public void remove(WallBase model) {
        values.remove(model);
    }

    public void add(List<WallBase> models) {
        values.addAll(models);
    }

    public void remove(List<WallBase> models) {
        values.beginBatchedUpdates();
        for (WallBase model : models) {
            values.remove(model);
        }
        values.endBatchedUpdates();
    }

    public void replaceAll(List<WallBase> models) {
        values.beginBatchedUpdates();
        for (int i = values.size() - 1; i >= 0; i--) {
            final WallBase model = values.get(i);
            if (!models.contains(model)) {
                values.remove(model);
            }
        }
        values.addAll(models);
        values.endBatchedUpdates();
    }
}