package tv.sportssidekick.sportssidekick.model.im;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.UserInfo;
import tv.sportssidekick.sportssidekick.service.FirebaseEvent;

/**
 * Created by Filip on 12/7/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ImModel {

    private static final String MUTE = "mute";
    private static final String TAG = "ImModel";
    private static ImModel instance;

    private String userId;
    private DatabaseReference imsChatsInfoRef;
    private DatabaseReference imsChatsMessagesRef;
    private DatabaseReference imsPublicChatsIndexRef;
    private DatabaseReference imsUserChatsIndexRef;
    private DatabaseReference imsNotificationQueueRef;
    private HashMap<String, ChatInfo> chatInfoCache;
    private List<ChatInfo> chatInfoCacheList;
    private List<ChatInfo> publicChatsInfo;

    public List<ChatInfo> getNonMemberPublicChatsInfo(){
        List<ChatInfo> filteredList = new ArrayList<>();
        for(ChatInfo info : publicChatsInfo){
            if(!info.getUsersIds().keySet().contains(getUserId())){
                filteredList.add(info);
            }
        }
        return filteredList;
    }

    private ImModel() {
        FirebaseDatabase ref = FirebaseDatabase.getInstance();
        DatabaseReference imsChatsRef = ref.getReference("imsChats"); //root of all chats
        imsChatsInfoRef = imsChatsRef.child("chatsInfo");
        imsChatsMessagesRef = imsChatsRef.child("chatsMessages");
        imsPublicChatsIndexRef = imsChatsRef.child("publicChatsIndex");
        imsUserChatsIndexRef = imsChatsRef.child("usersChatsIndex");//root for all user chat activity
        imsNotificationQueueRef = ref.getReference("queue-notify-im/tasks"); // notification queue
        chatInfoCache = new HashMap<>();
        chatInfoCacheList = new ArrayList<>();
        publicChatsInfo = new ArrayList<>();
        UserInfo userInfo = Model.getInstance().getUserInfo();
        if(userInfo!=null){
           userId = userInfo.getUserId();
        }

        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void userInfoListener(UserInfo info){
        userId = info.getUserId();
    }

    public static ImModel getInstance(){
        if(instance==null){
            instance = new ImModel();
        }
        return instance;
    }

    public void clear(){
        chatInfoCache.clear();
        chatInfoCacheList.clear();
        publicChatsInfo.clear();
    }

    public void reload(String userId){
        this.userId = userId;
        loadUserChats();
    }


    /////////////////////////////////////
    ///   IMS   API  - Chat Info API  ///
    /////////////////////////////////////

    /**
     * return a list of all user chats, this list is loaded asynchronously and gets updates when changed
     * you need to listen to notifyChatsInfoUpdates to get the updates in the list.
     *
     * @return [String : ChatInfo] chats info list
     */

//    public HashMap<String, ChatInfo> getUserChats() {
//        return chatInfoCache;
//    }

    public List<ChatInfo> getUserChatsList() {
        return chatInfoCacheList;
    }

    public ChatInfo getChatInfoById(String chatId){
        return  chatInfoCache.get(chatId);
    }

    boolean removeChatInfoWithId(String chatId){
        if(chatInfoCache.containsKey(chatId)){
            ChatInfo info = chatInfoCache.remove(chatId);
            chatInfoCacheList.remove(info);
            return true;
        }
        return false;
    }

    private void addChatInfoToCache(ChatInfo info){
        chatInfoCache.put(info.getId(),info);
        chatInfoCacheList.add(info);
    }
    /**
     * getAllPublicChats returns a list of all public chats in the system. you will need
     * to listen to notifyGetAllPublicChats to receive the updated list of public chats
     * this list does not include chats that the user is blocked from
     * <p>
     * cjw -- re-worked this slightly to fix issue where it's always returning a zero length array
     * and generally breaking because of race conditions with ASYNC Firebase requests
     */
    void getAllPublicChats() {
        imsPublicChatsIndexRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot==null){
                    return;
                }
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    if(child.getValue(Boolean.class)){
                        final String chatId = child.getKey();
                        DatabaseReference chatRef = imsChatsInfoRef.child(chatId);
                        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // TODO Rewrite logic for public chats
                                if(dataSnapshot.exists()){
                                    ChatInfo chatInfo = dataSnapshot.getValue(ChatInfo.class);
                                    chatInfo.setId(dataSnapshot.getKey());
                                    if(!chatInfo.isUserBlockedFromThisChat(getUserId())){
                                        publicChatsInfo.add(chatInfo);
                                        Log.d(TAG, "getAllPublicChats: " + chatId);
                                        newChatWasAddedToTheUserChatsList(chatId,false);
                                        EventBus.getDefault().post(new FirebaseEvent("Public chat detected.", FirebaseEvent.Type.PUBLIC_CHAT_DETECTED, chatInfo));
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void loadUserChats() {
        final DatabaseReference userChatsRef = imsUserChatsIndexRef.child(getUserId());
        Log.d(TAG, "userId: " + getUserId());
        getAllPublicChats();
        userChatsRef.removeEventListener(userChatsEventListener);
        userChatsRef.addChildEventListener(userChatsEventListener);
    }



    private ChildEventListener userChatsEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            String chatId = dataSnapshot.getKey();
            String value = dataSnapshot.getValue(String.class); // can be notify or mute
            Log.d(TAG, "Child added: " + chatId );
            Log.d(TAG, "Child added: " + s );
            Log.d(TAG, "Child added: " + value );
            newChatWasAddedToTheUserChatsList(chatId,MUTE.equals(value));
        }
        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String chatId = dataSnapshot.getKey();
            ChatInfo info = getChatInfoById(chatId);
            if(info!=null){
                info.wasRemovedByOwner();
                removeChatInfoWithId(chatId);
            }
            EventBus.getDefault().post(new FirebaseEvent("Chat removed.", FirebaseEvent.Type.CHAT_REMOVED, chatId));
        }
        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
        @Override
        public void onCancelled(DatabaseError databaseError) {}
    };


    private void newChatWasAddedToTheUserChatsList(final String chatId, final Boolean isMuted) {
        DatabaseReference chatRef = imsChatsInfoRef.child(chatId);
        Log.d(TAG, "New Chat Was Added To The User Chats List: " + chatId + " MUTED: " + isMuted);
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){ // chat removed
                    return;
                }
                ChatInfo info = dataSnapshot.getValue(ChatInfo.class);
                info.setId(dataSnapshot.getKey());
                info.setMuted(isMuted);
                boolean iAmIn = false;
                if(info.getUsersIds()!=null){
                    for(String uin : info.getUsersIds().keySet()){
                        if(getUserId().equals(uin)){
                            iAmIn = true;
                            break;
                        }
                    }
                } else {
                    Log.d(TAG,"There are no users in this chat, ignore it!");
                    return;
                }

                ChatInfo existingChatInfo = getChatInfoById(chatId);
                if(iAmIn){
                    Log.d(TAG, "User is in chat: " + chatId);
                    if(existingChatInfo != null && existingChatInfo.getId()!=null) {   // Got new chat with key that is already set
                        Log.d(TAG, "Chat is already in cache:" + chatId);
                        existingChatInfo.setEqualTo(info);
                    } else { // its not cached yet.
                        addChatInfoToCache(info);
                    }
                    EventBus.getDefault().post(new FirebaseEvent("Chat detected.", FirebaseEvent.Type.USER_CHAT_DETECTED, chatId));
                    Log.d(TAG, "Loading chat users and messages for " + chatId);
                    info.loadChatUsers();
                    info.loadMessages();
                } else {
                    Log.d(TAG, "User not in chat: " + chatId);
                    if(existingChatInfo != null && existingChatInfo.getId()!=null){
                        existingChatInfo.wasRemovedByOwner();
                        removeChatInfoWithId(chatId);
                    } else {
                        info.wasRemovedByOwner();
                    }
                    EventBus.getDefault().post(new FirebaseEvent("Removed from chat.", FirebaseEvent.Type.CHAT_REMOVED, chatId));
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
     */

    public void createNewChat(ChatInfo chatInfo){
        DatabaseReference newChatRef = imsChatsInfoRef.push();
        String key = newChatRef.getKey();
        chatInfo.setId(key);
        chatInfo.setOwner(getUserId());
        if(chatInfo.getUsersIds().size()==0){
            HashMap<String, Boolean> userIds = new HashMap<>();
            userIds.put(getUserId(), true);
            chatInfo.setUsersIds(userIds);
        }
        newChatRef.setValue(chatInfo);
        for(String uid : chatInfo.getUsersIds().keySet()){
            DatabaseReference userChatsRef = imsUserChatsIndexRef.child(uid).child(key);
            userChatsRef.setValue("notify");
        }
        addChatInfoToCache(chatInfo);
        chatInfo.loadChatUsers();
        chatInfo.loadMessages();
        if(chatInfo.getIsPublic()){
            DatabaseReference publicIndexRef  = imsPublicChatsIndexRef.child(chatInfo.getId());
            publicIndexRef.setValue(true);
        }
        EventBus.getDefault().post(new FirebaseEvent("Chat created.", FirebaseEvent.Type.CHAT_CREATED, key));
    }


    /***
     *          DO NOT USE THE FUNCTIONS BELOW DIRECTLY! USE CHATINFO API
     **/

    //you shouldnt use this function directly, call update chat on the chatInfo object
    void updateChat(ChatInfo chatInfo){
        DatabaseReference chatRef = imsChatsInfoRef.child(chatInfo.getId());
        chatRef.setValue(chatInfo);
        for(String uid : chatInfo.getUsersIds().keySet()){
            DatabaseReference userChatsRef = imsUserChatsIndexRef.child(uid).child(chatInfo.getId());
            userChatsRef.setValue("notify");
        }
    }

    //you shouldnt use this function directly, call join chat on the chatInfo object
    void joinChat(ChatInfo chatInfo, String uid){
        DatabaseReference chatUserRef = imsChatsInfoRef.child(chatInfo.getId()).child("usersIds").child(uid);
        chatUserRef.setValue(true);
        DatabaseReference userChatsRef = imsUserChatsIndexRef.child(uid).child(chatInfo.getId());
        userChatsRef.setValue("notify");
    }

    //you shouldnt use this function directly, call delete user on the chatInfo object
    void deleteUserFromChat(ChatInfo chatInfo, String uid){
        DatabaseReference userChatsRef = imsUserChatsIndexRef.child(uid).child(chatInfo.getId());
        userChatsRef.removeValue();

        //delete the user from chat info users list
        DatabaseReference chatRef = imsChatsInfoRef.child(chatInfo.getId()).child("usersIds").child(uid);
        chatRef.removeValue();
    }

    //you shouldnt use this function directly, call delete on the chatInfo object
    void deleteChat(ChatInfo chatInfo){
        removeChatInfoWithId(chatInfo.getId());
        DatabaseReference chatRef = imsChatsInfoRef.child(chatInfo.getId());
        chatRef.removeValue();
    }

    /**
     * // IMS
     **/

    // do not use this function, call the chat info one!
    void imsSendMessageToChat(ChatInfo chatInfo,ImsMessage message){
        // create a child reference with a unique key.
        DatabaseReference messageRef = imsChatsMessagesRef.child(chatInfo.getId()).child("messages").push();
        message.setId(messageRef.getKey());
        messageRef.setValue(message);

        UserInfo uinfo = Model.getInstance().getCachedUserInfoById(message.getSenderId());
        if(TextUtils.isEmpty(message.getText())){
            message.setText("");
        }

        NotificationMessage notificationQueueMessage = new NotificationMessage();
        notificationQueueMessage.setChatId(chatInfo.getId());
        notificationQueueMessage.setNewIM(true);
        notificationQueueMessage.setSenderId(message.getSenderId());
        notificationQueueMessage.setSenderName(uinfo.getNicName());
        notificationQueueMessage.setMessage(message.getText());

        DatabaseReference notificationRef = imsNotificationQueueRef.push();
        notificationRef.setValue(notificationQueueMessage);
    }

    private final static int K_MESSAGES_PER_PAGE = 25;
    //use chat notifyChatUpdate signal to track chat messages!
    void loadFirstPageOfMessagesForChat(ChatInfo chatInfo){
        Log.d(TAG, "Loading messages for " + chatInfo.getId());
        //Load the first K_MESSAGES_PER_PAGE messages
        Query messageQuery = imsChatsMessagesRef.child(chatInfo.getId()).child("messages").orderByKey().limitToLast(K_MESSAGES_PER_PAGE);
        final String chatInfoId = chatInfo.getId();
        messageQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               String lastKey = null;
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    ImsMessage message = child.getValue(ImsMessage.class);
                    String messageId = child.getKey();
                    message.setId(messageId);
                    message.initializeTimestamp();
                    FirebaseEvent fe = new FirebaseEvent("New message detected.", FirebaseEvent.Type.NEW_MESSAGE, message);
                    fe.setFilterId(chatInfoId);
                    EventBus.getDefault().post(fe);
                    lastKey = messageId;
               }
                if (lastKey == null){
                    lastKey = "";
                }
                final String lastKeyFinal = lastKey;
                // Add listener for new child in Chat
                Query messagesRef = imsChatsMessagesRef.child(chatInfoId).child("messages").limitToLast(1);
                messagesRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (!dataSnapshot.getKey().equals(lastKeyFinal)){
                            ImsMessage message = dataSnapshot.getValue(ImsMessage.class);
                            message.setId(dataSnapshot.getKey());
                            message.initializeTimestamp();
                            FirebaseEvent fe = new FirebaseEvent("New message added.", FirebaseEvent.Type.NEW_MESSAGE_ADDED, message);
                            fe.setFilterId(chatInfoId);
                            EventBus.getDefault().post(fe);
                        }
                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


    }

    // load next page of messages
    void imsLoadNextPageOfMessages(ChatInfo chatInfo){
        List<ImsMessage> messages = chatInfo.getMessages();
        String lastMessageId = messages.get(0).getId();
        Query messagesQuery = imsChatsMessagesRef.child(chatInfo.getId()).child("messages").orderByKey().limitToLast(K_MESSAGES_PER_PAGE+1).endAt(lastMessageId);
        final String chatInfoId = chatInfo.getId();
        messagesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ImsMessage> messagesNewPage = new ArrayList<>();
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    ImsMessage message = snap.getValue(ImsMessage.class);
                    message.setId(snap.getKey());
                    message.initializeTimestamp();
                    messagesNewPage.add(message);
                }
                FirebaseEvent fe = new FirebaseEvent("Next messages page loaded.", FirebaseEvent.Type.NEXT_PAGE_LOADED, messagesNewPage);
                fe.setFilterId(chatInfoId);
                EventBus.getDefault().post(fe);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    //use chat notifyChatUpdate signal to track chat messages!
    void observeMessageStatusChange(ChatInfo chatInfo){
        DatabaseReference msgref = imsChatsMessagesRef.child(chatInfo.getId()).child("messages");
        msgref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ImsMessage message = dataSnapshot.getValue(ImsMessage.class);
                message.setId(dataSnapshot.getKey());
                message.initializeTimestamp();
                EventBus.getDefault().post(new FirebaseEvent("Message is changed/updated.", FirebaseEvent.Type.MESSAGE_UPDATED, message));
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    // do not use this function, call the chat info one!
    void imsMarkMessageAsRead(ChatInfo chatInfo, ImsMessage message){
        DatabaseReference msgref = imsChatsMessagesRef.child(chatInfo.getId()).child("messages").child(message.getId()).child("wasReadBy");
        HashMap<String, Object> newValue = new HashMap<>();
        newValue.put(getUserId(),true);
        msgref.updateChildren(newValue);
    }

    // do not use this function, call the chat info one!
    void setUserIsTypingValue(boolean val, String senderId, String chatId){
        DatabaseReference typingIndicatorRef = imsChatsMessagesRef.child(chatId).child("typing").child(senderId);
        typingIndicatorRef.setValue(val);
        typingIndicatorRef.onDisconnect().removeValue();
    }

    // do not use this function, call the chat info one!
    // returnes the list of users info that are currently typing

    void imsUserTypingObserverForChat(String senderId,String chatId) {
        DatabaseReference typingIndicatorRef =  imsChatsMessagesRef.child(chatId).child("typing");
        typingIndicatorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<UserInfo> usersTyping = new ArrayList<>();
                HashMap<String,Boolean> temp2 = (HashMap<String, Boolean>) dataSnapshot.getValue();
                if(temp2!=null){
                    for(Map.Entry<String,Boolean> entry : temp2.entrySet()){
                        if(entry.getValue() && !entry.getKey().equals(userId)){
                            UserInfo info = Model.getInstance().getCachedUserInfoById(entry.getKey());
                            usersTyping.add(info);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }


    // do not use this function, call the chat info one!
    // add the given uid to the chat black list
    void blockUserFromJoinningChat(ChatInfo chatInfo, String userIdToBlock){
        DatabaseReference chatInfoRef = imsChatsMessagesRef.child(chatInfo.getId())
                .child("usersIdsBlackList").child(userIdToBlock);
        chatInfoRef.setValue(true);
    }

    // do not use this function, call the chat info one!
    // remove the given uid from the chat black list
    void unblockUserInThisChat(ChatInfo chatInfo, String userIdToBlock){
        DatabaseReference chatInfoRef = imsChatsMessagesRef.child(chatInfo.getId())
                .child("usersIdsBlackList").child(userIdToBlock);
        chatInfoRef.removeValue();
    }

    // do not use this function, call the chat info one!
    void setMuteChat(ChatInfo chatInfo,boolean isMuted){
        String mute = "notify";
        if (isMuted){
            mute = "mute";
        }
        DatabaseReference userChatRef = imsUserChatsIndexRef.child(getUserId()).child(chatInfo.getId());
        userChatRef.setValue(mute);
    }

    String getUserId() {
        return userId;
    }
}