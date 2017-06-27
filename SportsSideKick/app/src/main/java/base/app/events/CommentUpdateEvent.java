package base.app.events;

import base.app.model.wall.WallBase;

/**
 * Created by Filip on 6/27/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class CommentUpdateEvent extends BusEvent {

    public CommentUpdateEvent(WallBase wallItem) {
        super("");
        this.wallItem = wallItem;
    }

    public WallBase getWallItem() {
        return wallItem;
    }

    public void setWallItem(WallBase wallItem) {
        this.wallItem = wallItem;
    }

    WallBase wallItem;
}
