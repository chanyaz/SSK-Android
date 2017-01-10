package tv.sportssidekick.sportssidekick.model.wall;

/**
 * Created by Filip on 1/10/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class WallRumor extends WallBase {

    public String getTitle() {
        return title;
    }

    public WallRumor setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public WallRumor setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public WallRumor setUrl(String url) {
        this.url = url;
        return this;
    }

    private String title;
    private String subTitle;
    private String url;

    public WallRumor(){
        super();
    }

    public void setEqualTo(WallBase item){
        if (item instanceof WallRumor) {
            WallRumor bet = (WallRumor) item;
            this.title = bet.getTitle();
            this.subTitle = bet.getSubTitle();
            this.url = bet.getUrl();
            super.setEqualTo(item);
        }
    }
}
