package base.app.model.notifications;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import base.app.events.NotificationReceivedEvent;
import base.app.model.GSConstants;
import base.app.model.Model;
import base.app.model.friendship.FriendsListChangedEvent;
import base.app.model.user.GSMessageHandlerAbstract;
import base.app.model.user.UserInfo;
import base.app.model.wall.WallBase;

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
                    event = new NotificationReceivedEvent(2, "Accepted Friend Request", "", NotificationReceivedEvent.Type.FRIEND_REQUESTS);
                    EventBus.getDefault().post(event);
                    break;
                case "FriendRequestMessage":
                    event = new NotificationReceivedEvent(4, "New Friend Request", "", NotificationReceivedEvent.Type.FRIEND_REQUESTS);
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
                    if(message.contains("stoped following")){
                        FriendsListChangedEvent eventToUpdateFriendsList = new FriendsListChangedEvent();
                        EventBus.getDefault().post(eventToUpdateFriendsList);
                        event = new NotificationReceivedEvent(4, "Un-Followed", userInfo.getNicName(), NotificationReceivedEvent.Type.FOLLOWERS);
                        EventBus.getDefault().post(event);
                    } else if(message.contains("following")){
                        event = new NotificationReceivedEvent(4, "New Follower", userInfo.getNicName(), NotificationReceivedEvent.Type.FOLLOWERS);
                        EventBus.getDefault().post(event);
                    }
                }
                break;
            case "Wall":
                String operation = (String) data.get(GSConstants.OPERATION);
                switch (operation){
                    case GSConstants.OPERATION_LIKE:
                        String action = (String) data.get(GSConstants.ACTION);



                        if(action.equals(GSConstants.OPERATION_LIKE)){
                            if(data.containsKey(GSConstants.POST)){
                                WallBase post = WallBase.postFactory(data.get(GSConstants.POST), mapper, true);
                                if(post!=null){
                                    String postId = post.getPostId();
                                    event = new NotificationReceivedEvent(4, "New Like", "", NotificationReceivedEvent.Type.LIKES,postId);
                                    EventBus.getDefault().post(event);
                                }
                            }



                        }
                        break;
                }
                break;
            default:
                break;
        }

    }

}
