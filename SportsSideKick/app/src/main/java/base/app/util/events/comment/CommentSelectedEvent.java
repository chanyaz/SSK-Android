package base.app.util.events.comment;

import base.app.util.events.BusEvent;
import base.app.data.wall.Comment;

/**
 * Created by Filip on 10/25/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class CommentSelectedEvent extends BusEvent {
    Comment selectedComment;

    public CommentSelectedEvent(Comment selectedComment) {
        super(selectedComment.getId());
        this.selectedComment = selectedComment;
    }

    public Comment getSelectedComment() {
        return selectedComment;
    }
}
