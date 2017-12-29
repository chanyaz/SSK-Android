package base.app.events.comment;

import base.app.events.BusEvent;
import base.app.model.wall.PostComment;
import base.app.model.wall.WallBase;

/**
 * Created by v3 on 11/5/2017.
 */

public class CommentDeleteEvent extends BusEvent {
    private PostComment comment;

    public CommentDeleteEvent(WallBase wallItem) {
        super("");
        this.wallItem = wallItem;
    }

    public WallBase getWallItem() {
        return wallItem;
    }

    public void setWallItem(WallBase wallItem) {
        this.wallItem = wallItem;
    }

    WallBase wallItem;

    public void setComment(PostComment comment) {
        this.comment = comment;
    }

    public PostComment getComment() {
        return comment;
    }
}
