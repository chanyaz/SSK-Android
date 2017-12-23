package base.app.events;

import base.app.model.im.ImsMessage;

/**
 * Created by Filip on 10/25/2017.
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
