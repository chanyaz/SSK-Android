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
    private HashMap<String, ChatInfo> userChatsInfo;
    private List<ChatInfo> publicChatsInfo;

    private ImModel() {
        FirebaseDatabase ref = FirebaseDatabase.getInstance();
        DatabaseReference imsChatsRef = ref.getReference("imsChats"); //root of all chats
        imsChatsInfoRef = imsChatsRef.child("chatsInfo");
        imsChatsMessagesRef = imsChatsRef.child("chatsMessages");
        imsPublicChatsIndexRef = imsChatsRef.child("publicChatsIndex");
        imsUserChatsIndexRef = imsChatsRef.child("usersChatsIndex");//root for all user chat activity
        imsNotificationQueueRef = ref.getReference("queue-notify-im/tasks"); // notification queue
        userChatsInfo = new HashMap<>();
        publicChatsInfo = new ArrayList<>();
        userId = Model.getInstance().getUserInfo().getUserId();
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
        userChatsInfo.clear();
        publicChatsInfo = null;
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
                                if(chatInfo!=null){
                                    userChatsInfo.put(chatId,chatInfo);
                                    if(chatInfo.isPublic()&& !chatInfo.isUserBlockedFromThisChat(getUserId())){
                                        publicChatsInfo.add(chatInfo);
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
        Log.d(TAG, "userId: " + getUserId() );
        userChatsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String chatId = dataSnapshot.getKey();
                Log.d(TAG, "Child added: " + chatId );
                Log.d(TAG, "Child added: " + s );
                Log.d(TAG, "Child added: " + dataSnapshot.getValue().toString() );
                boolean muted = MUTE.equals(dataSnapshot.getValue(String.class));
                newChatWasAddedToTheUserChatsList(chatId,muted);
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String chatId = dataSnapshot.getKey();
                ChatInfo info = userChatsInfo.get(chatId);
                if(info!=null){
                    info.wasRemovedByOwner();
                    userChatsInfo.remove(info);
                }
                EventBus.getDefault().post(new FirebaseEvent("Chat removed.", FirebaseEvent.Type.CHAT_REMOVED, chatId));
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void newChatWasAddedToTheUserChatsList(final String chatId, final Boolean isMuted) {
        DatabaseReference chatRef = imsChatsInfoRef.child(chatId);
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatInfo info = dataSnapshot.getValue(ChatInfo.class);
                if(info!=null){
                    info.setMuted(isMuted);
                }
                boolean iAmIn = false;
                for(String uin : info.getUsersIds().keySet()){
                    if(getUserId().equals(uin)){
                        iAmIn = true;
                        break;
                    }
                }
                ChatInfo chatInfo = userChatsInfo.get(chatId);
                if (chatInfo != null) {
                    if(iAmIn){
                        EventBus.getDefault().post(new FirebaseEvent("Chat detected.", FirebaseEvent.Type.USER_CHAT_DETECTED, chatId));
                        if(chatInfo.getId()!=null) {
                            chatInfo.setEqualTo(info); // *** Got new chat with key that is already set
                        } else {
                            userChatsInfo.put(chatId,info);
                        }
                        info.loadChatUsers();
                        info.loadMessages();
                    } else {
                        EventBus.getDefault().post(new FirebaseEvent("Removed from chat.", FirebaseEvent.Type.CHAT_REMOVED, chatId));
                        if(chatInfo.getId()!=null){
                            chatInfo.wasRemovedByOwner();
                            userChatsInfo.remove(chatId);
                        } else {
                            info.wasRemovedByOwner();
                        }
                    }
                } else {
                    Log.d(TAG, "Chat is null!");
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
        userChatsInfo.put(chatInfo.getId(),chatInfo);
        chatInfo.loadChatUsers();
        chatInfo.loadMessages();
        if(chatInfo.isPublic()){
            DatabaseReference publicIndexRef  = imsPublicChatsIndexRef.child(chatInfo.getId());
            publicIndexRef.setValue(true);
        }
        EventBus.getDefault().post(new FirebaseEvent("Chat created.", FirebaseEvent.Type.CHAT_CREATED, key));
    }


    /***
     *
     *          DO NOT USE THE FUNCTIONS BELOW DIRECTLY! USE CHATINFO API
     *
     **/

    //you shouldnt use this function directly, call update chat on the chatInfo object
    public void updateChat(ChatInfo chatInfo){
        DatabaseReference chatRef = imsChatsInfoRef.child(chatInfo.getId());
        chatRef.setValue(chatInfo);
        for(String uid : chatInfo.getUsersIds().keySet()){
            DatabaseReference userChatsRef = imsUserChatsIndexRef.child(uid).child(chatInfo.getId());
            userChatsRef.setValue("notify");
        }
    }

    //you shouldnt use this function directly, call join chat on the chatInfo object
    public void joinChat(ChatInfo chatInfo, String uid){
        DatabaseReference chatUserRef = imsChatsInfoRef.child(chatInfo.getId()).child("usersIds").child(uid);
        chatUserRef.setValue(true);
        DatabaseReference userChatsRef = imsUserChatsIndexRef.child(uid).child(chatInfo.getId());
        userChatsRef.setValue("notify");
    }

    //you shouldnt use this function directly, call delete user on the chatInfo object
    public void deleteUserFromChat(ChatInfo chatInfo, String uid){
        DatabaseReference userChatsRef = imsUserChatsIndexRef.child(uid).child(chatInfo.getId());
        userChatsRef.removeValue();

        //delete the user from chat info users list
        DatabaseReference chatRef = imsChatsInfoRef.child(chatInfo.getId()).child("usersIds").child(uid);
        chatRef.removeValue();
    }

    //you shouldnt use this function directly, call delete on the chatInfo object
    public void deleteChat(ChatInfo chatInfo){
        removeChatFromChatList(chatInfo);
        DatabaseReference chatRef = imsChatsInfoRef.child(chatInfo.getId());
        chatRef.removeValue();
    }

    //you shouldnt use this function !!
    public void removeChatFromChatList(ChatInfo chatInfo){
        userChatsInfo.remove(chatInfo.getId());
    }

    /**
     * // IMS
     **/

    // do not use this function, call the chat info one!
    public void imsSendMessageToChat(ChatInfo chatInfo,ImsMessage message){
        // create a child reference with a unique key.
        DatabaseReference messageRef = imsChatsInfoRef.child(chatInfo.getId()).child("messages").push();
        message.setId(messageRef.getKey());
        messageRef.setValue(message);

        UserInfo uinfo = Model.getInstance().getCachedUserInfoById(getUserId());
        if(TextUtils.isEmpty(message.getText())){
            message.setText("");
        }

        NotificationMessage notificationQueueMessage = new NotificationMessage();
        notificationQueueMessage.setChatId(chatInfo.getId());
        notificationQueueMessage.setNewIM(true);
        notificationQueueMessage.setSenderId(getUserId());
        notificationQueueMessage.setSenderName(uinfo.getNicName());
        notificationQueueMessage.setMessage(message.getText());

        DatabaseReference notificationRef = imsNotificationQueueRef.push();
        notificationRef.setValue(notificationRef);
    }

    private final static int K_MESSAGES_PER_PAGE = 5;
    //use chat notifyChatUpdate signal to track chat messages!
    private void imsSetMessageObserverForChat(final ChatInfo chatInfo){
        //Load the first K_MESSAGES_PER_PAGE messages
        Query messageQuerry = imsChatsMessagesRef.child(chatInfo.getId())
                .child("messages").orderByKey().limitToLast(K_MESSAGES_PER_PAGE);

        messageQuerry.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               String lastKey = null;
                for(DataSnapshot child : dataSnapshot.getChildren()){
                   ImsMessage message = child.getValue(ImsMessage.class);
                    EventBus.getDefault().post(new FirebaseEvent("New message detected.", FirebaseEvent.Type.NEW_MESSAGE, message));
                   lastKey = child.getKey();
               }
                if (lastKey == null){
                    lastKey = "";
                }
                final String lastKeyFinal = lastKey;
                Query messagesRef = imsChatsMessagesRef.child(chatInfo.getId()).child("messages").limitToLast(1);
                messagesRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (!dataSnapshot.getKey().equals(lastKeyFinal)){
                            ImsMessage message = dataSnapshot.getValue(ImsMessage.class);
                            EventBus.getDefault().post(new FirebaseEvent("New message added.", FirebaseEvent.Type.NEW_MESSAGE_ADDED, message));
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
    public void imsLoadNextPageOfMessages(ChatInfo chatInfo){
        List<ImsMessage> messages = chatInfo.getMessages();
        String lastMessageId = messages.get(0).getId();
        Query messagesQuery = imsChatsMessagesRef.child(chatInfo.getId()).child("messages")
                .orderByKey().limitToLast(K_MESSAGES_PER_PAGE+1).endAt(lastMessageId);
        messagesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ImsMessage> messagesNewPage = new ArrayList<>();
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    ImsMessage message = snap.getValue(ImsMessage.class);
                    messagesNewPage.add(message);
                }
                EventBus.getDefault().post(new FirebaseEvent("Next messages page loaded.", FirebaseEvent.Type.NEXT_PAGE_LOADED, messagesNewPage));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    //use chat notifyChatUpdate signal to track chat messages!
    public void imsObserveMessageStatusChange(ChatInfo chatInfo){
        DatabaseReference msgref = imsChatsMessagesRef.child(chatInfo.getId()).child("messages");
        msgref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ImsMessage message = dataSnapshot.getValue(ImsMessage.class);
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
    public void imsMarkMessageAsRead(ChatInfo chatInfo, ImsMessage message){
        DatabaseReference msgref = imsChatsMessagesRef.child(chatInfo.getId()).child("messages").child(message.getId()).child("wasReadBy");
        HashMap<String, Object> newValue = new HashMap<>();
        newValue.put(getUserId(),true);
        msgref.updateChildren(newValue);
    }

    // do not use this function, call the chat info one!
    public void setUserIsTypingValue(boolean val, String senderId, String chatId){
        DatabaseReference typingIndicatorRef = imsChatsMessagesRef.child(chatId).child("typing").child(senderId);
        typingIndicatorRef.setValue(val);
        typingIndicatorRef.onDisconnect().removeValue();
    }
    // do not use this function, call the chat info one!
    // add the given uid to the chat black list
    public void blockUserFromJoinningChat(ChatInfo chatInfo, String userIdToBlock){
        DatabaseReference chatInfoRef = imsChatsMessagesRef.child(chatInfo.getId())
                .child("usersIdsBlackList").child(userIdToBlock);
        chatInfoRef.setValue(true);
    }

    // do not use this function, call the chat info one!
    // remove the given uid from the chat black list
    public void unblockUserInThisChat(ChatInfo chatInfo, String userIdToBlock){
        DatabaseReference chatInfoRef = imsChatsMessagesRef.child(chatInfo.getId())
                .child("usersIdsBlackList").child(userIdToBlock);
        chatInfoRef.removeValue();
    }

    // do not use this function, call the chat info one!
    public void setMuteChat(ChatInfo chatInfo,boolean isMuted){
        String mute = "notify";
        if (isMuted){
            mute = "mute";
        }
        DatabaseReference userChatRef = imsUserChatsIndexRef.child(getUserId()).child(chatInfo.getId());
        userChatRef.setValue(mute);
    }

    public String getUserId() {
        return userId;
    }
}