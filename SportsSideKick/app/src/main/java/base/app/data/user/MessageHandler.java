package base.app.data.user;

import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.autogen.GSMessageHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import base.app.util.commons.GSAndroidPlatform;

/**
 * Created by Filip on 3/13/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class MessageHandler {

    static private MessageHandler instance;

    private MessageHandler(){
        setupListeners();
    }


    public static MessageHandler getInstance(){
        if(instance==null){
            instance = new MessageHandler();
        }
        return instance;
    }

    List<GSMessageHandlerAbstract> delegates = new ArrayList<>();

    public void addDelegate(GSMessageHandlerAbstract delegate){
        delegates.add(delegate);
    }

    private void setupListeners(){
        GSMessageHandler messageHandler = GSAndroidPlatform.gs().getMessageHandler();
        messageHandler.setScriptMessageListener(new GSEventConsumer<GSMessageHandler.ScriptMessage>() {
            @Override
            public void onEvent(GSMessageHandler.ScriptMessage scriptMessage) {
                Map<String,Object> data = scriptMessage.getData().getBaseData();
                if(data.containsKey("type")){
                    String type = (String) data.get("type");
                    for(GSMessageHandlerAbstract delegate : delegates){
                        delegate.onGSScriptMessage(type,data);
                    }
                } else if(scriptMessage.getString("extCode")!=null){
                    for(GSMessageHandlerAbstract delegate : delegates){
                        delegate.onMessage(scriptMessage.getBaseData());
                    }
                }
            }
        });
        messageHandler.setTeamChatMessageListener(new GSEventConsumer<GSMessageHandler.TeamChatMessage>() {
            @Override
            public void onEvent(GSMessageHandler.TeamChatMessage teamChatMessage) {
              for(GSMessageHandlerAbstract delegate : delegates) {
                  delegate.onGSTeamChatMessage(teamChatMessage);
              }
            }
        });
    }

}
