package tv.sportssidekick.sportssidekick.model.im;

import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.GSData;
import com.gamesparks.sdk.api.autogen.GSMessageHandler;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.sportssidekick.sportssidekick.model.GSConstants;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.im.event.ChatsInfoUpdatesEvent;
import tv.sportssidekick.sportssidekick.model.user.GSMessageHandlerAbstract;
import tv.sportssidekick.sportssidekick.model.user.LoginStateReceiver;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.service.GSAndroidPlatform;

import static tv.sportssidekick.sportssidekick.model.GSConstants.*;
import static tv.sportssidekick.sportssidekick.model.Model.createRequest;

/**
 * Created by Filip on 12/7/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ImsManager extends GSMessageHandlerAbstract implements LoginStateReceiver.LoginStateListener{

    public static final String TAG = "ImsManager";

    private static ImsManager instance;
    private LoginStateReceiver loginStateReceiver;

//    private String userId;

    private HashMap<String, ChatInfo> chatInfoCache;

    private final ObjectMapper mapper; // jackson's object mapper
    private ImsManager() {
        mapper  = new ObjectMapper();
        chatInfoCache = new HashMap<>();
        Model.getInstance().setMessageHandlerDelegate(this);
        this.loginStateReceiver = new LoginStateReceiver(this);
    }

    public static ImsManager getInstance(){
        if(instance==null){
            instance = new ImsManager();
        }
        return instance;
    }

    private void clear(){
        chatInfoCache.clear();
        EventBus.getDefault().post(new ChatsInfoUpdatesEvent(getUserChatsList()));
    }

    public void reload(){
        clear();
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

    public List<ChatInfo> getUserChatsList() {
        return new ArrayList<>(chatInfoCache.values());
    }

    public ChatInfo getChatInfoById(String chatId){
        return  chatInfoCache.get(chatId);
    }

    private boolean removeChatInfoWithId(String chatId){
        if(chatInfoCache.containsKey(chatId)){
            chatInfoCache.remove(chatId);
            return true;
        }
        return false;
    }

    private void addChatInfoToCache(ChatInfo info){
        chatInfoCache.put(info.getChatId(),info);
    }

    public Task<List<ChatInfo>> getAllPublicChats() {
        final TaskCompletionSource<List<ChatInfo>> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    // Parse response
                    Object object = response.getScriptData().getBaseData().get(CHATS_INFO);
                    List<ChatInfo> chats = mapper.convertValue(object, new TypeReference<List<ChatInfo>>(){});
                    source.setResult(chats);
                    // on iOS, there is a notification, but we are handling this via callback
                }
            }
        };
        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
                .setEventKey("imsGetPublicChatGroupList")
                .setEventAttribute(OFFSET,0)
                .send(consumer);
        return source.getTask();
    }

    /**
     * getGlobalChats returns a list of all public chats in the system.
     */

    Task<List<ChatInfo>> getGlobalChats() {
        final TaskCompletionSource<List<ChatInfo>> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    // Parse response
                    Object object = response.getScriptData().getBaseData().get(CHATS_INFO);
                    List<ChatInfo> chats = mapper.convertValue(object, new TypeReference<List<ChatInfo>>(){});
                    source.setResult(chats);
                }
            }
        };

        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
            .setEventKey("imsGetGlobalChatGroupsList")
            .setEventAttribute(OFFSET,0)
            .send(consumer);
        return source.getTask();
    }

    public Task<List<ChatInfo>> getAllPublicChatsForUser(String userId) {
        final TaskCompletionSource<List<ChatInfo>> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(CHATS_INFO);
                    List<ChatInfo> chats = mapper.convertValue(object, new TypeReference<List<ChatInfo>>(){});
                    source.setResult(chats);
                }
            }
        };
        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
                .setEventKey("imsGetPublicChatGroupsListForUser")
                .setEventAttribute(USER_ID,userId)
                .setEventAttribute(OFFSET,0)
                .setEventAttribute(ENTRY_COUNT,50)
                .send(consumer);
        return source.getTask();
    }

    private void loadUserChats() {
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    // Parse response
                    chatInfoCache.clear();
                    Object object = response.getScriptData().getBaseData().get(CHATS_INFO);
                    List<ChatInfo> chats = mapper.convertValue(object, new TypeReference<List<ChatInfo>>(){});
                    // Go trough all ChatInfo objects and load users and messages
                    for (ChatInfo chat : chats) {
                        if(chat!=null){
                            addChatInfoToCache(chat);
                            chat.loadChatUsers();
                            chat.loadMessages();
                        } else {
                            Log.e(TAG,"Chat is null!");
                        }
                    }
                    EventBus.getDefault().post(new ChatsInfoUpdatesEvent(getUserChatsList()));
                }
            }
        };
        createRequest("imsGetUserChatGroupsList").send(consumer);
    }

    /**
     * createNewChat create a new chat according to the given chat info. the func will assign a new chat ID
     * to the new chat and will store it in the database.
     * you need to listen to notifyCreateNewChatSuccess to get  a success indication
     *
     * @param  chatInfo - the chat info to create (do not set the chat id)
     */

    public Task<ChatInfo> createNewChat(final ChatInfo chatInfo){
        final TaskCompletionSource<ChatInfo> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    // Parse response
                    Object object = response.getScriptData().getBaseData().get(CHAT_INFO);
                    ChatInfo chat = mapper.convertValue(object,ChatInfo.class);
                    chatInfo.setEqualTo(chat);
                    addChatInfoToCache(chatInfo);
                    chatInfo.loadChatUsers();
                    chatInfo.loadMessages();
                    source.setResult(chatInfo);
                } else {
                    source.setException(new Exception());
                }
            }
        };

        Map<String, Object> map = mapper.convertValue(chatInfo, new TypeReference<Map<String, Object>>(){});
        GSData data = new GSData(map);
        createRequest("imsCreateChatGroup")
                .setEventAttribute(CHAT_INFO,data)
                .send(consumer);
        return source.getTask();
    }


    /***
     *          DO NOT USE THE FUNCTIONS BELOW DIRECTLY! USE CHATINFO API
     **/

    //you shouldn't use this function directly, call update chat on the chatInfo object
    Task<ChatInfo> updateChat(final ChatInfo chatInfo){
        final TaskCompletionSource<ChatInfo> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
            if (!response.hasErrors()) {
                Object object = response.getScriptData().getBaseData().get(CHAT_INFO);
                ChatInfo newChatInfo = mapper.convertValue(object,ChatInfo.class);
                chatInfo.setEqualTo(newChatInfo);
                source.setResult(chatInfo);
            } else {
                source.setException(new Exception());
            }
            }
        };
        Map<String, Object> map = mapper.convertValue(chatInfo, new TypeReference<Map<String, Object>>(){});
        GSData data = new GSData(map);
        createRequest("imsUpdateChatGroup")
            .setEventAttribute("imsGroupInfo",data)
            .send(consumer);
        return source.getTask();
    }

    //you shouldn't use this function directly, call join chat on the chatInfo object
    Task<ChatInfo> joinChat(final ChatInfo chatInfo){
        final TaskCompletionSource<ChatInfo> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
            if (!response.hasErrors()) {
                Object object = response.getScriptData().getBaseData().get(CHAT_INFO);
                ChatInfo newChatInfo = mapper.convertValue(object,ChatInfo.class);
                chatInfo.setEqualTo(newChatInfo);
                addChatInfoToCache(chatInfo);
                chatInfo.loadMessages();
                source.setResult(chatInfo);
            } else {
                source.setException(new Exception());
            }
            EventBus.getDefault().post(new ChatsInfoUpdatesEvent(getUserChatsList()));
            }
        };
        createRequest(IMS_JOIN_CHAT_GROUP)
                .setEventAttribute(IMS_GROUP_ID,chatInfo.getChatId())
                .send(consumer);
        return source.getTask();
    }

    //you shouldn't use this function directly, call delete on the chatInfo object
    void deleteChat(final ChatInfo chatInfo){
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
            if (!response.hasErrors()) {
                removeChatInfoWithId(chatInfo.getChatId());
                EventBus.getDefault().post(new ChatsInfoUpdatesEvent(getUserChatsList()));
            }
            }
        };
        createRequest("imsDeleteChatGroup")
            .setEventAttribute(IMS_GROUP_ID,chatInfo.getChatId())
            .send(consumer);
    }

    void leaveChat(final ChatInfo chatInfo){
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    removeChatInfoWithId(chatInfo.getChatId());
                }
            }
        };
        createRequest("imsLeaveChatGroup")
            .setEventAttribute(IMS_GROUP_ID,chatInfo.getChatId())
            .send(consumer);
    }

    void removeChatFromChatList(ChatInfo info){
        removeChatInfoWithId(info.getChatId());
        EventBus.getDefault().post(new ChatsInfoUpdatesEvent(getUserChatsList()));
    }

    /**
     * // IMS
     **/

    // do not use this function, call the chat info one!
    void imsSendMessageToChat(ChatInfo chatInfo,ImsMessage message){
        String nic = Model.getInstance().getUserInfo().getNicName();
        if(TextUtils.isEmpty(nic)){
            nic = Model.getInstance().getUserInfo().getFirstName();
        }
        Map<String, Object> map = mapper.convertValue(message, new TypeReference<Map<String, Object>>(){});
        GSData data = new GSData(map);

        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
            .setEventKey("imsSendMessage")
            .setEventAttribute(GROUP_ID,chatInfo.getChatId())
            .setEventAttribute(MESSAGE,data)
            .setEventAttribute("senderNic",nic)
            .send(null);
    }

    //use chat notifyChatUpdate signal to track chat messages!
    void imsSetMessageObserverForChat(final ChatInfo chatInfo){
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    // Parse response
                    Object object = response.getScriptData().getBaseData().get(MESSAGES);
                    List<ImsMessage> messages = mapper.convertValue(object, new TypeReference<List<ImsMessage>>(){});
                    chatInfo.addReceivedMessage(messages);
                }
            }
        };
        createRequest(IMS_GET_CHAT_GROUPS_MESSAGES)
                .setEventAttribute(OFFSET,"0")
                .setEventAttribute(ENTRY_COUNT,MESSAGE_PAGE_SIZE)
                .setEventAttribute(GROUP_ID,chatInfo.getChatId())
                .send(consumer);
    }

    // load next page of messages
    public Task<List<ImsMessage>> loadPreviousPageOfMessages(final ChatInfo chatInfo){
        final TaskCompletionSource<List<ImsMessage>> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    // Parse response
                    Object object = response.getScriptData().getBaseData().get(MESSAGES);
                    List<ImsMessage> messages = mapper.convertValue(object, new TypeReference<List<ImsMessage>>(){});
                    // Go trough all messages objects and load users and messages
                    for (ImsMessage message : messages) {
                        message.initializeTimestamp();
                        message.determineSelfReadFlag();
                    }
                    source.setResult(messages);
                }
            }
        };
        createRequest(IMS_GET_CHAT_GROUPS_MESSAGES)
                .setEventAttribute(OFFSET,chatInfo.getMessages().size())
                .setEventAttribute(ENTRY_COUNT,MESSAGE_PAGE_SIZE)
                .setEventAttribute(GROUP_ID,chatInfo.getChatId())
                .send(consumer);

        return source.getTask();
    }

    // do not use this function, call the chat info one!
    void markMessageAsRead(final ChatInfo chatInfo,final ImsMessage message){
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if(!response.hasErrors()){
                    // Parse response
                    Object object = response.getScriptData().getBaseData().get(CHAT_INFO);
                    ChatInfo updatedChatInfo = mapper.convertValue(object,ChatInfo.class);
                    chatInfo.setEqualTo(updatedChatInfo);
                }
            }
        };
        createRequest("imsMarkMessageAsRead")
                .setEventAttribute(GROUP_ID,chatInfo.getChatId())
                .setEventAttribute("messageId",message.getId())
                .send(consumer);
    }

    // do not use this function, call the chat info one!
    void setUserIsTypingValue(boolean val, String chatId){
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {    }
        };
        createRequest("imsUpdateUserIsTypingState")
                .setEventAttribute(IS_TYPING_VALUE,String.valueOf(val).toLowerCase())
                .setEventAttribute(GROUP_ID,chatId)
                .send(consumer);
    }

    // do not use this function, call the chat info one!
    void setMuteChat(ChatInfo chatInfo,boolean isMuted){
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {    }
        };
        createRequest("imsSetChatMuteValue")
                .setEventAttribute("muteValue",String.valueOf(isMuted).toLowerCase())
                .setEventAttribute(GROUP_ID,chatInfo.getChatId())
                .send(consumer);
    }


    @Override
    public void onLogout() {
        clear();
    }

    @Override
    public void onLoginAnonymously() {
        reload();
    }

    @Override
    public void onLogin(UserInfo user) {
        reload();
    }

    @Override
    public void onLoginError(Error error) {
        clear();
    }

    @Override
    public void onGSTeamChatMessage(GSMessageHandler.TeamChatMessage msg){
        String data = (String)msg.getBaseData().get("imsGroups");
        try {
            ImsMessage message = mapper.readValue(data,ImsMessage.class);
            String chatId = msg.getTeamId();
            String teamType = msg.getTeamType();
            if(teamType!=null && chatId!=null){
                if(teamType.equals("imsGroups")){
                    ChatInfo chatInfo = getChatInfoById(chatId);
                    chatInfo.addReceivedMessage(message);
                }
            }
        } catch (IOException e) {
            Log.e(TAG,"Unhandled exception - Team Chat Message: " + data);
        }
    }

    @Override
    public void onGSScriptMessage(String type, Map<String,Object> data){
        String chatId;
        switch (type){
            case "ImsUpdateChatInfo":
//              TODO   ??? This part looks weird on iOS ?
                chatId = (String) data.get(CHAT_ID);
                if(chatId==null){
                    reload();
                }
                break;
            case "ImsUpdateUserIsTypingState":
                chatId = (String) data.get(CHAT_ID);
                String sender = (String) data.get(GSConstants.SENDER);
                String isTyping = (String) data.get(IS_TYPING_VALUE);
                if(chatId!=null && sender!=null && isTyping!=null){
                    if(!sender.equals(Model.getInstance().getUserInfo().getUserId())){
                        ChatInfo chatInfo = getChatInfoById(chatId);
                        if(chatInfo!=null){
                            chatInfo.updateUserIsTyping(sender,isTyping.equals("true"));
                        }
                    }
                }
                break;
            case "ImsMessage":
                ImsMessage message = mapper.convertValue(data.get(MESSAGE),ImsMessage.class);
                ChatInfo chatInfo = getChatInfoById((String)data.get(CHAT_ID));
                if(chatInfo!=null){
                    chatInfo.addReceivedMessage(message);
                } else {
                    Log.e(TAG,"UNHANDLED ImsMessage " + message + " Error: chat not found with id:" + data.get(CHAT_ID));
                }
                break;
        }

    }
}