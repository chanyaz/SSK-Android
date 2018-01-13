package base.app.util.events.post;

import base.app.util.events.BusEvent;
import base.app.data.wall.WallItem;

/**
 * Created by v3 on 11/5/2017.
 */

public class PostDeletedEvent extends BusEvent {

    public WallItem getPost() {
        return post;
    }

    private final WallItem post;

    public PostDeletedEvent(WallItem post) {
        super("");
        this.post = post;
    }
}
