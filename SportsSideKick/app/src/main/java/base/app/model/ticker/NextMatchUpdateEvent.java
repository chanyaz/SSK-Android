package base.app.model.ticker;

import base.app.events.BusEvent;

/**
 * Created by Filip on 8/23/2017.
 */

public class NextMatchUpdateEvent extends BusEvent {

    private String valueToDisplay;

    public NextMatchUpdateEvent() {
        super(null);
    }


}
