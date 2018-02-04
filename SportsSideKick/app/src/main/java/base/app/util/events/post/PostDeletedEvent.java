package base.app.util.events.post;

import base.app.util.events.BusEvent;
import base.app.data.wall.WallBase;

/**
 * Created by v3 on 11/5/2017.
 */

public class PostDeletedEvent extends BusEvent {

    public WallBase getPost() {
        return post;
    }

    private final WallBase post;

    public PostDeletedEvent(WallBase post) {
        super("");
        this.post = post;
    }
}
