package base.app.util.events.post;

import base.app.data.wall.WallBase;
import base.app.data.wall.WallPost;
import base.app.util.events.BusEvent;

/**
 * Created by Filip on 1/7/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class PostUpdateEvent extends BusEvent {

    public WallPost getPost() {
        return post;
    }

    private final WallPost post;

    public PostUpdateEvent(WallBase post) {
        super("");
        this.post = (WallPost) post;
    }
}
