package base.app.events;

import base.app.model.wall.WallBase;

/**
 * Created by Filip on 1/7/2017.
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
