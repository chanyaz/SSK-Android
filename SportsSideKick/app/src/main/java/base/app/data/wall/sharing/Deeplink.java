package base.app.data.wall.sharing;

/**
 * Created by Filip on 8/23/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class Deeplink {

    String id;
    SharingManager.ItemType type;

    public Deeplink(String id, SharingManager.ItemType type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SharingManager.ItemType getType() {
        return type;
    }

    public void setType(SharingManager.ItemType type) {
        this.type = type;
    }
}
