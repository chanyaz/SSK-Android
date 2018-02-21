package base.app.ui.adapter.content;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.ads.AdError;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.pixplicity.easyprefs.library.Prefs;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import base.app.R;
import base.app.data.Model;
import base.app.data.Translator;
import base.app.data.user.UserInfo;
import base.app.data.wall.WallBase;
import base.app.data.wall.WallBase.PostType;
import base.app.data.wall.WallNews;
import base.app.data.wall.WallPost;
import base.app.data.wall.WallStats;
import base.app.data.wall.WallStoreItem;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.ui.fragment.content.NewsItemFragment;
import base.app.ui.fragment.content.WallItemFragment;
import base.app.util.commons.Utility;
import base.app.util.ui.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;

import static base.app.ui.fragment.popup.ProfileFragment.isAutoTranslateEnabled;
import static base.app.util.commons.Utility.CHOSEN_LANGUAGE;

/**
 * Created by Djordje Krutil on 06/01/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class WallAdapter extends RecyclerView.Adapter<WallAdapter.ViewHolder> {

    private static final int ADS_INTERVAL = 30;
    private static final int ADS_COUNT = 10;
    private static final int WALL_ADVERT_VIEW_TYPE = 10005;

    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View view;
        // Content view bindings
        @Nullable
        @BindView(R.id.text_content)
        TextView contentTextView;
        @Nullable
        @BindView(R.id.play_button)
        ImageView playButton;
        @Nullable
        @BindView(R.id.image)
        ImageView imageView;
        @Nullable
        @BindView(R.id.authorImage)
        ImageView userImage;
        @Nullable
        @BindView(R.id.authorName)
        TextView author;
        // Like & comments view bindings
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
        @BindView(R.id.text_comment)
        TextView textComment;
        @Nullable
        @BindView(R.id.comment_container)
        View commentContainer;
        // Ad view bindings
        @Nullable
        @BindView(R.id.wall_native_ad_media_view)
        MediaView nativeAdMediaView;
        @Nullable
        @BindView(R.id.wall_native_ad_body)
        TextView nativeAdBody;
        @Nullable
        @BindView(R.id.wall_native_ad_social_context)
        TextView nativeAdSocialContext;
        @Nullable
        @BindView(R.id.captionAvatar)
        ImageView captionAvatar;
        @Nullable
        @BindView(R.id.socialSource)
        ImageView socialSource;
        @Nullable
        @BindView(R.id.dateLabel)
        TextView dateLabel;
        @Nullable
        @BindView(R.id.subheadTextView)
        TextView subheadTextView;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    private PostType[] postTypeValues;

    private int currentAdInterval;

    public WallAdapter(Context context) {
        this.context = context;
        postTypeValues = PostType.values();
        initializeNativeAdManagerAndRequestAds(ADS_COUNT);
        currentAdInterval = 0;
    }

    private NativeAdsManager manager;

    private void initializeNativeAdManagerAndRequestAds(int adsFrequency) {
        // Initialize a NativeAdsManager and request a number of ads
        manager = new NativeAdsManager(context, "102782376855276_240749436391902", ADS_COUNT);
        manager.setListener(new NativeAdsManager.Listener() {
            @Override
            public void onAdsLoaded() {
                //Ads Loaded callback
                if (manager.getUniqueNativeAdCount() > 0) {
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

    static void displayCommentsAndLikes(WallBase post, final ViewHolder holder) {
        if (post.getTimestamp() != null && holder.dateLabel != null) {
            String time = "" + DateUtils.getRelativeTimeSpanString(
                    (long) (post.getTimestamp() * 1000),
                    Utility.getCurrentTime(),
                    DateUtils.MINUTE_IN_MILLIS
            );
            holder.dateLabel.setText(time);
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
        if (post instanceof WallNews && ((WallNews) post).getSource() != null) {
            int sourceImageResource = 0;
            switch (((WallNews) post).getSource().toLowerCase()) {
                case "facebook":
                    sourceImageResource = R.drawable.ic_social_source_facebook;
                    break;
                case "twitter":
                    sourceImageResource = R.drawable.ic_social_source_twitter;
                    break;
                case "instagram":
                    sourceImageResource = R.drawable.ic_social_source_instagram;
                    break;
                case "gplus":
                    sourceImageResource = R.drawable.ic_social_source_gplus;
                    break;
            }
            ImageLoader.displayImage(sourceImageResource, holder.socialSource);
        }
    }

    // Replace the contents of a view (invoked by the layout manager)

    static void displayTitle(String value, ViewHolder holder) {
        if (holder.contentTextView != null && value != null) {
            holder.contentTextView.setText(value.replace("\n\n", "\n"));
        }
    }

    static void displaySubhead(WallBase item, ViewHolder holder) {
        if (holder.subheadTextView != null) {
            String subheadText = null;
            if (item instanceof WallNews) {
                subheadText = item.getContent();
            } else if (item instanceof WallPost) {
                subheadText = item.getBodyText();
            }
            if (subheadText != null && !subheadText.isEmpty()) {
                holder.subheadTextView.setText(subheadText);
                holder.subheadTextView.setVisibility(View.VISIBLE);
            } else {
                holder.subheadTextView.setVisibility(View.GONE);
            }
        }
    }

    static boolean displayPostImage(WallBase post, ViewHolder holder) {
        if (holder.imageView != null) {
            String coverImageUrl = post.getCoverImageUrl();
            if (coverImageUrl != null && !TextUtils.isEmpty(post.getCoverImageUrl())) {
                holder.imageView.setVisibility(View.VISIBLE);

                if (post instanceof WallStoreItem) {
                    holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                } else {
                    holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
                ImageLoader.displayImage(post.getCoverImageUrl(), holder.imageView, R.drawable.wall_detail_header_placeholder);
                return true;
            } else {
                holder.imageView.setVisibility(View.GONE);
                return false;
            }
        }
        return false;
    }

    static void displayUserInfo(final WallBase post, final ViewHolder holder) {
        Task<UserInfo> getUserTask = Model.getInstance().getUserInfoById(post.getWallId());
        getUserTask.addOnCompleteListener(new OnCompleteListener<UserInfo>() {
            @Override
            public void onComplete(@NonNull Task<UserInfo> task) {
                if (task.isSuccessful()) {
                    UserInfo user = task.getResult();
                    if (user != null) {
                        if (holder.captionAvatar != null) {
                            ImageLoader.displayRoundImage(
                                    user.getCircularAvatarUrl(),
                                    holder.captionAvatar
                            );
                        }
                        ImageLoader.displayRoundImage(user.getCircularAvatarUrl(), holder.userImage);
                        if (user.getNicName() != null && holder.author != null) {
                            holder.author.setText(user.getFirstName() + " " + user.getLastName());
                        }
                    }
                }
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        int viewResourceId = -1;
        if (viewType == WALL_ADVERT_VIEW_TYPE) {
            viewResourceId = R.layout.wall_native_ad;
        } else {
            switch (postTypeValues[viewType]) {
                case post:
                    viewResourceId = R.layout.wall_item_user_post;
                    break;
                case newsShare:
                case rumourShare:
                case socialShare:
                    viewResourceId = R.layout.wall_item_news;
                    break;
                case rumor:
                    viewResourceId = R.layout.wall_item_rumour;
                    break;
                case wallStoreItem:
                    viewResourceId = R.layout.wall_item_store;
                    break;
                case stats:
                    viewResourceId = R.layout.wall_item_stats;
                    break;
                case newsOfficial:
                case newsUnOfficial:
                case social:
                    viewResourceId = R.layout.wall_item_news;
                    break;
            }
        }
        View view = new View(context);
        if (viewResourceId != -1) {
            view = LayoutInflater.from(parent.getContext()).inflate(viewResourceId, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (isAdvert(position)) {
            // this is advert, show it!
            final NativeAd advert = manager.nextNativeAd();
            if (advert != null) {
                holder.nativeAdMediaView.setNativeAd(advert);
                holder.nativeAdBody.setText(advert.getAdTitle());
                holder.nativeAdSocialContext.setText(advert.getAdBody());
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.nativeAdMediaView.performClick();
                    }
                });
            }
        } else {
            // this is wall item
            final int index;
            if (currentAdInterval > 0) {
                index = position - (position / currentAdInterval);
            } else {
                index = position;
            }
            WallBase item = values.get(index);
            switch (item.getType()) {
                case post:
                    WallPost post = (WallPost) item;
                    displayUserInfo(post, holder);
                    boolean hasImage = displayPostImage(post, holder);
                    if (holder.contentTextView != null) {
                        if (hasImage) {
                            holder.contentTextView.setMaxLines(3);
                        } else {
                            holder.contentTextView.setMaxLines(6);
                        }
                    }
                    displayTitle(post.getTitle(), holder);
                    displayCommentsAndLikes(post, holder);

                    if (holder.playButton != null) {
                        holder.playButton.setVisibility(TextUtils.isEmpty(post.getVidUrl()) ? View.GONE : View.VISIBLE);
                    }
                    break;
                case newsShare:
                case rumourShare:
                case socialShare:
                    WallNews referencedItem = (WallNews) item.getReferencedItem();
                    if (referencedItem == null) return; // todo remove this line
                    displayUserInfo(item, holder);
                    displayTitle(referencedItem.getTitle() != null
                            ? referencedItem.getTitle() : referencedItem.getMessage(), holder);
                    displayPostImage(referencedItem, holder);
                    displayCommentsAndLikes(referencedItem, holder);
                    if (item.hasSharedComment()) {
                        holder.textComment.setText(item.getSharedComment());
                        holder.commentContainer.setVisibility(View.VISIBLE);
                        holder.userImage.setVisibility(View.GONE);
                    } else {
                        holder.commentContainer.setVisibility(View.GONE);
                        holder.userImage.setVisibility(View.VISIBLE);
                    }
                    if (holder.playButton != null) {
                        holder.playButton.setVisibility(TextUtils.isEmpty(referencedItem.getVidUrl()) ? View.GONE : View.VISIBLE);
                    }
                    break;
                case rumor:
                    displayTitle(item.getTitle(), holder);
                    displayCommentsAndLikes(item, holder);
                    break;
                case wallStoreItem:
                    WallStoreItem storeItem = (WallStoreItem) item;
                    displayUserInfo(storeItem, holder);
                    displayTitle(storeItem.getTitle(), holder);
                    displayPostImage(storeItem, holder);
                    displayCommentsAndLikes(storeItem, holder);
                    break;
                case stats:
                    WallStats statsItem = (WallStats) item;
                    displayUserInfo(statsItem, holder);
                    displayTitle(statsItem.getTitle(), holder);
                    displayPostImage(statsItem, holder);
                    displayCommentsAndLikes(statsItem, holder);
                    break;
                case betting:
                    break;
                case newsOfficial:
                case newsUnOfficial:
                case social:
                    NewsAdapter.showItemDetails(holder, (WallNews) item);
                    break;
                default:
                    Log.e("WallAdapter", "Unsupported post type: " + item.getType());
                    break;
            }
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.getAdapterPosition() == -1) return;
                    FragmentEvent fe;
                    WallBase item = values.get(holder.getAdapterPosition());
                    if (item.getReferencedItemId() == null || item.getReferencedItemId().isEmpty()) {
                        fe = new FragmentEvent(WallItemFragment.class);
                        fe.setId(item.getPostId());
                    } else {
                        fe = new FragmentEvent(NewsItemFragment.class);
                        fe.setId(item.getReferencedItemId());
                        fe.setSecondaryId(item.getPostId());
                    }
                    fe.setItem(item);
                    EventBus.getDefault().post(fe);
                }
            });
            if (isAutoTranslateEnabled() && item.isNotTranslated()) {
                String itemToTranslateId = item.getPostId();
                if (item.getReferencedItemId() != null) {
                    itemToTranslateId = item.getReferencedItemId();
                }
                if (item.getType() == PostType.newsShare
                        || item.getType() == PostType.rumourShare) {
                    translateInternalNewsItem(holder, itemToTranslateId, item);
                } else if (item.getType() == PostType.socialShare) {
                    translateInternalSocialItem(holder, itemToTranslateId, item);
                } else if (!(item instanceof WallNews)) { // Don't translate WallNews items, they are no supposed to be there. Only WallNewsShare and such are allowed
                    TaskCompletionSource<WallBase> task = new TaskCompletionSource<>();
                    task.getTask().addOnCompleteListener(new OnCompleteListener<WallBase>() {
                        @Override
                        public void onComplete(@NonNull Task<WallBase> task) {
                            int position = holder.getAdapterPosition();
                            if (task.isSuccessful() && position != -1) {
                                WallBase translatedItem = task.getResult();
                                if (translatedItem != null) {
                                    remove(position);
                                    add(position, translatedItem);
                                    notifyItemChanged(position);
                                }
                            }
                        }
                    });
                    Translator.getInstance().translatePost(
                            itemToTranslateId,
                            Prefs.getString(CHOSEN_LANGUAGE, "en"),
                            task,
                            item.getType()
                    );
                }
            }
        }
    }

    private void translateInternalNewsItem(final ViewHolder holder, String itemToTranslateId, final WallBase itemChildPointer) {
        TaskCompletionSource<WallNews> task = new TaskCompletionSource<>();
        task.getTask().addOnCompleteListener(new OnCompleteListener<WallNews>() {
            @Override
            public void onComplete(@NonNull Task<WallNews> task) {
                int position = holder.getAdapterPosition();
                if (task.isSuccessful() && position != -1) {
                    WallNews translatedParentItem = task.getResult();
                    itemChildPointer.setTranslatedTo(Prefs.getString(CHOSEN_LANGUAGE, "en"));
                    itemChildPointer.setTitle(translatedParentItem.getTitle());
                    itemChildPointer.setBodyText(translatedParentItem.getBodyText());
                    notifyItemChanged(position);
                }
            }
        });
        Translator.getInstance().translateNews(
                itemToTranslateId,
                Prefs.getString(CHOSEN_LANGUAGE, "en"),
                task
        );
    }

    private void translateInternalSocialItem(final ViewHolder holder, String itemToTranslateId, final WallBase itemChildPointer) {
        TaskCompletionSource<WallNews> task = new TaskCompletionSource<>();
        task.getTask().addOnCompleteListener(new OnCompleteListener<WallNews>() {
            @Override
            public void onComplete(@NonNull Task<WallNews> task) {
                int position = holder.getAdapterPosition();
                if (task.isSuccessful() && position != -1) {
                    WallNews translatedParentItem = task.getResult();
                    itemChildPointer.setTranslatedTo(Prefs.getString(CHOSEN_LANGUAGE, "en"));
                    itemChildPointer.setTitle(translatedParentItem.getTitle());
                    itemChildPointer.setBodyText(translatedParentItem.getBodyText());
                    notifyItemChanged(position);
                }
            }
        });
        Translator.getInstance().translateSocial(
                itemToTranslateId,
                Prefs.getString(CHOSEN_LANGUAGE, "en"),
                task
        );
    }

    private boolean isAdvert(int position) {
        return currentAdInterval > 0 && position % currentAdInterval == 0 && position > 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (isAdvert(position)) {
            return WALL_ADVERT_VIEW_TYPE;
        } else {
            int index = position;
            if (currentAdInterval > 0) {
                index = position - (position / currentAdInterval);
            }
            return values.get(index).getType().ordinal();
        }
    }

    @Override
    public int getItemCount() {
        if (currentAdInterval > 0) {
            return values.size() + values.size() / currentAdInterval;
        } else {
            return values.size();
        }
    }

    public void clear() {
        values.clear();
    }

    private final List<WallBase> values = new ArrayList<>();

    public void add(WallBase model) {
        values.add(model);
    }

    public void add(int position, WallBase model) {
        values.add(position, model);
    }

    public void addAll(List<WallBase> items) {
        values.addAll(items);
    }

    public void remove(WallBase model) {
        values.remove(model);
    }

    public void remove(int position) {
        if (values.get(position) != null) {
            values.remove(position);
        }
    }
}