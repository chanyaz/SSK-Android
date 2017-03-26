package tv.sportssidekick.sportssidekick.model.news;



import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.GSData;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;

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

    private String language;
    private String country;
    private String ID;
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
                .setEventAttribute("country", "uk")
                .setEventAttribute("id", Model.getInstance().getUserInfo().getUserId())
                .setEventAttribute("type", "official")
                .setEventAttribute("page", 0)
                .setEventAttribute("limit", 15)
                .send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    GSData data = response.getScriptData();

                    if (data == null)
                    {
                        return;
                    }

                    if (data.getObject("items") == null)
                    {
                        return;
                    }

                    GSData itemsData = data.getObject("items");

                    List<NewsItem> items = mapper.convertValue(itemsData, new TypeReference<List<NewsItem>>(){});

                    if (items.size() == 0 && "en".compareTo(language) !=0 && page == 0)
                    {
                        isLoading = false;
                        language = "en";

                        loadPage();

                        return;
                    }
                }
            }
        });
    }

}
