package base.app.util.events.post;

import base.app.util.events.BusEvent;
import base.app.data.wall.BaseItem;

/**
 * Created by Filip on 1/7/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class GetPostByIdEvent extends BusEvent {

    public BaseItem getPost() {
        return post;
    }

    BaseItem post;

    public GetPostByIdEvent(BaseItem post) {
        super("");
        this.post = post;
    }
}
