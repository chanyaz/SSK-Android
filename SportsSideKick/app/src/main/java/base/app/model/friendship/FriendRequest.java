package base.app.model.friendship;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import base.app.model.GSConstants;
import base.app.model.user.UserInfo;

/**
 * Created by Djordje on 21/01/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FriendRequest {

    @JsonProperty("_id")
    private String id;
    @JsonProperty(GSConstants.TIMESTAMP_TAG)
    private String timestamp;
    @JsonProperty("sender")
    private UserInfo sender;

    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    @JsonProperty("_id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty(GSConstants.TIMESTAMP_TAG)
    public String getTimestamp() {
        return timestamp;
    }

    @JsonProperty(GSConstants.TIMESTAMP_TAG)
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty(GSConstants.SENDER)
    public UserInfo getSender() {
        return sender;
    }

    @JsonProperty(GSConstants.SENDER)
    public void setSender(UserInfo sender) {
        this.sender = sender;
    }
}
