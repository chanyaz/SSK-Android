package tv.sportssidekick.sportssidekick.model.wall;

import android.support.annotation.NonNull;

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
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.service.PostCompleteEvent;
import tv.sportssidekick.sportssidekick.service.PostLoadCompleteEvent;
import tv.sportssidekick.sportssidekick.service.PostUpdateEvent;

import static tv.sportssidekick.sportssidekick.model.Model.createRequest;

/**
 * Created by Filip on 1/6/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class WallModel {

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

    private SimpleDateFormat sdf;

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
        sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        try {
            oldestFetchIntervalDateBound = sdf.parse("2016-10-15 00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
    public void mbListenerToUserWall() {
        final UserInfo uInfo = getCurrentUser();
        if (uInfo == null){
            return;
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
                    tasks.add(getUserPosts(uInfo.getUserId(), oldestFetchDate,null));


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

    public Task setlikeVal(final WallBase post, final boolean val){
        int increase = -1;
        if(val){
            increase = 1;
        }
        final UserInfo userInfo = getCurrentUser();

        if(val){
            setLikesIdIndex(post.getWallId(),post.getPostId());

            // post a notification re: like
            HashMap<String, Object> notificationQueueMessage = new HashMap<>();
            notificationQueueMessage.put("wallId",post.getWallId());
            notificationQueueMessage.put("postId",post.getPostId());
            notificationQueueMessage.put("newLike",true);
            notificationQueueMessage.put("likerId",userInfo.getUserId());
            notificationQueueMessage.put("likerName",userInfo.getNicName());

            // TODO Rewrite to GS
//            DatabaseReference notificationRef = mbNotificationQueueRef.push();
//            notificationRef.setValue(notificationQueueMessage);
        }else{
            unsetLikesIdIndex(post.getWallId(),post.getPostId());
        }
        final TaskCompletionSource source = new TaskCompletionSource<>();
        final int finalIncrement = increase;
        // TODO Rewrite to GS
//        DatabaseReference postRef = mbPostRef.child(post.getWallId()).child(post.getPostId()).child("likeCount");
//        postRef.runTransaction(new Transaction.Handler() {
//            @Override
//            public Transaction.Result doTransaction(MutableData mutableData) {
//                int count = mutableData.getValue(Integer.class);
//                count = count + finalIncrement;
//                mutableData.setValue(count);
//                return Transaction.success(mutableData);
//            }
//            @Override
//            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
//                if(databaseError==null){
//                } else if (committed){
//                    DatabaseReference postLikeRef = mbLikesRef.child(userInfo.getUserId()).child(post.getWallId()).child(post.getPostId());
//                    if(val){
//                        postLikeRef.setValue(DateUtils.currentTimeToFirebaseDate());
//                    }else{
//                        postLikeRef.removeValue();
//                    }
//                }
//                source.setResult(databaseError);
//            }
//        });
        return source.getTask();
    }

    /**
     * get all the Comments For the given Post
     *
     */
    public void getCommentsForPost(WallBase post){
        // TODO Rewrite to GS
//        DatabaseReference commentsRef = mbCommentsRef.child(post.getWallId()).child(post.getPostId());
//        commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                ArrayList<PostComment> comments = new ArrayList<>();
//                for(DataSnapshot snap : dataSnapshot.getChildren()){
//                    PostComment comment = snap.getValue(PostComment.class);
//                    comments.add(comment);
//                }
//                EventBus.getDefault().post(new GetCommentsCompleteEvent(comments));
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        });
    }

    /**
     * post the comment on the given post, once the comment is successfully stored in DB
     * the post comments count will be increeased by 1
     *
     */
    public Task postComment(final WallBase post, final PostComment comment){
        // TODO Rewrite to GS
        final TaskCompletionSource source = new TaskCompletionSource<>();
//        DatabaseReference commentRef = mbCommentsRef.child(post.getWallId()).child(post.getPostId()).push();
//        comment.setCommentId(commentRef.getKey());
//        commentRef.setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                DatabaseReference postRef = mbPostRef.child(post.getWallId()).child(post.getPostId()).child("commentsCount");
//
//                postRef.runTransaction(new Transaction.Handler() {
//                    @Override
//                    public Transaction.Result doTransaction(MutableData mutableData) {
//                        int count = mutableData.getValue(Integer.class);
//                        count = count + 1;
//                        mutableData.setValue(count);
//                        return Transaction.success(mutableData);
//                    }
//
//                    @Override
//                    public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
//                        if(databaseError!=null){
//                            source.setResult(databaseError);
//                        } else if (committed){
//                            UserInfo userInfo = getCurrentUser();
//
//                            // post a notification re: like
//                            HashMap<String, Object> notificationQueueMessage = new HashMap<>();
//                            notificationQueueMessage.put("wallId",post.getWallId());
//                            notificationQueueMessage.put("postId",post.getPostId());
//                            notificationQueueMessage.put("newComment",true);
//                            notificationQueueMessage.put("comment",comment);
//                            notificationQueueMessage.put("commenterId",userInfo.getUserId());
//                            notificationQueueMessage.put("commenterName",userInfo.getNicName());
//
//                            DatabaseReference notificationRef = mbNotificationQueueRef.push();
//                            notificationRef.setValue(notificationQueueMessage);
//                            source.setResult(null);
//                        }
//                        EventBus.getDefault().post(new PostCommentCompleteEvent(databaseError));
//                    }
//                });
//            }
//        });
        return source.getTask();
    }

    /**
     * post was shared on facebook
     *
     **/
    public void facebookShare(WallBase post)
    {
        // post a notification re: comment
        UserInfo userInfo = getCurrentUser();
        HashMap<String, Object> notificationQueueMessage = new HashMap<>();
        notificationQueueMessage.put("wallId",post.getWallId());
        notificationQueueMessage.put("postId",post.getPostId());
        notificationQueueMessage.put("facebookShare",true);
        notificationQueueMessage.put("sharerId",userInfo.getUserId());
        notificationQueueMessage.put("sharerName",userInfo.getNicName());
        // TODO Rewrite to GS
//        DatabaseReference notificationRef = mbNotificationQueueRef.push();
//        notificationRef.setValue(notificationQueueMessage);
    }

    /**
     * post was shared on twitter
     *
     **/
    public void twitterShare(WallBase post)
    {
        // post a notification re: comment
        UserInfo userInfo = getCurrentUser();
        HashMap<String, Object> notificationQueueMessage = new HashMap<>();
        notificationQueueMessage.put("wallId",post.getWallId());
        notificationQueueMessage.put("postId",post.getPostId());
        notificationQueueMessage.put("twitterShare",true);
        notificationQueueMessage.put("sharerId",userInfo.getUserId());
        notificationQueueMessage.put("sharerName",userInfo.getNicName());
        // TODO Rewrite to GS
//        DatabaseReference notificationRef = mbNotificationQueueRef.push();
//        notificationRef.setValue(notificationQueueMessage);
    }

    /**
     * get post by its id
     *
     */
    public void getPostById(String wallId, String postId){
        // TODO Rewrite to GS
//        DatabaseReference pRef = mbPostRef.child(wallId).child(postId);
//        pRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                WallBase post = WallBase.postFactory(dataSnapshot);
//                EventBus.getDefault().post(new GetPostByIdEvent(post));
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        });
    }

    // TODO FIXME Remove this after backend is properly implemented!
    public void setupEliavAsUserAndInitialize(){
       Task<UserInfo> task = Model.getInstance().getUserInfoById("sLqHBMbL3BQNgddTK0a4wmPfuA53");
        task.addOnSuccessListener(new OnSuccessListener<UserInfo>() {
            @Override
            public void onSuccess(UserInfo info) {
                userInfo = info;
                mbListenerToUserWall();
            }
        });
    }

    UserInfo userInfo;
    private UserInfo getCurrentUser(){
//        return Model.getInstance().getUserInfo();
        return userInfo;
    }
}

