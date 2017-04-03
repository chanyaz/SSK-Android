package tv.sportssidekick.sportssidekick.model.wall;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by Filip on 1/10/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true,value={"type"})
public class WallStoreItem extends WallBase {

    private String title;
    private String subTitle;
    private String url;
    private Date pubDate;
    @JsonProperty("coverImage")
    private String coverImage;
    @JsonProperty("coverImageUrl")
    private String coverImageUrl;
    @JsonProperty("coverAspectRatio")
    private float coverAspectRatio;
    //TODO need price from server?!

    public WallStoreItem(){
        super();
    }


    @JsonProperty("coverAspectRatio")
    public float getCoverAspectRatio() {
        return coverAspectRatio;
    }

    @JsonProperty("coverAspectRatio")
    public WallStoreItem setCoverAspectRatio(float coverAspectRatio) {
        this.coverAspectRatio = coverAspectRatio;
        return this;
    }

    @JsonProperty("coverImageUrl")
    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    @JsonProperty("coverImageUrl")
    public WallStoreItem setCoverImageUrl(String coverImage) {
        this.coverImageUrl = coverImage;
        return this;
    }

    @JsonProperty("coverImage")
    public String getCoverImage() {
        return coverImage;
    }

    @JsonProperty("coverImage")
    public WallStoreItem setCoverImage(String coverImage) {
        this.coverImage = coverImage;
        return this;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public WallStoreItem setPubDate(Date pubDate) {
        this.pubDate = pubDate;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public WallStoreItem setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public WallStoreItem setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public WallStoreItem setTitle(String title) {
        this.title = title;
        return this;
    }

    public void setEqualTo(WallBase item){
        if(item instanceof WallStoreItem){
            WallStoreItem post = (WallStoreItem) item;
            this.title = post.getTitle();
            this.subTitle = post.getSubTitle();
            this.url = post.getUrl();
            this.pubDate = post.getPubDate();
            this.coverImage = post.getCoverImage();
            this.coverAspectRatio = post.getCoverAspectRatio();
            super.setEqualTo(item);
        }
    }
}
