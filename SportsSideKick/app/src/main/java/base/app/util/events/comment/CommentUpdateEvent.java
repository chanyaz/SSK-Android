package base.app.util.events.comment;

import base.app.util.events.BusEvent;
import base.app.data.wall.PostComment;
import base.app.data.wall.WallBase;

/**
 * Created by Filip on 6/27/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class CommentUpdateEvent extends BusEvent {

    private PostComment comment;

    public CommentUpdateEvent(WallBase wallItem) {
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
