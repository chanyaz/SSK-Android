package tv.sportssidekick.sportssidekick.model.wall;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

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

import tv.sportssidekick.sportssidekick.model.FirebaseDateUtils;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.UserInfo;
import tv.sportssidekick.sportssidekick.service.GetCommentsCompleteEvent;
import tv.sportssidekick.sportssidekick.service.GetPostByIdEvent;
import tv.sportssidekick.sportssidekick.service.PostCommentCompleteEvent;
import tv.sportssidekick.sportssidekick.service.PostCompleteEvent;
import tv.sportssidekick.sportssidekick.service.PostLoadCompleteEvent;
import tv.sportssidekick.sportssidekick.service.PostUpdateEvent;

/**
 * Created by Filip on 1/6/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class WallModel {

    public static final String TIMESTAMP_TAG = "timestamp";
    private static WallModel instance;

    private DatabaseReference mbPostRef;
    private DatabaseReference mbLikesRef;
    private DatabaseReference mbCommentsRef;
    private DatabaseReference mbNotificationQueueRef;

    private long deltaTimeIntervalForPaging  = 60*60;
    private Date oldestFetchDate;
    private Date oldestFetchIntervalDateBound;

    private Map<String, Set<String>> likesIdIndex ; // wall id -> post Id

    public int getPostsTotalFetchCount() {
        return postsTotalFetchCount;
    }

    private int postsTotalFetchCount;
    private int postsIntervalFetchCount;
    private int minNumberOfPostsForInitialLoad;
    private int minNumberOfPostsForIntervalLoad;
    private List<DatabaseReference> mbWallListeners;
    private List<Query> mbWallListenersQ;

    public static WallModel getInstance(){
        if(instance==null){
            instance = new WallModel();
        }
        return instance;
    }

    private WallModel(){
        mbWallListeners = new ArrayList<>();
        mbWallListenersQ = new ArrayList<>();
        likesIdIndex = new HashMap<>();
        oldestFetchDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            oldestFetchIntervalDateBound = sdf.parse("2016-10-15");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DatabaseReference mbRef = FirebaseDatabase.getInstance().getReference().child("microBlogs");//root of all MB
        mbPostRef = mbRef.child("posts");
        mbLikesRef = mbRef.child("likes");
        mbCommentsRef = mbRef.child("comments");
        mbNotificationQueueRef = FirebaseDatabase.getInstance().getReference().child("queue-notify-mb/tasks");
    }

    private void clearWallListeners(){
        for(DatabaseReference ref :mbWallListeners){
            ref.removeEventListener(postChangedListener);
        }
        for(Query query : mbWallListenersQ){
            query.removeEventListener(postAddedListener);
        }
        mbWallListeners.clear();
        mbWallListenersQ.clear();
    }

    //user logged out so clearing all content.
    public void clear(){
        clearWallListeners();
        likesIdIndex.clear();
        postsTotalFetchCount = 0;
        postsIntervalFetchCount = 0;
        minNumberOfPostsForInitialLoad = 20;
        minNumberOfPostsForIntervalLoad = 10;
    }

    private Task<Void> mbListenerToUserPosts(String uid, Date since){
        final TaskCompletionSource<Void> source = new TaskCompletionSource<>();
        String sinceValue = FirebaseDateUtils.dateToFirebaseDate(since);
        Query mbUserPostsRef = mbPostRef.child(uid);
       // .orderByChild(TIMESTAMP_TAG).startAt(sinceValue) // TODO Investigate
        mbUserPostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    WallBase post = WallBase.postFactory(snap);
                    if(post!=null){
                        Set<String> likedPostsIds = likesIdIndex.get(post.getWallId());
                        if(likedPostsIds!=null){
                            post.setLikedByUser(likedPostsIds.contains(post.getPostId())); // update liked status
                        }
                        postsTotalFetchCount++;
                        EventBus.getDefault().post(new PostUpdateEvent(post));
                    }
                }
                source.setResult(null);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        Query mbUserRef = mbPostRef.child(uid).limitToLast(1);
        mbUserRef.addChildEventListener(postAddedListener);
        mbWallListenersQ.add(mbUserRef);

        DatabaseReference mbUserRef2 = mbPostRef.child(uid);
        mbUserRef2.addChildEventListener(postChangedListener);
        mbWallListeners.add(mbUserRef2);

        return source.getTask();
    }

    private ChildEventListener postAddedListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            WallBase post = WallBase.postFactory(dataSnapshot);
            if(post!=null){
                if(oldestFetchDate.compareTo(new Date(FirebaseDateUtils.getTimestampFromFirebaseDate(post.getTimestamp())))>0){ // Check how to compare this and what compare result returns ?
                    Set<String> likedPostsIds = likesIdIndex.get(post.getWallId());
                    if(likedPostsIds!=null){
                        post.setLikedByUser(likedPostsIds.contains(post.getPostId())); // update liked status
                    }
                    EventBus.getDefault().post(new PostUpdateEvent(post));
                }
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {}
        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
        @Override
        public void onCancelled(DatabaseError databaseError) {}
    };


    private ChildEventListener postChangedListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {}
        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            WallBase post = WallBase.postFactory(dataSnapshot);
            Set<String> likedPostsIds = null;
            if (post != null) {
                likedPostsIds = likesIdIndex.get(post.getWallId());
            }
            if(likedPostsIds!=null){
                post.setLikedByUser(likedPostsIds.contains(post.getPostId())); // update liked status
            }
            EventBus.getDefault().post(new PostUpdateEvent(post));
        }
        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {}
        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
        @Override
        public void onCancelled(DatabaseError databaseError) {}
    };

    private Task<Void> mbListenerToUserPosts(String uid, Date since, Date until){
        final TaskCompletionSource<Void> source = new TaskCompletionSource<>();
        String sinceValue = FirebaseDateUtils.dateToFirebaseDate(since);
        String untilValue = FirebaseDateUtils.dateToFirebaseDate(until);
        Query mbUserPostsRef = mbPostRef.child(uid).orderByChild(TIMESTAMP_TAG).startAt(sinceValue).endAt(untilValue);
        mbUserPostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    WallBase post = WallBase.postFactory(snap);
                    if(post!=null){
                        Set<String> likedPostsIds = likesIdIndex.get(post.getWallId());
                        if(likedPostsIds!=null){
                            post.setLikedByUser(likedPostsIds.contains(post.getPostId())); // update liked status
                        }
                        postsTotalFetchCount++;
                        postsIntervalFetchCount++;
                        EventBus.getDefault().post(new PostUpdateEvent(post));
                    }
                }
                source.setResult(null);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
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

    // we first read the likes list of that user to set the right values to  the relevant posts. then we load the posts...
    public void mbListenerToUserWall() {
        final UserInfo uInfo = getCurrentUser();
        if (uInfo == null){
            return;
        }
        clearWallListeners();
        DatabaseReference postLikeRef = mbLikesRef.child(uInfo.getUserId());
        likesIdIndex.clear();
        postsTotalFetchCount = 0;

        postLikeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot wall : dataSnapshot.getChildren()){
                    for(DataSnapshot post : wall.getChildren()){
                        setLikesIdIndex(wall.getKey(),post.getKey());
                    }
                }

                oldestFetchDate = new Date(oldestFetchDate.getTime() + deltaTimeIntervalForPaging);

                ArrayList<Task<Void>> tasks = new ArrayList<>();
                tasks.add(mbListenerToUserPosts(uInfo.getUserId(), oldestFetchDate));

                HashMap<String, Boolean> following = uInfo.getFollowing();
                if(following!=null){
                    for(Map.Entry<String,Boolean> entry : following.entrySet()){
                        tasks.add(mbListenerToUserPosts(entry.getKey(), oldestFetchDate));
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
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
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
        ArrayList<Task<Void>> tasks = new ArrayList<>();

        oldestFetchDate = new Date(oldestFetchDate.getTime() - deltaTimeIntervalForPaging);
//        let newDate = Date(timeIntervalSinceReferenceDate: 1 + self.oldestFetchDate.timeIntervalSinceReferenceDate)
//        self.oldestFetchDate = self.oldestFetchDate.addingTimeInterval(-self.deltaTimeIntervalForPaging)
        Date newDate = new Date();

        tasks.add(mbListenerToUserPosts(uInfo.getUserId(), oldestFetchDate, newDate));

        HashMap<String, Boolean> following = uInfo.getFollowing();
        if(following!=null){
            for(Map.Entry<String,Boolean> entry : following.entrySet()) {
                tasks.add(mbListenerToUserPosts(entry.getKey(), oldestFetchDate, newDate));
            }
        }

        Task<Void> serviceGroupTask = Tasks.whenAll(tasks);
        serviceGroupTask.addOnSuccessListener(
            new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void o) {
                    if (postsTotalFetchCount < minNumberOfPostsForInitialLoad || postsIntervalFetchCount < minNumberOfPostsForIntervalLoad){
                        if (deltaTimeIntervalForPaging < 365*24*60*60){
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

    /**
     * posting a new blog on this user wall
     * you need to listen on the post to get completion signal with success info
     *
     * @param  post to post
     */
    public void mbPost(WallBase post){
        post.setWallId(getCurrentUser().getUserId());
        DatabaseReference postRef = mbPostRef.child(post.wallId).push();
        post.setPostId(postRef.getKey());


        postRef.setValue(post, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                EventBus.getDefault().post(new PostCompleteEvent(databaseError));
            }
        });
    }

    public Task<DatabaseError> setlikeVal(final WallBase post, final boolean val){
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

            DatabaseReference notificationRef = mbNotificationQueueRef.push();
            notificationRef.setValue(notificationQueueMessage);
        }else{
            unsetLikesIdIndex(post.getWallId(),post.getPostId());
        }
        final TaskCompletionSource<DatabaseError> source = new TaskCompletionSource<>();
        final int finalIncrement = increase;
        DatabaseReference postRef = mbPostRef.child(post.getWallId()).child(post.getPostId()).child("likeCount");
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                int count = mutableData.getValue(Integer.class);
                count = count + finalIncrement;
                mutableData.setValue(count);
                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if(databaseError==null){
                } else if (committed){
                    DatabaseReference postLikeRef = mbLikesRef.child(userInfo.getUserId()).child(post.getWallId()).child(post.getPostId());
                    if(val){
                        postLikeRef.setValue(FirebaseDateUtils.currentTimeToFirebaseDate());
                    }else{
                        postLikeRef.removeValue();
                    }
                }
                source.setResult(databaseError);
            }
        });
        return source.getTask();
    }

    /**
     * get all the Comments For the given Post
     *
     */
    public void getCommentsForPost(WallBase post){
        DatabaseReference commentsRef = mbCommentsRef.child(post.getWallId()).child(post.getPostId());
        commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<PostComment> comments = new ArrayList<>();
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    PostComment comment = snap.getValue(PostComment.class);
                    comments.add(comment);
                }
                EventBus.getDefault().post(new GetCommentsCompleteEvent(comments));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    /**
     * post the comment on the given post, once the comment is successfully stored in DB
     * the post comments count will be increeased by 1
     *
     */
    public Task<DatabaseError> postComment(final WallBase post, final PostComment comment){
        DatabaseReference commentRef = mbCommentsRef.child(post.getWallId()).child(post.getPostId()).push();
        comment.setCommentId(commentRef.getKey());
        final TaskCompletionSource<DatabaseError> source = new TaskCompletionSource<>();
        commentRef.setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DatabaseReference postRef = mbPostRef.child(post.getWallId()).child(post.getPostId()).child("commentsCount");

                postRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        int count = mutableData.getValue(Integer.class);
                        count = count + 1;
                        mutableData.setValue(count);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                        if(databaseError!=null){
                            source.setResult(databaseError);
                        } else if (committed){
                            UserInfo userInfo = getCurrentUser();

                            // post a notification re: like
                            HashMap<String, Object> notificationQueueMessage = new HashMap<>();
                            notificationQueueMessage.put("wallId",post.getWallId());
                            notificationQueueMessage.put("postId",post.getPostId());
                            notificationQueueMessage.put("newComment",true);
                            notificationQueueMessage.put("comment",comment);
                            notificationQueueMessage.put("commenterId",userInfo.getUserId());
                            notificationQueueMessage.put("commenterName",userInfo.getNicName());

                            DatabaseReference notificationRef = mbNotificationQueueRef.push();
                            notificationRef.setValue(notificationQueueMessage);
                            source.setResult(null);
                        }
                        EventBus.getDefault().post(new PostCommentCompleteEvent(databaseError));
                    }
                });
            }
        });
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
        DatabaseReference notificationRef = mbNotificationQueueRef.push();
        notificationRef.setValue(notificationQueueMessage);
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
        DatabaseReference notificationRef = mbNotificationQueueRef.push();
        notificationRef.setValue(notificationQueueMessage);
    }

    /**
     * get post by its id
     *
     */
    public void getPostById(String wallId, String postId){
        DatabaseReference pRef = mbPostRef.child(wallId).child(postId);
        pRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                WallBase post = WallBase.postFactory(dataSnapshot);
                EventBus.getDefault().post(new GetPostByIdEvent(post));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

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

