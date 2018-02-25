package base.app.data.wall;

/**
 * Created by Filip on 1/10/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import base.app.data.Id;
import base.app.data.wall.sharing.SharingManager;
import base.app.util.commons.Utility;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WallNews extends WallBase {

    @JsonProperty("vidUrl")
    private String vidUrl;
    @JsonProperty("url")
    private String url;
    @JsonProperty("source")
    private String source;
    @JsonProperty("image")
    private String image;

    @Override
    public void setEqualTo(WallBase item) {
        super.setEqualTo(item);
        this.vidUrl = ((WallNews) item).vidUrl;
        this.url = ((WallNews) item).url;
        this.source = ((WallNews) item).source;
    }

    @JsonProperty("vidUrl")
    public String getVidUrl() {
        return vidUrl;
    }

    @JsonProperty("vidUrl")
    public void setVidUrl(String vidUrl) {
        this.vidUrl = vidUrl;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("source")
    public String getSource() {
        return Utility.capitalizeFirst(source);
    }

    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    @JsonProperty("_id")
    public void setId(Id id) {
        postId = id.getOid();
    }

    @JsonProperty("pubDate")
    public void setPubDate(Double pubDate) {
        this.timestamp = pubDate;
    }

    @JsonProperty("image")
    public void setImage(String image) {
        this.image = image;
        this.coverImageUrl = image;
    }

    @JsonProperty("image")
    public String getImage() {
        return image;
    }

    @Override
    public SharingManager.ItemType getItemType() {
        return SharingManager.ItemType.News;
    }
}

