package base.app.data.user.notifications;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import base.app.ui.fragment.user.auth.LoginApi;
import base.app.util.commons.GSConstants;
import base.app.data.TypeConverter;
import base.app.data.content.wall.FeedItem;
import base.app.data.user.GSMessageHandlerAbstract;
import base.app.data.user.User;
import base.app.util.events.FriendsListChangedEvent;
import base.app.util.events.NotificationEvent;

/**
 * Created by Filip on 4/19/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class InternalNotificationManager extends GSMessageHandlerAbstract {

    private static InternalNotificationManager instance;
    private final ObjectMapper mapper;

    public static InternalNotificationManager getInstance(){
        if(instance==null){
            instance = new InternalNotificationManager();
        }
        return instance;
    }

    private InternalNotificationManager(){
        mapper  = new ObjectMapper();
        LoginApi.getInstance().setMessageHandlerDelegate(this);
    }

    @Override
    public void onMessage(Map<String,Object> data){
        if(data.containsKey("extCode")){
            String extCode = (String) data.get("extCode");
            NotificationEvent event;
            switch (extCode){
                case "FriendRequestAcceptedMessage":
                    event = new NotificationEvent(4, "Accepted Friend Request", "", NotificationEvent.Type.FRIEND_REQUESTS);
                    EventBus.getDefault().post(event);
                    break;
                case "FriendRequestMessage":
                    event = new NotificationEvent(4, "New Friend Request", "", NotificationEvent.Type.FRIEND_REQUESTS);
                    EventBus.getDefault().post(event);
                    break;
            }
        }
    }

    @Override
    public void onGSScriptMessage(String type, Map<String,Object> data){
        Log.d("Internal Notifications", "Received script message: " + type);
        String message;
        NotificationEvent event;
        switch (type){
            case "ChatMessage":
                String chatId = (String) data.get(GSConstants.CHAT_ID);
                String description = (String) data.get(GSConstants.DESCRIPTION);
                String operation = (String) data.get(GSConstants.OPERATION);

                if (operation.equals("update")) {
                    event = new NotificationEvent(4, "New chat message", description, NotificationEvent.Type.IMS_MESSAGES);
                    EventBus.getDefault().post(event);
                }
                break;
            case "UserInfo":
                message = (String) data.get(GSConstants.MESSAGE);
                User user = mapper.convertValue(data.get(GSConstants.USER_INFO), User.class);
                if(message!=null){
                    if(message.contains("stoped following")){
                        FriendsListChangedEvent eventToUpdateFriendsList = new FriendsListChangedEvent();
                        EventBus.getDefault().post(eventToUpdateFriendsList);
                        event = new NotificationEvent(4, "Un-Followed", user.getNicName(), NotificationEvent.Type.FOLLOWERS);
                        EventBus.getDefault().post(event);
                    } else if(message.contains("following")){
                        event = new NotificationEvent(4, "New Follower", user.getNicName(), NotificationEvent.Type.FOLLOWERS);
                        EventBus.getDefault().post(event);
                    }
                }
                break;
            case "Wall":
                operation = (String) data.get(GSConstants.OPERATION);
                switch (operation){
                    case GSConstants.OPERATION_LIKE:
                        String action = (String) data.get(GSConstants.ACTION);
                        if(action.equals(GSConstants.OPERATION_LIKE)){
                            if(data.containsKey(GSConstants.POST)){
                                FeedItem post = TypeConverter.postFactory(data.get(GSConstants.POST), mapper, true);
                                if(post!=null){
                                    String postId = post.getId();
                                    event = new NotificationEvent(4, "New Like", "", NotificationEvent.Type.LIKES);
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
