package base.app.events;

/**
 * Created by Filip on 1/7/2017.
 */

public class PostCompleteEvent extends BusEvent {

    public Object getError() {
        return error;
    }

    Object error;

    public PostCompleteEvent(Object error) {
        super("");
        this.error = error;
    }
}
