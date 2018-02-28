package base.app.data.chat;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import base.app.data.Model;
import base.app.data.chat.event.ChatNotificationsEvent;
import base.app.data.chat.event.ChatUpdateEvent;
import base.app.data.chat.event.MessageUpdateEvent;
import base.app.data.chat.event.UserIsTypingEvent;
import base.app.data.user.UserInfo;

import static base.app.ClubConfig.CLUB_ID;
import static base.app.util.commons.AnalyticsTrackerKt.trackChatJoined;

/**
 * Created by Filip on 12/7/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatInfo {

    private static final String TAG = "CHAT INFO";

    @JsonProperty("_id")
    private String chatId;
    private String name;
    @JsonIgnore
    private ArrayList<UserInfo> chatUsers;
    private ArrayList<String> usersIds;
    private String avatarUrl;
    private String owner;
    private boolean isPublic = false;
    private ArrayList<String> blackList;  //only available for public chats
    private ArrayList<ImsMessage> messages;
    private int unreadCount = 0;
    private boolean isMuted = false;
    private boolean isOfficial = false;
    @JsonProperty("club_id")
    private String clubId;
    private List<UserInfo> usersTypingInfo = new ArrayList<>();

    public ChatInfo(String name, ArrayList<String> usersIds, String avatarUrl, boolean isPublic, boolean isOfficial, String chatId, String clubId) {
        super();
        owner = Model.getInstance().getUserInfo().getUserId();
        this.chatId = chatId;
        this.name = name;
        if (usersIds != null) {
            this.usersIds = usersIds;
        } else {
            this.usersIds = new ArrayList<>();
        }
        this.avatarUrl = avatarUrl;
        this.isPublic = isPublic;
        this.isOfficial = isOfficial;
        this.clubId = clubId;
    }

    public ChatInfo(String name, ArrayList<String> usersIds, String avatarUrl, boolean isPublic, boolean isOfficial) {
        super();
        owner = Model.getInstance().getUserInfo().getUserId();
        this.name = name;
        if (usersIds != null) {
            this.usersIds = usersIds;
        } else {
            this.usersIds = new ArrayList<>();
        }
        this.avatarUrl = avatarUrl;
        this.isPublic = isPublic;
        this.isOfficial = isOfficial;
    }

    public ChatInfo() {
    }

    void setEqualTo(ChatInfo info) {
        setChatId(info.getChatId());
        setName(info.getName());
        setUsersIds(info.getUsersIds());
        setAvatarUrl(info.getAvatarUrl());
        setOwner(info.getOwner());
        setIsPublic(info.getIsPublic());
        setOfficial(info.isOfficial());
        setBlackList(info.getBlackList());
        setIsMuted(info.getIsMuted());
        setUnreadCount(info.getUnreadCount());
        setClubId(info.getClubId());

        // This is different from iOS - do we really need this?
//        if(info.getMessages()!=null){
//            if(info.getMessages().size()>0){
//                setMessages(info.getMessages());
//            }
//        }
    }

    // don't use that, it is called on login
    Task<Void> loadChatUsers() {
        final TaskCompletionSource<Void> source = new TaskCompletionSource<>();
        if (chatUsers == null) {
            chatUsers = new ArrayList<>();
            final ArrayList<Task<UserInfo>> tasks = new ArrayList<>();
            for (String uid : getUsersIds()) {
                Task<UserInfo> task = Model.getInstance().getUserInfoById(uid);
                task.addOnCompleteListener(new OnCompleteListener<UserInfo>() {
                    @Override
                    public void onComplete(@NonNull Task<UserInfo> task) {
                        if (task.isSuccessful()) {
                            chatUsers.add(task.getResult());
                        }
                    }
                });
                tasks.add(task);
            }
            Tasks.whenAll(tasks).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        setupChatNicAndAvatar();
                        source.setResult(null);
                        EventBus.getDefault().post(new ChatNotificationsEvent(ChatInfo.this, ChatNotificationsEvent.Key.UPDATED_CHAT_USERS));
                    }
                }
            });
        } else {
            source.setResult(null);
        }
        return source.getTask();
    }

    /**
     * Get the chat title (name), if non is set and this chat between two users it returns
     * the second user nic name
     *
     * @return the chat name
     */
    public String getChatTitle() {
        if (!TextUtils.isEmpty(getName())) {
            return getName();
        } else {
            return "Unknown";
        }
    }

    /**
     * Get the chat avatar URL, if non is set and this chat between two users it returns
     * the second user avatar url
     *
     * @return avatar url
     */
    @JsonIgnore
    public String getChatAvatarUrl() {
        if (!TextUtils.isEmpty(getAvatarUrl())) {
            return getAvatarUrl();
        } else {
            return "";
        }
    }

    private void setupChatNicAndAvatar() {
        if (getUsersIds().size() == 2) {
            String firstUserId = getUsersIds().get(0);
            String secondUserId = getUsersIds().get(1);
            UserInfo info;
            String currentUserId = Model.getInstance().getUserInfo().getUserId();
            if (currentUserId == null || !currentUserId.equals(firstUserId)) {
                info = Model.getInstance().getCachedUserInfoById(firstUserId);
            } else {
                info = Model.getInstance().getCachedUserInfoById(secondUserId);
            }
            if (info != null) {
                String nic = info.getNicName();
                String avatar = info.getAvatarUrl();
                if (nic != null) {
                    setName(nic);
                }
                if (avatar != null) {
                    setAvatarUrl(avatar);
                }
            }
        }
    }

    /**
     * Do NOT use this function
     * Load all chat data including messages and sets the observers on chat message changes
     * This func must be called once to follow the chat messages
     * the chat messages will be returned by listenning to the following events:
     * notifyChatUpdate - will return an updated array of the chat messages when a new message
     * is received or message status was changed
     * notifyUserIsTyping - will return an array of the users which are currently typing
     */
    void loadMessages() {
        messages = new ArrayList<>();
        EventBus.getDefault().register(this);
        ImsManager.getInstance().imsSetMessageObserverForChat(this);
    }

    @Subscribe
    public void onEvent(MessageUpdateEvent event) {
        ImsMessage message = event.getMessage();
        messages.add(message);
        for (int i = 0; i < messages.size(); i++) {
            ImsMessage msg = messages.get(i);
            if (msg.getId().equals(message.getId())) {
                messages.set(i, message);
            }
            EventBus.getDefault().post(new ChatNotificationsEvent(this, ChatNotificationsEvent.Key.UPDATED_CHAT_MESSAGES));
        }
        EventBus.getDefault().post(new ChatUpdateEvent(this));
    }

    /**
     * Load Message history, this func load the previusly archived messages in this chat
     */
    public Task<List<ImsMessage>> loadPreviousMessagesPage() {
        return ImsManager.getInstance().loadPreviousPageOfMessages(this);
    }

    // TODO - This code is not present on iOS!
    private void sortMessages() {
        Collections.sort(messages, new Comparator<ImsMessage>() {
            @Override
            public int compare(ImsMessage lhs, ImsMessage rhs) {
                return lhs.getTimestamp().compareTo(rhs.getTimestamp());
            }
        });
    }

    /**
     * Send message to the chat
     *
     * @param message to send
     */
    public Task<ChatInfo> sendMessage(ImsMessage message) {
        Task<ChatInfo> chatInfoTask = ImsManager.getInstance().imsSendMessageToChat(this, message);
        messages.add(message);
        EventBus.getDefault().post(new ChatNotificationsEvent(this, ChatNotificationsEvent.Key.UPDATED_CHAT_MESSAGES));
        return chatInfoTask;
    }

    public void updateMessage(final ImsMessage message, final TaskCompletionSource<ChatInfo> completion) {
        if (message.getId() == null) {
            Log.e(TAG, "Message is not being updated - missing ID");
            if (completion != null) {
                completion.setResult(this);
            }
            return;
        }
        TaskCompletionSource<ImsMessage> updateMessageCompletion = new TaskCompletionSource<>();
        message.imsUpdateMessage(this, String.valueOf(CLUB_ID), updateMessageCompletion);
        updateMessageCompletion.getTask().addOnCompleteListener(new OnCompleteListener<ImsMessage>() {
            @Override
            public void onComplete(@NonNull Task<ImsMessage> task) {
                if (task.isSuccessful()) {
                    if (completion != null) {
                        completion.setResult(ChatInfo.this);
                    }
                    EventBus.getDefault().post(new ChatNotificationsEvent(message, ChatNotificationsEvent.Key.CHANGED_CHAT_MESSAGE));
                } else {
                    if (completion != null && task.getException() != null) {
                        completion.setException(task.getException());
                    }
                }

            }
        });
    }

    /**
     * Get the number of uread messages by the current user in this chat
     *
     * @return unread message count
     */
    public int unreadMessageCount() {
        int count = 0;
        if (Model.getInstance().getUserInfo() == null) {
            Log.e(TAG, "*** error - no user!");
            return -1;
        }
        String uid = Model.getInstance().getUserInfo().getUserId();
        if (messages == null || uid == null) {
            Log.e(TAG, "*** error need to load chat messages before asking for unreadMessageCount");
            return -1;
        }
        for (ImsMessage message : messages) {
            if (message.getSenderId() != null
                    && !message.getSenderId().equals(uid)
                    && !message.getReadFlag()) {
                count += 1;
            }
        }
        return count;
    }

    /**
     * Mark this message status as read for this user
     *
     * @param message to update
     */
    public void markMessageAsRead(ImsMessage message) {
        message.setReadFlag(true);
        if (message.getId() != null) {
            ImsManager.getInstance().markMessageAsRead(this, message);
        }
    }

    public void addUser(UserInfo uinfo) {
        if (Model.getInstance().getUserInfo() == null) {
            return;
        }
        String currentUserId = Model.getInstance().getUserInfo().getUserId();
        if (owner.equals(currentUserId)) {
            usersIds.add(uinfo.getUserId());
            updateChatInfo();
        } else {
            Log.e(TAG, "Error - cant add user to a chat that you are not the owner of!");
        }
    }

    /**
     * Update the chat info, this is good for update the name and avatar, do not use this
     * function to update users!
     **/
    public void updateChatInfo() {
        ImsManager.getInstance().updateChat(this);
    }

    /**
     * Delete a chat! this will be performed only if the current user is the chat owner
     * once deleted it will remove the chat from all user following this chat
     **/
    public void deleteChat() {
        String currentUserId = Model.getInstance().getUserInfo().getUserId();
        ImsManager.getInstance().removeChatFromChatList(this);
        if (owner.equals(currentUserId)) {
            ImsManager.getInstance().deleteChat(this);
        } else { // if im not the owner I remove myself from the chat
            ImsManager.getInstance().leaveChat(this);
        }
        messages.clear();
        EventBus.getDefault().post(new ChatNotificationsEvent(ChatInfo.this, ChatNotificationsEvent.Key.UPDATED_CHAT_MESSAGES));
        EventBus.getDefault().post(new ChatUpdateEvent(ChatInfo.this));
    }

    /**
     * This user was removed from this chat by the chat owner
     **/
    public void wasRemovedByOwner() {
        if (messages != null) {
            messages.clear();
            EventBus.getDefault().post(new ChatNotificationsEvent(ChatInfo.this, ChatNotificationsEvent.Key.UPDATED_CHAT_MESSAGES));
            EventBus.getDefault().post(new ChatUpdateEvent(ChatInfo.this));
        }
    }

    /**
     * Remove a user from this chat, only available if you are the owner and you are not removing yourself
     */
    public void removeUserFromChat(String uid) {
        String currentUserId = Model.getInstance().getUserInfo().getUserId();
        if (owner.equals(currentUserId) && !uid.equals(currentUserId)) {
            boolean shouldRemove = false;
            for (String entry : getUsersIds()) {
                if (entry.equals(uid)) {
                    getUsersIds().remove(entry);
                    shouldRemove = true;
                    break;
                }
            }
            if (shouldRemove) {
                updateChatInfo();
            }
        }
    }

    /**
     * Join a public chat, this func add the current user to this chat if this chat is a public chat.
     **/
    public void joinChat() {
        String currentUserId = Model.getInstance().getUserInfo().getUserId();
        if (isPublic && !isUserBlockedFromThisChat(currentUserId)) {
            ImsManager.getInstance().joinChat(this).addOnCompleteListener(new OnCompleteListener<ChatInfo>() {
                @Override
                public void onComplete(@NonNull Task<ChatInfo> task) {
                    if (task.isSuccessful()) {
                        loadChatUsers().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    EventBus.getDefault().post(new ChatNotificationsEvent(ChatInfo.this, ChatNotificationsEvent.Key.SET_CURRENT_CHAT));
                                }
                            }
                        });
                        trackChatJoined();
                    }

                }
            });
        }
    }

    /**
     * checks if a user is blocked
     *
     * @return blocked
     */
    public boolean isUserBlockedFromThisChat(String userId) {
        if (isPublic && blackList != null) {
            for (String id : blackList) {
                if (id.equals(userId)) {
                    return true;
                }
            }
        }
        return false;
    }

    // set the state of typing of this user - should be switch on when starting to type and off after pressing send
    // use the notifyUserIsTyping to get the list of users that are currently typing
    public void setUserIsTyping(boolean val) {
        ImsManager.getInstance().setUserIsTypingValue(val, getChatId());
    }

    void updateUserIsTyping(String userId, boolean isTypingValue) {
        if (isTypingValue) {
            UserInfo info = Model.getInstance().getCachedUserInfoById(userId);
            if (info != null) {
                usersTypingInfo.add(info);
            } else {
                Model.getInstance().getUserInfoById(userId);
            }
        } else {
            for (UserInfo info : new ArrayList<>(usersTypingInfo)) {
                if (info.getUserId().equals(userId)) {
                    usersTypingInfo.remove(info);
                }
            }
        }
        EventBus.getDefault().post(new UserIsTypingEvent(chatId, usersTypingInfo));
    }

    /**
     * Block user, this func block the given user from joinning a public chat, you must be the owner
     * of the chat to perform this operation
     *
     * @param userId user ID to block
     */
    public void blockUserFromJoinningThisChat(String userId) {
        String currentUserId = Model.getInstance().getUserInfo().getUserId();
        if (isPublic && owner.equals(currentUserId)) {
            if (!blackList.contains(userId)) {
                blackList.add(userId);
            }
            usersIds.remove(userId);
            updateChatInfo();
        }
    }

    /**
     * Un Block user, this func unblock the given user in this public chat, you must be the owner
     * of the chat to perform this operation
     *
     * @param userId user ID to unblock
     */
    public void unblockUserInThisChat(String userId) {
        String currentUserId = Model.getInstance().getUserInfo().getUserId();
        if (isPublic && owner.equals(currentUserId)) {
            //check if the user is actually blocked then unblock
            if (isUserBlockedFromThisChat(userId)) {
                updateChatInfo();
            }
        }
    }

    /**
     * mute push notifications on this chat for this user
     *
     * @param isMuted Bool
     */
    public void setMuteChat(boolean isMuted) {
        this.setIsMuted(isMuted);
        ImsManager.getInstance().setMuteChat(this, isMuted);
    }

    public void updateMessageJson(Map<String, Object> data) {
        String messageId = (String) data.get("_id");
        String locid = (String) data.get("locid");

        if (messageId == null && locid == null) {
            Log.e(TAG, "Error: Can't identify message");
            return;
        }

        for (ImsMessage message : messages) {
            if (message.getLocid().equals(locid) || message.getId().equals(messageId)) {
                message.updateFrom(data);
                EventBus.getDefault().post(new ChatNotificationsEvent(message, ChatNotificationsEvent.Key.CHANGED_CHAT_MESSAGE));
            }
        }

    }

    public void addReceivedMessage(ImsMessage message) {
        if (message.getLocid() != null) {
            for (ImsMessage m : messages) {
                if (message.getLocid().equals(m.getLocid())) {
                    return;
                }
            }
        }

        if (messages == null) {
            messages = new ArrayList<>();
        }
        message.initializeTimestamp();
        message.determineSelfReadFlag();
        messages.add(message);
        sortMessages();
        EventBus.getDefault().post(new ChatNotificationsEvent(this, ChatNotificationsEvent.Key.UPDATED_CHAT_MESSAGES));
        EventBus.getDefault().post(new ChatUpdateEvent(ChatInfo.this));
    }

    public void addReceivedMessage(List<ImsMessage> messages) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        for (ImsMessage message : messages) {
            message.initializeTimestamp();
            message.determineSelfReadFlag();
        }
        this.messages.addAll(messages);
        sortMessages();
        EventBus.getDefault().post(new ChatNotificationsEvent(ChatInfo.this, ChatNotificationsEvent.Key.UPDATED_CHAT_MESSAGES));
        EventBus.getDefault().post(new ChatUpdateEvent(ChatInfo.this));
    }

    public void deleteMessage(final ImsMessage message, final TaskCompletionSource<ImsMessage> completion) {
        if (message.getId() == null && completion == null) {
            completion.setResult(null);
        }
        deleteMessage(message);
        TaskCompletionSource<ImsMessage> deleteMessageCompletion = new TaskCompletionSource<>();
        message.imsDeleteMessage(this, CLUB_ID, deleteMessageCompletion);

        //deleteMessage(message);
        deleteMessageCompletion.getTask().addOnCompleteListener(new OnCompleteListener<ImsMessage>() {
            @Override
            public void onComplete(@NonNull Task<ImsMessage> task) {
                if (task.isSuccessful()) {
                    if (completion != null) {
                        completion.setResult(message);
                    }
                    EventBus.getDefault().post(new ChatNotificationsEvent(message, ChatNotificationsEvent.Key.DELETED_CHAT_MESSAGE));
                } else {
                    if (completion != null && task.getException() != null) {
                        completion.setException(task.getException());
                    }
                }
            }
        });
    }

    public void deleteMessage(ImsMessage message) {
        messages.remove(message);
        EventBus.getDefault().post(new ChatNotificationsEvent(message, ChatNotificationsEvent.Key.DELETED_CHAT_MESSAGE));
    }

    @JsonProperty("_id")
    public String getChatId() {
        return chatId;
    }

    @JsonProperty("_id")
    private void setChatId(String chatId) {
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

    private ArrayList<String> getBlackList() {
        return blackList;
    }

    private void setBlackList(ArrayList<String> blackList) {
        this.blackList = blackList;
    }

    public ArrayList<ImsMessage> getMessages() {
        return messages;
    }

    private void setMessages(ArrayList<ImsMessage> messages) {
        this.messages = messages;
    }

    public boolean getIsMuted() {
        return isMuted;
    }

    private void setIsMuted(boolean muted) {
        isMuted = muted;
    }

    private int getUnreadCount() {
        return unreadCount;
    }

    private void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public boolean isOfficial() {
        return isOfficial;
    }

    public void setOfficial(boolean official) {
        isOfficial = official;
    }

    @JsonProperty("club_id")
    public String getClubId() {
        return clubId;
    }

    @JsonProperty("club_id")
    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

}
