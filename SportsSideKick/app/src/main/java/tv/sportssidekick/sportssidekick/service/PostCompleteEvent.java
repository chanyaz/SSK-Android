package tv.sportssidekick.sportssidekick.service;

/**
 * Created by Filip on 1/7/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class PostCompleteEvent extends BusEvent {

    public Object getError() {
        return error;
    }

    Object error;

    public PostCompleteEvent(Object error) {
        super("");
        this.error = error;
    }
}
