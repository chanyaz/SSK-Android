package base.app.data.content;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.GSData;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.Map;

import base.app.data.TypeConverter;
import base.app.data.TypeConverter.ItemType;
import base.app.data.chat.ChatMessage;
import base.app.data.content.wall.Comment;
import base.app.data.content.wall.News;
import base.app.data.content.wall.Post;
import base.app.util.commons.GSAndroidPlatform;

import static base.app.ClubConfig.CLUB_ID;
import static base.app.util.commons.GSConstants.CLUB_ID_TAG;
import static base.app.util.commons.GSConstants.ID_SHORT;
import static base.app.util.commons.GSConstants.POST_ID;
import static base.app.util.commons.GSConstants.TO_LANGUAGE;

/**
 * Created by Filip on 9/12/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class Translator {

    private static Translator instance;

    private final ObjectMapper mapper;

    private Translator() {
        mapper = new ObjectMapper();
    }

    public static Translator getInstance() {
        if (instance == null) {
            instance = new Translator();
        }
        return instance;
    }

    public void translateNews(String itemId, String language, final TaskCompletionSource<News> completion) {
        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
                .setEventKey("translateNewsItem")
                .setEventAttribute(POST_ID, itemId)
                .setEventAttribute(TO_LANGUAGE, language)
                .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
                .send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.LogEventResponse response) {
                        if (!response.hasErrors()) {
                            GSData data = response.getScriptData().getObject("item");
                            News item = null;
                            if (data != null) {
                                item = mapper.convertValue(data.getBaseData(), new TypeReference<News>() {
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

    public void translatePost(String itemId, String language, final TaskCompletionSource<Post> completion) {
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
                            data.put(CLUB_ID_TAG, ItemType.Post);
                            Post item = TypeConverter.postFactory(data, mapper, false);
                            completion.setResult(item);
                        }
                    }
                });
    }

    public void translateMessage(String itemId, String language, final TaskCompletionSource<ChatMessage> completion) {
        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
                .setEventKey("translateComment")
                .setEventAttribute(ID_SHORT, itemId)
                .setEventAttribute(TO_LANGUAGE, language)
                .send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.LogEventResponse response) {
                        if (!response.hasErrors()) {
                            GSData messageInfo = response.getScriptData().getObject("item");
                            ChatMessage message = null;
                            if (messageInfo != null) {
                                message = ChatMessage.getDefaultMessage();
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
     * translate post comment
     */
    public void translatePostComment(String itemId, String language, final TaskCompletionSource<Comment> completion) {
        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
                .setEventKey("translateComment")
                .setEventAttribute(ID_SHORT, itemId)
                .setEventAttribute(TO_LANGUAGE, language)
                .send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.LogEventResponse response) {
                        if (!response.hasErrors()) {
                            GSData postCommentData = response.getScriptData().getObject("item");
                            Comment comment = null;
                            if (postCommentData != null) {
                                comment = mapper.convertValue(postCommentData.getBaseData(), new TypeReference<Comment>() {
                                });
                            }
                            if (completion != null) {
                                if (comment != null) {
                                    completion.setResult(comment);
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
}
