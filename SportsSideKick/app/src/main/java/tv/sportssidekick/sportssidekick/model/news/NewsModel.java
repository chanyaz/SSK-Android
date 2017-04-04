package tv.sportssidekick.sportssidekick.model.news;



import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.GSData;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tv.sportssidekick.sportssidekick.service.GSAndroidPlatform;
import tv.sportssidekick.sportssidekick.util.Utility;


/**
 * Created by Djordje Krutil on 27.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class NewsModel {

    public enum NewsType {

        OFFICIAL {
            public String toString() {
                return "official";
            }
        },
        UNOFFICIAL {
            public String toString() {
                return "webhose";
            }
        };

        NewsType() {
        }
    }

    private static NewsModel instance;

    private int itemsPerPage;
    private static final int DEFAULT_PAGE_LENGTH = 20;
    private static final int DEFAULT_PAGE_NEWS = 20;
    private static final int DEFAULT_PAGE_RUMORS = 20;
    private int pageRumors = 0;
    private int pageNews = 0;

    List<NewsItem> newsItems;
    List<NewsItem> rumorsItems;

    private String language;
    private String country;
    private String ID;

    private boolean isLoadingNews;
    private boolean isLoadingRumors;
    private ObjectMapper mapper; // jackson's object mapper
    private HashMap<String, String> config;

    public void setLoading(boolean loading, NewsType type) {
        if(type.equals(NewsType.OFFICIAL)){
            isLoadingNews = loading;
        } else {
            isLoadingRumors = loading;
        }
    }


    public static NewsModel getInstance(){
        if(instance==null){
            instance = new NewsModel();
        }
        return instance;
    }

    public NewsModel() {
        newsItems = new ArrayList<>();
        rumorsItems = new ArrayList<>();
        mapper = new ObjectMapper();
        config = new HashMap<>();
        config = Utility.getClubConfig();
        config = new HashMap<>();
        config = Utility.getClubConfig();
        this.itemsPerPage = DEFAULT_PAGE_NEWS;
        this.country = config.get("Country");
        this.ID = config.get("ID");
        this.language = config.get("Language");
        this.pageNews = 0;
        this.pageRumors = 0;
    }

    public void loadPage(final NewsType type) {

        final int page = type == NewsType.OFFICIAL ? pageNews : pageRumors;
        if (this.isLoadingNews && type.equals(NewsType.OFFICIAL) || this.isLoadingRumors && type.equals(NewsType.UNOFFICIAL)){
            return;
        }
        if(type.equals(NewsType.OFFICIAL)){
            isLoadingNews = true;
        } else {
            isLoadingRumors = true;
        }

        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
                .setEventKey("newsGetPage")
                .setEventAttribute("language", language)
                .setEventAttribute("country", country)
                .setEventAttribute("id", ID)
                .setEventAttribute("type", type.toString())
                .setEventAttribute("page", page )
                .setEventAttribute("limit", itemsPerPage)
                .send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    GSData data = response.getScriptData();

                    if (data == null)
                    {
                        return;
                    }

                    if (data.getBaseData().get("items") == null)
                    {
                        return;
                    }

                    List<NewsItem> receivedItems = mapper.convertValue(data.getBaseData().get("items"), new TypeReference<List<NewsItem>>(){});
                    if (receivedItems.size() == 0 && !"en".equals(language) && page == 0)
                    {
                        if(type.equals(NewsType.OFFICIAL)){
                            isLoadingNews = false;
                        } else {
                            isLoadingRumors = false;
                        }
                        language = "en";
                        loadPage(type);
                        return;
                    }
                    else {
                        NewsPageEvent newsItemsEvent;
                        if(type.equals(NewsType.OFFICIAL)){
                            newsItems.addAll(receivedItems);
                            newsItemsEvent = new NewsPageEvent(newsItems);
                            pageNews++;
                        } else {
                            rumorsItems.addAll(receivedItems);
                            newsItemsEvent = new NewsPageEvent(rumorsItems);
                            pageRumors++;
                        }
                        EventBus.getDefault().post(newsItemsEvent);
                    }
                }
            }
        });
    }

    public NewsItem getCachedItemById(String id, NewsType type) {
        if(type == NewsType.OFFICIAL){
            for(NewsItem item : newsItems){
                if(item.getId().getOid().equals(id)){
                    return item;
                }
            }
        } else {
            for(NewsItem item : rumorsItems){
                if(item.getId().getOid().equals(id)){
                    return item;
                }
            }
        }
        return null;
    }

    public List<NewsItem> getAllCachedItems(NewsType type) {
        return type == NewsType.OFFICIAL ? newsItems : rumorsItems;
    }
}
