package base.app.events.comment;

import base.app.events.BusEvent;
import base.app.model.wall.PostComment;
import base.app.model.wall.WallBase;

/**
 * Created by Filip on 6/27/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class CommentUpdatedEvent extends BusEvent {

    private PostComment comment;

    public CommentUpdatedEvent(WallBase wallItem) {
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
