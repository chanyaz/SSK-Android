package base.app.util.events;

import base.app.data.wall.WallItem;

/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class BusEvent {

    protected String id;
    protected String secondaryId;
    protected WallItem item;

    public BusEvent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSecondaryId() {
        return secondaryId;
    }

    public void setSecondaryId(String secondaryId) {
        this.secondaryId = secondaryId;
    }

    public WallItem getItem() {
        return item;
    }

    public void setItem(WallItem item) {
        this.item = item;
    }
}
