package base.app.model.wall;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.GSData;
import com.gamesparks.sdk.api.autogen.GSRequestBuilder;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;

import org.greenrobot.eventbus.EventBus;
import org.json.simple.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import base.app.model.AWSFileUploader;
import base.app.model.DateUtils;
import base.app.model.GSConstants;
import base.app.model.Model;
import base.app.model.friendship.FriendsManager;
import base.app.model.sharing.SharingManager;
import base.app.model.user.GSMessageHandlerAbstract;
import base.app.model.user.UserInfo;
import base.app.events.GetCommentsCompleteEvent;
import base.app.events.GetPostByIdEvent;
import base.app.events.PostCommentCompleteEvent;
import base.app.events.PostCompleteEvent;
import base.app.events.PostLoadCompleteEvent;
import base.app.events.PostUpdateEvent;

import static base.app.ClubConfig.CLUB_ID;
import static base.app.model.GSConstants.CLUB_ID_TAG;
import static base.app.model.Model.createRequest;

/**
 * Created by Filip on 1/6/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class WallModel extends GSMessageHandlerAbstract {

    private static final String TAG = " WallModel";
    private static WallModel instance;

    private static final long ONE_HOUR = 3600000;
    private static final long D_LIMIT = 365 * 24 * ONE_HOUR;
    private long deltaTimeIntervalForPaging  = ONE_HOUR; // One day
    private Date oldestFetchDate;
    private Date oldestFetchIntervalDateBound;

    private final ObjectMapper mapper; // jackson's object mapper


    private int postsTotalFetchCount = 0;
    private int postsIntervalFetchCount = 0;
    private int minNumberOfPostsForInitialLoad = 20;
    private int minNumberOfPostsForIntervalLoad = 10;

    HashMap<String, WallBase> cahchedItems;
    public List<WallBase> getListCacheItems() {
        return listCacheItems;
    }
    List<WallBase> listCacheItems;


    public static WallModel getInstance(){
        if(instance==null){
            instance = new WallModel();
        }
        return instance;
    }

    private WallModel(){
        oldestFetchDate = new Date();
        mapper  = new ObjectMapper();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        try {
            oldestFetchIntervalDateBound = sdf.parse("2016-10-15 00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Model.getInstance().setMessageHandlerDelegate(this);
    }

    private void clearWallListeners(){}

    //user logged out so clearing all content.
    public void clear(){
        clearWallListeners();
        postsTotalFetchCount = 0;
        postsIntervalFetchCount = 0;
        minNumberOfPostsForInitialLoad = 20;
        minNumberOfPostsForIntervalLoad = 10;
    }

    // NOT USED
    private void onUserPosts(WallBase post){
//        if(post.getTimestamp()) {
//            if(post.timestamp.compare(oldestFetchDate) == ComparisonResult.orderedDescending ) {
//                self.notifyPostUpdate.emit(post)
//            }
//        }
    }

    private Task<Void> getUserPosts(final UserInfo userInfo, Date since,final Date toDate){
        final TaskCompletionSource<Void> source = new TaskCompletionSource<>();
        String sinceValue = DateUtils.dateToFirebaseDate(since);

        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    JSONArray jsonArrayOfPosts = (JSONArray) response.getScriptData().getBaseData().get(GSConstants.POSTS);
                    if(jsonArrayOfPosts.size()>0){
                         for(Object postAsJson : jsonArrayOfPosts){
                            WallBase post = WallBase.postFactory(postAsJson, mapper);
                             if(post!=null){
                                 post.setSubTitle(userInfo.getNicName());
                                 postsTotalFetchCount += 1;
                                 EventBus.getDefault().post(new PostUpdateEvent(post));
                                 if (toDate != null){
                                     postsIntervalFetchCount += 1;
                                 }
                             }
                        }
                    }
                    source.setResult(null);
                    return;
                }
                source.setException(new Exception("There was an error while trying to get user's posts."));
            }
        };
       GSRequestBuilder.LogEventRequest request = createRequest("wallGetUserPosts")
                .setEventAttribute(GSConstants.USER_ID,userInfo.getUserId())
                .setEventAttribute(GSConstants.SINCE_DATE,sinceValue);
        if(toDate!=null){
            String untilValue = DateUtils.dateToFirebaseDate(toDate);
            request.setEventAttribute(GSConstants.TO_DATE,untilValue);
        }
        request.send(consumer);
        return source.getTask();
    }


     /** starts getting all posts related to that user, all posts that where posted by this user
     and all posts posted by the people this user follows
     you need to listen to mbPostUpdate events which will return
     all old posts + new posts + updated posts **/

    public void fetchPosts() {
        if (getCurrentUser() == null){
            Log.e(TAG, "There is no current user info - aborting Post fetch!");
            return;
        }
        clearWallListeners();
        postsTotalFetchCount = 0;

        oldestFetchDate = new Date(oldestFetchDate.getTime() - deltaTimeIntervalForPaging);
        final ArrayList<Task<Void>> tasks = new ArrayList<>();
        tasks.add(getUserPosts(getCurrentUser(), oldestFetchDate,null));


        Task<List<UserInfo>> followingTask = FriendsManager.getInstance().getUserFollowingList(Model.getInstance().getUserInfo().getUserId(),0);
        followingTask.addOnCompleteListener(new OnCompleteListener<List<UserInfo>>() {
            @Override
            public void onComplete(@NonNull Task<List<UserInfo>> task) {
                if(task.isSuccessful()){
                    List<UserInfo> following = task.getResult();
                    if(following!=null){
                        for(UserInfo entry : following){
                            tasks.add(getUserPosts(entry, oldestFetchDate,null));
                        }
                    }
                    Task<Void> serviceGroupTask = Tasks.whenAll(tasks);
                    serviceGroupTask.addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void o) {
                                    if(postsTotalFetchCount < 20){
                                        fetchPreviousPageOfPosts(0);
                                    } else {
                                        EventBus.getDefault().post(new PostLoadCompleteEvent());
                                    }
                                }
                            }
                    );
                }
            }
        });
    }

    /**
     * this func fetches the previous posts of the last X days previous to the posts presented.
     * the retrieved posts will be returned to the caller using the "PostUpdateEvent" event
     * the posts are not ordered and may contain duplicated posts.
     *
     */
    public void fetchPreviousPageOfPosts(int initialFetchCount) {
        postsIntervalFetchCount = initialFetchCount;
        UserInfo uInfo = getCurrentUser();
        if (uInfo == null) {
            EventBus.getDefault().post(new PostLoadCompleteEvent());
            return;
        }
        final ArrayList<Task<Void>> tasks = new ArrayList<>();
        final Date newDate =new Date(oldestFetchDate.getTime() + 1000); // Add one second to exclude oldest post from last page
        oldestFetchDate = new Date(oldestFetchDate.getTime() - deltaTimeIntervalForPaging);

        tasks.add(getUserPosts(uInfo, oldestFetchDate, newDate));

        Task<List<UserInfo>> followingTask = FriendsManager.getInstance().getUserFollowingList(uInfo.getUserId(), 0);
        followingTask.addOnCompleteListener(new OnCompleteListener<List<UserInfo>>() {
            @Override
            public void onComplete(@NonNull Task<List<UserInfo>> task) {
                if (task.isSuccessful()) {
                    List<UserInfo> following = task.getResult();
                    if (following != null) {
                        for (UserInfo entry : following) {
                            tasks.add(getUserPosts(entry, oldestFetchDate, newDate));
                        }
                    }
                    Task<Void> serviceGroupTask = Tasks.whenAll(tasks);
                    serviceGroupTask.addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void o) {
                                    boolean stillToLoad = postsTotalFetchCount < minNumberOfPostsForInitialLoad ||
                                            postsIntervalFetchCount < minNumberOfPostsForIntervalLoad;
                                    if (!stillToLoad) {
                                        EventBus.getDefault().post(new PostLoadCompleteEvent());
                                        return;
                                    }
                                    if (deltaTimeIntervalForPaging < D_LIMIT) {
                                        deltaTimeIntervalForPaging *= 2;
                                    }
                                    boolean fetchNext = oldestFetchDate.compareTo(oldestFetchIntervalDateBound) > 0;

                                    if (fetchNext) {
                                        fetchPreviousPageOfPosts(postsIntervalFetchCount);
                                        return;
                                    }
                                    EventBus.getDefault().post(new PostLoadCompleteEvent());
                                }
                            }
                    );
                }
            }
        });
    }

    /**
     * posting a new blog on this user wall
     * you need to listen on the post to get completion signal with success info
     *
     * @param  post to post
     */
    public void mbPost(WallBase post){
        post.setWallId(getCurrentUser().getUserId());
        post.setPostId(DateUtils.currentTimeToFirebaseDate() + AWSFileUploader.generateRandName(10));

        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.POST);
                    WallBase post = WallBase.postFactory(object, mapper);
                    EventBus.getDefault().post(new PostUpdateEvent(post));
                    EventBus.getDefault().post(new PostCompleteEvent(post));
                }
            }
        };
        Map<String, Object> map = mapper.convertValue(post, new TypeReference<Map<String, Object>>(){});
        map.put("type", post.getTypeAsInt());
        GSData data = new GSData(map);
        createRequest("wallPostToWall")
                .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
                .setEventAttribute(GSConstants.POST,data)
                .send(consumer);
    }

    public Task<Void> setlikeVal(final WallBase post, final boolean val){
        int increase = val ? 1 : -1;
        final TaskCompletionSource<Void> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.POST);
                    WallBase post = WallBase.postFactory(object, mapper);
                    EventBus.getDefault().post(new PostUpdateEvent(post));
                } else {
                    EventBus.getDefault().post(new PostUpdateEvent(null));
                }
                source.setResult(null);
            }
        };
        createRequest("wallUpdatePostLike")
                .setEventAttribute(GSConstants.WALL_ID,post.getWallId())
                .setEventAttribute(GSConstants.POST_ID,post.getPostId())
                .setEventAttribute(GSConstants.INCREASE,increase)
                .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
                .send(consumer);
        return source.getTask();
    }

    /**
     * get all the Comments For the given Post
     *
     */
    public void getCommentsForPost(WallBase post){
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.COMMENTS);
                    List<PostComment> comments = mapper.convertValue(object, new TypeReference<List<PostComment>>(){});
                    EventBus.getDefault().post(new GetCommentsCompleteEvent(comments));
                } else {
                    EventBus.getDefault().post(new GetCommentsCompleteEvent(null));
                }
            }
        };
        createRequest("wallGetPostComments")
            .setEventAttribute(GSConstants.WALL_ID,post.getWallId())
            .setEventAttribute(GSConstants.POST_ID,post.getPostId())
            .setEventAttribute(GSConstants.OFFSET,0)
            .setEventAttribute(GSConstants.ENTRY_COUNT,30)
            .send(consumer);
    }

    /**
     * post the comment on the given post, once the comment is successfully stored in DB
     * the post comments count will be increeased by 1
     *
     */
    public Task<PostComment> postComment(final WallBase post, final PostComment comment){
        final TaskCompletionSource<PostComment> source = new TaskCompletionSource<>();
        comment.setId(DateUtils.currentTimeToFirebaseDate() + AWSFileUploader.generateRandName(10));
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.COMMENT);
                    PostComment comment = mapper.convertValue(object, new TypeReference<PostComment>(){});
                    EventBus.getDefault().post(new PostCommentCompleteEvent(comment));
                    source.setResult(comment);
                } else {
                    source.setException(new Exception("Posting of comment failed!"));
                }
            }
        };
        Map<String, Object> map = mapper.convertValue(comment, new TypeReference<Map<String, Object>>(){});
        GSData data = new GSData(map);
        createRequest("wallAddPostComment")
                .setEventAttribute(GSConstants.COMMENT,data)
                .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
                .send(consumer);
        return source.getTask();
    }

    /**
     * post was shared to a share target
     * @param  item post, the target
     * @param  shareTarget where post should be shared
     */

    public void itemShared(final WallBase item, SharingManager.ShareTarget shareTarget) {
        SharingManager.getInstance().increment(item,shareTarget).addOnCompleteListener(new OnCompleteListener<Map<String, Object>>() {
            @Override
            public void onComplete(@NonNull Task<Map<String, Object>> task) {
                if(task.isSuccessful()){
                    WallBase post = WallBase.postFactory(task.getResult(), mapper);
                    item.setEqualTo(post);
                }
            }
        });
    }

    public void getPostById(String wallId, String postId){
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.POST);
                    WallBase post = WallBase.postFactory(object, mapper);
                    EventBus.getDefault().post(new GetPostByIdEvent(post));
                } else {
                    EventBus.getDefault().post(new GetPostByIdEvent(null));
                }
            }
        };
        createRequest("wallGetPostById")
                .setEventAttribute(GSConstants.WALL_ID,wallId)
                .setEventAttribute(GSConstants.POST_ID,postId)
                .send(consumer);

    }

    private UserInfo getCurrentUser(){
        return Model.getInstance().getUserInfo();
    }

    @Override
    public void onGSScriptMessage(String type, Map<String,Object> data){
        switch (type){
            case "Wall":
                parseWallMessage(data);
                break;
            case "sharingCountIncremented":
                parseShareCountMessage(data);
            default:
                //Log.e(TAG,"UNHANDLED ScriptMessage type: " + type + " DATA: " + data);
                break;
        }

    }

    private void parseShareCountMessage(Map<String, Object> data) {
        String itemType = (String) data.get(GSConstants.ITEM_TYPE);
        if(itemType!=null) {
            switch (itemType) {
                case GSConstants.WALL_POST:
                case GSConstants.NEWS:
                case GSConstants.RUMOUR:
                    WallBase post = WallBase.postFactory(data.get(GSConstants.ITEM), mapper);
                    EventBus.getDefault().post(new PostUpdateEvent(post));
                    break;
            }
        }
    }

    public void parseWallMessage(Map<String,Object> data){
        String operation = (String) data.get(GSConstants.OPERATION);
        if(operation!=null){
            Object object = data.get(GSConstants.POST);
            WallBase post = WallBase.postFactory(object, mapper);
            if(post!=null){
                switch (operation){
                    case GSConstants.OPERATION_COMMENT:
                    case GSConstants.OPERATION_NEW_POST:
                    case GSConstants.OPERATION_UPDATE_POST:
                        EventBus.getDefault().post(new PostUpdateEvent(post));
                }
            }
        }
    }
}

