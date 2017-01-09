package tv.sportssidekick.sportssidekick.model.wall;

import java.util.Date;

import tv.sportssidekick.sportssidekick.model.Model;

/**
 * Created by Filip on 1/7/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class PostComment {

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getPosterId() {
        return posterId;
    }

    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private String commentId;
    private String posterId;
    private Date timestamp;
    private String comment;

    public PostComment(String comment) {
        this.commentId = "";
        this.posterId = Model.getInstance().getUserInfo().getUserId();
        this.timestamp = new Date();
        this.comment = comment;
    }
}
