package tv.sportssidekick.sportssidekick.model.im;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Filip on 12/7/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ImModel {

    private String userId;
    private DatabaseReference imsChatsRef;
    private DatabaseReference imsChatsInfoRef;
    private DatabaseReference imsChatsMessagesRef;
    private DatabaseReference imsPublicChatsIndexRef;
    private DatabaseReference imsUserChatsIndexRef;
    private DatabaseReference imsNotificationQueueRef;
    private ChatInfo userChatsInfo;

    private ImModel() {
        FirebaseDatabase ref = FirebaseDatabase.getInstance();
        imsChatsRef = ref.getReference("imsChats"); //root of all chats
        imsChatsInfoRef = imsChatsRef.child("chatsInfo");
        imsChatsMessagesRef = imsChatsRef.child("chatsMessages");
        imsPublicChatsIndexRef = imsChatsRef.child("publicChatsIndex");
        imsUserChatsIndexRef = imsChatsRef.child("usersChatsIndex");//root for all user chat activity
        imsNotificationQueueRef = ref.getReference("queue-notify-im/tasks"); // notification queue
    }

    /////////////////////////////////////
    ///   IMS   API  - Chat Info API  ///
    /////////////////////////////////////

    /**
     * return a list of all user chats, this list is loaded adynchronousely and gets updates when changed
     * you need to listen to notifyChatsInfoUpdates to get the updates in the list.
     *
     * @return [String : ChatInfo] chats info list
     */

    public ChatInfo getUserChats() {
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

    }

    private void loadUserChats() {

    }

    private void newChatWasAddedToTheUserChatsList(String chatId, Boolean isMuted) {

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
    private void updateChat(ChatInfo chatInfo){ }

    //you shouldnt use this function directly, call join chat on the chatInfo object
    private void joinChat(ChatInfo chatInfo, String uid){}

    //you shouldnt use this function directly, call delete user on the chatInfo object
    private void deleteUserFromChat(ChatInfo chatInfo, String uid){}

    //you shouldnt use this function directly, call delete on the chatInfo object
    private void deleteChat(ChatInfo chatInfo){}

    //you shouldnt use this function !!
    private void removeChatFromChatList(ChatInfo chatInfo){}

    /**
     * // IMS
     **/

    // do not use this function, call the chat info one!
    private void imsSendMessageToChat(ChatInfo chatInfo,ImsMessage message){}

    //use chat notifyChatUpdate signal to track chat messages!
    private void imsSetMessageObserverForChat(ChatInfo chatInfo){}

    // load next page of messages
    private void imsLoadNextPageOfMessages(ChatInfo chatInfo /*, completeB:@escaping (([ImsMessage])->Void ) */){}

    //use chat notifyChatUpdate signal to track chat messages!
    private void imsObserveMessageStatusChange(ChatInfo chatInfo){}

    // do not use this function, call the chat info one!
    private void imsMarkMessageAsRead(ChatInfo chatInfo,ImsMessage message){}

    // do not use this function, call the chat info one!
    private void setUserIsTypingValue(boolean val, String senderId , String chatId){}
    // do not use this function, call the chat info one!
    // add the given uid to the chat black list
    private void blockUserFromJoinningChat(String userIdToBlock){}

    // do not use this function, call the chat info one!
    // remove the given uid from the chat black list
    private void unblockUserInThisChat(ChatInfo chatInfo, String userIdToBlock){}

    // do not use this function, call the chat info one!
    private void setMuteChat(ChatInfo chatInfo,boolean isMuted){}

}