package tv.sportssidekick.sportssidekick.fragment;


import tv.sportssidekick.sportssidekick.service.BusEvent;

/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class FragmentEvent extends BusEvent {

    public Class getType() {
        return classType;
    }

    public void setType(Class type) {
        this.classType = type;
    }

    private Class classType;
    private boolean isReturning;

    public FragmentEvent(Class type) {
        super(null);
        this.classType = type;
        isReturning = false;
    }

    public FragmentEvent(Class type, boolean isReturning) {
        super(null);
        this.classType = type;
        this.isReturning = isReturning;
    }

    public boolean isReturning() {
        return isReturning;
    }

    public FragmentEvent setReturning(boolean returning) {
        isReturning = returning;
        return this;
    }
}
