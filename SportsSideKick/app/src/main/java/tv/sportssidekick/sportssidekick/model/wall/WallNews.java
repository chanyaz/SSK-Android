package tv.sportssidekick.sportssidekick.model.wall;

/**
 * Created by Filip on 1/10/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class WallNews extends WallBase {

    String title;
    String subTitle;
    String bodyText;
    String coverImageUrl;

    public String getTitle() {
        return title;
    }

    public WallNews setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public WallNews setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    public String getBodyText() {
        return bodyText;
    }

    public WallNews setBodyText(String bodyText) {
        this.bodyText = bodyText;
        return this;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public WallNews setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
        return this;
    }

    public float getCoverAspectRatio() {
        return coverAspectRatio;
    }

    public WallNews setCoverAspectRatio(float coverAspectRatio) {
        this.coverAspectRatio = coverAspectRatio;
        return this;
    }

    public String getVidUrl() {
        return vidUrl;
    }

    public WallNews setVidUrl(String vidUrl) {
        this.vidUrl = vidUrl;
        return this;
    }

    float coverAspectRatio;
    String vidUrl;

    public WallNews(){
        super();
    }

    public void setEqualTo(WallBase item){
        if (item instanceof WallNews) {
            WallNews bet = (WallNews) item;
            this.title = bet.getTitle();
            this.subTitle = bet.getSubTitle();
            this.bodyText = bet.getBodyText();
            this.coverImageUrl = bet.getCoverImageUrl();
            this.coverAspectRatio = bet.getCoverAspectRatio();
            this.vidUrl = bet.getVidUrl();
            super.setEqualTo(item);
        }
    }
}
