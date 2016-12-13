package tv.sportssidekick.sportssidekick.model.im;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tv.sportssidekick.sportssidekick.model.Model;

/**
 * Created by Filip on 12/7/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ImModel {

    private static final String MUTE = "mute";
    private static ImModel instance;

    private String userId;
    private DatabaseReference imsChatsRef;
    private DatabaseReference imsChatsInfoRef;
    private DatabaseReference imsChatsMessagesRef;
    private DatabaseReference imsPublicChatsIndexRef;
    private DatabaseReference imsUserChatsIndexRef;
    private DatabaseReference imsNotificationQueueRef;
    private HashMap<String, ChatInfo> userChatsInfo;
    private List<ChatInfo> publicChatsInfo;

    private ImModel() {
        FirebaseDatabase ref = FirebaseDatabase.getInstance();
        imsChatsRef = ref.getReference("imsChats"); //root of all chats
        imsChatsInfoRef = imsChatsRef.child("chatsInfo");
        imsChatsMessagesRef = imsChatsRef.child("chatsMessages");
        imsPublicChatsIndexRef = imsChatsRef.child("publicChatsIndex");
        imsUserChatsIndexRef = imsChatsRef.child("usersChatsIndex");//root for all user chat activity
        imsNotificationQueueRef = ref.getReference("queue-notify-im/tasks"); // notification queue
        userChatsInfo = new HashMap<>();
        publicChatsInfo = new ArrayList<>();
        userId = Model.getInstance().getUserInfo().getUserId();
    }

    public static ImModel getInstance(){
        if(instance==null){
            instance = new ImModel();
        }
        return instance;
    }

    /////////////////////////////////////
    ///   IMS   API  - Chat Info API  ///
    /////////////////////////////////////

    /**
     * return a list of all user chats, this list is loaded asynchronousely and gets updates when changed
     * you need to listen to notifyChatsInfoUpdates to get the updates in the list.
     *
     * @return [String : ChatInfo] chats info list
     */

    public HashMap<String, ChatInfo> getUserChats() {
        return userChatsInfo;
    }

    /**
     * getAllPublicChats returnes a list of all public chats in the system. you will need to listen to notifyGetAllPublicChats
     * to receive the updated list of public chats
     * this list does not include chats that the user is blocked from
     * <p>
     * cjw -- re-worked this slightly to fix issue where it's always returning a zero length array
     * and generally breaking because of race conditions with ASYNC Firebase requests
     */
    public void getAllPublicChats() {
        publicChatsInfo = new ArrayList<ChatInfo>();
        imsPublicChatsIndexRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    if(child.getValue(Boolean.class)){
                        final String chatId = child.getKey();
                        DatabaseReference chatRef = imsChatsInfoRef.child(chatId);
                        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ChatInfo chatInfo = dataSnapshot.getValue(ChatInfo.class);
                                userChatsInfo.put(chatId,chatInfo);
                                if(chatInfo.isPublic()&& !chatInfo.isUserBlockedFromThisChat(userId)){
                                    publicChatsInfo.add(chatInfo);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void loadUserChats() {
        final DatabaseReference userChatsRef = imsUserChatsIndexRef.child(userId);

        userChatsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String chatId = dataSnapshot.getKey();
                boolean muted = MUTE.equals(dataSnapshot.getValue(String.class));
                newChatWasAddedToTheUserChatsList(chatId,muted);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String chatId = dataSnapshot.getKey();
                ChatInfo info = userChatsInfo.get(chatId);
                if(info!=null){
                    info.wasRemovedByOwner();
                    userChatsInfo.remove(info);
                }
                // TODO notify chat info update/change?
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        
    }

    private void newChatWasAddedToTheUserChatsList(final String chatId, final Boolean isMuted) {
        // TODO Get chat infos async?
        DatabaseReference chatRef = imsChatsInfoRef.child(chatId);
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatInfo info = dataSnapshot.getValue(ChatInfo.class);
                if(info!=null){
                    info.setMuted(isMuted);
                }
                boolean iAmIn = false;
                for(String uin : info.getUsersIds()){
                    if(userId.equals(uin)){
                        iAmIn = true;
                        break;
                    }
                }
                if(iAmIn){
                    if(userChatsInfo.get(chatId).getId()!=null) {
//                        self.userChatsInfo[chatInfo!.chatId]?.setEqualTo(chatInfo!)
//                        //print ("*** Got new chat with key that is already set")
                        //TODO not sure whats going on up here
                    } else {
                        userChatsInfo.put(chatId,info);
                    }
                    info.loadChatUsers();
                    info.loadMessages();
                } else {
                  if(userChatsInfo.get(chatId).getId()!=null){
                      userChatsInfo.get(chatId).wasRemovedByOwner();
                      userChatsInfo.remove(chatId);
                      // TODO notify chat info update/change?
                  } else {
                      info.wasRemovedByOwner();
                  }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }


    /**
     * createNewChat create a new chat according to the given chat info. the func will assign a new chat ID
     * to the new chat and will store it in the database.
     * you need to listen to notifyCreateNewChatSuccess to get  a success indication
     *
     * @param  chatInfo - the chat info to create (do not set the chat id)
     * @return void
     */

    public void createNewChat(ChatInfo chatInfo){

    }


    /***
     *
     *          DO NOT USE THE FUNCTIONS BELOW DIRECTLY! USE CHATINFO API
     *
     **/

    //you shouldnt use this function directly, call update chat on the chatInfo object
    public void updateChat(ChatInfo chatInfo){ }

    //you shouldnt use this function directly, call join chat on the chatInfo object
    public void joinChat(ChatInfo chatInfo, String uid){}

    //you shouldnt use this function directly, call delete user on the chatInfo object
    public void deleteUserFromChat(ChatInfo chatInfo, String uid){}

    //you shouldnt use this function directly, call delete on the chatInfo object
    public void deleteChat(ChatInfo chatInfo){}

    //you shouldnt use this function !!
    public void removeChatFromChatList(ChatInfo chatInfo){}

    /**
     * // IMS
     **/

    // do not use this function, call the chat info one!
    public void imsSendMessageToChat(ChatInfo chatInfo,ImsMessage message){}

    //use chat notifyChatUpdate signal to track chat messages!
    private void imsSetMessageObserverForChat(ChatInfo chatInfo){}

    // load next page of messages
    private void imsLoadNextPageOfMessages(ChatInfo chatInfo /*, completeB:@escaping (([ImsMessage])->Void ) */){}

    //use chat notifyChatUpdate signal to track chat messages!
    private void imsObserveMessageStatusChange(ChatInfo chatInfo){}

    // do not use this function, call the chat info one!
    public void imsMarkMessageAsRead(ChatInfo chatInfo, ImsMessage message){}

    // do not use this function, call the chat info one!
    public void setUserIsTypingValue(boolean val, String senderId, String chatId){}
    // do not use this function, call the chat info one!
    // add the given uid to the chat black list
    public void blockUserFromJoinningChat(String userIdToBlock){}

    // do not use this function, call the chat info one!
    // remove the given uid from the chat black list
    public void unblockUserInThisChat(ChatInfo chatInfo, String userIdToBlock){}

    // do not use this function, call the chat info one!
    public void setMuteChat(ChatInfo chatInfo,boolean isMuted){}

    public String getUserId() {
        return userId;
    }

    public void removeChatObservers(ChatInfo chatInfo) {

    }
}