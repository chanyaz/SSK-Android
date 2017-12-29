package base.app.events.chat;

import base.app.events.BusEvent;
import base.app.model.im.ImsMessage;

/**
 * Created by Filip on 10/25/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class MessageSelectedEvent extends BusEvent {
    ImsMessage selectedMessage;

    public MessageSelectedEvent(ImsMessage selectedMessage) {
        super(selectedMessage.getId());
        this.selectedMessage = selectedMessage;
    }

    public ImsMessage getSelectedMessage() {
        return selectedMessage;
    }
}
