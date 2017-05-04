package tv.sportssidekick.sportssidekick.model;

import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;

import java.util.HashMap;
import java.util.Map;

import tv.sportssidekick.sportssidekick.GSAndroidPlatform;


/**
 * Created by Filip Jovanovic on 09/02/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class Analytics {
    private static final String TAG = "Analytics";
    public static final String CATEGORY = "Category";
    private static Analytics instance;

    private Analytics() {
    }

    public static Analytics getInstance() {
        if (instance == null) {
            instance = new Analytics();
        }
        return instance;
    }

    /**
     * @param data Custom data payload
     * @param key The key you want to track this analysis with.
     * @param isEnd Use the value true to indicate it’s an end timer
     * @param isStart Use the value true to indicate it’s an end timer
     */
    public static void trackEvent(Map<String, Object> data, String key, boolean isEnd, boolean isStart) {
        GSAndroidPlatform.gs().getRequestBuilder().createAnalyticsRequest()
                .setData(data)
                .setEnd(isEnd)
                .setKey(key)
                .setStart(isStart)
                .send(new GSEventConsumer<GSResponseBuilder.AnalyticsResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.AnalyticsResponse analyticsResponse) {
                        // Do nothing with analytic response... ?
                    }
                });
    }

    /**
     * @param eventKey unique event key that can be found in @AnalyticsConstants
     */
    public static void trackEventStarted(String eventCategoryKey, String eventKey) {
        Map<String, Object> data = new HashMap<>();
        data.put(CATEGORY,eventCategoryKey);
        trackEvent(data, eventKey, false, true);
    }

    /**
     * @param eventKey unique event key that can be found in @AnalyticsConstants
     */
    public static void trackEventEnded(String eventCategoryKey, String eventKey) {
        Map<String, Object> data = new HashMap<>();
        data.put(CATEGORY,eventCategoryKey);
        trackEvent(null, eventKey, true, false);
    }

    /**
     * @param pageKey Page Key that can be found in @AnalyticsConstants
     */
    public static void trackPageOpened(String pageKey) {
        trackEvent(null, pageKey, false, false);
    }

}