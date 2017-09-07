package base.app.events;

/**
 * Created by Djordje Krutil on 21.4.2017..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class NotificationReceivedEvent extends BusEvent {

    private int closeTime;
    private String title;
    private String postId;
    private String description;
    private Type type;

    public enum Type {
        FRIEND_REQUESTS,
        FOLLOWERS,
        LIKES,
    }

    public NotificationReceivedEvent(int closeTime, String title, String description, Type type, String postId) {
        super("");
        this.closeTime = closeTime;
        this.title = title;
        this.postId = postId;
        this.description = description;
        this.type = type;
    }

    public NotificationReceivedEvent(int closeTIme, String title, String description, Type type) {
        super("");
        this.closeTime = closeTIme;
        this.title = title;
        this.description = description;
        this.type = type;
    }

    public int getCloseTime() {
        return closeTime;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Type getType() {
        return type;
    }


    public String getPostId() {
        return postId;
    }
}
