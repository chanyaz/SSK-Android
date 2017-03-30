package tv.sportssidekick.sportssidekick.model.videoChat;

import java.util.HashMap;

public class VideoChatItem {

    String id;
    String ownerId;
    HashMap<String, String> participants;
    boolean isClosed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public VideoChatItem(String id, String ownerId, HashMap<String, String> participants) {
        this.id = id;
        this.ownerId = ownerId;
        this.participants = participants;

        if (participants !=null)
        {
            for (String uid : participants.values())
            {
                if(uid != ownerId)
                {
                    this.participants.put(uid, "");// TODO change this! self.participants[uid] = Date().toFirebase()
                }
                else {
                    this.participants.put(uid, "accepted");
                }
            }
        }
    }

    public void update(VideoChatItem item){ // TODO
//        if let isClosed = data["isClosed"] as? Bool {
//            self.isClosed = isClosed
//        }
//
//        if let dic = data["participants"] as? [String:String]! {
//                self.participants = dic
//        }
    }
}
