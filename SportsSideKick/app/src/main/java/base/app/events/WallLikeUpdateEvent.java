package base.app.events;

/**
 * Created by Filip on 6/27/2017.
 */

public class WallLikeUpdateEvent extends BusEvent {

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public WallLikeUpdateEvent(String wallId, String postId, int count) {
        super("");
        this.wallId = wallId;
        this.postId = postId;
        this.count = count;
    }

    String wallId;
    String postId;
    int count;
}
