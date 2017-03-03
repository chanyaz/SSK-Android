package tv.sportssidekick.sportssidekick.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

/**
 * Created by Filip on 12/7/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class FirebseObject {

    @Exclude
    public String getId() {
        return id;
    }

    public FirebseObject setId(String id) {
        this.id = id;
        return this;
    }

    @Exclude
    private String id;
}
