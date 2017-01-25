package tv.sportssidekick.sportssidekick.model.club;

import java.util.Date;

/**
 * Created by Filip on 1/24/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class TvChannel {

    String id;
    String name;
    String url;
    Date date;
    String imageUrl;

    public TvChannel(String id, String name, String url, Date date, String imageUrl) {

        this.id = id;
        this.name = name;
        this.url = url;
        this.date = date;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public TvChannel setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public TvChannel setName(String name) {
        this.name = name;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public TvChannel setUrl(String url) {
        this.url = url;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public TvChannel setDate(Date date) {
        this.date = date;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public TvChannel setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }
}
