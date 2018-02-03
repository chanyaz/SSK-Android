package base.app.data.user.friends;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import base.app.data.user.User;
import base.app.util.commons.GSConstants;

/**
 * Created by Djordje on 21/01/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FriendRequest {

    @JsonProperty("id")
    private String id;
    @JsonProperty(GSConstants.TIMESTAMP_TAG)
    private String timestamp;
    @JsonProperty("sender")
    private User sender;

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
    public User getSender() {
        return sender;
    }

    @JsonProperty(GSConstants.SENDER)
    public void setSender(User sender) {
        this.sender = sender;
    }
}

