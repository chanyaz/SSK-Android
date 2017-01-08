package tv.sportssidekick.sportssidekick.model.wall;

/**
 * Created by Filip on 1/7/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class WallPost extends WallBase{

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public float getCoverAspectRatio() {
        return coverAspectRatio;
    }

    public void setCoverAspectRatio(float coverAspectRatio) {
        this.coverAspectRatio = coverAspectRatio;
    }

    public String getVidUrl() {
        return vidUrl;
    }

    public void setVidUrl(String vidUrl) {
        this.vidUrl = vidUrl;
    }

    private String title;
    private String subTitle;
    private String bodyText;
    private String coverImageUrl;
    private float coverAspectRatio = 0.5625f;
    private String vidUrl;

    public WallPost(){
        super();
    }

    public WallPost(String title, String subTitle, String bodyText, String coverImageUrl, float coverAspectRatio, String vidUrl) {
        this.title = title;
        this.subTitle = subTitle;
        this.bodyText = bodyText;
        this.coverImageUrl = coverImageUrl;
        this.coverAspectRatio = coverAspectRatio;
        this.vidUrl = vidUrl;
    }

    public void setEqualTo(WallBase item){
        if(item instanceof WallPost){
            WallPost post = (WallPost) item;
            this.title = post.getTitle();
            this.subTitle = post.getSubTitle();
            this.bodyText = post.getBodyText();
            this.coverImageUrl = post.getCoverImageUrl();
            this.coverAspectRatio = post.getCoverAspectRatio();
            this.vidUrl = post.getVidUrl();
            super.setEqualTo(item);
        }
    }
}
