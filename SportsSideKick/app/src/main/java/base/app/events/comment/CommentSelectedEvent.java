package base.app.events.comment;

import base.app.events.BusEvent;
import base.app.model.im.ImsMessage;
import base.app.model.wall.PostComment;

/**
 * Created by Filip on 10/25/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class CommentSelectedEvent extends BusEvent {
    PostComment selectedComment;

    public CommentSelectedEvent(PostComment selectedComment) {
        super(selectedComment.getId());
        this.selectedComment = selectedComment;
    }

    public PostComment getSelectedComment() {
        return selectedComment;
    }
}
