package base.app.data.news;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.GSData;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import base.app.data.GSAndroidPlatform;
import base.app.data.wall.WallNews;
import base.app.util.commons.Utility;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by Djordje Krutil on 27.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class NewsModel {

    public enum NewsType {

        OFFICIAL {
            public String toString() {
                return "newsOfficial";
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

    private List<WallNews> newsItems;
    private List<WallNews> rumorsItems;

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

    private NewsModel() {
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

                    List<WallNews> receivedItems = mapper.convertValue(data.getBaseData().get("items"), new TypeReference<List<WallNews>>(){});
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
                            saveNewsToCache(receivedItems, false);
                            pageNews++;
                        } else {
                            saveNewsToCache(receivedItems, true);
                            pageRumors++;
                        }
                        newsItemsEvent = new NewsPageEvent(receivedItems);
                        EventBus.getDefault().post(newsItemsEvent);
                    }
                }
            }
        });
    }

    private void saveNewsToCache(List<WallNews> receivedItems, boolean isRumours) {
        // Memory cache
        newsItems.addAll(receivedItems);

        // Database cache
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Type listOfBecons = new TypeToken<List<WallNews>>() {}.getType();

        String strBecons = new Gson().toJson(newsItems, listOfBecons);
        String key;
        if (isRumours) {
            key = "RUMOUR_ITEMS";
        } else {
            key = "NEWS_ITEMS";
        }
        preferences.edit().putString(key, strBecons).apply();
    }

    private List<WallNews> loadNewsFromCache(boolean isRumours) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        Type listOfBecons = new TypeToken<List<WallNews>>() {}.getType();

        String key;
        if (isRumours) {
            key = "RUMOUR_ITEMS";
        } else {
            key = "NEWS_ITEMS";
        }

        String savedString = preferences.getString(key, "");
        if (!savedString.isEmpty()) {
            return new Gson().fromJson(
                    savedString, listOfBecons);
        } else {
            return new ArrayList<>();
        }
    }

    public WallNews loadItemFromCache(String id, NewsType type) {
        if(type == NewsType.OFFICIAL){
            for(WallNews item : loadNewsFromCache(false)){
                if(item.getPostId().equals(id)){
                    return item;
                }
            }
        } else {
            for(WallNews item : loadNewsFromCache(true)){
                if(item.getPostId().equals(id)){
                    return item;
                }
            }
        }
        return null;
    }

    public List<WallNews> getAllCachedItems(NewsType type) {
        return type == NewsType.OFFICIAL ? loadNewsFromCache(false) : loadNewsFromCache(true);
    }
}
