package tv.sportssidekick.sportssidekick.model.ticker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.GSData;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;

import org.greenrobot.eventbus.EventBus;

import tv.sportssidekick.sportssidekick.service.GSAndroidPlatform;


/**
 * Created by Djordje Krutil on 9.3.2017..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class NextMatchModel {

    private static NextMatchModel instance;
    private String language;
    public static String DEFAULT_LENGUAGE = "en";
    private final ObjectMapper mapper; // jackson's object mapper
    NewsTickerInfo newsTickerInfo;

    public NewsTickerInfo getTickerInfo() {
        return newsTickerInfo;
    }

    private void setTickerInfo(NewsTickerInfo newsTickerInfo) {
        this.newsTickerInfo = newsTickerInfo;
        EventBus.getDefault().post(newsTickerInfo);
    }


    public static NextMatchModel getInstance(){
        if(instance==null){
            instance = new NextMatchModel();
        }
        return instance;
    }


    public NextMatchModel ()  {
        mapper =  new ObjectMapper();
    }

    public void getNextMatchInfo()
    {
        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
                .setEventKey("tickerGetNextMatch")
                .setEventAttribute("language", language)
                .send(onNextMatchLoaded);
    }

    GSEventConsumer<GSResponseBuilder.LogEventResponse> onNextMatchLoaded = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
        @Override
        public void onEvent(GSResponseBuilder.LogEventResponse response) {
            if (!response.hasErrors()) {
                GSData data = response.getScriptData();

                if (data == null)
                {
                    fallback();
                    return;
                }

                if (data.getObject("match") == null)
                {
                    fallback();
                    return;
                }
                GSData matchData = data.getObject("match");
                setTickerInfo(mapper.convertValue(matchData.getBaseData(),NewsTickerInfo.class));
            }
            else {
                fallback();
            }
        }
    };

    private void fallback()
    {
        if (DEFAULT_LENGUAGE.compareTo(this.language)==0)
        {
            return;
        }
        changeLanguage(DEFAULT_LENGUAGE);
        getNextMatchInfo();
    }

    private void changeLanguage (String language)
    {
        this.language = language;
    }
}
