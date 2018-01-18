package base.app.util.events.comment;

import base.app.util.events.BusEvent;
import base.app.data.wall.Comment;
import base.app.data.wall.BaseItem;

/**
 * Created by Filip on 6/27/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class CommentUpdatedEvent extends BusEvent {

    private Comment comment;

    public CommentUpdatedEvent(BaseItem wallItem) {
        super("");
        this.wallItem = wallItem;
    }

    public BaseItem getWallItem() {
        return wallItem;
    }

    public void setWallItem(BaseItem wallItem) {
        this.wallItem = wallItem;
    }

    BaseItem wallItem;

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Comment getComment() {
        return comment;
    }
}
