package base.app.util.events;

import base.app.data.content.wall.BaseItem;

/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class BusEvent {

    protected String id;
    protected BaseItem item;

    public BusEvent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BaseItem getItem() {
        return item;
    }

    public void setItem(BaseItem item) {
        this.item = item;
    }
}