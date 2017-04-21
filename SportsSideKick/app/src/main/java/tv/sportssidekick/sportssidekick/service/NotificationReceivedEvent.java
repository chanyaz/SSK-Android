package tv.sportssidekick.sportssidekick.service;

/**
 * Created by Djordje Krutil on 21.4.2017..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class NotificationReceivedEvent extends BusEvent {

    private int closeTIme;
    private String title;
    private String description;
    private int type;

    public NotificationReceivedEvent(int closeTIme, String title, String description, int type) {
        super("");
        this.closeTIme = closeTIme;
        this.title = title;
        this.description = description;
        this.type = type;
    }

    public int getCloseTIme() {
        return closeTIme;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getType() {
        return type;
    }
}
