package base.app.events;

import base.app.model.im.ImsMessage;
import base.app.model.wall.PostComment;

/**
 * Created by Filip on 10/25/2017.
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
