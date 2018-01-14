package base.app.data.wall;

import android.support.annotation.NonNull;
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

import java.io.Serializable;
import java.util.HashMap;

import base.app.data.user.UserInfo;

/**
 * Created by Filip on 1/6/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class WallItem implements Serializable {

    private static final String TAG = "WALL BASE";

    static private HashMap<String, WallItem> cache = new HashMap<>();
    @JsonIgnore // NOTE: we set Post type in factory method, not trough automatic JSON parsing!
    private PostType itemType = PostType.post;

    public static void clear() {
        cache.clear();
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
        wallComment,
        rumourShare,
        postShare,
        social,
        socialShare
    }

    @JsonProperty("timestamp")
    protected Double timestamp;
    @JsonProperty("wallId")
    private String wallId = "";
    @JsonProperty("postId")
    String postId = "";
    @JsonProperty("likeCount")
    private int likeCount = 0;
    @JsonProperty("likedByUser")
    private boolean likedByUser;
    @JsonProperty("commentsCount")
    protected int commentsCount = 0;
    @JsonProperty("shareCount")
    private int shareCount = 0;
    @JsonProperty("subTitle")
    private String subTitle = "";
    @JsonProperty("title")
    protected String title;
    @JsonProperty("bodyText")
    String bodyText;
    @JsonProperty("coverImageUrl")
    String coverImageUrl;
    @JsonProperty("coverAspectRatio")
    private Float coverAspectRatio;
    @JsonProperty("referencedItemClub")
    private String referencedItemClub;
    @JsonProperty("referencedItemId")
    private String referencedItemId;
    @JsonProperty("sharedComment")
    private String sharedComment;
    @JsonProperty("translatedTo")
    private String translatedTo;
    @JsonProperty("url")
    private String url;
    @JsonProperty("vidUrl")
    private String vidUrl;

    private UserInfo poster;

    public UserInfo getPoster() {
        return poster;
    }

    public String getVidUrl() {
        return vidUrl;
    }

    public void setVidUrl(String vidUrl) {
        this.vidUrl = vidUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPoster(UserInfo poster) {
        this.poster = poster;
    }

    public static HashMap<String, WallItem> getCache() {
        return cache;
    }

    public static WallItem postFactory(Object wallItem, ObjectMapper mapper, boolean putInCache) {
        JsonNode node = mapper.valueToTree(wallItem);
        if (node.has("type")) {
            TypeReference typeReference = null;
            PostType type;

            if (node.get("type").canConvertToInt()) {
                int typeValue = node.get("type").intValue();
                type = PostType.values()[typeValue - 1];
            } else {
                String objectType = node.get("type").textValue();
                type = PostType.valueOf(objectType);
            }
            switch (type) {
                case post:
                case wallComment:
                case social:
                    typeReference = new TypeReference<Post>() {
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
                case wallStoreItem:
                    typeReference = new TypeReference<WallStoreItem>() {
                    };
                    break;
                case newsOfficial:
                case rumor:
                    typeReference = new TypeReference<News>() {
                    };
                    break;
                default:
                    Log.e(TAG, "ERROR ----- unsupported post type " + node.get("type").textValue() + "\n\n" + node);
            }

            WallItem item = mapper.convertValue(wallItem, typeReference);
            item.setType(type);

            // TODO @Filip - Fix me - preventing cache of non-wall items
            if (putInCache) {
                WallItem cachedItem = cache.get(item.getPostId());
                if (cachedItem != null) {
                    cachedItem.setEqualTo(item);
                    item = cachedItem;
                } else {
                    cache.put(item.getPostId(), item);
                }
            }

            return item;
        }
        return null;
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

    public Float getCoverAspectRatio() {
        return coverAspectRatio;
    }

    public void setCoverAspectRatio(Float coverAspectRatio) {
        this.coverAspectRatio = coverAspectRatio;
    }

    public String getReferencedItemClub() {
        return referencedItemClub;
    }

    public void setReferencedItemClub(String referencedItemClub) {
        this.referencedItemClub = referencedItemClub;
    }

    public String getReferencedItemId() {
        return referencedItemId;
    }

    public void setReferencedItemId(String referencedItemId) {
        this.referencedItemId = referencedItemId;
    }

    public String getSharedComment() {
        return sharedComment;
    }

    public boolean hasSharedComment() {
        return sharedComment != null &&
                !sharedComment.replaceAll(" ", "").isEmpty();
    }

    public void setSharedComment(String sharedComment) {
        this.sharedComment = sharedComment;
    }

    @JsonProperty("type")
    public int getTypeAsInt() {
        return itemType.ordinal() + 1;
    }

    @JsonIgnore
    public PostType getType() {
        return itemType;
    }

    @JsonIgnore
    public void setType(PostType type) {
        this.itemType = type;
    }

    public boolean isNotTranslated() {
        return translatedTo == null;
    }

    public void toggleLike() {
        likedByUser = !likedByUser;
        if (likedByUser) {
            likeCount += 1;
        } else {
            likeCount -= 1;
        }
        WallModel.getInstance().setLikeCount(this, likedByUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, "Like set to value " + likedByUser);
                }
            }
        });
    }

    public void setEqualTo(WallItem item) {
        timestamp = item.timestamp;
        itemType = item.itemType;
        wallId = item.wallId;
        postId = item.postId;
        likeCount = item.likeCount;
        likedByUser = item.likedByUser;
        commentsCount = item.commentsCount;
        shareCount = item.shareCount;
        title = item.title;
        subTitle = item.subTitle;
        bodyText = item.bodyText;
        coverImageUrl = item.coverImageUrl;
        coverAspectRatio = item.coverAspectRatio;
    }
}
