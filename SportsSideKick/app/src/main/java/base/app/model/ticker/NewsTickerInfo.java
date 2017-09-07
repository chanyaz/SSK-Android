
package base.app.model.ticker;

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
