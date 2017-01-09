package tv.sportssidekick.sportssidekick.model.wall;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    private WallModel instance;

    private DatabaseReference mbPostRef;
    private DatabaseReference mbLikesRef;
    private DatabaseReference mbCommentsRef;
    private DatabaseReference mbNotificationQueueRef;

    private double deltaTimeIntervalForPaging  = 60*60;
    private Date oldestFetchIntervalTimestamp;
    private Date oldestFetchIntervalTimestampBound;

    private Map<String, Set<String>> likesIdIndex ; // wall id -> post Id
    private int postsTotalFetchCount;
    private int postsIntervalFetchCount;
    private int minNumberOfPostsForInitialLoad;
    private int minNumberOfPostsForIntervalLoad;
    private List<DatabaseReference> mbWallListeners;
    private List<Query> mbWallListenersQ;

    WallModel getInstance(){
        if(instance==null){
            instance = new WallModel();
        }
        return instance;
    }

    private WallModel(){
        mbWallListeners = new ArrayList<>();
        mbWallListenersQ = new ArrayList<>();
        oldestFetchIntervalTimestamp = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            oldestFetchIntervalTimestampBound = sdf.parse("2016-10-15");
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

    //user loged out so clearing all content.
    public void clear(){
        clearWallListeners();
        likesIdIndex.clear();
        postsTotalFetchCount = 0;
        postsIntervalFetchCount = 0;
        minNumberOfPostsForInitialLoad = 20;
        minNumberOfPostsForIntervalLoad = 10;
    }

    private void mbListenerToUserPosts(String uid, Date since){
        Query mbUserPostsRef = mbPostRef.child(uid).orderByChild("timestamp").startAt("" /*since.toFirebase()*/); // TODO Date to string ?

        mbUserPostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    WallBase post = WallBase.postFactory(snap);
                    if(post!=null){
                        boolean like = likesIdIndex.get(post.getWallId()).contains(post.getPostId());
                        if(like){ // TODO Do we need this check?
                            post.setLikedByUser(like);
                        }
                        postsTotalFetchCount++;
                        EventBus.getDefault().post(new PostUpdateEvent(post));
                    }
                }
//                completeBlock()      TODO Task.complete?
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
    }

    private ChildEventListener postAddedListener =  new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            WallBase post = WallBase.postFactory(dataSnapshot);
            if(post!=null){
                if(oldestFetchIntervalTimestamp.compareTo(new Date(post.getTimestamp()))>0){ // TODO Check how to compare this and what compare result returns!
                    boolean like = likesIdIndex.get(post.getWallId()).contains(post.getPostId());
                    if(like){ // TODO Do we need this check?
                        post.setLikedByUser(like);
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
            boolean like = likesIdIndex.get(post.getWallId()).contains(post.getPostId());
            if(like){ // TODO Do we need this check?
                post.setLikedByUser(like);
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

    private void mbListenerToUserPosts(String uid, Date since, Date until){
        Query mbUserPostsRef = mbPostRef.child(uid).orderByChild("timestamp").startAt("" /*since.toFirebase()*/).endAt("" /*until.toFirebase()*/ ); // TODO Date to string ?

        mbUserPostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    WallBase post = WallBase.postFactory(snap);
                    if(post!=null){
                        boolean like = likesIdIndex.get(post.getWallId()).contains(post.getPostId());
                        if(like){ // TODO Do we need this check?
                            post.setLikedByUser(like);
                        }
                        postsTotalFetchCount++;
                        postsIntervalFetchCount++;
                        EventBus.getDefault().post(new PostUpdateEvent(post));
                    }
                }
//                completeBlock()      TODO Task.complete?
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
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
        if (Model.getInstance().getUserInfo() == null){
            return;
        }
        clearWallListeners();
        DatabaseReference postLikeRef = mbLikesRef.child(Model.getInstance().getUserInfo().getUserId());
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
                UserInfo uInfo = Model.getInstance().getUserInfo();
//              oldestFetchIntervalTimestamp = oldestFetchIntervalTimestamp + deltaTimeIntervalForPaging; TODO Date Addition?

                // TODO What is DispatchGroup?
//                let serviceGroup = DispatchGroup();

//                serviceGroup.enter();
                mbListenerToUserPosts(uInfo.getUserId(), oldestFetchIntervalTimestamp); // TODO Task ?
//                {
//                    serviceGroup.leave();
//                }

                HashMap<String, Boolean> following = uInfo.getFollowing();
                for(Map.Entry<String,Boolean> entry : following.entrySet()){
//                    serviceGroup.enter();
                    mbListenerToUserPosts(entry.getKey(), oldestFetchIntervalTimestamp); // TODO Task ?
//                    {
//                        serviceGroup.leave();
//                    }
                }
//                serviceGroup.notify(queue: DispatchQueue.main, execute: {
                    if(postsTotalFetchCount < 20){
                    fetchPreviousePageOfPosts(0);
                } else {
                    EventBus.getDefault().post(new PostLoadCompleteEvent());
                }
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
    private void fetchPreviousePageOfPosts(int initialFetchCount) {
        postsIntervalFetchCount = initialFetchCount;
        UserInfo uInfo = Model.getInstance().getUserInfo();
//        let serviceGroup = DispatchGroup();

//        let newDate = Date(timeIntervalSinceReferenceDate: 1 + self.oldestFetchIntervalTimestamp.timeIntervalSinceReferenceDate)
//        self.oldestFetchIntervalTimestamp = self.oldestFetchIntervalTimestamp.addingTimeInterval(-self.deltaTimeIntervalForPaging)
        Date newDate = new Date(); // TODO Implement above!

        // cjw -- these were spelled wrong, so I've corrected that, BUT
        // things *appeared* to be working anyway...so who knows what is actually going on here...
//         serviceGroup.enter();
        mbListenerToUserPosts(uInfo.getUserId(), oldestFetchIntervalTimestamp, newDate);
//        {
//            serviceGroup.leave();
//        }


        HashMap<String, Boolean> following = uInfo.getFollowing();
        for(Map.Entry<String,Boolean> entry : following.entrySet()) {
//                    serviceGroup.enter();
            mbListenerToUserPosts(entry.getKey(), oldestFetchIntervalTimestamp, newDate); // TODO Task ?
//                    {
//                        serviceGroup.leave();
//                    }
        }

//        serviceGroup.notify(queue: DispatchQueue.main, execute: {
            if (postsTotalFetchCount < minNumberOfPostsForInitialLoad || postsIntervalFetchCount < minNumberOfPostsForIntervalLoad){
                if (deltaTimeIntervalForPaging < 365*24*60*60){
                    deltaTimeIntervalForPaging *= 2;
                }
                if(oldestFetchIntervalTimestamp.compareTo(oldestFetchIntervalTimestampBound)>0){ // TODO Check how to compare this and what compare result returns!
                    fetchPreviousePageOfPosts(postsIntervalFetchCount);
                }else{
                    EventBus.getDefault().post(new PostLoadCompleteEvent());
                }
            }else{
                EventBus.getDefault().post(new PostLoadCompleteEvent());
            }
//        })
    }

    /**
     * posting a new blog on this user wall
     * you need to listen on the post to get completion signal with success info
     *
     * @param  post to post
     */
    public void mbPost(WallBase post){
        post.setWallId(Model.getInstance().getUserInfo().getUserId());
        DatabaseReference postRef = mbPostRef.child(post.wallId).push();
        post.setPostId(postRef.getKey());


        postRef.setValue(post, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                EventBus.getDefault().post(new PostCompleteEvent(databaseError));
            }
        });
    }

    public void setlikeVal(final WallBase post, final boolean val /*complBlock:@escaping ((NSError?) -> Void)*/){
        int increase = -1;
        if(val){
            increase = 1;
        }
        final UserInfo userInfo = Model.getInstance().getUserInfo();

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
                if(databaseError!=null){
//                    complBlock(error as NSError?) TODO return Task ?
                } else if (committed){
                    DatabaseReference postLikeRef = mbLikesRef.child(userInfo.getUserId()).child(post.getWallId()).child(post.getPostId());
                    if(val){
//                        postLikeRef.setValue(Date().toFirebase()); TODO Implement Date transformation
                    }else{
                        postLikeRef.removeValue();
                    }
//                    complBlock(nil) TODO return Task ?
                }
            }
        });
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
    public void postComment(final WallBase post, final PostComment comment){
        DatabaseReference commentRef = mbCommentsRef.child(post.getWallId()).child(post.getPostId()).push();
        comment.setCommentId(commentRef.getKey());
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
//                      complBlock(error as NSError?) TODO return Task ?
                        } else if (committed){
                            UserInfo userInfo = Model.getInstance().getUserInfo();

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
                        }
                        EventBus.getDefault().post(new PostCommentCompleteEvent(databaseError));
                    }
                });
            }
        });
    }

    /**
     * post was shared on facebook
     *
     **/
    public void facebookShare(WallBase post)
    {
        // post a notification re: comment
        UserInfo userInfo = Model.getInstance().getUserInfo();
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
        UserInfo userInfo = Model.getInstance().getUserInfo();
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

}

