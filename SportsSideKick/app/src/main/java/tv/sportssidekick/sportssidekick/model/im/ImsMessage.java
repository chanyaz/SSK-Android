package tv.sportssidekick.sportssidekick.model.im;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

import tv.sportssidekick.sportssidekick.model.DateUtils;
import tv.sportssidekick.sportssidekick.model.Model;

/**
 * Created by Filip on 12/7/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImsMessage {

    private static final float ASPECT_RATIO_DEFAULT = 0.5625f;

    @JsonProperty("_id")
    private String id;
    private String text;
    private String senderId;
    private float imageAspectRatio;
    private String timestamp;
    private String imageUrl;
    private String vidUrl;
    private HashMap<String, Boolean> wasReadBy;

    // Upload info
    private String localPath;
    private String uploadStatus;

    // Message type
    private String type;

    public ImsMessage(){}

    public ImsMessage(String id, String text, String senderId, float imageAspectRatio, String timestamp, String imageUrl, String vidUrl, HashMap<String, Boolean> wasReadBy, String localPath, String uploadStatus, String type, boolean readFlag, long timestampEpoch) {
        this.id = id;
        this.text = text;
        this.senderId = senderId;
        this.imageAspectRatio = imageAspectRatio;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
        this.vidUrl = vidUrl;
        this.wasReadBy = wasReadBy;
        this.localPath = localPath;
        this.uploadStatus = uploadStatus;
        this.type = type;
        this.readFlag = readFlag;
        this.timestampEpoch = timestampEpoch;
    }

    public ImsMessage(String text, String senderId, String timestamp, String imageUrl) {
        this.text = text;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
    }

    public static ImsMessage getDefaultMessage() {
        ImsMessage message = new ImsMessage();
        message.setImageAspectRatio(ASPECT_RATIO_DEFAULT);
        message.setTimestamp(DateUtils.currentTimeToFirebaseDate());
        message.setSenderId(Model.getInstance().getUserInfo().getUserId());
        message.setReadFlag(false);
        return message;
    }

    public void determineSelfReadFlag(){
        String userId = Model.getInstance().getUserInfo().getUserId();
        if(wasReadBy!=null){
            for(String key : wasReadBy.keySet()){
                if(userId.equals(key)){
                    readFlag = true;
                }
            }
        }
    }

    public String getSenderId() {
        return senderId;
    }

    public ImsMessage setSenderId(String senderId) {
        this.senderId = senderId;
        return this;
    }

    public float getImageAspectRatio() {
        return imageAspectRatio;
    }

    public ImsMessage setImageAspectRatio(float imageAspectRatio) {
        this.imageAspectRatio = imageAspectRatio;
        return this;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public ImsMessage setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public HashMap<String, Boolean> getWasReadBy() {
        return wasReadBy;
    }

    public ImsMessage setWasReadBy(HashMap<String, Boolean> wasReadBy) {
        this.wasReadBy = wasReadBy;
        return this;
    }

    public boolean getReadFlag() {
        return readFlag;
    }

    public ImsMessage setReadFlag(boolean readFlag) {
        this.readFlag = readFlag;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ImsMessage setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getVidUrl() {
        return vidUrl;
    }

    public ImsMessage setVidUrl(String vidUrl) {
        this.vidUrl = vidUrl;
        return this;
    }

    public String getText() {
        return text;
    }

    public ImsMessage setText(String text) {
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

    public String getTimeAgo(){
        String timeAgo = android.text.format.DateUtils.getRelativeTimeSpanString(timestampEpoch).toString();
        if(timeAgo.equals("0 minutes ago")){
            timeAgo = "Just Now";
        } else {
            timeAgo = timeAgo.replace(" minutes","m");
            timeAgo = timeAgo.replace(" minute","m");
        }
        return timeAgo;
    }

    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    @JsonProperty("_id")
    public void setId(String id) {
        this.id = id;
    }
}
