package tv.sportssidekick.sportssidekick.model.friendship;

/**
 * Created by Filip on 12/27/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */


//-----------------------------------------------------------------------------------------//
//--                                                                                     --//
//--                               Friendship management                                 --//
//--                                                                                     --//
//-----------------------------------------------------------------------------------------//

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.ArrayList;
import java.util.List;

import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;

import static tv.sportssidekick.sportssidekick.model.Model.createRequest;


public class FriendsManager {

    private static final String OFFSET = "offset";
    private static final String ENTRY_COUNT = "entryCount";

    private static FriendsManager instance;
    private final ObjectMapper mapper; // jackson's object mapper

    public static FriendsManager getInstance(){
        if(instance==null){
            instance = new FriendsManager();
        }
        return instance;
    }

    private FriendsManager(){
        friendsIds = new ArrayList<>();
        mapper  = new ObjectMapper();
    }

    private List<String> friendsIds;

    /**
     * InviteFriend - send an invitation to join SSK app the the given friend
     *
     * @param  emailAddress - email address of the friend to invite
     * @return void
     */
    public void inviteFriend(String emailAddress) {
        // TODO Missing API ?
    }


    /**
     * GetFriends - returns the user friends list
     *
     * @return the user friends list
     */
    public Task<List<UserInfo>> getFriends(int offset){
        final TaskCompletionSource<List<UserInfo>> source = new TaskCompletionSource<>();

        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get("friends");
                    List<UserInfo> friends = mapper.convertValue(object, new TypeReference<List<UserInfo>>(){});
                    while(friends.contains(null)){
                        friends.remove(null);
                    }
                    source.setResult(friends);
                }
            }
        };
        createRequest("friendGetFriendsList")
                .setEventAttribute(ENTRY_COUNT,"50")
                .setEventAttribute(OFFSET,"50")
                .send(consumer);
        return source.getTask();
    }

    public boolean checkIfFriend(String uid) {
        return friendsIds != null && friendsIds.contains(uid);
    }

    /**
     * SendFriendRequest - send a friendship request to the given user
     *
     * @param  user
     */
    public void sendFriendRequest(UserInfo user){
        UserInfo info = Model.getInstance().getUserInfo();
        // TODO Rewrite to GS
//        Model
//            .getInstance()
//            .getUserInfoRef()
//            .child(user.getUserId())
//            .child("friendshipRequests")
//            .child(info.getUserId())
//            .setValue(true);
    }

    /**
     * rejectFriendRequest - reject a friendship request from user
     *
     * @param  user
     * @return void
     */
    public void rejectFriendRequest(UserInfo user){
        // Just remove request
        UserInfo info = Model.getInstance().getUserInfo();
        // TODO Rewrite to GS
//        Model
//            .getInstance()
//            .getUserInfoRef()
//            .child(info.getUserId())
//            .child("friendshipRequests")
//            .child(user.getUserId())
//            .removeValue();
    }


    /**
     * getOpenRequests - gets the list of open friendship requests
     *
     */
    public List<UserInfo> getOpenFriendRequests(){
        List<UserInfo> openRequests = new ArrayList<>();
        UserInfo info = Model.getInstance().getUserInfo();

//        for(String userId : info.getFriendshipRequests().keySet()){
//            openRequests.add(Model.getInstance().getCachedUserInfoById(userId));
//        }
        return  openRequests;
    }

     private  void clear(){
        // TODO ("*** clear FriendsManager")

    }

    public void logout() {
        clear();
    }

}
