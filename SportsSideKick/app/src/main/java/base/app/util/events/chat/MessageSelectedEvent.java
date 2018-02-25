package base.app.util.events.chat;

import base.app.util.events.BusEvent;
import base.app.data.chat.ImsMessage;

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
