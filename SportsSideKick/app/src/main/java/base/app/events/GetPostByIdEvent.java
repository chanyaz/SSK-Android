package base.app.events;

import base.app.model.wall.WallBase;

/**
 * Created by Filip on 1/7/2017.
 */

public class GetPostByIdEvent extends BusEvent {

    public WallBase getPost() {
        return post;
    }

    WallBase post;

    public GetPostByIdEvent(WallBase post) {
        super("");
        this.post = post;
    }
}
