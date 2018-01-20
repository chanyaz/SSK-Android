package base.app.data.chat;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.GSData;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;
import com.google.android.gms.tasks.TaskCompletionSource;

import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

import base.app.util.commons.GSAndroidPlatform;
import base.app.util.commons.DateUtils;
import base.app.util.commons.Model;

import static base.app.util.commons.GSConstants.CLUB_ID_TAG;
import static base.app.util.commons.GSConstants.GROUP_ID;
import static base.app.util.commons.GSConstants.MESSAGE;
import static base.app.util.commons.GSConstants.MESSAGE_ID;

/**
 * Created by Filip on 12/7/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

@JsonIgnoreProperties(ignoreUnknown = true,value={"imageAspectRatio","timestampEpoch","timeAgo"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessage {

    private static final String TAG = "IMS MESSAGE";

    private final ObjectMapper mapper;

    private static final float ASPECT_RATIO_DEFAULT = 0.5625f;

    @JsonProperty("_id")
    private String id;
    private String locid;
    private String text;
    private String senderId;
    private float imageAspectRatio;
    private String timestamp;
    private String imageUrl;
    private String vidUrl;
    private List<String> wasReadBy;

    // Upload info
    private String localPath;
    private String uploadStatus;

    // Message type
    private String type;


    public ChatMessage(){
        mapper = new ObjectMapper();
    }

    public ChatMessage(String text, String senderId, String timestamp, String imageUrl) {
        mapper = new ObjectMapper();
        this.text = text;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
    }

    public static ChatMessage getDefaultMessage() {
        ChatMessage message = new ChatMessage();
        message.setImageAspectRatio(ASPECT_RATIO_DEFAULT);
        message.setTimestamp(DateUtils.currentTimeToFirebaseDate());
        message.initializeTimestamp();
        message.setSenderId(Model.getInstance().getUserInfo().getUserId());
        message.setReadFlag(false);
        return message;
    }

    public void determineSelfReadFlag(){
        String userId = Model.getInstance().getUserInfo().getUserId();
        if(wasReadBy!=null){
            for(String key : wasReadBy){
                if(userId.equals(key)){
                    readFlag = true;
                }
            }
        }
    }

    public String getSenderId() {
        return senderId;
    }

    public ChatMessage setSenderId(String senderId) {
        this.senderId = senderId;
        return this;
    }

    public float getImageAspectRatio() {
        return imageAspectRatio;
    }

    public ChatMessage setImageAspectRatio(float imageAspectRatio) {
        this.imageAspectRatio = imageAspectRatio;
        return this;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public ChatMessage setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public List<String> getWasReadBy() {
        return wasReadBy;
    }

    public ChatMessage setWasReadBy(List<String> wasReadBy) {
        this.wasReadBy = wasReadBy;
        return this;
    }

    public boolean getReadFlag() {
        return readFlag;
    }

    public ChatMessage setReadFlag(boolean readFlag) {
        this.readFlag = readFlag;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ChatMessage setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getVidUrl() {
        return vidUrl;
    }

    public ChatMessage setVidUrl(String vidUrl) {
        this.vidUrl = vidUrl;
        return this;
    }

    public String getText() {
        return text;
    }

    public ChatMessage setText(String text) {
        this.text = text;
        return this;
    }


    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private boolean readFlag = false;
    private long timestampEpoch;
    public long getTimestampEpoch() {
        return timestampEpoch;
    }

    public void initializeTimestamp(){
        timestampEpoch = DateUtils.getTimestampFromFirebaseDate(timestamp);
    }

    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    @JsonProperty("_id")
    public void setId(String id) {
        this.id = id;
    }


    public String getLocid() {
        return locid;
    }

    public void setLocid(String locid) {
        this.locid = locid;
    }

    void imsUpdateMessage(ChatInfo chatInfo, String clubId, final TaskCompletionSource<ChatMessage> source){
        Map<String, Object> map = mapper.convertValue(this, new TypeReference<Map<String, Object>>() {});
        map.remove("_id");
        GSData data = new GSData(map);
        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
                .setEventKey("imsMessageUpdate")
                .setEventAttribute(GROUP_ID, chatInfo.getChatId())
                .setEventAttribute(MESSAGE, data)
                .setEventAttribute(MESSAGE_ID, getId())
                .setEventAttribute(CLUB_ID_TAG, clubId)
                .send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.LogEventResponse response) {
                        if(!response.hasErrors()){
                            GSData messageInfo = response.getScriptData().getObject("message");
                            if(messageInfo!=null){
                                ChatMessage.this.updateFrom(messageInfo.getBaseData());
                            }
                            if(source!=null){
                                // TODO @Filip returns both message & chat objects at once - completion?(chatInfo, message)
                                source.setResult(ChatMessage.this);
                            }
                        } else {
                            Log.e(TAG,"Failed to update message!");
                            if(source!=null){
                                source.setException(new Exception("Something went wrong with update of IMS message."));
                            }
                        }

                    }
                });
    }

    public void updateFrom(Map<String, Object> data){
        if (data.containsKey("_id")) {
            Object id = data.get("_id");
            if(id instanceof String){
                setId((String)id);
            } else {
                JSONObject idAsObject = (JSONObject)id;
                if(idAsObject.containsKey("$oid")){
                    setId((String) idAsObject.get("$oid"));
                }
            }
        }
        if (data.containsKey("locid")) {
            setLocid((String) data.get("locid"));
        }
        if (data.containsKey("text")) {
            setText((String) data.get("text"));
        }
        if (data.containsKey("senderId")) {
            setSenderId((String) data.get("senderId"));
        }
        if (data.containsKey("imageUrl")) {
            setImageUrl((String) data.get("imageUrl"));
        }
        if (data.containsKey("vidUrl")) {
            setVidUrl((String) data.get("vidUrl"));
        }
        if (data.containsKey("localPath")) {
            setLocalPath((String) data.get("localPath"));
        }
        if (data.containsKey("uploadStatus")) {
            setUploadStatus((String) data.get("uploadStatus"));
        }
        if (data.containsKey("type")) {
            setType((String) data.get("type"));
        }
        if (data.containsKey("wasReadBy")) {
           setWasReadBy((List<String>) data.get("wasReadBy"));
        }

        if (data.containsKey("readFlag")) {
            setReadFlag((Boolean) data.get("readFlag"));
        }
        if (data.containsKey("imageAspectRatio")) {
            Object imageAspectRatioData =  data.get("imageAspectRatio");
            if(imageAspectRatioData instanceof Long){
                setImageAspectRatio(((Long)imageAspectRatioData).floatValue());
            } else if(imageAspectRatioData instanceof Float)
            setImageAspectRatio(((Float)imageAspectRatioData));
        }
    }

    // do not use this function, call the chat info one!
    public void imsDeleteMessage(ChatInfo chatInfo, int clubId,  final TaskCompletionSource<ChatMessage> source){
        Map<String, Object> map = mapper.convertValue(this, new TypeReference<Map<String, Object>>() {});
        map.remove("_id");
        GSData data = new GSData(map);
        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
                .setEventKey("imsMessageDelete")
                .setEventAttribute(GROUP_ID, chatInfo.getChatId())
                .setEventAttribute(MESSAGE, data)
                .setEventAttribute(MESSAGE_ID, getId())
                .setEventAttribute(CLUB_ID_TAG, clubId)
                .send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.LogEventResponse response) {
                        if(!response.hasErrors()){
                            GSData messageInfo = response.getScriptData().getObject("message");
                            if(messageInfo!=null){
                                ChatMessage.this.updateFrom(messageInfo.getBaseData());
                            }
                            if(source!=null){
                                // TODO @Filip returns both message & chat objects at once - completion?(chatInfo, message)
                                source.setResult(ChatMessage.this);
                            }
                        } else {
                            Log.e(TAG,"Failed to delete message!");
                            if(source!=null){
                                source.setException(new Exception("Something went wrong with delete of IMS message."));
                            }
                        }

                    }
                });
    }

}
