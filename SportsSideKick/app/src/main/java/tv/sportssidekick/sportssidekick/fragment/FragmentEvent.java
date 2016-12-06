package tv.sportssidekick.sportssidekick.fragment;


import tv.sportssidekick.sportssidekick.service.BusEvent;

/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class FragmentEvent extends BusEvent {

    public enum Type {
        WALL, // Add all types of Fragments here
        NEWS,
        VIDEO_CHAT,
        RUMOURS,
        STORE,
        INITIAL,
        LOGIN,
        FORGOT_PASSWORD
    }

    private Type type;
     Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }

    public FragmentEvent(Type type) {
        super(null);
        this.type = type;
    }
}
