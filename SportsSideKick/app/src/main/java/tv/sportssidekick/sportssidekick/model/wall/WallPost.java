package tv.sportssidekick.sportssidekick.model.wall;

/**
 * Created by Filip on 1/7/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WallPost extends WallBase {

    @JsonProperty("vidUrl")
    private String vidUrl;

    @JsonProperty("vidUrl")
    public String getVidUrl() {
        return vidUrl;
    }

    @JsonProperty("vidUrl")
    public void setVidUrl(String vidUrl) {
        this.vidUrl = vidUrl;
    }
}