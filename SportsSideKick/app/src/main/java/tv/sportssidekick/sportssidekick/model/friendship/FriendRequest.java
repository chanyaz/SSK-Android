package tv.sportssidekick.sportssidekick.model.friendship;

import java.util.Date;

/**
 * Created by Djordje on 21/01/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class FriendRequest {

    String id;
    String displayedName;
    String imageUrl;
    Date date;

    public FriendRequest(String id, String displayedName, String imageUrl, Date date) {
        this.id = id;
        this.displayedName = displayedName;
        this.imageUrl = imageUrl;
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayedName() {
        return displayedName;
    }

    public void setDisplayedName(String displayedName) {
        this.displayedName = displayedName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
