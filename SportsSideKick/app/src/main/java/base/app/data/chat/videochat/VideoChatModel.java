package base.app.data.chat.videochat;


import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.GSData;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import base.app.data.user.User;
import base.app.util.commons.GSConstants;
import base.app.util.commons.Model;
import base.app.data.user.GSMessageHandlerAbstract;

import static base.app.ClubConfig.CLUB_ID;
import static base.app.util.commons.GSConstants.CLUB_ID_TAG;


public class VideoChatModel extends GSMessageHandlerAbstract {

    private static final String TAG = "VIDEO CHAT";

    private VideoChatItem activeVideoChatItem;
    private HashMap<String,VideoChatItem> pendingInvitations;
    private String userId;
    private final ObjectMapper mapper;

    private VideoChatEvent videoChatEvent;

    public enum VideoChatNotificationType{
        CALL_INVITE {
            public String toString() {
                return "You have been invited to a video chat!";
            }
        }
    }

    private static VideoChatModel instance;

    private VideoChatModel() {
        mapper = new ObjectMapper();
        pendingInvitations = new HashMap<>();
        Model.getInstance().setMessageHandlerDelegate(this);
    }

    public static VideoChatModel getInstance(){
        if(instance == null){
            instance = new VideoChatModel();
        }
        return instance;
    }


    private String getUserId() {
        if(Model.getInstance().getUserInfo()!=null) {
            return Model.getInstance().getUserInfo().getUserId();
        } else {
            return null;
        }
    }

    private boolean isInvitationValid(VideoChatItem chat) {
        boolean inChat = chat.getParticipants().containsKey(getUserId());
        boolean hasOthers = chat.participants.size() > 1;
        boolean hasActiveParticipants = false;

        for(Map.Entry<String,String> entry : chat.getParticipants().entrySet()){
            if(entry.getValue().equals("accepted")){
                hasActiveParticipants = true;
                break;
            }
        }
        return inChat && hasOthers && hasActiveParticipants;
    }

    // Find the VideoChatItem to update - it may be the active one, or a pending invitation
    // The only things that will need updating are the participants and their states
    private VideoChatItem update(Map<String, Object> data) {
        VideoChatItem item = mapper.convertValue(data, new TypeReference<VideoChatItem>() {});
        //Object object = data.get(GSConstants.VIDEO_CHAT_ITEM);
        if(data!=null){
            item = mapper.convertValue(data, new TypeReference<VideoChatItem>() {});
        } else {
            Log.d(TAG,"Error in update method of Video Chat model!");
        }


        boolean isActive = activeVideoChatItem != null && activeVideoChatItem.getId().equals(item.getId());
        VideoChatItem itemToUpdate = null;
        if (isActive) {
            itemToUpdate = activeVideoChatItem;
        } else {
            if (pendingInvitations.containsKey(item.getId())) {
                itemToUpdate = pendingInvitations.get(item.getId());
            }
        }
        if(itemToUpdate!=null){
            itemToUpdate.update(item);
        }
        return item;
    }

    private Task<VideoChatItem> leaveOrReject(String conferenceId, String leaveType){
        final TaskCompletionSource<VideoChatItem> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.VIDEO_CHAT_ITEM);
                    if(object!=null){
                        VideoChatItem item = mapper.convertValue(object, new TypeReference<VideoChatItem>() {});
                        source.setResult(item);
                        return;
                    }
                }
                source.setException(new Exception("There was an error while trying to leave a video chat."));
            }
        };
        Model.createRequest(leaveType)
            .setEventAttribute("conferenceId", conferenceId)
            .send(consumer);
        return source.getTask();
    }

    // YOU OR OTHERS have been invited
    private void onInvited(Map<String, Object> data) {
        VideoChatItem item = update(data);

        // Check to see if this is an invite message for a call we're in already.
        // If it is, then it's for new people we need to account for
        if(activeVideoChatItem!=null && activeVideoChatItem.getId().equals(item.getId())) {
            Object object = data.get(GSConstants.DATA);
            List<String> invitees = mapper.convertValue(object, new TypeReference<List<String>>() {
            });
            if (invitees != null) {
                for (String invitee : invitees) {
                    // Fix: If a user is logged in on two devices, they could potentially join the chat
                    // twice, so we make sure that can't happen here
                    if (!invitee.equals(userId)) {
                        EventBus.getDefault().post(new VideoChatEvent(VideoChatEvent.Type.onUserInvited, invitee));
                    }
                }
            }
            return;
        }
        // Is it an existing invitation we have yet to accept / reject?
        // If so, do nothing else
        if(pendingInvitations.containsKey(item.getId().getOid())){
            return;
        }
        // This is an invitation for us to join a chat
        // we are not yet invited to and are not a part of
        if (!isInvitationValid(item)) {
            return;
        }
        pendingInvitations.put((item.getId().getOid()),item);
        EventBus.getDefault().post(new VideoChatEvent(VideoChatEvent.Type.onSelfInvited, item.getId().getOid(),item));
    }

    private void onInviteExpired(Map<String,Object> data){
        VideoChatItem item = update(data);
        String playerId = (String) data.get(GSConstants.DATA);
        if(playerId==null){
            return;
        }
        if (activeVideoChatItem!=null)
        {
            if(activeVideoChatItem.getId().equals(item.getId())) {
                // If it's the active video item, this should only ever be someone else's invite
                if(!playerId.equals(userId)){
                    EventBus.getDefault().post(new VideoChatEvent(VideoChatEvent.Type.onUserInvitationRejected, playerId));
                }
            } else {
                if(!playerId.equals(userId)){
                    EventBus.getDefault().post(new VideoChatEvent(VideoChatEvent.Type.onInvitationRevoked, item.getId().getOid()));
                    pendingInvitations.remove(item.getId());
                }
            }
        }
    }

    private void onRejected(Map<String,Object> data){
        VideoChatItem item = update(data);
        if(activeVideoChatItem.getId().equals(item.getId())) {
            String id = (String) data.get(GSConstants.DATA);
            if(id!=null){
                EventBus.getDefault().post(new VideoChatEvent(VideoChatEvent.Type.onUserInvitationRejected, id));
            }
        }
    }

    // A conference room relevant to the current user has closed
    private void onClosed(Map<String,Object> data) {
        VideoChatItem item = update(data);
        if(pendingInvitations.containsKey(item.getId())){
            pendingInvitations.remove(item.getId());
        }
        EventBus.getDefault().post(new VideoChatEvent(VideoChatEvent.Type.onChatClosed, item.getId().getOid()));
    }

    public Task<VideoChatItem> create(List<String> users){
        final TaskCompletionSource<VideoChatItem> source = new TaskCompletionSource<>();

        if(activeVideoChatItem != null) {
            leave(activeVideoChatItem.getId().getOid());
        }

        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.VIDEO_CHAT_ITEM);
                    if(object!=null){
                        VideoChatItem item = mapper.convertValue(object, new TypeReference<VideoChatItem>() {});
                        activeVideoChatItem = item;
                        source.setResult(item);
                        return;
                    }
                }
                source.setException(new Exception("There was an error while trying to create a video chat."));
            }
        };

       GSData participants = new GSData();
       participants.getBaseData().put("participants",users);
       Model.createRequest("vcCreate")
               .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
               .setEventAttribute("users",participants)
                .send(consumer);
        return source.getTask();
    }

    public Task<VideoChatItem> join(String conferenceId){
        final TaskCompletionSource<VideoChatItem> source = new TaskCompletionSource<>();
        if(activeVideoChatItem!=null && activeVideoChatItem.getId().equals(conferenceId)){
            Log.e(TAG,"Already part of this conference! " + conferenceId);
            source.setException(new Exception("Already part of this conference! " + conferenceId));
            return source.getTask();
        }
        if(activeVideoChatItem != null) {
            leave(activeVideoChatItem.getId().getOid());
        }

        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.VIDEO_CHAT_ITEM);
                    if(object!=null){
                        VideoChatItem item = mapper.convertValue(object, new TypeReference<VideoChatItem>() {});
                        activeVideoChatItem = item;
                        pendingInvitations.remove(item.getId());
                        source.setResult(item);
                        return;
                    }
                }
                source.setException(new Exception("There was an error while trying to join a video chat."));
            }
        };


        Model.createRequest("vcJoin")
            .setEventAttribute("conferenceId",conferenceId)
            .send(consumer);
        return source.getTask();
    }

    public Task<VideoChatItem> leave(String conferenceId){
        activeVideoChatItem = null;
        return leaveOrReject(conferenceId,"vcLeave");
    }

    public Task<VideoChatItem> reject(String conferenceId){
        pendingInvitations.remove(conferenceId);
        return leaveOrReject(conferenceId,"vcReject");
    }

    public Task<VideoChatItem> invite(String conferenceId,List<String> users){
        final TaskCompletionSource<VideoChatItem> source = new TaskCompletionSource<>();

        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.VIDEO_CHAT_ITEM);
                    if (object != null) {
                        VideoChatItem item = update(response.getScriptData().getBaseData());
                        source.setResult(item);
                        return;
                    }
                }
                source.setException(new Exception("There was an error while trying to invite users to a video chat."));
            }
        };

        GSData participants = new GSData();
        participants.getBaseData().put("participants",users);
        Model.createRequest("vcInvite")
                .setEventAttribute("users",participants)
                .setEventAttribute("conferenceId",conferenceId)
                .send(consumer);
        return source.getTask();
    }

    public Task<String> getToken(String conferenceId){
        final TaskCompletionSource<String> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.TOKEN);
                    if(object!=null){
                        source.setResult((String)object);
                        return;
                    }
                }
                source.setException(new Exception("There was an error while trying to get token for a video chat."));
            }
        };
        Model.createRequest("vcGetToken")
                .setEventAttribute("conferenceId",conferenceId)
                .send(consumer);
        return source.getTask();
    }

    public void reset(){
        activeVideoChatItem = null;
        pendingInvitations.clear();
        userId = Model.getInstance().getUserInfo().getUserId();
    }

    public List<String> getInvitedUsers(){
        if(activeVideoChatItem!=null){
            List<String> users = new ArrayList<>();
            for(Map.Entry<String,String> entry : activeVideoChatItem.getParticipants().entrySet()){
                if(entry.getValue().equals("accepted")){
                    users.add(entry.getKey());
                }
            }
            if(users.size()>0){
                return users;
            }
        }
        return null;
    }

    public VideoChatItem getPendingInvite(String id){
        if(pendingInvitations.containsKey(id)){
           return pendingInvitations.get(id);
        }
        return null;
    }

    @Override
    public void onGSScriptMessage(String type, Map<String,Object> data){
        switch (type){
            case "vcInvited":
                onInvited(data);
                break;
            case "vcInviteExpired":
                onInviteExpired(data);
                break;
            case "vcRejected":
                onRejected(data);
                break;
            case "vcClosed":
                onClosed(data);
                break;
            case "vcJoined":
                update(data);
                break;
            case "vcLeft":
                update(data);
                break;
            default:
                Log.e(TAG,"Unhandled script message type: " + type + ", with data: " + data);
                break;
        }
    }
    @Subscribe
    public void userInfoListener(User info){
        userId = info.getUserId();
    }

    public VideoChatEvent getVideoChatEvent() {
        return videoChatEvent;
    }

    public void setVideoChatEvent(VideoChatEvent videoChatEvent) {
        this.videoChatEvent = videoChatEvent;
    }
}
