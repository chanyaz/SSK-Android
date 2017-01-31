package tv.sportssidekick.sportssidekick.model.club;

/**
 * Created by Filip on 1/30/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class Station {

    private String url;
    private String name;
    private String coverImageUrl;

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

    private boolean isPodcast;

    public Station(String stationUrl, String stationName, String stationCoverImage, boolean isPodcast) {
        this.url = stationUrl;
        this.name = stationName;
        this.coverImageUrl = stationCoverImage;
        this.isPodcast = isPodcast;
    }
}
