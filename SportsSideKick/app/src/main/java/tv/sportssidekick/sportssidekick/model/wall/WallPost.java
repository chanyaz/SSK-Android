package tv.sportssidekick.sportssidekick.model.wall;

/**
 * Created by Filip on 1/7/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import tv.sportssidekick.sportssidekick.model.Id;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true,value={"type"})
public class WallPost extends WallBase {

    @JsonProperty("_id")
    private Id id;
    @JsonProperty("coverAspectRatio")
    private Float coverAspectRatio;
    @JsonProperty("bodyText")
    private String bodyText;
    @JsonProperty("coverImageUrl")
    private String coverImageUrl;
    @JsonProperty("title")
    private String title;
    @JsonProperty("subTitle")
    private String subTitle;
    @JsonProperty("vidUrl")
    private String vidUrl;

    @JsonProperty("_id")
    public Id getId() {
        return id;
    }

    @JsonProperty("_id")
    public void setId(Id id) {
        this.id = id;
    }

    @JsonProperty("coverAspectRatio")
    public Float getCoverAspectRatio() {
        return coverAspectRatio;
    }

    @JsonProperty("coverAspectRatio")
    public void setCoverAspectRatio(Float coverAspectRatio) {
        this.coverAspectRatio = coverAspectRatio;
    }

    @JsonProperty("bodyText")
    public String getBodyText() {
        return bodyText;
    }

    @JsonProperty("bodyText")
    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    @JsonProperty("coverImageUrl")
    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    @JsonProperty("coverImageUrl")
    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("subTitle")
    public String getSubTitle() {
        return subTitle;
    }

    @JsonProperty("subTitle")
    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    @JsonProperty("vidUrl")
    public String getVidUrl() {
        return vidUrl;
    }

    @JsonProperty("vidUrl")
    public void setVidUrl(String vidUrl) {
        this.vidUrl = vidUrl;
    }
}