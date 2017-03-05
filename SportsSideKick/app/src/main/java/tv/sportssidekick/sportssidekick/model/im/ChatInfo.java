package tv.sportssidekick.sportssidekick.model.im;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.Exclude;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.UserInfo;
import tv.sportssidekick.sportssidekick.service.GameSparksEvent;

/**
 * Created by Filip on 12/7/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ChatInfo {

    private static final String TAG = "CHAT INFO";

    private String chatId;
    private String name;
    private ArrayList<String> usersIds;
    private String avatarUrl;
    private String owner;
    private boolean isPublic = false;
    private ArrayList<String> blackList;  //only available for public chats
    private ArrayList<ImsMessage> messages;

    private boolean isMuted = false;

    private String currentUserId;

    public ChatInfo(String name, ArrayList<String> userIds, String avatarUrl, boolean isPublic) {
        this.setName(name);
        this.setUsersIds(userIds);
        this.setAvatarUrl(avatarUrl);
        this.setIsPublic(isPublic);
        // currentUserId = ImModel.getInstance().getUserId();
    }

    public ChatInfo() {
        // currentUserId = ImModel.getInstance().getUserId();
    }


    void setEqualTo(ChatInfo info) {
        setChatId(info.getChatId());
        setName(info.getName());
        setUsersIds(info.getUsersIds());
        setAvatarUrl(info.getAvatarUrl());
        setOwner(info.getOwner());
        setIsPublic(info.getIsPublic());
        setBlackList(info.getBlackList());
        setIsMuted(info.getIsMuted());
        if(info.getMessages()!=null){
            setMessages(info.getMessages());
        } else {
            setMessages(new ArrayList<ImsMessage>());
        }

       // currentUserId = ImModel.getInstance().getUserId();
    }

    /**
     * Get the chat title (name), if non is set and this chat between two users it returns
     * the second user nic name
     *
     * @return the chat name
     */
    @Exclude
    public String getChatTitle(){
        if (!TextUtils.isEmpty(getName())){
            return getName();
        }else{
            if (getUsersIds().size() == 2){
                String userId = currentUserId;
                String firstUserId = getUsersIds().get(0);
                String secondUserId = getUsersIds().get(1);
                if(!userId.equals(firstUserId)){
                    String nic = Model.getInstance().getCachedUserInfoById(firstUserId).getNicName();
                    if(nic!=null){
                        return nic;
                    }
                } else {
                    String nic = Model.getInstance().getCachedUserInfoById(secondUserId).getNicName();
                    if(nic!=null){
                        return nic;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get the chat avatar URL, if non is set and this chat between two users it returns
     * the second user avatar url
     * @return avatar url
     */
    @Exclude
    public String getChatAvatarUrl(){
        if (!TextUtils.isEmpty(getAvatarUrl())){
            return getAvatarUrl();
        }else{
            if (getUsersIds().size() == 2){
                String userId = currentUserId;
                String firstUserId = getUsersIds().get(0);
                String secondUserId = getUsersIds().get(1);
                String avatar;
                if(!userId.equals(firstUserId)){
                    avatar = Model.getInstance().getCachedUserInfoById(firstUserId).getAvatarUrl();
                } else {
                    avatar = Model.getInstance().getCachedUserInfoById(secondUserId).getAvatarUrl();
                }
                if(avatar!=null){
                    return avatar;
                }
            }
        }
        return "";
    }

    // dont use that, it is called on login
    void loadChatUsers(){
//        Log.d(TAG, "Requesting Load of chat users");
//        EventBus.getDefault().register(this);
//        final ArrayList<Task<UserInfo>> tasks = new ArrayList<>();
//        for(String uid : getUsersIds()){
//            Log.d(TAG, "Getting User Info for chat user " + uid);
//            Task task = Model.getInstance().getUserInfoById(uid);
//            tasks.add(task);
//        }
//        Task allUsersTask = Tasks.whenAll(tasks);
//        allUsersTask.addOnSuccessListener(
//            new OnSuccessListener() {
//                @Override
//                public void onSuccess(Object o) {
//                    Log.e(TAG, "ALL USERS DOWNLOADED!");
//                    for(Task t : tasks){
//                        UserInfo info = (UserInfo) t.getResult();
//                        Log.e(TAG, "USER ID : " + info.getUserId());
//                    }
//                }
//            }
//        );
    }

    /**
     * Do NOT use this function
     * Load all chat data including messages and sets the observers on chat message changes
     * This func must be called once to follow the chat messages
     * the chat messages will be returned by listenning to the following events:
     *   notifyChatUpdate - will return an updated array of the chat messages when a new message
     *                      is received or message status was changed
     *   notifyUserIsTyping - will return an array of the users which are currently typing
     */
    void loadMessages(){
        messages = new ArrayList<ImsMessage>();
        ImModel.getInstance().imsSetMessageObserverForChat(this);
        ImModel.getInstance().observeMessageStatusChange(this);
        ImModel.getInstance().imsUserTypingObserverForChat(Model.getInstance().getUserInfo().getUserId(), getChatId());
    }

    @Subscribe
    public void onNewMessagesEvent(GameSparksEvent event){
        ImsMessage message;
        if(getChatId().equals(event.getFilterId())){
            switch (event.getEventType()){
                case NEW_MESSAGE:
                    message = (ImsMessage) event.getData();
                    Log.d(TAG, "NEW MESSAGE EVENT : " + message.getId() + " for chat: " + getChatId());
                    messages.add(message);
                    break;
                case MESSAGE_UPDATED:
                    message = (ImsMessage) event.getData();
                    Log.d(TAG, "MESSAGE UPDATED EVENT : " + message.getId());
                    break;
                case NEW_MESSAGE_ADDED:
                    message = (ImsMessage) event.getData();
                    Log.d(TAG, "NEW MESSAGE ADDED EVENT : " + message.getId());
                    messages.add(message);
                    break;
                case NEXT_PAGE_LOADED:
                    ArrayList<ImsMessage> messagesNewPage = (ArrayList<ImsMessage>)event.getData();
                    for(ImsMessage m : messagesNewPage){
                        boolean exists = false;
                        for(ImsMessage mOld : messages){
                            if(mOld.getId().equals(m.getId())){
                                exists = true;
                            }
                        }
                        if(!exists){
                            Log.d(TAG,"Adding message to list: " + m.getId());
                            messages.add(m);
                        }
                    }

                    Collections.sort(messages, new Comparator<ImsMessage>() {
                        @Override
                        public int compare(ImsMessage lhs, ImsMessage rhs) {
                            return lhs.getTimestamp().compareTo(rhs.getTimestamp());
                        }
                    });
                    break;
            }
        }
    }


    @Subscribe // TODO REMOVE GENERICS
    public void onNewMessagesEvent(NewMessagesEvent event){
        messages.addAll(event.getValues());
        // TODO  notifyChatUpdate
    }

    @Subscribe
    public void onMessagesChangedEvent(ImsMessage changedMessage){
        for(ImsMessage message : messages){
            if(message.getId().equals(changedMessage.getId())){
                message = changedMessage;
                // TODO notifyChatUpdate
            }
        }
    }

    /**
     * Load Message history, this func load the previusly archived messages in this chat
     */
    public void loadPreviouseMessagesPage(){
        ImModel.getInstance().loadNextPageOfMessages(this);
    }



    /**
     * Send message to the chat
     * @param  message to send
     */
    public void sendMessage(ImsMessage message){
           ImModel.getInstance().imsSendMessageToChat(this, message);
    }

    /**
     * Get the number of uread messages by the current user in this chat
     * @return unread message count
     */
    public int unreadMessageCount(){
        int count = 0;
        String uid = currentUserId;
        if (messages == null){
            Log.d(TAG,"*** error need to load chat messages before asking for unreadMessageCount");
            return -1;
        }
        for(ImsMessage message : messages){
            if (!message.getSenderId().equals(uid) && !message.getReadFlag()){
                count += 1;
            }
        }
        return count;
    }

    /**
     * Mark this message status as read for this user
     * @param  message to update
     */
    public void markMessageAsRead(ImsMessage message){
        ImModel.getInstance().markMessageAsRead(this, message);
    }


    public void addUser(UserInfo uinfo){
        if(owner.equals(currentUserId)){
            getUsersIds().add(uinfo.getUserId());
            updateChatInfo();
        }else{
            // ("*** Error - cant add user to a chat that you are not the owner of!")
        }
    }

    //TODO Implement this
    public void addUserIfChatIsGlobal(UserInfo uinfo){
//        GSImsManager.instance.getGlobalChats { (chatsInfo) in
//            if (chatsInfo != nil){
//                for chatInfo in chatsInfo!{
//                if chatInfo.chatId == self.chatId {
//                    if chatInfo.usersIds.contains(uinfo.userId) {
//                        print("ERROR - User already added to Global chat")
//                    }else{
//                        self.loadChatUsers {
//                            self.usersIds.append(uinfo.userId)
//                            self.chatUsers!.append(uinfo)
//                            self.updateChatInfo()
//                        }
//                    }
//                }else{
//                    print("ERROR - Trying ot add user to not Global chat")
//                }
//                }
//            }
//        }
    }

    /**
     * Update the chat info, this is good for update the name and avatar, do not use this
     * function to update users!
     **/
    public void updateChatInfo(){
        ImModel.getInstance().updateChat(this);
    }

    /**
     * Delete a chat! this will be performed only if the current user is the chat owner
     * once deleted it will remove the chat from all user following this chat
     **/
    public void deleteChat(){
        if (owner.equals(currentUserId)){
            for(String uid : getUsersIds()) {
              //  ImModel.getInstance().deleteUserFromChat(this,uid);
            }
            ImModel.getInstance().deleteChat(this);
        }else{ // if im not the owner I remove myself from the chat
            ImModel.getInstance().removeChatInfoWithId(getChatId());
            //ImModel.getInstance().deleteUserFromChat(this,currentUserId);
        }
        messages.clear();
        EventBus.getDefault().post(new GameSparksEvent("Chat Deleted and processed.", GameSparksEvent.Type.CHAT_DELETED_PROCESSED, getChatId()));
    }

    /**
     * This user was removed from this chat by the chat owner
     **/
    public void wasRemovedByOwner(){
        if(messages!=null){
            messages.clear();
            EventBus.getDefault().post(new GameSparksEvent("This user was removed from this chat by the chat owner.", GameSparksEvent.Type.CHAT_REMOVED_PROCESSED, getChatId()));
        }
    }

    /**
     * Remove a user from this chat, only available if you are the owner and you are not removing yourself
     */
    public void removeUserFromChat(String uid){
        if (owner.equals(currentUserId) && !uid.equals(currentUserId)){
            boolean shouldRemove = false;
            for(String entry : getUsersIds()) {
                if (entry.equals(uid)) {
                    getUsersIds().remove(entry);
                    shouldRemove = true;
                    break;
                }
            }
            if(shouldRemove){
               // ImModel.getInstance().deleteUserFromChat(this,uid);
            }
        }
    }

    /**
     * Join a public chat, this func add the current user to this chat if this chat is a public chat.
     **/
    public void joinChat(){
        if(isPublic && !isUserBlockedFromThisChat(currentUserId)){
           // ImModel.getInstance().joinChat(this, currentUserId);
        }
    }

    /**
     * checks if a user is blocked
     * @return blocked
     */
    public boolean isUserBlockedFromThisChat(String userId){
        boolean blocked = false;
        if(isPublic && blackList != null){
            for (String id : blackList){
                if (id.equals(userId)){
                    blocked = true;
                }
            }
        }
        return blocked;
    }

    // set the state of typing of this user - should be switch on when starting to type and off after pressing send
    // use the notifyUserIsTyping to get the list of users that are currently typing
    public void setUserIsTyping(boolean val){
        ImModel.getInstance().setUserIsTypingValue(val, getChatId());
    }

    //TODO Implement this
//    var usersTypingInfo = [UserInfo]()
//    func updateUserIsTyping(_ userId:String, _ isTypingValue:Bool){
//        if isTypingValue{
//            if let uinfo = GSModel.instance.getCachedUserInfoById(userId){
//                usersTypingInfo.append(uinfo)
//            }else{
//                GSModel.instance.getUserInfoById(userId, callback: { (user) in
//                    // cache his data for the next time...
//                })
//            }
//        }else{
//            for (index, element) in usersTypingInfo.enumerated() {
//                if element.userId == userId {
//                    usersTypingInfo.remove(at: index)
//                    break;
//                }
//            }
//        }
//        notifyUserIsTyping.emit(usersTypingInfo)
//    }

    /**
     * Block user, this func block the given user from joinning a public chat, you must be the owner
     * of the chat to perform this operation
     *
     * @param  userId  user ID to block
     */
    public void blockUserFromJoinningThisChat(String userId){
        if(isPublic && owner.equals(currentUserId)){
            //first remove this user from this chat if he is a member
            removeUserFromChat(userId);
            //add the user to the black list
            ImModel.getInstance().blockUserFromJoiningChat(this, userId);
        }
    }

    /**
     * Un Block user, this func unblock the given user in this public chat, you must be the owner
     * of the chat to perform this operation
     *
     * @param  userId user ID to unblock
     */
    public void unblockUserInThisChat(String userId){
        if(isPublic && owner.equals(currentUserId)){
            //check if the user is actully blocked then unblock
            if (isUserBlockedFromThisChat(userId)){
                ImModel.getInstance().unblockUserInThisChat(this, userId);
            }
        }
    }

    /**
     * mute push notifications on this chat for this user
     *
     * @param  isMuted Bool
     */
    public void setMuteChat(boolean isMuted){
        this.setIsMuted(isMuted);
        ImModel.getInstance().setMuteChat(this,isMuted);
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getUsersIds() {
        return usersIds;
    }

    public void setUsersIds(ArrayList<String> usersIds) {
        this.usersIds = usersIds;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public ArrayList<String> getBlackList() {
        return blackList;
    }

    public void setBlackList(ArrayList<String> blackList) {
        this.blackList = blackList;
    }

    public ArrayList<ImsMessage> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<ImsMessage> messages) {
        this.messages = messages;
    }

    public boolean getIsMuted() {
        return isMuted;
    }

    public void setIsMuted(boolean muted) {
        isMuted = muted;
    }



}
