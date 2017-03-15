package tv.sportssidekick.sportssidekick.model.user;

import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.autogen.GSMessageHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tv.sportssidekick.sportssidekick.service.GSAndroidPlatform;

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
                }
            }
        });

    }

}
