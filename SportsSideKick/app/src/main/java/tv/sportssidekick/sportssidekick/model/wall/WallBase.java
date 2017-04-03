package tv.sportssidekick.sportssidekick.model.wall;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by Filip on 1/6/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true,value={"type"})
public abstract class WallBase {

    private static final String TAG = "WALLBASE";

    @JsonIgnore
    public PostType getType() {
        return type;
    }

    @JsonIgnore
    public void setType(PostType type) {
        this.type = type;
    }

    public enum PostType {
        post,
        news,
        betting,
        stats,
        rumor,
        wallStoreItem
    }

    private String wallId;
    private String postId;
    private String timestamp;
    private int likeCount;
    private boolean likedByUser;
    private int commentsCount;
    private int shareCount;
    private PostType type;


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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean getLikedByUser() {
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




    public void toggleLike(){
        likedByUser = !likedByUser;
        if(likedByUser){
            likeCount += 1;
        } else {
            likeCount -= 1;
        }
        WallModel.getInstance().setlikeVal(this, likedByUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.i(TAG,"Like set to value " + likedByUser);
                }
            }
        });
    }

    public void setEqualTo(WallBase item){
        this.timestamp = item.timestamp;
        this.likeCount = item.likeCount;
        this.likedByUser = item.likedByUser;
        this.commentsCount = item.commentsCount;
        this.shareCount = item.shareCount;
        this.type = item.type;
    }

    @Nullable
    static WallBase postFactory(Object wallItem, ObjectMapper mapper) {
        JsonNode node = mapper.valueToTree(wallItem);
        if (node.has("type") && node.get("type").canConvertToInt()) {
            int typeValue = node.get("type").intValue();
            PostType type = PostType.values()[typeValue - 1];
            TypeReference typeReference = null;
            switch (type) {
                case post:
                    typeReference = new TypeReference<WallPost>() {};
                    break;
                case news:
                    typeReference = new TypeReference<WallNews>() {};
                    break;
                case betting:
                    typeReference = new TypeReference<WallBetting>() {};
                    break;
                case stats:
                    typeReference = new TypeReference<WallStats>() {};
                    break;
                case rumor:
                    typeReference = new TypeReference<WallRumor>() {};
                    break;
                case wallStoreItem:
                    typeReference = new TypeReference<WallStoreItem>(){};
            }
            WallBase item = mapper.convertValue(wallItem, typeReference);
            item.setType(type);
            return item;
        }
        return null;
    }
}
