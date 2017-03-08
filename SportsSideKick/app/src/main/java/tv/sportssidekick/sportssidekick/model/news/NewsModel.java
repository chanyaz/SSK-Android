package tv.sportssidekick.sportssidekick.model.news;



import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Djordje Krutil on 27.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class NewsModel {

    private static NewsModel instance;

    private NewsItem.NewsType type;
    private int itemsPerPage;
    private static final int DEFAULT_PAGE_LENGTH = 25;
    private int page = 0;
    private String language;
    private String lastPubDate;
    private boolean isLoading;
    private boolean firstPageLoaded;
    private Map<String, NewsItem> newsCache;


    public NewsItem getCachedItemById(String id) {
        return newsCache.get(id);
    }

    public NewsModel() {
    }

    private NewsModel(NewsItem.NewsType type, int pageLength) {
        // TODO Rewrite to GS
//        this.ref = FirebaseDatabase.getInstance();
//        this.newsRef = ref.getReference("news").child("en").child("portugal").child("1680").child(type.toString());
        this.type = type;
        this.itemsPerPage = pageLength;
        this.language = Locale.getDefault().getDisplayLanguage();
        isLoading = false;
        firstPageLoaded = false;
        resetPageCount();
        addObservers();
        newsCache = new HashMap<>();
    }

    public static NewsModel getDefault() {
        if (instance == null) {
            instance = new NewsModel(NewsItem.NewsType.OFFICIAL, DEFAULT_PAGE_LENGTH);
        }
        return instance;
    }

    public NewsModel initialze(NewsItem.NewsType type, int pageLength) {
        this.type = type;
        this.itemsPerPage = pageLength;
        return this;
    }

    private void addObservers() {
        String currentTime = String.valueOf(System.currentTimeMillis() / 1000L) + ".000";
        // TODO Rewrite to GS
//        newsRef.orderByChild("pubDate")
//                .startAt(currentTime)
//                .limitToLast(itemsPerPage + 1)
//                .addChildEventListener(childEventListener);
    }

    // TODO Rewrite to GS
//    ChildEventListener childEventListener = new ChildEventListener() {
//        @Override
//        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//            if (!firstPageLoaded) {
//                return;
//            }
//            ArrayList<NewsItem> newsItems = processSnapshot(dataSnapshot);
//            EventBus.getDefault().post(new NewsPageEvent(newsItems)); //onNewItems
//        }
//
//        @Override
//        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//        }
//
//        @Override
//        public void onChildRemoved(DataSnapshot dataSnapshot) {
//        }
//
//        @Override
//        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//        }
//
//        @Override
//        public void onCancelled(DatabaseError databaseError) {
//        }
//    };

    // We get the data back in ascending order, so it we need to reverse sort for the expected behaviour
    // of newest items first, first though, we grab the pubDate
    // to use as a query constraint
    private ArrayList<NewsItem> processSnapshot(Object data) {
        ArrayList<NewsItem> items = new ArrayList<>();
        boolean isFirst = true;

        // TODO Rewrite to GS
//        for (DataSnapshot child : dataSnapshot.getChildren()) {
//            NewsItem newsItem = child.getValue(NewsItem.class);
//            newsItem.setId(child.getKey());
//            if (isFirst) {
//                isFirst = false;
//                lastPubDate = newsItem.getPubDate();
//            }
//            newsCache.put(newsItem.getId(), newsItem);
//            items.add(newsItem);
//        }
        if (items.size() > 0) {
            page++;
        }
        if (page > 0 && !items.isEmpty()) {
            items.remove(0);
        }
        Collections.reverse(items);
        return items;
    }

    public void resetPageCount() {
        this.lastPubDate = String.valueOf(System.currentTimeMillis() / 1000L);
        this.page = 0;
    }

    // This method auto-increments the 'page' of data we're loading, saves having two methods
    public void loadPage() {
        if (!isLoading) {
            isLoading = true;
            // TODO Rewrite to GS
//            newsRef.orderByChild("pubDate")
//                    .endAt(lastPubDate)
//                    .limitToLast(itemsPerPage + 1)
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            if (!dataSnapshot.exists()) {
//                                return;
//                            }
//                            ArrayList<NewsItem> newsItems = processSnapshot(dataSnapshot);
//                            EventBus.getDefault().post(new NewsPageEvent(newsItems)); // onPageLoaded
//                            isLoading = false;
//                            if (!firstPageLoaded) {
//                                firstPageLoaded = true;
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
        } else {
            return;
        }
    }
}
