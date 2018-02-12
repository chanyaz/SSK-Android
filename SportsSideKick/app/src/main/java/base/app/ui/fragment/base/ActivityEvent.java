package base.app.ui.fragment.base;


import base.app.util.events.BusEvent;

/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ActivityEvent extends BusEvent {

    public Class getType() {
        return classType;
    }

    public void setType(Class type) {
        this.classType = type;
    }

    private Class classType;

    public ActivityEvent(Class type) {
        super(null);
        this.classType = type;
    }
}
