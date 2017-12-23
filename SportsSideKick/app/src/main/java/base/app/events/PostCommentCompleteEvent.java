package base.app.events;

import base.app.model.wall.PostComment;

/**
 * Created by Filip on 1/7/2017.
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
