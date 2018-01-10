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
import base.app.data.sharing.SharingManager;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WallNews extends WallBase {

    @JsonProperty("vidUrl")
    private String vidUrl;
    @JsonProperty("url")
    private String url;
    @JsonProperty("source")
    private String source;

    @Override
    public void setEqualTo(WallBase item) {
        super.setEqualTo(item);
        this.vidUrl = ((WallNews)item).vidUrl;
        this.url = ((WallNews)item).url;
        this.source = ((WallNews)item).source;
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
        return source;
    }

    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    @JsonProperty("_id")
    public void setId(Id id) {
        postId = id.getOid();
    }

    @JsonProperty("strap")
    public void setStrap(String strap) {
        this.subTitle = strap;
    }

    @JsonProperty("pubDate")
    public void setPubDate(Double pubDate) {
        this.timestamp = pubDate;
    }

    @JsonProperty("image")
    public void setImage(String image) {
        this.coverImageUrl = image;
    }

    @JsonProperty("content")
    public void setContent(String content) {
        this.bodyText = content;
    }

    @Override
    public SharingManager.ItemType getItemType(){
        if (!getSharedComment().replaceAll(" ", "").isEmpty()) {
            return SharingManager.ItemType.NewsShare;
        }
        return SharingManager.ItemType.News;
    }
}

