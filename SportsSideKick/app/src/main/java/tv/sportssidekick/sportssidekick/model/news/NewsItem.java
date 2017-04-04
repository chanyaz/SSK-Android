package tv.sportssidekick.sportssidekick.model.news;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.SimpleDateFormat;

import tv.sportssidekick.sportssidekick.model.Id;

/**
 * Created by Djordje Krutil on 27.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class NewsItem {



    @JsonProperty("_id")
    private Id id;
    private String title;
    private String url;
    private Double pubDate;

    private String type;
    private String source;
    private String content;
    private String strap;
    private String image;
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

    @JsonProperty("_id")
    public Id getId() {
        return id;
    }

    @JsonProperty("_id")
    public void setId(Id id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Double getPubDate() {
        return pubDate;
    }

    public void setPubDate(Double pubDate) {
        this.pubDate = pubDate;
    }
//
//    @JsonIgnore
//    public NewsType getType() {
//        if(type.equalsIgnoreCase("OFFICIAL")){
//            return  NewsType.OFFICIAL;
//        } else {
//            return NewsType.UNOFFICIAL;
//        }
//    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStrap() {
        return strap;
    }

    public void setStrap(String strap) {
        this.strap = strap;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
