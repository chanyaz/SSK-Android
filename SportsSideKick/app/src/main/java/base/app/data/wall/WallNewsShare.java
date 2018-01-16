package base.app.data.wall;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Filip on 4/6/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class WallNewsShare extends WallItem {

    @JsonProperty("vidUrl")
    private String vidUrl;

    public String getVidUrl() {
        return vidUrl;
    }
}
