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
import org.greenrobot.eventbus.Subscribe;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.sportssidekick.sportssidekick.model.GSConstants;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.user.GSMessageHandlerAbstract;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.service.GSAndroidPlatform;
import tv.sportssidekick.sportssidekick.service.GameSparksEvent;

import static tv.sportssidekick.sportssidekick.model.GSConstants.CHATS_INFO;
import static tv.sportssidekick.sportssidekick.model.GSConstants.CHAT_ID;
import static tv.sportssidekick.sportssidekick.model.GSConstants.CHAT_INFO;
import static tv.sportssidekick.sportssidekick.model.GSConstants.ENTRY_COUNT;
import static tv.sportssidekick.sportssidekick.model.GSConstants.GROUP_ID;
import static tv.sportssidekick.sportssidekick.model.GSConstants.IMS_GET_CHAT_GROUPS_MESSAGES;
import static tv.sportssidekick.sportssidekick.model.GSConstants.IMS_GROUP_ID;
import static tv.sportssidekick.sportssidekick.model.GSConstants.IMS_JOIN_CHAT_GROUP;
import static tv.sportssidekick.sportssidekick.model.GSConstants.IS_TYPING_VALUE;
import static tv.sportssidekick.sportssidekick.model.GSConstants.MESSAGE;
import static tv.sportssidekick.sportssidekick.model.GSConstants.MESSAGES;
import static tv.sportssidekick.sportssidekick.model.GSConstants.MESSAGE_PAGE_SIZE;
import static tv.sportssidekick.sportssidekick.model.GSConstants.OFFSET;
import static tv.sportssidekick.sportssidekick.model.Model.createRequest;

/**
 * Created by Filip on 12/7/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ImsManager extends GSMessageHandlerAbstract{

    public static final String TAG = "ImsManager";

    private static ImsManager instance;

    private String userId;

    private HashMap<String, ChatInfo> chatInfoCache;
    private List<ChatInfo> chatInfoCacheList;

    private final ObjectMapper mapper; // jackson's object mapper
    private ImsManager() {
        mapper  = new ObjectMapper();
        chatInfoCache = new HashMap<>();
        chatInfoCacheList = new ArrayList<>();
        UserInfo userInfo = Model.getInstance().getUserInfo();
        if(userInfo!=null){
           userId = userInfo.getUserId();
        }
        EventBus.getDefault().register(this);
        Model.getInstance().setMessageHandlerDelegate(this);
    }

    @Subscribe
    public void userInfoListener(UserInfo info){
        userId = info.getUserId();
    }

    public static ImsManager getInstance(){
        if(instance==null){
            instance = new ImsManager();
        }
        return instance;
    }

    private void clear(){
        chatInfoCache.clear();
        chatInfoCacheList.clear();
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
        return chatInfoCacheList;
    }

    public ChatInfo getChatInfoById(String chatId){
        return  chatInfoCache.get(chatId);
    }

    private boolean removeChatInfoWithId(String chatId){
        if(chatInfoCache.containsKey(chatId)){
            ChatInfo info = chatInfoCache.remove(chatId);
            chatInfoCacheList.remove(info);
            return true;
        }
        return false;
    }

    private void addChatInfoToCache(ChatInfo info){
        if(chatInfoCache.containsKey(info.getChatId())){
            chatInfoCacheList.remove(info);
        }
        chatInfoCache.put(info.getChatId(),info);
        chatInfoCacheList.add(info);
    }
    /**
     * getAllPublicChats returns a list of all public chats in the system.
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
                    // Go trough all ChatInfo objects and load users and messages
                    for (ChatInfo chat : chats) {
                        String chatId = chat.getChatId();
                        EventBus.getDefault().post(new GameSparksEvent("Chat detected.", GameSparksEvent.Type.GLOBAL_CHAT_DETECTED, chatId));
                        addChatInfoToCache(chat);
                    }
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

    public Task<List<ChatInfo>> getAllPublicChats() {
        final TaskCompletionSource<List<ChatInfo>> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    // Parse response
                    Object object = response.getScriptData().getBaseData().get(CHATS_INFO);
                    List<ChatInfo> chats = mapper.convertValue(object, new TypeReference<List<ChatInfo>>(){});
                    // Go trough all ChatInfo objects and load users and messages
                    for (ChatInfo chat : chats) {
                        String chatId = chat.getChatId();
                        EventBus.getDefault().post(new GameSparksEvent("Chat detected.", GameSparksEvent.Type.PUBLIC_CHAT_DETECTED, chatId));
                        addChatInfoToCache(chat);
                    }
                    source.setResult(chats);
                }
            }
        };
        GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest()
            .setEventKey("imsGetPublicChatGroupList")
            .setEventAttribute(OFFSET,0)
            .send(consumer);
        return source.getTask();
    }

//    private String toJson(Object object){
//        try {
//            return mapper.writeValueAsString(object);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    private void loadUserChats() {
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    // Parse response
                    Object object = response.getScriptData().getBaseData().get(CHATS_INFO);
                    List<ChatInfo> chats = mapper.convertValue(object, new TypeReference<List<ChatInfo>>(){});
                    // Go trough all ChatInfo objects and load users and messages
                    for (ChatInfo chat : chats) {
                        if(chat!=null){
                            String chatId = chat.getChatId();
                            EventBus.getDefault().post(new GameSparksEvent("Chat detected.", GameSparksEvent.Type.USER_CHAT_DETECTED, chatId));
                            addChatInfoToCache(chat);
                            chat.loadChatUsers(userId);
                            chat.loadMessages();
                        } else {
                            Log.e(TAG,"Chat is null!");
                        }

                    }
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

    public void createNewChat(final ChatInfo chatInfo){
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    // Parse response
                    Object object = response.getScriptData().getBaseData().get(CHAT_INFO);
                    ChatInfo newChatInfo = mapper.convertValue(object,ChatInfo.class);
                    chatInfo.setEqualTo(newChatInfo);
                    addChatInfoToCache(chatInfo);
                    chatInfo.loadMessages();
                    EventBus.getDefault().post(new GameSparksEvent("Chat created.", GameSparksEvent.Type.CHAT_CREATED, chatInfo.getChatId()));
                }
            }
        };

        Map<String, Object> map = mapper.convertValue(chatInfo, new TypeReference<Map<String, Object>>(){});
        GSData data = new GSData(map);

        createRequest("imsCreateChatGroup")
                .setEventAttribute(CHAT_INFO,data)
                .send(consumer);
    }


    /***
     *          DO NOT USE THE FUNCTIONS BELOW DIRECTLY! USE CHATINFO API
     **/

    //you shouldn't use this function directly, call update chat on the chatInfo object
    void updateChat(final ChatInfo chatInfo){
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(CHAT_INFO);
                    ChatInfo newChatInfo = mapper.convertValue(object,ChatInfo.class);
                    chatInfo.setEqualTo(newChatInfo);
                    addChatInfoToCache(chatInfo);
                }
            }
        };

        Map<String, Object> map = mapper.convertValue(chatInfo, new TypeReference<Map<String, Object>>(){});
        GSData data = new GSData(map);
        createRequest("imsUpdateChatGroup")
            .setEventAttribute("imsGroupInfo",data)
            .send(consumer);
    }

    //you shouldn't use this function directly, call join chat on the chatInfo object
    void joinChat(final ChatInfo chatInfo){
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(CHAT_INFO);
                    ChatInfo newChatInfo = mapper.convertValue(object,ChatInfo.class);
                    chatInfo.setEqualTo(newChatInfo);
                    addChatInfoToCache(chatInfo);
                    chatInfo.loadMessages();
                }
            }
        };

        createRequest(IMS_JOIN_CHAT_GROUP)
                .setEventAttribute(IMS_GROUP_ID,chatInfo.getChatId())
                .send(consumer);
    }

    //you shouldn't use this function directly, call delete on the chatInfo object
    void deleteChat(final ChatInfo chatInfo){
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    removeChatInfoWithId(chatInfo.getChatId());
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
                    JSONArray jsonArray = (JSONArray) response.getScriptData().getBaseData().get(MESSAGES);
                    List<ImsMessage> messages = new ArrayList<>();
                    for(Object value : jsonArray){
                        ImsMessage message = mapper.convertValue(value,ImsMessage.class);
                        messages.add(message);
                    }
                    // Go trough all messages objects and load users and messages
                    for (ImsMessage message : messages) {
                        message.initializeTimestamp();
                        message.determineSelfReadFlag();
                        GameSparksEvent fe = new GameSparksEvent("New message detected.", GameSparksEvent.Type.NEW_MESSAGE, message);
                        fe.setFilterId(chatInfo.getChatId());
                        EventBus.getDefault().post(fe);
                    }
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
    void loadNextPageOfMessages(final ChatInfo chatInfo){
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
                    GameSparksEvent fe = new GameSparksEvent("Next messages page loaded.", GameSparksEvent.Type.NEXT_PAGE_LOADED, messages);
                    fe.setFilterId(chatInfo.getChatId());
                    EventBus.getDefault().post(fe);
                }
            }
        };
        createRequest(IMS_GET_CHAT_GROUPS_MESSAGES)
                .setEventAttribute(OFFSET,chatInfo.getMessages().size())
                .setEventAttribute(ENTRY_COUNT,MESSAGE_PAGE_SIZE)
                .setEventAttribute(GROUP_ID,chatInfo.getChatId())
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

    // do not use this function, call the chat info one!
    void markMessageAsRead(final ChatInfo chatInfo, ImsMessage message){
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

    public void setupMessageListeners(){
        GSMessageHandler messageHandler = GSAndroidPlatform.gs().getMessageHandler();
        messageHandler.setScriptMessageListener(new GSEventConsumer<GSMessageHandler.ScriptMessage>() {
            @Override
            public void onEvent(GSMessageHandler.ScriptMessage scriptMessage) {
                Map<String,Object> data = scriptMessage.getData().getBaseData();
                if(data.containsKey("type")){
                    String type = (String) data.get("type");
                    onGSScriptMessage(type,data);
                }
            }
        });


    }

    @Override
    public void onGSTeamChatMessage(GSMessageHandler.TeamChatMessage msg){
        String data = (String)msg.getBaseData().get("imsGroups");
        try {
            Log.e(TAG,"UNHANDLED TeamChatMessage DATA: " + data);
            ImsMessage message = mapper.readValue(data,ImsMessage.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGSScriptMessage(String type, Map<String,Object> data){
        switch (type){
            case "ImsUpdateChatInfo":
                reload();
                break;
            case "ImsUpdateUserIsTypingState":
                String chatId = (String) data.get(CHAT_ID);
                String sender = (String) data.get(GSConstants.SENDER);
                String isTyping = (String) data.get(IS_TYPING_VALUE);
                if(chatId!=null && sender!=null && isTyping!=null){
                    if(!sender.equals(userId)){
                        ChatInfo chatInfo = getChatInfoById(chatId);
                        if(chatInfo!=null){
                            chatInfo.updateUserIsTyping(sender,isTyping.equals("true"));
                        }
                    }
                }
                break;
            case "ImsMessage":
            case "ImsMesssage": // TODO REMOVE WHEN TYPO IS FIXED!
                ImsMessage message = mapper.convertValue(data.get(MESSAGE),ImsMessage.class);
                ChatInfo chatInfo = ImsManager.getInstance().getChatInfoById((String)data.get(CHAT_ID));
                if(chatInfo!=null){
                    chatInfo.addRecievedMessage(message);
                } else {
                    Log.e(TAG,"UNHANDLED ImsMessage " + message + " Error: chat not found with id:" + (String)data.get(CHAT_ID));
                }
                break;
            default:
                Log.e(TAG,"UNHANDLED ScriptMessage type: " + type + " DATA: " + data);
                break;
        }

    }
}