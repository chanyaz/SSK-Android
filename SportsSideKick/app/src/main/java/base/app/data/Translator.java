package base.app.data;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.GSData;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.HashMap;
import java.util.Map;

import base.app.R;
import base.app.data.chat.ImsMessage;
import base.app.data.wall.PostComment;
import base.app.data.wall.WallBase;
import base.app.data.wall.WallNews;
import base.app.data.wall.WallSocial;
import base.app.util.GSAndroidPlatform;
import base.app.util.commons.XmlLanguageMapParser;

import static base.app.ClubConfig.CLUB_ID;
import static base.app.util.GSConstants.CLUB_ID_TAG;
import static base.app.util.GSConstants.ID_SHORT;
import static base.app.util.GSConstants.POST_ID;
import static base.app.util.GSConstants.TO_LANGUAGE;
import static base.app.util.commons.Utility.CHOSEN_LANGUAGE;

/**
 * Created by Filip on 9/12/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class Translator {

    private static Translator instance;
    private static final String DEFAULT_LANGUAGE = "es";

    private HashMap<String, String> languagesList;

    private String selectedLanguage;
    private String selectedLanguageCode;

    private final ObjectMapper mapper; // jackson's object mapper

    private Translator() {
        mapper = new ObjectMapper();
    }

    public static Translator getInstance() {
        if (instance == null) {
            instance = new Translator();
        }
        return instance;
    }

    public void initialize(Context context) {
        languagesList = XmlLanguageMapParser.parseLanguage(context, R.xml.languages);
        //Set default language
        if (selectedLanguage == null) {
            if (languagesList.containsKey(DEFAULT_LANGUAGE)) {
                selectedLanguage = languagesList.get(DEFAULT_LANGUAGE);
                selectedLanguageCode = DEFAULT_LANGUAGE;
            }
        }
    }

    public void translateNews(String itemId, String language, final TaskCompletionSource<WallNews> completion) {
        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
                .setEventKey("translateNewsItem")
                .setEventAttribute(POST_ID, itemId)
                .setEventAttribute(TO_LANGUAGE, language)
                .setEventAttribute("clubId", CLUB_ID)
                .send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.LogEventResponse response) {
                        if (!response.hasErrors()) {
                            GSData data = response.getScriptData().getObject("item");
                            WallNews item = null;
                            if (data != null) {
                                item = mapper.convertValue(data.getBaseData(), new TypeReference<WallNews>() {
                                });
                            }
                            if (completion != null) {
                                if (item != null) {
                                    completion.setResult(item);
                                } else {
                                    completion.setException(new Exception("Something went wrong with translation of News."));
                                }
                            }
                        } else {
                            Log.e("TAG", "Failed to translate Wall Post!");
                            if (completion != null) {
                                completion.setException(new Exception("Something went wrong with translation of News."));
                            }
                        }

                    }
                });
    }

    public void translateSocial(String itemId, String language, final TaskCompletionSource<WallNews> completion) {
        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
                .setEventKey("translateSocialItem")
                .setEventAttribute(POST_ID, itemId)
                .setEventAttribute(TO_LANGUAGE, language)
                .setEventAttribute("clubId", CLUB_ID)
                .send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.LogEventResponse response) {
                        if (!response.hasErrors()) {
                            GSData data = response.getScriptData().getObject("item");
                            WallNews item = null;
                            if (data != null) {
                                item = mapper.convertValue(data.getBaseData(), new TypeReference<WallSocial>() {
                                });
                            }
                            if (completion != null) {
                                if (item != null) {
                                    item.setTitle(item.getMessage());
                                    completion.setResult(item);
                                } else {
                                    completion.setException(new Exception("Something went wrong with translation of News."));
                                }
                            }
                        } else {
                            Log.e("TAG", "Failed to translate Wall Post!");
                            if (completion != null) {
                                completion.setException(new Exception("Something went wrong with translation of News."));
                            }
                        }

                    }
                });
    }

    public void translatePost(String itemId, String language, final TaskCompletionSource<WallBase> completion, final WallBase.PostType postType) {
        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
                .setEventKey("translateWallPost")
                .setEventAttribute(POST_ID, itemId)
                .setEventAttribute(TO_LANGUAGE, language)
                .send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.LogEventResponse response) {
                        if (!response.hasErrors()) {
                            GSData itemObj = response.getScriptData().getObject("item");
                            if (itemObj == null) return;
                            Map<String, Object> data = itemObj.getBaseData();
                            data.put(CLUB_ID_TAG, postType);
                            WallBase item = WallBase.postFactory(data, mapper, false);
                            completion.setResult(item);
                        }
                    }
                });
    }

    public void translateMessage(String itemId, final TaskCompletionSource<ImsMessage> completion) {
        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
                .setEventKey("translateIM")
                .setEventAttribute("msgId", itemId)
                .setEventAttribute(TO_LANGUAGE, Prefs.getString(CHOSEN_LANGUAGE, "en"))
                .send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.LogEventResponse response) {
                        if (!response.hasErrors()) {
                            GSData messageInfo = response.getScriptData().getObject("item");
                            ImsMessage message = null;
                            if (messageInfo != null) {
                                message = ImsMessage.createMessage();
                                message.updateFrom(messageInfo.getBaseData());
                            }
                            if (completion != null) {
                                if (message != null) {
                                    completion.setResult(message);
                                } else {
                                    completion.setException(new Exception("Something went wrong with translation of the message."));
                                }
                            }
                        } else {
                            Log.e("TAG", "Failed to translate message!");
                            if (completion != null) {
                                completion.setException(new Exception("Something went wrong with translation of the message."));
                            }
                        }

                    }
                });
    }

    /**
     * translate - translate createPost comment
     * itemId, itemType, language
     *
     * @param itemId     - the item to translate
     * @param language   - ISO-639-1 two letter designation for each language
     * @param completion - return translated item or fail
     */
    public void translatePostComment(String itemId, String language, final TaskCompletionSource<PostComment> completion) {
        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
                .setEventKey("translateComment")
                .setEventAttribute(ID_SHORT, itemId)
                .setEventAttribute(TO_LANGUAGE, language)
                .send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.LogEventResponse response) {
                        if (!response.hasErrors()) {
                            GSData postCommentData = response.getScriptData().getObject("item");
                            PostComment postComment = null;
                            if (postCommentData != null) {
                                postComment = mapper.convertValue(postCommentData.getBaseData(), new TypeReference<PostComment>() {
                                });
                            }
                            if (completion != null) {
                                if (postComment != null) {
                                    completion.setResult(postComment);
                                } else {
                                    completion.setException(new Exception("Something went wrong with translation of Comment."));
                                }
                            }
                        } else {
                            Log.e("TAG", "Failed to translate message!");
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
