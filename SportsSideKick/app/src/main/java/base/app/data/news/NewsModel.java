package base.app.data.news;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.GSData;
import com.gamesparks.sdk.api.autogen.GSRequestBuilder;
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

import static base.app.ClubConfig.CLUB_ID;
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
                return "official";
            }
        },
        UNOFFICIAL {
            public String toString() {
                return "webhose";
            }
        },
        SOCIAL {
            public String toString() { return "social"; }
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
        if (type.equals(NewsType.OFFICIAL)) {
            isLoadingNews = loading;
        } else {
            isLoadingRumors = loading;
        }
    }

    public static NewsModel getInstance() {
        if (instance == null) {
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
        itemsPerPage = DEFAULT_PAGE_NEWS;
        country = config.get("Country");
        ID = config.get("ID");
        language = config.get("Language");
        pageNews = 0;
        pageRumors = 0;
    }

    public void loadPage(final NewsType type) {
        final int page = 0;

        if (type.equals(NewsType.OFFICIAL)) {
            isLoadingNews = true;
        } else if (type.equals(NewsType.UNOFFICIAL)){
            isLoadingRumors = true;
        }
        String eventKey = "";
        switch (type) {
            case OFFICIAL:
                eventKey = "newsGetPage";
                break;
            case UNOFFICIAL:
                eventKey = "getNewsRumours";
                break;
            case SOCIAL:
                eventKey = "socialGetPage";
                break;
        }
        GSRequestBuilder.LogEventRequest request = GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
                .setEventKey(eventKey)
                .setEventAttribute("language", language)
                .setEventAttribute("country", country)
                .setEventAttribute("type", type.toString())
                .setEventAttribute("limit", itemsPerPage);
        switch (type) {
            case OFFICIAL:
                request.setEventAttribute("id", ID);
                request.setEventAttribute("page", page);
                break;
            case UNOFFICIAL:
                request.setEventAttribute("clubId", CLUB_ID);
                request.setEventAttribute("skip", page);
            break;
            case SOCIAL:
                request.setEventAttribute("id", ID);
                request.setEventAttribute("skip", page);
                break;
        }
        request.send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.LogEventResponse response) {
                        if (!response.hasErrors()) {
                            GSData data = response.getScriptData();

                            if (data == null) {
                                return;
                            }

                            if (data.getBaseData().get("items") == null) {
                                return;
                            }

                            List<WallNews> receivedItems = mapper.convertValue(data.getBaseData().get("items"), new TypeReference<List<WallNews>>() {
                            });
                            if (receivedItems.size() == 0 && !"en".equals(language) && page == 0) {
                                if (type.equals(NewsType.OFFICIAL)) {
                                    isLoadingNews = false;
                                } else {
                                    isLoadingRumors = false;
                                }
                                language = "en";
                                loadPage(type);
                                return;
                            } else {
                                NewsPageEvent newsItemsEvent;
                                if (type.equals(NewsType.OFFICIAL)) {
                                    pageNews++;
                                } else {
                                    pageRumors++;
                                }
                                saveNewsToCache(receivedItems, type);

                                newsItemsEvent = new NewsPageEvent(receivedItems);
                                EventBus.getDefault().post(newsItemsEvent);
                            }
                        }
                    }
                });
    }

    private void saveNewsToCache(List<WallNews> receivedItems, NewsType type) {
        // Memory cache
        newsItems.addAll(receivedItems);

        // Database cache
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Type listOfBecons = new TypeToken<List<WallNews>>() {
        }.getType();

        String strBecons = new Gson().toJson(newsItems, listOfBecons);
        String key = "";
        switch (type) {
            case OFFICIAL:
                key = "NEWS_ITEMS";
                break;
            case UNOFFICIAL:
                key = "RUMOUR_ITEMS";
                break;
            case SOCIAL:
                key = "SOCIAL_ITEMS";
                break;
        }
        preferences.edit().putString(key, strBecons).apply();
    }

    private List<WallNews> loadNewsFromCache(NewsType type) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        Type listOfBecons = new TypeToken<List<WallNews>>() {
        }.getType();

        String key = "";
        switch (type) {
            case OFFICIAL:
                key = "NEWS_ITEMS";
                break;
            case UNOFFICIAL:
                key = "RUMOUR_ITEMS";
                break;
            case SOCIAL:
                key = "SOCIAL_ITEMS";
                break;
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
        for (WallNews item : loadNewsFromCache(type)) {
            if (item.getPostId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    public List<WallNews> getAllCachedItems(NewsType type) {
        return loadNewsFromCache(type);
    }
}
