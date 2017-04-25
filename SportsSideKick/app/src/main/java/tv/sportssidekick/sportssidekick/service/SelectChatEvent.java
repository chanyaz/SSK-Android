package tv.sportssidekick.sportssidekick.service;

/**
 * Created by Filip on 12/15/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class SelectChatEvent extends BusEvent {

    int position;

    public SelectChatEvent(int position) {
        super(null);
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
