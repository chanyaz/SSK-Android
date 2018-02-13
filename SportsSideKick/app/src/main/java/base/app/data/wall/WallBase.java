package base.app.data.wall;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import base.app.data.news.NewsModel;
import base.app.data.sharing.Shareable;
import base.app.data.sharing.SharingManager;
import base.app.data.user.UserInfo;

/**
 * Created by Filip on 1/6/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class WallBase implements Shareable, Serializable {

    private static final String TAG = "WALL BASE";

    static private HashMap<String, WallBase> cache = new HashMap<>();
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
        socialShare,
        social
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
    String subTitle = "";
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
    @JsonProperty("message")
    private String message;
    @JsonProperty("content")
    private String content;
    @JsonProperty("strap")
    private String strap;
    @JsonProperty("referencedItem")
    private WallNews referencedItem;

    private UserInfo poster;

    public WallBase getReferencedItem() {
        return referencedItem;
    }

    public void setReferencedItem(WallNews referencedItem) {
        this.referencedItem = referencedItem;
    }

    public UserInfo getPoster() {
        return poster;
    }

    public void setPoster(UserInfo poster) {
        this.poster = poster;
    }

    public static HashMap<String, WallBase> getCache() {
        return cache;
    }

    public static WallBase postFactory(Object wallItem, ObjectMapper mapper, boolean putInCache) {
        JsonNode node = mapper.valueToTree(wallItem);
        if (node.has("type")) {
            TypeReference typeReference = null;
            PostType type;

            if (node.get("type").canConvertToInt()) {
                int typeValue = node.get("type").intValue();
                type = PostType.values()[typeValue - 1];
            } else {
                String objectType = node.get("type").textValue();
                objectType = objectType
                        .replace("official", "newsOfficial")
                        .replace("webhose", "newsUnOfficial");
                type = PostType.valueOf(objectType);
            }
            switch (type) {
                case post:
                case wallComment:
                    typeReference = new TypeReference<WallPost>() {
                    };
                    break;
                case social:
                    typeReference = new TypeReference<WallSocial>() {
                    };
                    break;
                case socialShare:
                    typeReference = new TypeReference<WallSocialShare>() {
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
                case rumourShare:
                    typeReference = new TypeReference<WallRumourShare>() {
                    };
                    break;
                case wallStoreItem:
                    typeReference = new TypeReference<WallStoreItem>() {
                    };
                    break;
                case newsOfficial:
                case newsUnOfficial:
                    typeReference = new TypeReference<WallNews>() {
                    };
                    break;
                default:
                    Log.e(TAG, "ERROR ----- unsupported post type " + node.get("type").textValue() + "\n\n" + node);
            }

            WallBase item = mapper.convertValue(wallItem, typeReference);
            item.setType(type);
            if (type == PostType.newsUnOfficial || type == PostType.rumourShare) {
                item.setPostId("UNOFFICIAL$$$" + item.getPostId());
            }

            // TODO @Filip - Fix me - preventing cache of non-wall items
                if (putInCache) {
                WallBase cachedItem = cache.get(item.getPostId());
                if (cachedItem != null) {
                    cachedItem.setEqualTo(item);
                    item = cachedItem;
                } else {
                    cache.put(item.getPostId(), item);
                }

                NewsModel.NewsType newsType = null;
                if (item.getType() == PostType.newsOfficial) {
                    newsType = NewsModel.NewsType.OFFICIAL;
                } else if (item.getType() == PostType.newsUnOfficial) {
                    newsType = NewsModel.NewsType.UNOFFICIAL;
                } else if (item.getType() == PostType.social) {
                    newsType = NewsModel.NewsType.SOCIAL;
                }
                if (newsType == null) return item;
                List<WallNews> savedItems = NewsModel.getInstance().getAllCachedItems(newsType);
                for (int i = 0; i < savedItems.size(); i++) {
                    WallNews savedItem = savedItems.get(i);
                    if (savedItem.postId.equals(item.postId)) {
                        savedItems.remove(savedItem);
                        savedItems.add(i, (WallNews) item);
                    }
                }
                NewsModel.getInstance().saveNewsToCache(savedItems, newsType);
            }

            return item;
        }
        return null;
    }

    @JsonProperty("timestamp")
    public String getTimestampAsString() {
        if (timestamp == null) return "";
        return String.valueOf(timestamp.longValue() / 1000) + "." + String.valueOf((int) (timestamp.longValue() % 1000) + "00");
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

    public void setTranslatedTo(String translatedTo) {
        this.translatedTo = translatedTo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void toggleLike() {
        likedByUser = !likedByUser;
        if (likedByUser) {
            likeCount += 1;
        } else {
            likeCount -= 1;
        }
        WallModel.getInstance().setLikeCount(this, likedByUser);
    }

    @Override
    public void incrementShareCount(SharingManager.ShareTarget shareTarget) {
        WallModel.getInstance().incrementShareCount(this, shareTarget);
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
        this.coverAspectRatio = item.coverAspectRatio;
    }

    @JsonProperty("content")
    public void setContent(String content) {
        this.bodyText = content;
        this.content = content;
    }

    @JsonProperty("content")
    public String getContent() {
        return content;
    }

    @JsonProperty("strap")
    public String getStrap() {
        return strap;
    }

    @JsonProperty("strap")
    public void setStrap(String strap) {
        this.strap = strap;
    }
}
