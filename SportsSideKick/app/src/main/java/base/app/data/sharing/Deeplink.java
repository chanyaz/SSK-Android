package base.app.data.sharing;

/**
 * Created by Filip on 8/23/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class Deeplink {

    String id;
    ShareHelper.ItemType type;

    public Deeplink(String id, ShareHelper.ItemType type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ShareHelper.ItemType getType() {
        return type;
    }

    public void setType(ShareHelper.ItemType type) {
        this.type = type;
    }
}
