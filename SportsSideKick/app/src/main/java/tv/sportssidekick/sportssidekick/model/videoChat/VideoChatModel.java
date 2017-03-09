package tv.sportssidekick.sportssidekick.model.videoChat;



import java.util.Date;
import java.util.List;

import tv.sportssidekick.sportssidekick.model.Model;

/**
 * Created by Djordje Krutil on 27.1.2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class VideoChatModel {

    public enum VideoChatNotificationType{
        CALL_INVITE {
            public String toString() {
                return "You have been invited to a video chat!";
            }
        }

    }

    public VideoChatModel() {
    }

    private String getUserId()
    {
        return Model.getInstance().getUserInfo().getUserId();
    }

    private boolean isInvitationValid(VideoChatItem chat, Date invitationDate)
    {
        boolean inChat = chat.participants.containsKey(this.getUserId());
        boolean hasOthers = false;
        if (chat.participants.size() > 1)
        {
            hasOthers = true;
        }
        boolean hasActiveParticipants = false;

//        for (int i=0; i < chat.participants.size(); i++)
//        {
//            if(chat.participants.)
//        }

//        for (_, state) in chat.participants {
//        if state == "accepted" {
//            hasActiveParticipants = true
//            break
//        }
//    }

        if (invitationDate != null)
        {
//            if invitationDate != nil {
//            let isValidAge:Bool = Date().compare(invitationDate!.addingTimeInterval(self.invitationPendingTime)) == ComparisonResult.orderedAscending
//            return inChat && hasOthers && isValidAge && hasActiveParticipants
        }

        return inChat && hasOthers && hasActiveParticipants;
    }

    private void sendNotification(String chatId, List<String> userIds, VideoChatNotificationType notificationType)
    {

//        String userName = Model.getInstance().getCachedUserInfoById(userIds);
    }


}
