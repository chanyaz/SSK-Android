package base.app.data.news;

import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.GSData;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.json.simple.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import base.app.data.GSAndroidPlatform;
import base.app.data.GSConstants;
import base.app.data.wall.News;
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

    private int itemsPerPage = DEFAULT_PAGE_NEWS;
    private static final int DEFAULT_PAGE_LENGTH = 20;
    private static final int DEFAULT_PAGE_NEWS = 30;
    private static final int DEFAULT_PAGE_RUMORS = 20;
    private int pageRumors = 0;
    private int pageNews = 0;

    private List<News> newsItems;
    private List<News> rumorsItems;

    private String language;
    private String country;

    private boolean isLoadingNews;
    private boolean isLoadingRumors;
    private ObjectMapper mapper;
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
        country = config.get("Country");
        language = config.get("Language");
        pageNews = 0;
        pageRumors = 0;
    }

    public void loadPage(final NewsType type) {
        loadPage(type, null);
    }

    public void loadPage(final NewsType type, @Nullable final MutableLiveData<List<News>> liveData) {
        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
                .setEventKey("newsGetPage")
                .setEventAttribute("language", language)
                .setEventAttribute("country", country)
                .setEventAttribute("type", type.toString())
                .setEventAttribute("page", 0)
                .setEventAttribute("limit", itemsPerPage)
                .send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.LogEventResponse response) {
                        if (!response.hasErrors()) {
                            GSData data = response.getScriptData();

                            if (data == null
                                    || data.getBaseData().get(GSConstants.ITEMS) == null) {
                                return;
                            }
                            JSONArray jsonArrayOfNews = (JSONArray)
                                    response.getScriptData().getBaseData().get(GSConstants.ITEMS);
                            List<News> receivedItems = new ArrayList<>();
                            for (Object newsObject : jsonArrayOfNews) {
                                News item = mapper.convertValue(newsObject, new TypeReference<News>(){});
                                JsonNode node = mapper.valueToTree(newsObject);
                                item.id = node.get("_id").asText();
                                receivedItems.add(item);
                            }
                            if (liveData != null) {
                                liveData.postValue(receivedItems);
                            }
                            if (type.equals(NewsType.OFFICIAL)) {
                                saveNewsToCache(receivedItems, false);
                                pageNews++;
                            } else {
                                saveNewsToCache(receivedItems, true);
                                pageRumors++;
                            }
                            NewsPageEvent newsItemsEvent = new NewsPageEvent(receivedItems);
                            EventBus.getDefault().post(newsItemsEvent);
                        }
                    }
                });
    }

    private void saveNewsToCache(List<News> receivedItems, boolean isRumours) {
        // Memory cache
        newsItems.addAll(receivedItems);

        // Database cache
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Type listOfBecons = new TypeToken<List<News>>() {
        }.getType();

        String strBecons = new Gson().toJson(newsItems, listOfBecons);
        String key;
        if (isRumours) {
            key = "RUMOUR_ITEMS";
        } else {
            key = "NEWS_ITEMS";
        }
        preferences.edit().putString(key, strBecons).apply();
    }

    private List<News> loadNewsFromCache(boolean isRumours) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        Type listOfBecons = new TypeToken<List<News>>() {
        }.getType();

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

    public News loadItemFromCache(String id, NewsType type) {
        if (type == NewsType.OFFICIAL) {
            for (News item : loadNewsFromCache(false)) {
                if (item.getId().equals(id)) {
                    return item;
                }
            }
        } else {
            for (News item : loadNewsFromCache(true)) {
                if (item.getId().equals(id)) {
                    return item;
                }
            }
        }
        return null;
    }

    public List<News> getAllCachedItems(NewsType type) {
        return type == NewsType.OFFICIAL ? loadNewsFromCache(false) : loadNewsFromCache(true);
    }
}
