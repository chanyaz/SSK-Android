
package tv.sportssidekick.sportssidekick.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

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

    public static void initializeTicker(FirebaseDatabase database){
        // Get a reference to our posts
        DatabaseReference tickerReference = database.getReference("ticker");

        // Attach a listener to read the data at our ticker reference
        tickerReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Ticker ticker = dataSnapshot.getValue(Ticker.class);
                EventBus.getDefault().post(ticker);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
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
