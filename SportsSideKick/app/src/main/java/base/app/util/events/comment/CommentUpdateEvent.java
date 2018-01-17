package base.app.util.events.comment;

import base.app.util.events.BusEvent;
import base.app.data.wall.Comment;
import base.app.data.wall.WallItem;

/**
 * Created by Filip on 6/27/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class CommentUpdateEvent extends BusEvent {

    private Comment comment;

    public CommentUpdateEvent(WallItem wallItem) {
        super("");
        this.wallItem = wallItem;
    }

    public WallItem getWallItem() {
        return wallItem;
    }

    public void setWallItem(WallItem wallItem) {
        this.wallItem = wallItem;
    }

    WallItem wallItem;

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Comment getComment() {
        return comment;
    }
}
