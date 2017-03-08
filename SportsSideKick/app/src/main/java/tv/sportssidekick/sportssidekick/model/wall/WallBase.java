package tv.sportssidekick.sportssidekick.model.wall;

import android.support.annotation.Nullable;
import tv.sportssidekick.sportssidekick.model.UserInfo;

/**
 * Created by Filip on 1/6/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class WallBase {

    enum PostType {
        post,
        news,
        betting,
        stats,
        rumor,
        wallStoreItem
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

    public UserInfo getPoster() {
        return poster;
    }

    public void setPoster(UserInfo poster) {
        this.poster = poster;
    }

    protected String wallId;
    protected String postId;
    protected String timestamp;
    protected int likeCount;
    protected boolean likedByUser;
    protected int commentsCount;

    protected UserInfo poster;

    public void toggleLike(){
        if(likedByUser){
            likedByUser = false;
            likeCount -= 1;
        } else {

        }
    }

    public void setEqualTo(WallBase item){
        this.timestamp = item.timestamp;
        this.likeCount = item.likeCount;
        this.likedByUser = item.likedByUser;
        this.commentsCount = item.commentsCount;
    }

    @Nullable
    static WallBase postFactory(Object snapshot) {
        // TODO Rewrite to GS
        throw new UnsupportedOperationException("Implement first with GS!");
//        if(!snapshot.exists()){
//            return null;
//        }
//        int typeCode = snapshot.child("type").getValue(Integer.class);
//
//        PostType postType = PostType.values()[typeCode-1];
//
//        switch (postType) {
//            case post:
//                break;
//            case news:
//                break;
//            case betting:
//                break;
//            case stats:
//                break;
//            case rumor:
//                break;
//            case wallStoreItem:
//                break;
//        }
//        return snapshot.getValue(WallPost.class);
    }
}
