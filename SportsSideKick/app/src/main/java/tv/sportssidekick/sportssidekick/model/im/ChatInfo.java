package tv.sportssidekick.sportssidekick.model.im;

import android.text.TextUtils;

import java.util.List;

import tv.sportssidekick.sportssidekick.model.UserInfo;

/**
 * Created by Filip on 12/7/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ChatInfo extends FirebseObject {

    String name;
    List<String> usersIds;
    String avatarUrl;
    String owner;
    boolean isPublic;
    List<String> userIdsBlackList;
    List<ImsMessage> messages;
    boolean isMuted;
    List<UserInfo> chatUsers;


    public ChatInfo(String name, List<String> userIds, String avatarUrl, boolean isPublic) {
        this.name = name;
        this.usersIds = userIds;
        this.avatarUrl = avatarUrl;
        this.isPublic = isPublic;
    }

    /**
     * Get the chat title (name), if non is set and this chat between two users it returns
     * the second user nic name
     *
     * @return the chat name
     */
    public String getChatTitle(){
        if (!TextUtils.isEmpty(name)){
            return name;
        }else{
            if (usersIds.size() == 2){
//                if (usersIds[0] != ImModel.instance.userId){
//                if let nic = Model.instance.getCachedUserInfoById(usersIds[0])?.nicName{
//                    return nic
//                }
//                }else{
//                    if let nic = Model.instance.getCachedUserInfoById(usersIds[1])?.nicName{
//                        return nic
//                    }
//                }
                return "TBA"; // TODO Finish after cached users are present
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
        if (!TextUtils.isEmpty(avatarUrl)){
            return avatarUrl;
        }else{
            if (usersIds.size() == 2){
//                if usersIds[0] != ImModel.instance.userId!{
//                if let avatar = Model.instance.getCachedUserInfoById(usersIds[0])?.avatarUrl{
//                    return avatar
//                }
//                }else{
//                    if let avatar = Model.instance.getCachedUserInfoById(usersIds[1])?.avatarUrl{
//                        return avatar
//                    }
//                }
                return ""; // TODO Finish after cached users are present
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
    private void loadMessages(){
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
//        ImModel.instance.imsSendMessageToChat(self, message: message)
    }

    /**
     * Get the number of uread messages by the current user in this chat
     *
     * @return unread message count
     */
    public int unreadMessageCount(){
        int count = 0;
//        let uid = ImModel.instance.userId!
//        if (messages == nil){
//            print("*** error need to load chat messages befor asking for unreadMessageCount")
//            return -1
//        }
//        for msg in messages!{
//        if (msg.senderId != uid && msg.readFlag == false){
//            count += 1
//        }
//        }
        return count;
    }

    /**
     * Mark this message status as read for this user
     *
     * @param  message to update
     * @return void
     */
    public void markMessageAsRead(ImsMessage message){
//        ImModel.instance.imsMarkMessageAsRead(self, message: message)
    }


    public void addUser(UserInfo uinfo){
//        if(self.owner == ImModel.instance.userId!){
//            self.usersIds.append(uinfo.userId)
//            self.chatUsers!.append(uinfo)
//            self.updateChatInfo()
//        }else{
//            print("*** Error - cant add user to a chat that you are not the owner of!")
//        }
    }

    /**
     * Update the chat info, this is good for update the name and avatar, do not use this
     * function to update users!
     *
     * @return void
     */
    public void updateChatInfo(){
       // ImModel.instance.updateChat(self)
    }

    /**
     * Delete a chat! this will be p[erformed inly if the current user is the chat owner
     * once deleted it will remove the chat from all user following this chat
     *
     * @return void
     */
    public void deleteChat(){
//        if (self.owner == ImModel.instance.userId!){
//            for uid in self.usersIds{
//                ImModel.instance.deleteUserFromChat(self,uid: uid)
//            }
//            ImModel.instance.deleteChat(self)
//        }else{// if im not the owner i remove myself from the chat
//            ImModel.instance.removeChatFromChatList(self)
//            ImModel.instance.deleteUserFromChat(self,uid: ImModel.instance.userId!)
//        }
//        messages?.removeAll()
//        self.notifyChatUpdate.emit()
    }

    /**
     * This user was removed from this chat by the chat owner
     *
     * @return void
     */
    public void wasRemovedByOwner(){
//        ImModel.instance.removeChatObservers(self)
//        messages?.removeAll()
//        self.notifyChatUpdate.emit()
    }

    /**
     * Remove a user from this chat, only available if you are the owner and you are not removing yourself
     *
     * @return void
     */
    public void removeUserFromChat(String uid){
//        if (self.owner == ImModel.instance.userId! && uid != ImModel.instance.userId!){
//            var shouldRemove = false
//            for index in 0...(self.usersIds.count-1){
//                if (self.usersIds[index] == uid){
//                    self.usersIds.remove(at: index)
//                    shouldRemove = true
//                    break;
//                }
//            }
//            for index in 0...self.chatUsers!.count-1{
//                if (self.chatUsers![index].userId == uid){
//                    self.chatUsers!.remove(at: index)
//                    break;
//                }
//            }
//            if(shouldRemove == true){
//                ImModel.instance.deleteUserFromChat(self,uid: uid)
//            }
//        }
    }

    /**
     * Join a public chat, this func add the current user to this chat if this chat is a public chat.
     *
     * @return void
     */
    public void joinChat(){
//        if(self.isPublic && self.isUserBlockedFromThisChat(ImModel.instance.userId!) == false){
//            ImModel.instance.joinChat(self, uid: ImModel.instance.userId!)
//        }
    }

    /**
     * checks if a user is blocked
     *
     * @return blocked
     */
    public boolean isUserBlockedFromThisChat(String userId){
        boolean blocked = false;
//        if(self.isPublic == true && self.usersIdsBlackList != nil){
//            for uid in self.usersIdsBlackList!{
//            if (uid == userId){
//                blocked = true
//            }
//            }
//        }
        return blocked;
    }

    // set the state of typing of this user - should be switch on when starting to type and off after pressing send
    // use the notifyUserIsTyping to get the list of users that are currently typing
    public void setUserIsTyping(boolean val){
    //    ImModel.instance.setUserIsTypingValue(val, sender: ImModel.instance.userId!, inChatId: self.chatId)
    }

    /**
     * Block user, this func block the given user from joinning a public chat, you must be the owner
     * of the chat to perform this operation
     *
     * @param  userId  user ID to block
     * @return void
     */
    public void blockUserFromJoinningThisChat(String userId){
//        if(self.isPublic && self.owner == ImModel.instance.userId!){
//            //first remove this user from this chat if he is a member
//            removeUserFromChat(userId)
//
//            //add the user to the black list
//            ImModel.instance.blockUserFromJoinningChat(self, userIdToBlock: userId)
//        }
    }
    /**
     * Un Block user, this func unblock the given user in this public chat, you must be the owner
     * of the chat to perform this operation
     *
     * @param  userId user ID to unblock
     * @return void
     */
    public void unblockUserInThisChat(String userId){
//        if(self.isPublic && self.owner == ImModel.instance.userId!){
//            //check if the user is actully blocked then unblock
//            if (self.isUserBlockedFromThisChat(userId)){
//                ImModel.instance.unblockUserInThisChat(self, userIdToUnBlock: userId)
//            }
//        }
    }

    /**
     * mute push notifications on this chat for this user
     *
     * @param  isMuted Bool
     * @return void
     */
    public void setMuteChat(boolean isMuted){
        this.isMuted = isMuted;
//        ImModel.instance.setMuteChat(self,isMuted:isMuted)
    }




}
