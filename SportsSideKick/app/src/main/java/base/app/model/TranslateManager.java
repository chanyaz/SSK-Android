package base.app.model;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.GSData;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.HashMap;
import java.util.Map;

import base.app.GSAndroidPlatform;
import base.app.R;
import base.app.model.im.ImsMessage;
import base.app.model.wall.PostComment;
import base.app.model.wall.WallBase;
import base.app.model.wall.WallNews;
import base.app.util.XmlLanguageMapParser;

import static base.app.ClubConfig.CLUB_ID;
import static base.app.model.GSConstants.CLUB_ID_TAG;
import static base.app.model.GSConstants.ITEM_ID;
import static base.app.model.GSConstants.ITEM_TYPE;
import static base.app.model.GSConstants.LANGUAGE;
import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by Filip on 9/12/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class TranslateManager {



    private static TranslateManager instance;
    private static final String DEFAULT_LANGUAGE = "es";
    private static final String IMS = "IMs", WALL_POST = "WallPost",  NEWS = "News", WALL_COMMENT = "WallComment";

    private HashMap<String, String> languagesList;

    private String selectedLanguage;
    private String selectedLanguageCode;

    private final ObjectMapper mapper; // jackson's object mapper

    private TranslateManager(){
            mapper = new ObjectMapper();
    }

    public static TranslateManager getInstance(){
        if(instance==null){
            instance = new TranslateManager();
        }
        return instance;
    }

    public void initialize(Context context){
        languagesList = XmlLanguageMapParser.parseLanguage(context, R.xml.languages);
        //Set default language
        if(selectedLanguage==null){
            if(languagesList.containsKey(DEFAULT_LANGUAGE)){
                selectedLanguage = languagesList.get(DEFAULT_LANGUAGE);
                selectedLanguageCode = DEFAULT_LANGUAGE;
            }
        }
    }

    /**
     * translate - translate news
     *itemId, itemType, language
     * @param  itemId - the item to translate
     * @param  language - ISO-639-1 two letter designation for each language
     * @param  completion - return translated item or fail
     */
    public void translateNews(String itemId, String language, final TaskCompletionSource<WallNews> completion){
        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
                .setEventKey("translateContent")
                .setEventAttribute(ITEM_ID, itemId)
                .setEventAttribute(ITEM_TYPE, NEWS)
                .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
                .setEventAttribute(LANGUAGE, language)
                .send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.LogEventResponse response) {
                        if(!response.hasErrors()){
                            GSData data = response.getScriptData().getObject("item");
                            WallNews item = null;
                            if(data!=null){
                                item = mapper.convertValue(data.getBaseData(), new TypeReference<WallNews>(){});
                            }
                            if(completion!=null){
                                if(item!=null) {
                                    completion.setResult(item);
                                } else {
                                    completion.setException(new Exception("Something went wrong with translation of News."));
                                }
                            }
                        } else {
                            Log.e(TAG,"Failed to translate Wall Post!");
                            if(completion!=null){
                                completion.setException(new Exception("Something went wrong with translation of News."));
                            }
                        }

                    }
                });
    }

    /**
     * translate - translate post
     *itemId, itemType, language
     * @param  itemId - the item to translate
     * @param  language - ISO-639-1 two letter designation for each language
     * @param  completion - return translated item or fail
     */
    public void translatePost(String itemId, String language, final TaskCompletionSource<WallBase> completion, final WallBase.PostType postType){
        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
                .setEventKey("translateContent")
                .setEventAttribute(ITEM_ID, itemId)
                .setEventAttribute(ITEM_TYPE, WALL_POST)
                .setEventAttribute(LANGUAGE, language)
                .setEventAttribute(CLUB_ID_TAG, "")
                .send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.LogEventResponse response) {
                        if(!response.hasErrors()){
                            Map<String,Object> data = response.getScriptData().getObject("item").getBaseData();
                            data.put(CLUB_ID_TAG,postType);
                            WallBase item = null;
                            if(data!=null){
                                item = WallBase.postFactory(data,mapper, false);
                            }
                            if(completion!=null){
                                if(item!=null) {
                                    completion.setResult(item);
                                } else {
                                    completion.setException(new Exception("Something went wrong with translation of Wall Post."));
                                }
                            }
                        } else {
                            Log.e(TAG,"Failed to translate Wall Post!");
                            if(completion!=null){
                                completion.setException(new Exception("Something went wrong with translation of Wall Post."));
                            }
                        }

                    }
                });
    }

    /**
     * translate - translate IM message
     * @param  itemId - the item to translate
     * @param  language - ISO-639-1 two letter designation for each language
     * @param  completion - return translated item or fail
     */
    public void translateMessage(String itemId, String language , final TaskCompletionSource<ImsMessage> completion){
        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
                .setEventKey("translateContent")
                .setEventAttribute(ITEM_ID, itemId)
                .setEventAttribute(ITEM_TYPE, IMS)
                .setEventAttribute(ITEM_TYPE, IMS)
                .setEventAttribute(CLUB_ID_TAG, "")
                .setEventAttribute(LANGUAGE, language)
                .send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.LogEventResponse response) {
                        if(!response.hasErrors()){
                            GSData messageInfo = response.getScriptData().getObject("item");
                            ImsMessage message = null;
                            if(messageInfo!=null){
                                message = ImsMessage.getDefaultMessage();
                                message.updateFrom(messageInfo.getBaseData());
                            }
                            if(completion!=null){
                                if(message!=null){
                                    completion.setResult(message);
                                } else {
                                    completion.setException(new Exception("Something went wrong with translation of the message."));
                                }
                            }
                        } else {
                            Log.e(TAG,"Failed to translate message!");
                            if(completion!=null){
                                completion.setException(new Exception("Something went wrong with translation of the message."));
                            }
                        }

                    }
                });
    }

    /**
     * translate - translate post comment
     * itemId, itemType, language
     * @param  itemId - the item to translate
     * @param  language - ISO-639-1 two letter designation for each language
     * @param  completion - return translated item or fail
     */
    public void translatePostComment(String itemId, String language , final TaskCompletionSource<PostComment> completion){
        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
                .setEventKey("translateContent")
                .setEventAttribute(ITEM_ID, itemId)
                .setEventAttribute(ITEM_TYPE, WALL_COMMENT)
                .setEventAttribute(LANGUAGE, language)
                .setEventAttribute(CLUB_ID_TAG, "")
                .send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.LogEventResponse response) {
                        if (!response.hasErrors()) {
                            GSData postCommentData = response.getScriptData().getObject("item");
                            PostComment postComment = null;
                            if (postCommentData != null) {
                                postComment = mapper.convertValue(postCommentData.getBaseData(), new TypeReference<PostComment>(){});
                            }
                            if (completion != null) {
                                if (postComment != null) {
                                    completion.setResult(postComment);
                                } else {
                                    completion.setException(new Exception("Something went wrong with translation of Comment."));
                                }
                            }
                        } else {
                            Log.e(TAG, "Failed to translate message!");
                            if (completion != null) {
                                completion.setException(new Exception("Something went wrong with translation of Comment."));
                            }
                        }

                    }
                });

    }

    public HashMap<String, String> getLanguageList() {
        return languagesList;
    }
}
