package tv.sportssidekick.sportssidekick.model.videoChat;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by Djordje Krutil on 27.1.2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class VideoChatItem {

    String id;
    String ownerId;
    HashMap<String, String> participants;

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
                    this.participants.put(uid, "");// TODO chaneg it!!! self.participants[uid] = Date().toFirebase()
                }
                else {
                    this.participants.put(uid, "accepted");
                }
            }
        }
    }
}
