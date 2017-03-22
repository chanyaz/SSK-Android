package tv.sportssidekick.sportssidekick.model.wall;

import android.support.annotation.Nullable;

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
        // TODO
//        WallModel.instance.setlikeVal(self, val: self.likedByUser) {
//            error in
//
//            if error != nil {
//                print("WallBase.toggleLike() -> Error: \(error)")
//                return
//            }
//        }
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
