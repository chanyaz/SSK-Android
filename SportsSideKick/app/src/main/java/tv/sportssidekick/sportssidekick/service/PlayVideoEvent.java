package tv.sportssidekick.sportssidekick.service;

/**
 * Created by Filip on 12/20/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class PlayVideoEvent extends BusEvent {

    public PlayVideoEvent(String id) {
        super(id);
    }
}