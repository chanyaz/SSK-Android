package base.app.data.content.tv;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import base.app.data.Id;

/**
 * Created by Filip on 1/30/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Station {



    @JsonProperty("_id")
    private Id id;
    @JsonProperty("url")
    private String url;
    @JsonProperty("name")
    private String name;
    @JsonProperty("coverImageUrl")
    private String coverImageUrl;
    @JsonProperty("isPodcast")
    private boolean isPodcast;
    @JsonProperty("club_id")
    private int clubId;

    public Station() { }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public boolean isPodcast() {
        return isPodcast;
    }

    public void setPodcast(boolean podcast) {
        isPodcast = podcast;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



    public Station(String stationUrl, String stationName, String stationCoverImage, boolean isPodcast) {
        this.url = stationUrl;
        this.name = stationName;
        this.coverImageUrl = stationCoverImage;
        this.isPodcast = isPodcast;
    }
}
