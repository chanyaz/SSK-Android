package base.app.util.events.comment;

import base.app.util.events.BusEvent;
import base.app.data.wall.PostComment;

/**
 * Created by Filip on 10/25/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class CommentSelectedEvent extends BusEvent {
    PostComment selectedComment;

    public CommentSelectedEvent(PostComment selectedComment) {
        super(selectedComment.getId().getOid());
        this.selectedComment = selectedComment;
    }

    public PostComment getSelectedComment() {
        return selectedComment;
    }
}
