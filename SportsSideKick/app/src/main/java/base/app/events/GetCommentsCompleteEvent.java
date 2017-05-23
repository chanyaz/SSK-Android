package base.app.events;

import java.util.List;
import base.app.model.wall.PostComment;

/**
 * Created by Filip on 1/7/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class GetCommentsCompleteEvent extends BusEvent {

    public List<PostComment> getCommentList() {
        return commentList;
    }

    List<PostComment> commentList;

    public GetCommentsCompleteEvent(List<PostComment> commentList) {
        super("");
        this.commentList = commentList;
    }
}
