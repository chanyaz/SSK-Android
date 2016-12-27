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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.UserInfo;
import tv.sportssidekick.sportssidekick.service.FirebaseEvent;

/**
 * Friendship relations are stored under the path:  /friendshipIndex/<user id> -<friend user id>: true
 * and the same goes for the friend user.. /friendshipIndex/<friend user id>  -<user id>: true
 *
 *
 * Friends request are stored in the user info.
 **/



public class FriendsManager {

    private static FriendsManager instance;

    public static FriendsManager getInstance(){
        if(instance==null){
            instance = new FriendsManager();
        }
        return instance;
    }

    private FriendsManager(){
        friendsIds = new ArrayList<>();
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
    public void getFriends(){
        UserInfo info = Model.getInstance().getUserInfo();
        DatabaseReference ref = Model.getInstance().getRef().getReference("friendshipIndex").child(info.getUserId());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendsIds = new ArrayList<>();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    String userInfoId = child.getKey();
                    friendsIds.add(userInfoId);
                    // TODO Get user info for id?
                }
                EventBus.getDefault().post(new FirebaseEvent("All friends Ids downloaded", FirebaseEvent.Type.FRIEND_DOWNLOADED, friendsIds));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
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
        Model
            .getInstance()
            .getUserInfoRef()
            .child(user.getUserId())
            .child("friendshipRequests")
            .child(info.getUserId())
            .setValue(true);
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
        Model
            .getInstance()
            .getUserInfoRef()
            .child(info.getUserId())
            .child("friendshipRequests")
            .child(user.getUserId())
            .removeValue();
    }


    /**
     * getOpenRequests - gets the list of open friendship requests
     *
     */
    public List<UserInfo> getOpenFriendRequests(){
        List<UserInfo> openRequests = new ArrayList<>();
        UserInfo info = Model.getInstance().getUserInfo();

        for(String userId : info.getFriendshipRequests()){
            openRequests.add(Model.getInstance().getCachedUserInfoById(userId));
        }
        return  openRequests;
    }

     private  void clear(){
        // TODO ("*** clear FriendsManager")

    }

    public void logout() {
        clear();
    }

}
