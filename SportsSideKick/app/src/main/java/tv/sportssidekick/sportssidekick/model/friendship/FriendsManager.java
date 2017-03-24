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
import com.gamesparks.sdk.api.autogen.GSRequestBuilder;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import tv.sportssidekick.sportssidekick.model.DateUtils;
import tv.sportssidekick.sportssidekick.model.GSConstants;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.user.GSMessageHandlerAbstract;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.service.GSAndroidPlatform;

import static tv.sportssidekick.sportssidekick.model.Model.createRequest;


public class FriendsManager extends GSMessageHandlerAbstract {

    private static FriendsManager instance;
    private final ObjectMapper mapper; // jackson's object mapper

    public static FriendsManager getInstance(){
        if(instance==null){
            instance = new FriendsManager();
        }
        return instance;
    }

    private FriendsManager(){
        mapper  = new ObjectMapper();
        Model.getInstance().setMessageHandlerDelegate(this);
    }

    /**
     * InviteFriend - send an invitation to join SSK app the the given friend
     * @param  emailAddress - email address of the friend to invite
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
                    Object object = response.getScriptData().getBaseData().get(GSConstants.FRIENDS);
                    List<UserInfo> friends = mapper.convertValue(object, new TypeReference<List<UserInfo>>(){});
                    while(friends.contains(null)){
                        friends.remove(null);
                    }
                    source.setResult(friends);
                }  else {
                    source.setException(new Exception());
                }
            }
        };
        createRequest("friendGetFriendsList")
                .setEventAttribute(GSConstants.ENTRY_COUNT,"50")
                .setEventAttribute(GSConstants.OFFSET,offset)
                .send(consumer);
        return source.getTask();
    }

    /**
     * getMutualFriendsListWithUser - returns the mutual friends list with the given user
     * @return the user friends list
     *
     */
    public Task<List<UserInfo>> getMutualFriendsListWithUser(String userId, int offset){
        final TaskCompletionSource<List<UserInfo>> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.FRIENDS);
                    List<UserInfo> friends = mapper.convertValue(object, new TypeReference<List<UserInfo>>(){});
                    while(friends.contains(null)){
                        friends.remove(null);
                    }
                    source.setResult(friends);
                } else {
                    source.setException(new Exception());
                }
            }
        };
        createRequest("friendGetMutualFriendsListWithUser")
                .setEventAttribute(GSConstants.USER_ID,userId)
                .setEventAttribute(GSConstants.ENTRY_COUNT,"50")
                .setEventAttribute(GSConstants.OFFSET,offset)
                .send(consumer);
        return source.getTask();
    }

    /**
     * SendFriendRequest - send a friendship request to the given user
     * @param  userId user Id
     */
    public Task<UserInfo> sendFriendRequest(String userId){
        final TaskCompletionSource<UserInfo> source = new TaskCompletionSource<>();
        GSRequestBuilder.CreateChallengeRequest request = GSAndroidPlatform.gs().getRequestBuilder().createCreateChallengeRequest();
        GSEventConsumer<GSResponseBuilder.CreateChallengeResponse> consumer = new GSEventConsumer<GSResponseBuilder.CreateChallengeResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.CreateChallengeResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.USER_INFO);
                    UserInfo user = mapper.convertValue(object, new TypeReference<UserInfo>(){});
                    source.setResult(user);
                } else {
                    source.setException(new Exception());
                }
            }
        };

        request.setAccessType("PRIVATE");
        request.setChallengeShortCode("friendRequest");
        Calendar c = Calendar.getInstance(); // starts with today's date and time
        c.add(Calendar.WEEK_OF_YEAR, 2);  // advances day by 2
        Date date = c.getTime(); // gets modified time
        request.setEndTime(date); //set the end time for two weeks
        request.getBaseData().put(GSConstants.TIMESTAMP_TAG,DateUtils.currentTimeToFirebaseDate());
        List<String> usersToChallenge = new ArrayList<>();
        usersToChallenge.add(userId);
        request.setUsersToChallenge(usersToChallenge);

        request.send(consumer);
        return source.getTask();
    }


    /**
     * acceptFriendRequest - accept a friendship request from user
     * @param  friendRequestId user id
     * @return void
     */
    public Task<UserInfo> acceptFriendRequest(String friendRequestId){
        final TaskCompletionSource<UserInfo> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.USER_INFO);
                    UserInfo user = mapper.convertValue(object, new TypeReference<UserInfo>(){});
                    source.setResult(user);
                } else {
                    source.setException(new Exception());
                }
            }
        };
        createRequest("friendAcceptFriendRequest")
            .setEventAttribute(GSConstants.FRIEND_REQUEST_ID,friendRequestId)
            .send(consumer);
        return source.getTask();
    }

    /**
     * rejectFriendRequest - reject a friendship request from user
     *
     * @param  friendRequestId id of request
     * @return void
     */
    public Task<Boolean> rejectFriendRequest(String friendRequestId){
        final TaskCompletionSource<Boolean> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    source.setResult(response.hasErrors());
                } else {
                    source.setResult(false);
                }
            }
        };
        createRequest("friendRejectFriendRequest")
            .setEventAttribute(GSConstants.FRIEND_REQUEST_ID,friendRequestId)
            .send(consumer);
        return source.getTask();
    }


    /**
     * getOpenRequests - gets the list of open friendship requests
     *
     */
    public Task<List<FriendRequest>>  getOpenFriendRequests(int offset){
        final TaskCompletionSource<List<FriendRequest>> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get("requests");
                    List<FriendRequest> friends = mapper.convertValue(object, new TypeReference<List<FriendRequest>>(){});
                    while(friends.contains(null)){
                        friends.remove(null);
                    }
                    source.setResult(friends);
                } else {
                    source.setException(new Exception());
                }
            }
        };
        createRequest("friendListAllFriendRequests")
                .setEventAttribute(GSConstants.OFFSET,offset)
                .setEventAttribute(GSConstants.ENTRY_COUNT,50)
                .send(consumer);
        return source.getTask();
    }

    /**
     * deleteFriend - remove a user from the current user friends list
     * @param  userId to un friend
     * @return void
     */
    public Task<UserInfo> deleteFriend(String userId){
        final TaskCompletionSource<UserInfo> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.USER_INFO);
                    UserInfo user = mapper.convertValue(object, new TypeReference<UserInfo>(){});
                    source.setResult(user);
                } else {
                    source.setException(new Exception());
                }
            }
        };
        createRequest("friendDeleteFriend")
                .setEventAttribute(GSConstants.USER_ID,userId)
                .send(consumer);
        return source.getTask();
    }

     private  void clear(){
        // TODO ("*** clear FriendsManager")

    }

    public void logout() {
        clear();
    }

    //---------------------------------------
    //              Following
    //---------------------------------------

    /**
     * getUserFollowersList - gets the list all users that follows the current user
     * @param  offset - for paging
     * @return Void
     */
    public Task<List<UserInfo>> getUserFollowersList(String userId, int offset){
        final TaskCompletionSource<List<UserInfo>> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get("followers");
                    List<UserInfo> followers = mapper.convertValue(object, new TypeReference<List<UserInfo>>(){});
                    while(followers.contains(null)){
                        followers.remove(null);
                    }
                    source.setResult(followers);
                }  else {
                    source.setException(new Exception());
                }
            }
        };
        createRequest("friendGetFollowersList")
                .setEventAttribute(GSConstants.ENTRY_COUNT,"50")
                .setEventAttribute(GSConstants.OFFSET,offset)
                .setEventAttribute(GSConstants.USER_ID,userId)
                .send(consumer);
        return source.getTask();
    }

    /**
     * getUserFollowingList - gets the list all users that the current user follows
     * @param  offset - for paging
     * @return Void
     */
    public Task<List<UserInfo>> getUserFollowingList(String userId, int offset){
        final TaskCompletionSource<List<UserInfo>> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get("following");
                    List<UserInfo> followers = mapper.convertValue(object, new TypeReference<List<UserInfo>>(){});
                    while(followers.contains(null)){
                        followers.remove(null);
                    }
                    source.setResult(followers);
                }  else {
                    source.setException(new Exception());
                }
            }
        };
        createRequest("friendGetFollowingList")
                .setEventAttribute(GSConstants.ENTRY_COUNT,"50")
                .setEventAttribute(GSConstants.OFFSET,offset)
                .setEventAttribute(GSConstants.USER_ID,userId)
                .send(consumer);
        return source.getTask();
    }

    /**
     * followUser - start following the given user
     * @param  userId - the user to follow
     * @return Void
     */
    public Task<UserInfo> followUser(String userId){
        final TaskCompletionSource<UserInfo> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.USER_INFO);
                    UserInfo user = mapper.convertValue(object, new TypeReference<UserInfo>(){});
                    source.setResult(user);
                } else {
                    source.setException(new Exception());
                }
            }
        };
        createRequest("friendFollowFriend")
                .setEventAttribute(GSConstants.USER_ID,userId)
                .send(consumer);
        return source.getTask();
    }

    /**
     * unFollowUser - stop following the given user
     * @param  userId - the user to stop follow
     * @return Void
     */
    public Task<UserInfo> unFollowUser(String userId){
        final TaskCompletionSource<UserInfo> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.USER_INFO);
                    UserInfo user = mapper.convertValue(object, new TypeReference<UserInfo>(){});
                    source.setResult(user);
                } else {
                    source.setException(new Exception());
                }
            }
        };
        createRequest("friendUnFollowFriend")
                .setEventAttribute(GSConstants.USER_ID,userId)
                .send(consumer);
        return source.getTask();
    }




    @Override
    public void onGSScriptMessage(String type, Map<String,Object> data){
        switch (type){
            case "UserInfo":
                String operation = (String) data.get(GSConstants.OPERATION);
                if(operation!=null){
                    if(!operation.equals("update")){
                        if(data.containsKey(GSConstants.USER_INFO)) {
                            UserInfo userInfo = mapper.convertValue(data.get(GSConstants.USER_INFO),UserInfo.class);
                            // TODO Emit userInfo - a friend is updated! ( or its me?! )
                        }
                    }
                }
            break;
        }
    }

   // TODO ?
//   func onMessage(msg:GSMessage?){
//       if let extCode = msg?.getAttribute("extCode") as? String{
//           if extCode ==  "FriendRequestAcceptedMessage"{
//               NotificationCenter.default.post(name: SKKConstants.Keys.Notifications.Friends.kFriendsListChanged,
//                       object: msg)
//           }
//       }
//   }

}
