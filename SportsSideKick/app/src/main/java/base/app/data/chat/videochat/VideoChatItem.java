package base.app.data.chat.videochat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

import base.app.util.commons.Id;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoChatItem {

    @JsonProperty("_id")
    Id id;
    String ownerId;
    HashMap<String, String> participants;
    boolean isClosed = false;

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public HashMap<String, String> getParticipants() {
        return participants;
    }

    public void setParticipants(HashMap<String, String> participants) {
        this.participants = participants;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public VideoChatItem() { }

    public VideoChatItem(Id id, String ownerId, HashMap<String, String> participants) {
        this.id = id;
        this.ownerId = ownerId;
        this.participants = participants;

        if (participants !=null)
        {
            for (String uid : participants.values())
            {
                if(uid != ownerId)
                {
                    this.participants.put(uid, "");// TODO @Djordje change this! self.participants[uid] = Date().toFirebase()
                }
                else {
                    this.participants.put(uid, "accepted");
                }
            }
        }
    }

    public void update(VideoChatItem item){
        this.isClosed = item.isClosed();
        this.participants = item.getParticipants();
    }
}
