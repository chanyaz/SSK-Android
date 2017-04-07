package tv.sportssidekick.sportssidekick.model.wall;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Filip on 4/6/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class WallNewsShare extends WallBase{

    @JsonProperty("vidUrl")
    private String vidUrl;

    @Override
    public void setEqualTo(WallBase item) {
        super.setEqualTo(item);
        this.vidUrl = ((WallNewsShare)item).vidUrl;

    }
}
