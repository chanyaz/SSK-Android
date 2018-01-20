package base.app.data.content.nextmatch;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.GSData;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;
import com.pixplicity.easyprefs.library.Prefs;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import base.app.data.GSAndroidPlatform;
import base.app.util.commons.Utility;

import static base.app.ClubConfig.CLUB_ID;
import static base.app.data.GSConstants.CLUB_ID_TAG;


/**
 * Created by Djordje Krutil on 9.3.2017..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class NextMatchModel {

    private static NextMatchModel instance;
    private String language;
    private static final String DEFAULT_LANGUAGE = "en";
    private final ObjectMapper mapper;
    private NewsTickerInfo newsTickerInfo;

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

    private NextMatchModel ()  {
        mapper =  new ObjectMapper();
    }

    public void getNextMatchInfo() {
        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
                .setEventKey("tickerGetNextMatch")
                .setEventAttribute("language", language)
                .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
                 .send(onNextMatchLoaded);
    }

    private GSEventConsumer<GSResponseBuilder.LogEventResponse> onNextMatchLoaded = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
        @Override
        public void onEvent(GSResponseBuilder.LogEventResponse response) {
            if (!response.hasErrors()) {
                GSData data = response.getScriptData();
                if (data == null) {
                    fallback();
                    return;
                }
                if (data.getObject("match") == null) {
                    fallback();
                    return;
                }
                GSData matchData = data.getObject("match");
                saveTickerInfoToCache(matchData.getBaseData().toString());
                NewsTickerInfo info = mapper.convertValue(matchData.getBaseData(),NewsTickerInfo.class);
                setTickerInfo(info);
            }
            else {
                fallback();
            }
        }
    };

    public boolean isNextMatchUpcoming(){
        if(newsTickerInfo!=null){
            if(newsTickerInfo.getMatchDate()!=null){
                long timestamp = Long.parseLong(newsTickerInfo.getMatchDate());
                long now = (Utility.getCurrentTime() /1000);
                long secondsFromMatchStart = now - timestamp;
                if(now<timestamp){ // match start is in future
                    return true;
                } else if(secondsFromMatchStart<=60*110){ // match started in less than 110 minutes
                    return true;
                }
            }
        }
        return false;
    }

    private void fallback(){
        if(DEFAULT_LANGUAGE.equals(this.language)){
            return;
        }
        changeLanguage(DEFAULT_LANGUAGE);
        getNextMatchInfo();
    }

    private void changeLanguage (String language) {
        this.language = language;
    }

    public void saveTickerInfoToCache(String info){
        Prefs.putString("TICKER_INFO",info);
    }

   @Nullable
   public NewsTickerInfo loadTickerInfoFromCache(){
        String infoAsString = Prefs.getString("TICKER_INFO",null);
        if(newsTickerInfo==null && infoAsString!=null){
            try {
                newsTickerInfo =  mapper.readValue(infoAsString,NewsTickerInfo.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return newsTickerInfo;
    }
}
