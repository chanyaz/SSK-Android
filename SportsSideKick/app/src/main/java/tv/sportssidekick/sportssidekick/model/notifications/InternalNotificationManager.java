package tv.sportssidekick.sportssidekick.model.notifications;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import tv.sportssidekick.sportssidekick.model.GSConstants;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.user.GSMessageHandlerAbstract;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.model.wall.WallPost;
import tv.sportssidekick.sportssidekick.events.NotificationReceivedEvent;

/**
 * Created by Filip on 4/19/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class InternalNotificationManager extends GSMessageHandlerAbstract {

    private static InternalNotificationManager instance;
    private final ObjectMapper mapper; // jackson's object mapper

    public static InternalNotificationManager getInstance(){
        if(instance==null){
            instance = new InternalNotificationManager();
        }
        return instance;
    }

    private InternalNotificationManager(){
        mapper  = new ObjectMapper();
        Model.getInstance().setMessageHandlerDelegate(this);
    }

    @Override
    public void onMessage(Map<String,Object> data){
        if(data.containsKey("extCode")){
            String extCode = (String) data.get("extCode");
            NotificationReceivedEvent event;
            switch (extCode){
                case "FriendRequestAcceptedMessage":
                    event = new NotificationReceivedEvent(2, "Accepted Friend Request", "", 1);
                    EventBus.getDefault().post(event);
                    break;
                case "FriendRequestMessage":
                    event = new NotificationReceivedEvent(4, "New Friend Request", "", 1);
                    EventBus.getDefault().post(event);
                    break;
            }
        }
    }

    @Override
    public void onGSScriptMessage(String type, Map<String,Object> data){
        Log.d("Internal Notifications", "Received script message: " + type);
        String message;
        NotificationReceivedEvent event;
        switch (type){
            case "ImsMessage":
                String chatId = (String) data.get(GSConstants.CHAT_ID);
                String text = (String) data.get(GSConstants.DESCRIPTION);
                // TODO: @Filip to be implemented when done on iOS
                break;
            case "UserInfo":
                message = (String) data.get(GSConstants.MESSAGE);
                UserInfo userInfo = mapper.convertValue(data.get(GSConstants.USER_INFO), UserInfo.class);
                if(message!=null){
                    if(message.contains("unfollowing")){
                        event = new NotificationReceivedEvent(4, "Un-Followed", userInfo.getNicName(), 2);
                        EventBus.getDefault().post(event);
                    } else if(message.contains("following")){
                        event = new NotificationReceivedEvent(4, "New Follower", userInfo.getNicName(), 2);
                        EventBus.getDefault().post(event);
                    }
                }
                break;
            case "Wall":
                message = (String) data.get(GSConstants.MESSAGE);
                WallPost wallPost = mapper.convertValue(data.get(GSConstants.POST), WallPost.class);
                event = new NotificationReceivedEvent(4, "New Wall Post", message, 3);
                EventBus.getDefault().post(event);
                break;
            default:
                break;
        }

    }

}
