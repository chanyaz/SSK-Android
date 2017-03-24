package tv.sportssidekick.sportssidekick.model.wall;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import tv.sportssidekick.sportssidekick.model.AWSFileUploader;
import tv.sportssidekick.sportssidekick.model.DateUtils;
import tv.sportssidekick.sportssidekick.model.GSConstants;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.friendship.FriendsManager;
import tv.sportssidekick.sportssidekick.model.user.GSMessageHandlerAbstract;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.service.GetCommentsCompleteEvent;
import tv.sportssidekick.sportssidekick.service.GetPostByIdEvent;
import tv.sportssidekick.sportssidekick.service.PostCommentCompleteEvent;
import tv.sportssidekick.sportssidekick.service.PostCompleteEvent;
import tv.sportssidekick.sportssidekick.service.PostLoadCompleteEvent;
import tv.sportssidekick.sportssidekick.service.PostUpdateEvent;

import static tv.sportssidekick.sportssidekick.model.Model.createRequest;

/**
 * Created by Filip on 1/6/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class WallModel extends GSMessageHandlerAbstract {

    private static final String TAG = " WallModel";
    private static WallModel instance;

    private static final long ONE_HOUR = 3600000;
    private long deltaTimeIntervalForPaging  = ONE_HOUR; // One day
    private Date oldestFetchDate;
    private Date oldestFetchIntervalDateBound;

    private Map<String, Set<String>> likesIdIndex ; // wall id -> post Id
    private final ObjectMapper mapper; // jackson's object mapper


    private int postsTotalFetchCount = 0;
    private int postsIntervalFetchCount = 0;
    private int minNumberOfPostsForInitialLoad = 20;
    private int minNumberOfPostsForIntervalLoad = 10;

    public static WallModel getInstance(){
        if(instance==null){
            instance = new WallModel();
        }
        return instance;
    }

    private WallModel(){
        likesIdIndex = new HashMap<>();
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
        likesIdIndex.clear();
        postsTotalFetchCount = 0;
        postsIntervalFetchCount = 0;
        minNumberOfPostsForInitialLoad = 20;
        minNumberOfPostsForIntervalLoad = 10;
    }

    private Task<Void> getUserPosts(String uid, Date since,final Date until){
        final TaskCompletionSource<Void> source = new TaskCompletionSource<>();
        String sinceValue = DateUtils.dateToFirebaseDate(since);

        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.POSTS);
                    // TODO Convert list of maps to concrete objects - and for each object do:
                    WallBase post = new WallBase();
                    postsTotalFetchCount += 1;
                    EventBus.getDefault().post(new PostUpdateEvent(post));
                    if (until != null){
                        postsIntervalFetchCount += 1;
                    }
                    source.setResult(null);
                }  else {
                    source.setException(new Exception());
                }
            }
        };
       GSRequestBuilder.LogEventRequest request = createRequest("wallGetUserPosts")
                .setEventAttribute(GSConstants.USER_ID,uid)
                .setEventAttribute(GSConstants.SINCE_DATE,sinceValue);
        if(until!=null){
            String untilValue = DateUtils.dateToFirebaseDate(until);
            request.setEventAttribute(GSConstants.TO_DATE,untilValue);
        }
        request.send(consumer);
        return source.getTask();
    }

    /**
     * starts getting all posts related to that uyser, all posts that where posted by this user
     * and all posts posted by the people this user follows
     * you need to listen to mbPostUpdate events which wiull return all old posts + new posts
     * + updated posts
     **/

    private void setLikesIdIndex(String wallId,String postId){
        if (likesIdIndex.get(wallId) == null){
            likesIdIndex.put(wallId, new HashSet<String>());
        }
        likesIdIndex.get(wallId).add(postId);
    }
    private void unsetLikesIdIndex(String wallId,String postId){
        if (likesIdIndex.get(wallId) != null){
            likesIdIndex.get(wallId).remove(postId);
        }
    }

    private boolean getLikesIdIndex(String wallId,String postId) {
        return likesIdIndex.get(wallId) != null && likesIdIndex.get(wallId).contains(postId);
    }

    // we first read the likes list of that user to set the right values to the relevant posts. then we load the posts...
    public void fetchPosts() {
        final String userId;
        if (getCurrentUser() == null){
            Log.e(TAG, "There is no current user info - aborting Post fetch!");
            return;
        } else {
            userId = getCurrentUser().getUserId();
        }
        clearWallListeners();
        likesIdIndex.clear();
        postsTotalFetchCount = 0;

        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.POSTS);

                    oldestFetchDate = new Date(oldestFetchDate.getTime() - deltaTimeIntervalForPaging);
                    final ArrayList<Task<Void>> tasks = new ArrayList<>();
                    tasks.add(getUserPosts(userId, oldestFetchDate,null));


                   Task<List<UserInfo>> followingTask = FriendsManager.getInstance().getUserFollowingList(Model.getInstance().getUserInfo().getUserId(),0);
                   followingTask.addOnCompleteListener(new OnCompleteListener<List<UserInfo>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<UserInfo>> task) {
                            if(task.isSuccessful()){
                                List<UserInfo> following = task.getResult();
                                if(following!=null){
                                    for(UserInfo entry : following){
                                        tasks.add(getUserPosts(entry.getUserId(), oldestFetchDate,null));
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
            }
        };
        GSRequestBuilder.LogEventRequest request = createRequest("wallGetUserLikes");
        request.send(consumer);
    }

    /**
     * this func fetches the previous posts of the last X days previous to the posts presented.
     * the retrieved posts will be returned to the caller using the "PostUpdateEvent" event
     * the posts are not ordered and may contain duplicated posts.
     *
     */
    private void fetchPreviousPageOfPosts(int initialFetchCount) {
        postsIntervalFetchCount = initialFetchCount;
        UserInfo uInfo = getCurrentUser();
        final ArrayList<Task<Void>> tasks = new ArrayList<>();
        final Date newDate =new Date(oldestFetchDate.getTime() + 1000); // Add one second to exclude oldest post from last page
        oldestFetchDate = new Date(oldestFetchDate.getTime() - deltaTimeIntervalForPaging);
        tasks.add(getUserPosts(uInfo.getUserId(), oldestFetchDate, newDate));


        Task<List<UserInfo>> followingTask = FriendsManager.getInstance().getUserFollowingList(Model.getInstance().getUserInfo().getUserId(),0);
        followingTask.addOnCompleteListener(new OnCompleteListener<List<UserInfo>>() {
            @Override
            public void onComplete(@NonNull Task<List<UserInfo>> task) {
                if(task.isSuccessful()){
                    List<UserInfo> following = task.getResult();
                    if(following!=null){
                        for(UserInfo entry : following){
                            tasks.add(getUserPosts(entry.getUserId(), oldestFetchDate,newDate));
                        }
                    }
                    Task<Void> serviceGroupTask = Tasks.whenAll(tasks);
                    serviceGroupTask.addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void o) {
                                    if (postsTotalFetchCount < minNumberOfPostsForInitialLoad || postsIntervalFetchCount < minNumberOfPostsForIntervalLoad){
                                        if (deltaTimeIntervalForPaging < 365*24* ONE_HOUR){
                                            deltaTimeIntervalForPaging *= 2;
                                        }
                                        if(oldestFetchDate.compareTo(oldestFetchIntervalDateBound)>0){
                                            fetchPreviousPageOfPosts(postsIntervalFetchCount);
                                        }else{
                                            EventBus.getDefault().post(new PostLoadCompleteEvent());
                                        }
                                    }else{
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
                    // TODO Convert to post and set local object to be equal to this one
                    WallBase post = new WallBase();
                    EventBus.getDefault().post(new PostCompleteEvent(post));

                } else {
                    EventBus.getDefault().post(new PostCompleteEvent(""));
                }
            }
        };
        Map<String, Object> map = mapper.convertValue(post, new TypeReference<Map<String, Object>>(){});
        GSData data = new GSData(map);
        createRequest("wallPostToWall")
                .setEventAttribute(GSConstants.POST,data)
                .send(consumer);
    }

    public Task<Void> setlikeVal(final WallBase post, final boolean val){
        int increase = -1;
        if(val){
            increase = 1;
        }

        if(val){
            setLikesIdIndex(post.getWallId(),post.getPostId());
        }else{
            unsetLikesIdIndex(post.getWallId(),post.getPostId());
        }
        final TaskCompletionSource<Void> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.POST);
                    // TODO Convert to post and set local object to be equal to this one
                    WallBase post = new WallBase();
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
                    // TODO Convert to post and set local object to be equal to this one
                    List<PostComment> comments = new ArrayList<>();
                    EventBus.getDefault().post(new GetCommentsCompleteEvent(comments));
                } else {
                    // TODO What to do in case of error?
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
    public Task postComment(final WallBase post, final PostComment comment){
        // TODO Rewrite to GS
        final TaskCompletionSource source = new TaskCompletionSource<>();

        //TODO - post notification
        comment.setId(DateUtils.currentTimeToFirebaseDate() + AWSFileUploader.generateRandName(10));

        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.COMMENT);
                    // TODO Convert to post and set local object to be equal to this one
                    PostComment comment = new PostComment();
                    EventBus.getDefault().post(new PostCommentCompleteEvent(comment));
                    source.setResult(null);
                } else {
                    source.setException(null);
                    // TODO What to do in case of error?
                }
            }
        };
        Map<String, Object> map = mapper.convertValue(comment, new TypeReference<Map<String, Object>>(){});
        GSData data = new GSData(map);
        createRequest("wallAddPostComment")
                .setEventAttribute(GSConstants.COMMENT,data)
                .send(consumer);
        return source.getTask();
    }

    // TODO: Notify GS of successful share, increment count, callback to app to allow state and UI updates
    /**
     * post was shared on facebook
     *
     **/
    public void facebookShare(WallBase post) {}

    /**
     * post was shared on twitter
     *
     **/
    public void twitterShare(WallBase post) {}

    /**
     * get post by its id
     *
     */
    public void getPostById(String wallId, String postId){
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.POST);
                    // TODO Convert to post and set local object to be equal to this one
                    EventBus.getDefault().post(new GetPostByIdEvent(new WallPost()));
                } else {
                    // TODO What to do in case of error?
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
                String operation = (String) data.get(GSConstants.OPERATION);
                if(operation!=null){
                    if(operation.equals("new_post") ||operation.equals("update_post") ){
                        Object object = data.get(GSConstants.POST);
                        // TODO Convert to post and set local object to be equal to this one
                        EventBus.getDefault().post(new PostUpdateEvent(new WallPost()));
                    } else if (operation.equals(GSConstants.COMMENT)){ // WTF - Same code?
                        Object object = data.get(GSConstants.POST);
                        // TODO Convert to post and set local object to be equal to this one
                        EventBus.getDefault().post(new PostUpdateEvent(new WallPost()));
                    }
                }
                break;
            default:
                Log.e(TAG,"UNHANDLED ScriptMessage type: " + type + " DATA: " + data);
                break;
        }

    }
}

