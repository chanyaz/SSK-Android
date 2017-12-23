package base.app.events;

/**
 * Created by Filip on 12/5/2016.
 */

public class BusEvent {

    protected String id;

    public BusEvent(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
