package tv.sportssidekick.sportssidekick.model;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by Filip on 12/7/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class FirebseObject {

    public String getId() {
        return id;
    }

    public FirebseObject setId(String id) {
        this.id = id;
        return this;
    }

    private String id;

    public void loadKey(DatabaseReference reference){
        id = reference.getKey();
    }

}
