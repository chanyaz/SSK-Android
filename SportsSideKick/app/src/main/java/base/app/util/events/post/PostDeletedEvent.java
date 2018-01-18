package base.app.util.events.post;

import base.app.util.events.BusEvent;
import base.app.data.wall.BaseItem;

/**
 * Created by v3 on 11/5/2017.
 */

public class PostDeletedEvent extends BusEvent {

    public BaseItem getPost() {
        return post;
    }

    private final BaseItem post;

    public PostDeletedEvent(BaseItem post) {
        super("");
        this.post = post;
    }
}
