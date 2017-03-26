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

import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.service.GSAndroidPlatform;
import tv.sportssidekick.sportssidekick.util.Utility;


/**
 * Created by Djordje Krutil on 27.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class NewsModel {

    private static NewsModel instance;

    private NewsItem.NewsType type;

    private int itemsPerPage;
    private static final int DEFAULT_PAGE_LENGTH = 20;
    private int page = 0;

    HashMap<String, NewsItem> cachedItems;
    List<NewsItem> items;

    private String language;
    private String country;
    private String ID;

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    private boolean isLoading;
    private ObjectMapper mapper; // jackson's object mapper
    private HashMap<String, String> config;

    public static NewsModel getInstance(){
        if(instance==null){
            instance = new NewsModel();
        }
        return instance;
    }

    public NewsModel() {
        items = new ArrayList<>();
        cachedItems = new HashMap<>();
        mapper = new ObjectMapper();
        config = new HashMap<>();
        config = Utility.getClubConfig();
    }

    public NewsModel(NewsItem.NewsType type, int pageLength ) {

        config = new HashMap<>();
        config = Utility.getClubConfig();
        pageLength = 15;
        this.type = type;
        this.itemsPerPage = pageLength;
        this.country = config.get("Country");
        this.ID = config.get("ID");
        this.language = config.get("Language");

        resetPageCount();
    }

    private void reload (String language)
    {
        if (language != null)
        {
            this.language = language;
        }

        resetPageCount();
        loadPage();
    }

    public void resetPageCount() {
        this.page = 0;
    }

    public void loadPage() {

        if (this.isLoading == true)
        {
            return;
        }

        this.isLoading = true;

        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
                .setEventKey("newsGetPage")
                .setEventAttribute("language", "en")
                .setEventAttribute("country", "portugal")
                .setEventAttribute("id", 1680) //TODO id?
                .setEventAttribute("type", NewsItem.NewsType.OFFICIAL.toString())
                .setEventAttribute("page", 0)
                .setEventAttribute("limit", 20)
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

//                    GSData itemsData = data.getObject("items");

                    items = mapper.convertValue(data.getBaseData().get("items"), new TypeReference<List<NewsItem>>(){});

                    if (items.size() == 0 /*&& "en".compareTo(language) !=0 */ && page == 0)
                    {
                        isLoading = false;
                        language = "en";

                        loadPage();

                        return;
                    }
                    else {
                        for (int i =0; i <items.size(); i++)
                        {
                            cachedItems.put(items.get(i).getId(), items.get(i));
                        }
                        NewsPageEvent newsItemsEvent = new NewsPageEvent(items);
                        EventBus.getDefault().post(newsItemsEvent);
                    }
                }
            }
        });
    }

    public NewsItem getCachedItemById(String id) {
        return cachedItems.get(id);
    }

    public List<NewsItem> getCachedItems() {
        return items;
    }
}
