package tv.sportssidekick.sportssidekick.model.im;

import java.util.List;

/**
 * Created by Filip on 2/3/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class NewMessagesEvent {

    public NewMessagesEvent(List<ImsMessage> values) {
        this.values = values;
    }

    public List<ImsMessage> getValues() {
        return values;
    }

    public NewMessagesEvent setValues(List<ImsMessage> values) {
        this.values = values;
        return this;
    }

    List<ImsMessage> values;
}
