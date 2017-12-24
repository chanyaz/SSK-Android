package base.app.model.ticker;

import base.app.events.BusEvent;

/**
 * Created by Filip on 8/23/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class NextMatchUpdateEvent extends BusEvent {

    private String valueToDisplay;

    public NextMatchUpdateEvent() {
        super(null);
    }


}
