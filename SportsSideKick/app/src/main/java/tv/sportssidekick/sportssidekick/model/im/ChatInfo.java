package tv.sportssidekick.sportssidekick.model.im;

import android.text.TextUtils;

import java.util.List;

import tv.sportssidekick.sportssidekick.model.FirebseObject;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.UserInfo;

/**
 * Created by Filip on 12/7/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ChatInfo extends FirebseObject {

    private String name;
    private List<String> usersIds;
    private String avatarUrl;
    private String owner;
    private boolean isPublic;
    private List<String> userIdsBlackList;
    private List<ImsMessage> messages;
    private boolean isMuted;
    private List<UserInfo> chatUsers;


    public ChatInfo(String name, List<String> userIds, String avatarUrl, boolean isPublic) {
        this.setName(name);
        this.setUsersIds(userIds);
        this.setAvatarUrl(avatarUrl);
        this.setPublic(isPublic);
    }

    /**
     * Get the chat title (name), if non is set and this chat between two users it returns
     * the second user nic name
     *
     * @return the chat name
     */
    public String getChatTitle(){
        if (!TextUtils.isEmpty(getName())){
            return getName();
        }else{
            if (getUsersIds().size() == 2){
                if(usersIds.get(0)!=ImModel.getInstance().getUserId()){
                    String nic = Model.getInstance().getCachedUserInfoById(usersIds.get(0)).getNicName();
                    if(nic!=null){
                        return nic;
                    }
                } else {
                    String nic = Model.getInstance().getCachedUserInfoById(usersIds.get(1)).getNicName();
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
     *
     * @return avatar url
     */
    public String getChatAvatarUrl(){
        if (!TextUtils.isEmpty(getAvatarUrl())){
            return getAvatarUrl();
        }else{
            if (getUsersIds().size() == 2){
                if(usersIds.get(0)!=ImModel.getInstance().getUserId()){
                    String avatar = Model.getInstance().getCachedUserInfoById(usersIds.get(0)).getAvatarUrl();
                    if(avatar!=null){
                        return avatar;
                    }
                } else {
                    String avatar = Model.getInstance().getCachedUserInfoById(usersIds.get(1)).getAvatarUrl();
                    if(avatar!=null){
                        return avatar;
                    }
                }
            }
        }
        return "";
    }

    /**
     * Array of the chat users info
     **/

    // dont use that, it is called on login
    public void loadChatUsers(){
//        chatUsers = [UserInfo]()
//        for uid in self.usersIds{
//            Model.instance.getUserInfoById(uid){(userInfo) in
//                self.chatUsers!.append(userInfo!)
//                if(self.chatUsers!.count == self.usersIds.count){
//                    callback();
//                }
//            }
//        }
    }


    /**
     * Do NOT use this function
     * Load all chat data ioncluding messages and sets the observers on chat message changes
     * This func must be called once to follow the chat messages
     * the chat messages will be returned by listenning to the following events:
     *   notifyChatUpdate - will return an updated array of the chat messages when a new message
     *                      is received or message status was changed
     *   notifyUserIsTyping - will return an array of the users which are currently typing
     *
     * @return void
     */
    public void loadMessages(){
//        if (AddMessagelistener == nil){
//            self.messages = [ImsMessage]()
//
//            AddMessagelistener = ImModel.instance.chatMessages.on(self){(msg) in
//                self.messages?.append(msg)
//                print("--- chat info - got message add")
//                self.notifyChatUpdate.emit()
//
//            }
//
//            ChangedMessagelistener = ImModel.instance.messageUpdate.on(self){(msg) in
//                for index in 0...self.messages!.count-1{
//                    if (self.messages![index].id == msg.id){
//                        self.messages![index] = msg
//                    }
//                }
//                print("--- chat info - got message changed")
//                self.notifyChatUpdate.emit()
//            }
//
//            ImModel.instance.imsSetMessageObserverForChat(self)
//            ImModel.instance.imsObserveMessageStatusChange(self)
//            ImModel.instance.imsUserTypingObserverForChat(sender: ImModel.instance.userId!, inChatId: self.chatId) { (isTyping) in
//                self.notifyUserIsTyping.emit(isTyping)
//            }
//        }
    }

    /**
     * Load Message history, this func load the previusly archived messages in this chat
     *
     * @return void
     */
    public void loadPreviouseMessagesPage(){
//        ImModel.instance.imsLoadNextPageOfMessages(self) { (msgs) in
//            self.messages = self.messages! + msgs
//            self.notifyChatUpdate.emit()
//        }
    }

    /**
     * Send message to the chat
     *
     * @param  message to send
     * @return void
     */
    public void sendMessage(ImsMessage message){
           ImModel.getInstance().imsSendMessageToChat(this, message);
    }

    /**
     * Get the number of uread messages by the current user in this chat
     *
     * @return unread message count
     */
    public int unreadMessageCount(){
        int count = 0;
        String uid = ImModel.getInstance().getUserId();
        if (messages == null){
            // ("*** error need to load chat messages befor asking for unreadMessageCount")
            return -1;
        }
        for(ImsMessage message : messages){
        if (!message.getSenderId().equals(uid) && !message.isReadFlag()){
            count += 1;
        }
        }
        return count;
    }

    /**
     * Mark this message status as read for this user
     *
     * @param  message to update
     * @return void
     */
    public void markMessageAsRead(ImsMessage message){
        ImModel.getInstance().imsMarkMessageAsRead(this, message);
    }


    public void addUser(UserInfo uinfo){
        if(owner == ImModel.getInstance().getUserId()){
            usersIds.add(uinfo.getUserId());
            chatUsers.add(uinfo);
            updateChatInfo();
        }else{
            // ("*** Error - cant add user to a chat that you are not the owner of!")
        }
    }

    /**
     * Update the chat info, this is good for update the name and avatar, do not use this
     * function to update users!
     *
     * @return void
     */
    public void updateChatInfo(){
        ImModel.getInstance().updateChat(this);
    }

    /**
     * Delete a chat! this will be p[erformed inly if the current user is the chat owner
     * once deleted it will remove the chat from all user following this chat
     *
     * @return void
     */
    public void deleteChat(){
        if (owner == ImModel.getInstance().getUserId()){
            for(String uid : usersIds) {
                ImModel.getInstance().deleteUserFromChat(this,uid);
            }
            ImModel.getInstance().deleteChat(this);
        }else{// if im not the owner i remove myself from the chat
            ImModel.getInstance().removeChatFromChatList(this);
            ImModel.getInstance().deleteUserFromChat(this,ImModel.getInstance().getUserId());
        }
        messages.clear();
        // TODO notifyChatUpdate.emit()
    }

    /**
     * This user was removed from this chat by the chat owner
     *
     * @return void
     */
    public void wasRemovedByOwner(){
        ImModel.getInstance().removeChatObservers(this);
        messages.clear();
        // TODO notifyChatUpdate.emit()
    }

    /**
     * Remove a user from this chat, only available if you are the owner and you are not removing yourself
     *
     * @return void
     */
    public void removeUserFromChat(String uid){
        if (owner == ImModel.getInstance().getUserId() && uid != ImModel.getInstance().getUserId()){
            boolean shouldRemove = false;
            for(int i = 0; i< usersIds.size()-1; i++){
                if (usersIds.get(i).equals(uid)){
                    usersIds.remove(i);
                    shouldRemove = true;
                    break;
                }
            }
            for(int i = 0; i< chatUsers.size()-1; i++){
                if (chatUsers.get(i).getUserId().equals(uid)){
                    chatUsers.remove(i);
                    break;
                }
            }
            if(shouldRemove){
                ImModel.getInstance().deleteUserFromChat(this,uid);
            }
        }
    }

    /**
     * Join a public chat, this func add the current user to this chat if this chat is a public chat.
     *
     * @return void
     */
    public void joinChat(){
        if(isPublic && !isUserBlockedFromThisChat(ImModel.getInstance().getUserId())){
            ImModel.getInstance().joinChat(this, ImModel.getInstance().getUserId());
        }
    }

    /**
     * checks if a user is blocked
     *
     * @return blocked
     */
    public boolean isUserBlockedFromThisChat(String userId){
        boolean blocked = false;
        if(isPublic && userIdsBlackList != null){
            for (String id : userIdsBlackList){
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
        ImModel.getInstance().setUserIsTypingValue(val, ImModel.getInstance().getUserId(), getId());
    }

    /**
     * Block user, this func block the given user from joinning a public chat, you must be the owner
     * of the chat to perform this operation
     *
     * @param  userId  user ID to block
     * @return void
     */
    public void blockUserFromJoinningThisChat(String userId){
        if(isPublic && owner == ImModel.getInstance().getUserId()){
            //first remove this user from this chat if he is a member
            removeUserFromChat(userId);
            //add the user to the black list
            ImModel.getInstance().blockUserFromJoinningChat(userId);
        }
    }
    /**
     * Un Block user, this func unblock the given user in this public chat, you must be the owner
     * of the chat to perform this operation
     *
     * @param  userId user ID to unblock
     * @return void
     */
    public void unblockUserInThisChat(String userId){
        if(isPublic && owner == ImModel.getInstance().getUserId()){
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
     * @return void
     */
    public void setMuteChat(boolean isMuted){
        this.setMuted(isMuted);
        ImModel.getInstance().setMuteChat(this,isMuted);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getUsersIds() {
        return usersIds;
    }

    public void setUsersIds(List<String> usersIds) {
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

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public List<String> getUserIdsBlackList() {
        return userIdsBlackList;
    }

    public void setUserIdsBlackList(List<String> userIdsBlackList) {
        this.userIdsBlackList = userIdsBlackList;
    }

    public List<ImsMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ImsMessage> messages) {
        this.messages = messages;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public List<UserInfo> getChatUsers() {
        return chatUsers;
    }

    public void setChatUsers(List<UserInfo> chatUsers) {
        this.chatUsers = chatUsers;
    }
}
