package tv.sportssidekick.sportssidekick.service;

import tv.sportssidekick.sportssidekick.model.wall.WallBase;

/**
 * Created by Filip on 1/7/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
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
