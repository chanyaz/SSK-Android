package tv.sportssidekick.sportssidekick.model.wall;

import java.util.Date;

/**
 * Created by Filip on 1/10/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class WallStoreItem extends WallBase {

    private String title;
    private String subTitle;
    private String url;
    private Date pubDate;
    private String coverImage;
    private float coverAspectRatio;

    public WallStoreItem(){
        super();
    }

    public float getCoverAspectRatio() {
        return coverAspectRatio;
    }

    public WallStoreItem setCoverAspectRatio(float coverAspectRatio) {
        this.coverAspectRatio = coverAspectRatio;
        return this;
    }

    public String getCoverImage() {
        return coverImage;
    }

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
