
package tv.sportssidekick.sportssidekick.model;

import java.util.List;

public class Ticker {

    private String firstClubUrl;
    private String matchDate;
    private List<String> news = null;
    private String secondClubUrl;
    private String title;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Ticker() {
    }

    /**
     * 
     * @param firstClubUrl
     * @param title
     * @param matchDate
     * @param news
     * @param secondClubUrl
     */
    public Ticker(String firstClubUrl, String matchDate, List<String> news, String secondClubUrl, String title) {
        super();
        this.firstClubUrl = firstClubUrl;
        this.matchDate = matchDate;
        this.news = news;
        this.secondClubUrl = secondClubUrl;
        this.title = title;
    }

    /**
     * 
     * @return
     *     The firstClubUrl
     */
    public String getFirstClubUrl() {
        return firstClubUrl;
    }

    /**
     * 
     * @param firstClubUrl
     *     The firstClubUrl
     */
    public void setFirstClubUrl(String firstClubUrl) {
        this.firstClubUrl = firstClubUrl;
    }

    /**
     * 
     * @return
     *     The matchDate
     */
    public String getMatchDate() {
        return matchDate;
    }

    /**
     * 
     * @param matchDate
     *     The matchDate
     */
    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    /**
     * 
     * @return
     *     The news
     */
    public List<String> getNews() {
        return news;
    }

    /**
     * 
     * @param news
     *     The news
     */
    public void setNews(List<String> news) {
        this.news = news;
    }

    /**
     * 
     * @return
     *     The secondClubUrl
     */
    public String getSecondClubUrl() {
        return secondClubUrl;
    }

    /**
     * 
     * @param secondClubUrl
     *     The secondClubUrl
     */
    public void setSecondClubUrl(String secondClubUrl) {
        this.secondClubUrl = secondClubUrl;
    }

    /**
     * 
     * @return
     *     The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

}
