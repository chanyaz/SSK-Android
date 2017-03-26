package tv.sportssidekick.sportssidekick.model.wall;

/**
 * Created by Filip on 1/10/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true,value={"type"})
public class WallNews extends WallBase {

    @JsonProperty("title")
    private String title;
    @JsonProperty("subTitle")
    private String subTitle;
    @JsonProperty("bodyText")
    private String bodyText;
    @JsonProperty("coverImageUrl")
    private String coverImageUrl;
    @JsonProperty("vidUrl")
    private String vidUrl;
    @JsonProperty("coverAspectRatio")
    private Float coverAspectRatio;

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

    @JsonProperty("vidUrl")
    public String getVidUrl() {
        return vidUrl;
    }

    @JsonProperty("vidUrl")
    public void setVidUrl(String vidUrl) {
        this.vidUrl = vidUrl;
    }

    @JsonProperty("coverAspectRatio")
    public Float getCoverAspectRatio() {
        return coverAspectRatio;
    }

    @JsonProperty("coverAspectRatio")
    public void setCoverAspectRatio(Float coverAspectRatio) {
        this.coverAspectRatio = coverAspectRatio;
    }

}

