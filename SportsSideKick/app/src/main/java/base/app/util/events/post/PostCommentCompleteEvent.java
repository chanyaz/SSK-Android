package base.app.util.events.post;

import base.app.util.events.BusEvent;
import base.app.data.wall.PostComment;

/**
 * Created by Filip on 1/7/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class PostCommentCompleteEvent extends BusEvent {

    public PostComment getComment() {
        return comment;
    }

    PostComment comment;

    public PostCommentCompleteEvent(PostComment comment) {
        super("");
        this.comment = comment;
    }
}
