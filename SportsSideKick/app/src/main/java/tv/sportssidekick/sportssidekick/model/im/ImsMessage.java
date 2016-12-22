package tv.sportssidekick.sportssidekick.model.im;

import android.text.format.DateUtils;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

import tv.sportssidekick.sportssidekick.model.FirebseObject;
import tv.sportssidekick.sportssidekick.model.Model;

/**
 * Created by Filip on 12/7/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ImsMessage extends FirebseObject {

    private static final float ASPECT_RATIO_DEFAULT = 0.5625f;
    private String text;
    private String senderId;
    private float imageAspectRatio;
    private String timestamp;
    private HashMap<String, Boolean> wasReadBy;
    @Exclude
    private boolean readFlag = false;
    private String imageUrl;
    private String vidUrl;


    @Exclude
    public long getTimestampEpoh() {
        return timestampEpoh;
    }

    @Exclude
    private long timestampEpoh;


    public void initializeTimestamp(){
        if(timestamp!=null){
            timestampEpoh = Long.valueOf(timestamp.replace(".",""))/100;
        } else {
            timestampEpoh = 0;
        }
    }

    @Exclude
    public String getTimeAgo(){
        String timeago = DateUtils.getRelativeTimeSpanString(timestampEpoh).toString();
        if(timeago.equals("0 minutes ago")){
            timeago = "Just Now";
        } else {
            timeago = timeago.replace(" minutes","m");
            timeago = timeago.replace(" minute","m");
        }
        return timeago;
    }

    public ImsMessage(String text, String senderId, String timestamp, String imageUrl) {
        this.text = text;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
    }

    public ImsMessage(){}

    public static ImsMessage getDefaultMessage() {
        ImsMessage message = new ImsMessage();
        message.setImageAspectRatio(ASPECT_RATIO_DEFAULT);
        long currentTime = System.currentTimeMillis();
        String str = String.valueOf(currentTime);
        str = new StringBuilder(str).insert(str.length()-3, ".").append("33").toString();
        message.setTimestamp(str);
        message.setSenderId(Model.getInstance().getUserInfo().getUserId());
        return message;
    }

    // TODO ReadBy - detect if read by this user?
    public void determineSelfReadFlag(){
        String userId = Model.getInstance().getUserInfo().getUserId();
        for(String key : wasReadBy.keySet()){
            if(userId.equals(key)){
                readFlag = true;
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

    @Exclude
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
}
