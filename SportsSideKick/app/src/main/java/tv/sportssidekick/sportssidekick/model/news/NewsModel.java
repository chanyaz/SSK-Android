package tv.sportssidekick.sportssidekick.model.news;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Djordje Krutil on 27.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class NewsModel {

    private static NewsModel instance;

    NewsItem.NewsType type;
    FirebaseDatabase ref;
    int itemsPerPage;
    public static int DEFAULT_PAGE_LENGTH = 25;
    int page = 0;
    String language;
    String ID;
    DatabaseReference newsRef;
    String lastPubDate;
    boolean isLoading;
    boolean firstPageLoaded;
private Map<String, NewsItem> newsCache;


    public NewsItem getCachedItemById(String id){
        return newsCache.get(id);
    }
    public NewsModel() {}

    private NewsModel(NewsItem.NewsType type, int pageLength)
    {
        this.ref = FirebaseDatabase.getInstance();
        this.newsRef = ref.getReference("news").child("en").child("portugal").child("1680").child(type.toString()); // TODO Adjust for multilanguage support
        this.type = type;
        this.itemsPerPage = pageLength;
        this.language = Locale.getDefault().getDisplayLanguage();
        resetPageCount();
        isLoading = false;
        firstPageLoaded = false;
        resetPageCount();
        addObservers();
        newsCache = new HashMap<>();
    }

    public static NewsModel getDefault(){
        if(instance==null){
            instance = new NewsModel(NewsItem.NewsType.OFFICIAL, DEFAULT_PAGE_LENGTH);
        }
        return instance;
    }

    public NewsModel initialze(NewsItem.NewsType type, int pageLength){
        this.type = type;
        this.itemsPerPage = pageLength;
        return this;
    }

    private void addObservers(){
        newsRef.orderByChild("pubDate")
                .startAt(String.valueOf(System.currentTimeMillis())) // TODO Change to custom format
                .addChildEventListener(childEventListener);
    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if (!firstPageLoaded) {
                return;
            }
            List<NewsItem> newsItems = processSnapshot(dataSnapshot);
            EventBus.getDefault().post(newsItems); //onNewItems
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) { }
        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
        @Override
        public void onCancelled(DatabaseError databaseError) { }
    };

    // We get the data back in ascending order, so it we need to reverse sort for the expected behaviour
    // of newest items first, first though, we grab the pubDate
    // to use as a query constraint
    private List<NewsItem> processSnapshot( DataSnapshot dataSnapshot)  {
        List<NewsItem> items = new ArrayList<>();
        boolean isFirst = true;

        for(DataSnapshot child : dataSnapshot.getChildren()){
            NewsItem newsItem = child.getValue(NewsItem.class);
            newsItem.setId(child.getKey());
            if(newsItem!=null) {
                if (isFirst == true) {
                    isFirst = false;
                    lastPubDate = newsItem.getPubDate();
                }
                newsCache.put(newsItem.getId(),newsItem);
                items.add(newsItem);
            }
        }
        if (items.size() > 0) {
            page++;
        }
        if (page > 0 && !items.isEmpty()) {
            items.remove(0);
        }
        Collections.reverse(items);
        return items;
    }

    public void resetPageCount()
    {
        this.lastPubDate ="";
        this.page = 0;
    }

    // This method auto-increments the 'page' of data we're loading, saves having two methods
    public void loadPage()
    {
        if (!isLoading)
        {
            isLoading =true;
            List<NewsItem> items = new ArrayList<>();
            newsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        return;
                    }
                    List<NewsItem> newsItems = processSnapshot(dataSnapshot);
                    EventBus.getDefault().post(newsItems); // onPageLoaded
                    isLoading = false;
                    if(!firstPageLoaded) {
                        firstPageLoaded = true;
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            return;
        }
    }
}
