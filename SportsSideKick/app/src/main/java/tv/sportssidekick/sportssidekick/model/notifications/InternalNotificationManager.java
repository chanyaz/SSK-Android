package tv.sportssidekick.sportssidekick.model.notifications;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import tv.sportssidekick.sportssidekick.model.GSConstants;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.user.GSMessageHandlerAbstract;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.model.wall.WallPost;

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
            switch (extCode){
                case "FriendRequestAcceptedMessage":
                    // TODO Show notification - title: "Accepted Friend Request", description: "", closeTime: 2
                    // TODO set notification on click listener to show all friends popup!
                    break;
                case "FriendRequestMessage":
                    // TODO Show notification - title: "New Friend Request", description: "", closeTime: 4
                    // TODO set notification on click listener to show all friends popup!
                    break;
            }
        }
    }

    @Override
    public void onGSScriptMessage(String type, Map<String,Object> data){
        Log.d("Internal Notifications", "Received script message: " + type);
        String message;
        switch (type){
            case "ImsMessage":
                String chatId = (String) data.get(GSConstants.CHAT_ID);
                String text = (String) data.get(GSConstants.DESCRIPTION);
                // TODO: to be implemented when done on iOS
                break;
            case "UserInfo":
                message = (String) data.get(GSConstants.MESSAGE);
                UserInfo userInfo = mapper.convertValue(data.get(GSConstants.USER_INFO), UserInfo.class);
                if(message!=null){
                    if(message.contains("unfollowing")){
                        // TODO Show notification - title: "New Follower", description: userInfo.getNicName(), closeTime: 4
                        // TODO set notification on click listener to show followers popup!
                    } else if(message.contains("following")){
                        // TODO Show notification - title: "Un-Followed", description: userInfo.getNicName(), closeTime: 4
                        // TODO set notification on click listener to show followers popup!
                    }
                }
                break;
            case "Wall":
                message = (String) data.get(GSConstants.MESSAGE);
                WallPost wallPost = mapper.convertValue(data.get(GSConstants.POST), WallPost.class);
                // TODO Show notification - title: "New Wall Post", description: message, closeTime: 4
                // TODO set notification on click listener to show new wall post
                break;
            default:
                break;
        }

    }

}
