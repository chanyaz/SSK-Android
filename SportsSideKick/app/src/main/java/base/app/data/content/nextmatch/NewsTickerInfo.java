
package base.app.data.content.nextmatch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsTickerInfo {

    private String title;
    private List<String> news = null;
    private String firstClubUrl;
    private String secondClubUrl;
    private String firstClubName;
    private String secondClubName;
    private String matchDate;
    private String language;

    /**
     * No args constructor for use in serialization
     * 
     */
    public NewsTickerInfo() {
    }



//    {"firstClubName":"RFC","secondClubUrl":"http:\/\/desporto_stats.imgs.sapo.pt\/3\/Teams\/M74559.png","club_id":1680,"title":"SPORTING CP","news":["Rúben Semedo for under-21's","Champions League: B. Dortmund, 1-Sporting CP, 0","Alvalade youngsters break down the yellow wall and climb up the Group F table","Sporting CP pens deal with FHK","Bruno de Carvalho in the USA to celebrate 50th Anniversary"],"language":"en","secondClubName":"SCP","_id":{"$oid":"58b41c47dbb71400011a91c2"},"firstClubUrl":"http:\/\/desporto_stats.imgs.sapo.pt\/3\/Teams\/M74557.png","matchDate":"1509138000"}

    /**
     *
     * @param news
     * @param title
     * @param firstClubUrl
     * @param secondClubUrl
     * @param firstClubName
     * @param secondClubName
     * @param matchDate
     */
    public NewsTickerInfo(List<String> news, String title, String firstClubUrl, String secondClubUrl, String firstClubName, String secondClubName, String matchDate) {
        super();
        this.title = title;
        this.news = news;
        this.firstClubUrl = firstClubUrl;
        this.secondClubUrl = secondClubUrl;
        this.matchDate = matchDate;
        this.firstClubName = firstClubName;
        this.secondClubName = secondClubName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getNews() {
        return news;
    }

    public void setNews(List<String> news) {
        this.news = news;
    }

    public String getFirstClubUrl() {
        return firstClubUrl;
    }

    public void setFirstClubUrl(String firstClubUrl) {
        this.firstClubUrl = firstClubUrl;
    }

    public String getSecondClubUrl() {
        return secondClubUrl;
    }

    public void setSecondClubUrl(String secondClubUrl) {
        this.secondClubUrl = secondClubUrl;
    }

    public String getFirstClubName() {
        return firstClubName;
    }

    public void setFirstClubName(String firstClubName) {
        this.firstClubName = firstClubName;
    }

    public String getSecondClubName() {
        return secondClubName;
    }

    public void setSecondClubName(String secondClubName) {
        this.secondClubName = secondClubName;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
