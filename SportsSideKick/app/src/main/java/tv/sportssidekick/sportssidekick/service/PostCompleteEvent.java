package tv.sportssidekick.sportssidekick.service;

import com.google.firebase.database.DatabaseError;

/**
 * Created by Filip on 1/7/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class PostCompleteEvent extends BusEvent {

    public DatabaseError getError() {
        return error;
    }

    DatabaseError error;

    public PostCompleteEvent(DatabaseError error) {
        super("");
        this.error = error;
    }
}
