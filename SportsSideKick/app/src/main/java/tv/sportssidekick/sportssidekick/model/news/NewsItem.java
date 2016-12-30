package tv.sportssidekick.sportssidekick.model.news;

import com.google.firebase.database.DataSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Djordje Krutil on 27.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class NewsItem {

    public enum NewsType{

        OFFICIAL {
            public String toString() {
                return "official";
            }
        },
        UNOFFICIAL {
            public String toString() {
                return "news-now";
            }
        };

        NewsType() {}
    }

    private String id;
    private String title;
    private String url;
    private String pubDate;
    private NewsType type;
    private String source;
    private String content;
    private String strap;
    private String image;
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public NewsType getType() {
        return type;
    }

    public void setType(NewsType type) {
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

//    public NewsItem(String id, String title, String url, Date punData, NewsType type, String source, String content, String strap, String image) {
//        this.id = id;
//        this.title = title;
//        this.url = url;
//        this.pubData = punData;
//        this.type = type;
//        this.source = source;
//        this.content = content;
//        this.strap = strap;
//        this.image = image;
//    }
//
//    public NewsItem(DataSnapshot snapshot, NewsType type)
//    {
//        snapshot.child("url");
//        this.id = snapshot.getKey();
//        this.title = snapshot.child("title").toString();
//        this.url = snapshot.child("url").toString();
//        this.punData = new java.util.Date(Long.getLong(snapshot.child("pubDate").toString()));
//        this.type = type;
//        this.source = snapshot.child("source").toString();
//        this.content = snapshot.child("content").toString();
//        this.strap = snapshot.child("strap").toString();
//        this.image = snapshot.child("image").toString();
//    };
}
