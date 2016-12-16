package tv.sportssidekick.sportssidekick.model.im;

import java.util.HashMap;
import java.util.List;

import tv.sportssidekick.sportssidekick.model.FirebseObject;
import tv.sportssidekick.sportssidekick.model.Model;

/**
 * Created by Filip on 12/7/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ImsMessage extends FirebseObject {

    private String text;
    private String senderId;
    private float imageAspectRatio; // TODO default is  = 0.5625;
    private String timestamp;
    private HashMap<String, Boolean> wasReadBy;
    private boolean readFlag = false;
    private String imageUrl;
    private String vidUrl;

    public ImsMessage(String text, String senderId, String timestamp, String imageUrl) {
        this.text = text;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
    }

    public ImsMessage() {
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

    public boolean isReadFlag() {
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