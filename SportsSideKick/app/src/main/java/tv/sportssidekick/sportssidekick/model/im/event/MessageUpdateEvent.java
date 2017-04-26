package tv.sportssidekick.sportssidekick.model.im.event;

import tv.sportssidekick.sportssidekick.model.im.ImsMessage;

/**
 * Created by Filip on 4/25/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class MessageUpdateEvent {

    ImsMessage message;

    public MessageUpdateEvent(ImsMessage message) {
        this.message = message;
    }

    public ImsMessage getMessage() {
        return message;
    }
}
