package base.app.model.wall;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;

import base.app.model.sharing.Shareable;
import base.app.model.sharing.SharingManager;
import base.app.model.tutorial.WallTip;
import base.app.model.user.UserInfo;

/**
 * Created by Filip on 1/6/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class WallBase implements Shareable {

    private static final String TAG = "WALLBASE";
    static private HashMap<String, WallBase> cache = new HashMap<>();

    @JsonProperty("timestamp")
    protected Double timestamp;
    @JsonIgnore // NOTE: we set Post type in factory method, not trough automatic JSON parsing!
    protected PostType itemType = PostType.post;
    @JsonProperty("wallId")
    protected String wallId = "";
    @JsonProperty("postId")
    protected String postId = "";
    @JsonProperty("likeCount")
    protected int likeCount = 0;
    @JsonProperty("likedByUser")
    protected boolean likedByUser = false;
    @JsonProperty("commentsCount")
    protected int commentsCount = 0;
    @JsonProperty("shareCount")
    protected int shareCount = 0;
    @JsonProperty("title")
    protected String title;
    @JsonProperty("subTitle")
    protected String subTitle;
    @JsonProperty("bodyText")
    protected String bodyText;
    @JsonProperty("coverImageUrl")
    protected String coverImageUrl;
    @JsonProperty("vidUrl")
    protected String vidUrl;
    @JsonProperty("coverAspectRatio")
    protected Float coverAspectRatio = 0.5625f;

    private WallAdvert wallNativeAd;

    public WallAdvert getWallNativeAd() {
        return wallNativeAd;
    }

    public void setWallNativeAd(WallAdvert wallNativeAd) {
        this.wallNativeAd = wallNativeAd;
    }

    UserInfo poster;

    public UserInfo getPoster() {
        return poster;
    }

    public void setPoster(UserInfo poster) {
        this.poster = poster;
    }

    public static void clear() {
        cache.clear();
    }

    public static String getTAG() {
        return TAG;
    }

    public static HashMap<String, WallBase> getCache() {
        return cache;
    }

    public static void setCache(HashMap<String, WallBase> cache) {
        WallBase.cache = cache;
    }

    @Nullable
    static WallBase postFactory(Object wallItem, ObjectMapper mapper) {
        JsonNode node = mapper.valueToTree(wallItem);
        if (node.has("type") && node.get("type").canConvertToInt()) {
            int typeValue = node.get("type").intValue();
            PostType type = PostType.values()[typeValue - 1];
            TypeReference typeReference = new TypeReference<WallBase>() {};
            switch (type) {
                case post:
                    typeReference = new TypeReference<WallPost>() {
                    };
                    break;
                case newsShare:
                    typeReference = new TypeReference<WallNewsShare>() {
                    };
                    break;
                case betting:
                    typeReference = new TypeReference<WallBetting>() {
                    };
                    break;
                case stats:
                    typeReference = new TypeReference<WallStats>() {
                    };
                    break;
                case rumor:
                    typeReference = new TypeReference<WallRumor>() {
                    };
                    break;
                case wallStoreItem:
                    typeReference = new TypeReference<WallStoreItem>() {
                    };
                    break;
                case tip:
                    typeReference = new TypeReference<WallTip>() {
                    };
            }
            WallBase item = mapper.convertValue(wallItem, typeReference);
            item.setType(type);

            WallBase cachedItem = cache.get(item.getPostId());
            if(cachedItem!=null){
                cachedItem.setEqualTo(item);
                item = cachedItem;
            } else {
                cache.put(item.getPostId(),item);
            }
            return item;
        }
        return null;
    }

    @JsonProperty("timestamp")
    public String getTimestampAsString() {
        return String.valueOf(timestamp.longValue()/1000) + "." +  String.valueOf((int)(timestamp.longValue()%1000) + "00");
    }


    public Double getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(Double timestamp) {
        this.timestamp = timestamp;
    }

    public String getWallId() {
        return wallId;
    }

    public void setWallId(String wallId) {
        this.wallId = wallId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLikedByUser() {
        return likedByUser;
    }

    public void setLikedByUser(boolean likedByUser) {
        this.likedByUser = likedByUser;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getVidUrl() {
        return vidUrl;
    }

    public void setVidUrl(String vidUrl) {
        this.vidUrl = vidUrl;
    }

    public Float getCoverAspectRatio() {
        return coverAspectRatio;
    }

    public void setCoverAspectRatio(Float coverAspectRatio) {
        this.coverAspectRatio = coverAspectRatio;
    }


    @JsonProperty("type")
    public int getTypeAsInt() {
        return itemType.ordinal()+1;
    }


    @JsonIgnore
    public PostType getType() {
        return itemType;
    }

    @JsonIgnore
    public void setType(PostType type) {
        this.itemType = type;
    }


    public void toggleLike() {
        likedByUser = !likedByUser;
        if (likedByUser) {
            likeCount += 1;
        } else {
            likeCount -= 1;
        }
        WallModel.getInstance().setlikeVal(this, likedByUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, "Like set to value " + likedByUser);
                }
            }
        });
    }

    public void setEqualTo(WallBase item) {
        this.timestamp = item.timestamp;
        this.itemType = item.itemType;
        this.wallId = item.wallId;
        this.postId = item.postId;
        this.likeCount = item.likeCount;
        this.likedByUser = item.likedByUser;
        this.commentsCount = item.commentsCount;
        this.shareCount = item.shareCount;
        this.title = item.title;
        this.subTitle = item.subTitle;
        this.bodyText = item.bodyText;
        this.coverImageUrl = item.coverImageUrl;
        this.vidUrl = item.vidUrl;
        this.coverAspectRatio = item.coverAspectRatio;
    }

    @Override
    public void incrementShareCount(SharingManager.ShareTarget shareTarget) {
        WallModel.getInstance().itemShared(this, shareTarget);
    }

    public enum PostType {
        post,
        newsShare,
        betting,
        stats,
        rumor,
        wallStoreItem,
        newsOfficial,
        newsUnOfficial,
        tip,
        nativeAd
    }
}