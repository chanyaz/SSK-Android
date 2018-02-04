package base.app.util.events;

import base.app.data.wall.WallBase;

/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class BusEvent {

    protected String id;
    protected String secondaryId;
    protected WallBase item;

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

    public WallBase getItem() {
        return item;
    }

    public void setItem(WallBase item) {
        this.item = item;
    }
}
