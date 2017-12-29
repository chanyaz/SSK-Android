package base.app.util.events.post;

import base.app.util.events.BusEvent;
import base.app.data.wall.WallBase;

/**
 * Created by Filip on 1/7/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class PostUpdateEvent extends BusEvent {


    public WallBase getPost() {
        return post;
    }

    private final WallBase post;

    public PostUpdateEvent(WallBase post) {
        super("");
        this.post = post;
    }
}
