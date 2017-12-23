package base.app.model.im.event;

import base.app.model.im.ImsMessage;

/**
 * Created by Filip on 4/25/2017.
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
