package base.app.data.wall;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.GSData;
import com.gamesparks.sdk.api.autogen.GSRequestBuilder;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import org.greenrobot.eventbus.EventBus;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import base.app.data.DateUtils;
import base.app.data.FileUploader;
import base.app.data.GSConstants;
import base.app.data.Model;
import base.app.data.sharing.SharingManager;
import base.app.data.user.GSMessageHandlerAbstract;
import base.app.data.user.UserInfo;
import base.app.util.events.comment.CommentDeleteEvent;
import base.app.util.events.comment.CommentUpdateEvent;
import base.app.util.events.comment.CommentUpdatedEvent;
import base.app.util.events.comment.GetCommentsCompleteEvent;
import base.app.util.events.post.GetPostByIdEvent;
import base.app.util.events.post.PostCommentCompleteEvent;
import base.app.util.events.post.PostDeletedEvent;
import base.app.util.events.post.PostUpdateEvent;
import base.app.util.events.post.WallLikeUpdateEvent;

import static base.app.ClubConfig.CLUB_ID;
import static base.app.data.GSConstants.CLUB_ID_TAG;
import static base.app.data.Model.createRequest;

/**
 * Created by Filip on 1/6/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class WallModel extends GSMessageHandlerAbstract {

    private static WallModel instance;
    private final ObjectMapper mapper;

    public static WallModel getInstance() {
        if (instance == null) {
            instance = new WallModel();
        }
        return instance;
    }

    private WallModel() {
        mapper = new ObjectMapper();
        Model.getInstance().setMessageHandlerDelegate(this);
    }

    /**
     * starts getting all posts related to that user, all posts that where posted by this user
     * and all posts posted by the people this user follows
     * you need to listen to mbPostUpdate events which will return all old posts + new posts
     * + updated posts
     */
    public void loadWallPosts(int offset, int entryCount, final TaskCompletionSource<List<WallBase>> completion) {
        final UserInfo userInfo = Model.getInstance().getUserInfo();

        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                List<WallBase> wallItems = new ArrayList<>();
                if (!response.hasErrors()) {
                    JSONArray jsonArrayOfPosts = (JSONArray) response.getScriptData().getBaseData().get(GSConstants.ITEMS);
                    if (jsonArrayOfPosts.size() > 0) {
                        for (Object postAsJson : jsonArrayOfPosts) {
                            WallBase post = WallBase.postFactory(postAsJson, mapper, true);
                            if (post != null) {
                                post.setSubTitle(userInfo.getNicName());
                                wallItems.add(post);
                            }
                        }
                    }
                }
                completion.setResult(wallItems);
            }
        };

        GSRequestBuilder.LogEventRequest request = createRequest("wallGetItems")
                .setEventAttribute(GSConstants.USER_ID, userInfo.getUserId())
                .setEventAttribute(GSConstants.LANGUAGE, "")
                .setEventAttribute(GSConstants.OFFSET, offset)
                .setEventAttribute(GSConstants.ENTRY_COUNT, entryCount);
        request.send(consumer);
    }

    /**
     * Posting a new blog on this user wall
     */
    public void createPost(WallBase post) {
        post.setWallId(getCurrentUser().getUserId());
        post.setPostId(DateUtils.currentTimeToFirebaseDate() + FileUploader.generateRandName(10));

        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.POST);
                    WallBase.postFactory(object, mapper, true);
                }
            }
        };
        Map<String, Object> map = mapper.convertValue(post, new TypeReference<Map<String, Object>>() {
        });
        map.put("type", post.getTypeAsInt());
        GSData data = new GSData(map);
        createRequest("wallPostToWall")
                .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
                .setEventAttribute(GSConstants.POST, data)
                .send(consumer);
    }

    public Task<Void> deletePost(final WallBase post) {
        final TaskCompletionSource<Void> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                source.setResult(null);
            }
        };

        Map<String, Object> map = mapper.convertValue(post, new TypeReference<Map<String, Object>>() {
        });
        map.put("type", post.getTypeAsInt());
        GSData data = new GSData(map);

        createRequest("wallDeletePost")
                .setEventAttribute(GSConstants.POST, data)
                .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
                .send(consumer);
        return source.getTask();
    }

    public Task<Void> updatePost(final WallBase post) {
        final TaskCompletionSource<Void> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.POST);
                    WallBase post = WallBase.postFactory(object, mapper, true);
                    EventBus.getDefault().post(new PostUpdateEvent(post));
                }
                source.setResult(null);
            }
        };
        Map<String, Object> map = mapper.convertValue(post, new TypeReference<Map<String, Object>>() {
        });
        map.put("type", post.getTypeAsInt());
        GSData data = new GSData(map);

        createRequest("wallUpdatePost")
                .setEventAttribute(GSConstants.POST, data)
                .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
                .send(consumer);
        return source.getTask();
    }

    // user logged out so clearing all content.
    public void clear() {
        WallBase.clear();
    }

    Task<Void> setLikeCount(final WallBase post, final boolean val) {
        int increase = val ? 1 : -1;
        final TaskCompletionSource<Void> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.POST);
                    WallBase post = WallBase.postFactory(object, mapper, true);
                    EventBus.getDefault().post(new PostUpdateEvent(post));
                } else {
                    EventBus.getDefault().post(new PostUpdateEvent(null));
                }
                source.setResult(null);
            }
        };
        createRequest("wallUpdatePostLike")
                .setEventAttribute(GSConstants.WALL_ID, post.getWallId())
                .setEventAttribute(GSConstants.POST_ID, post.getPostId())
                .setEventAttribute(GSConstants.INCREASE, increase)
                .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
                .send(consumer);
        return source.getTask();
    }

    /**
     * get all the Comments For the given Post
     */
    private static final int DEFAULT_COMMENTS_PAGE = 10;

    public void getCommentsForPost(WallBase post) {
        getCommentsForPost(post, 0);
    }

    public void getCommentsForPost(WallBase post, int fetchedCount) {
        int pageSize = DEFAULT_COMMENTS_PAGE;
        if (fetchedCount >= post.getCommentsCount()) {
            EventBus.getDefault().post(new GetCommentsCompleteEvent(null));
            return;
        }
        int offset = 0;
        pageSize = post.getCommentsCount();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.COMMENTS);
                    List<PostComment> comments = mapper.convertValue(object, new TypeReference<List<PostComment>>() {
                    });
                    EventBus.getDefault().post(new GetCommentsCompleteEvent(comments));
                } else {
                    EventBus.getDefault().post(new GetCommentsCompleteEvent(null));
                }
            }
        };
        createRequest("wallGetPostComments")
                .setEventAttribute(GSConstants.WALL_ID, post.getWallId())
                .setEventAttribute(GSConstants.POST_ID, post.getPostId())
                .setEventAttribute(GSConstants.OFFSET, offset)
                .setEventAttribute(GSConstants.ENTRY_COUNT, pageSize)
                .send(consumer);
    }

    /**
     * post the comment on the given post, once the comment is successfully stored in DB
     * the post comments count will be increased by 1
     *
     */
    public void postComment(final PostComment comment) {
        comment.setId(DateUtils.currentTimeToFirebaseDate() + FileUploader.generateRandName(10));
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object commentObj = response.getScriptData().getBaseData().get(GSConstants.COMMENT);
                    PostComment comment = mapper.convertValue(commentObj, new TypeReference<PostComment>() {
                    });
                    Object postObj = response.getScriptData().getBaseData().get(GSConstants.POST);
                    WallBase post = WallBase.postFactory(postObj, mapper, true);

                    EventBus.getDefault().post(new PostCommentCompleteEvent(comment, post));
                    EventBus.getDefault().post(new PostUpdateEvent(post));
                } else {
                    Log.e("WallModel", "Posting of comment failed!");
                }
            }
        };
        Map<String, Object> map = mapper.convertValue(comment, new TypeReference<Map<String, Object>>() {
        });
        GSData data = new GSData(map);
        createRequest("wallAddPostComment")
                .setEventAttribute(GSConstants.COMMENT, data)
                .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
                .send(consumer);
    }

    public void deletePostComment(PostComment comment) {
        final TaskCompletionSource<Void> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                source.setResult(null);
            }
        };
        Map<String, Object> map = mapper.convertValue(comment, new TypeReference<Map<String, Object>>() {
        });
        GSData data = new GSData(map);
        createRequest("wallDeletePostComment")
                .setEventAttribute(GSConstants.COMMENT, data)
                .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
                .send(consumer);
        source.getTask();
    }

    public Task<Void> updatePostComment(PostComment comment) {
        final TaskCompletionSource<Void> source = new TaskCompletionSource<>();
        comment.setId(DateUtils.currentTimeToFirebaseDate() + FileUploader.generateRandName(10));
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                source.setResult(null);
            }
        };
        Map<String, Object> map = mapper.convertValue(comment, new TypeReference<Map<String, Object>>() {
        });
        GSData data = new GSData(map);
        createRequest("wallUpdatePostComment")
                .setEventAttribute(GSConstants.COMMENT, data)
                .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
                .send(consumer);
        return source.getTask();
    }

    /**
     * createPost was shared to a share target
     *
     * @param item        createPost, the target
     * @param shareTarget where createPost should be shared
     */

    void incrementShareCount(final WallBase item, SharingManager.ShareTarget shareTarget) {
        SharingManager.getInstance().increment(item, shareTarget).addOnCompleteListener(new OnCompleteListener<Map<String, Object>>() {
            @Override
            public void onComplete(@NonNull Task<Map<String, Object>> task) {
                if (task.isSuccessful()) {
                    WallBase post = WallBase.postFactory(task.getResult(), mapper, true);
                    item.setEqualTo(post);
                }
            }
        });
    }

    /**
     * get createPost by its id (used for loading from notification)
     *
     * @param wallId the wall id
     * @param postId the createPost id
     */
    private void getPostById(String wallId, String postId,
                             @Nullable GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer) {
        if (consumer == null) {
            consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
                @Override
                public void onEvent(GSResponseBuilder.LogEventResponse response) {
                    if (!response.hasErrors()) {
                        Object object = response.getScriptData().getBaseData().get(GSConstants.POST);
                        WallBase post = WallBase.postFactory(object, mapper, true);
                        EventBus.getDefault().post(new GetPostByIdEvent(post));
                    } else {
                        EventBus.getDefault().post(new GetPostByIdEvent(null));
                    }
                }
            };
        }
        createRequest("wallGetPostById")
                .setEventAttribute(GSConstants.WALL_ID, wallId)
                .setEventAttribute(GSConstants.POST_ID, postId)
                .send(consumer);
    }

    public void getPostById(String wallId, String postId) {
        getPostById(wallId, postId, null);
    }

    public Task<Void> wallSetMuteValue(String value) {
        final TaskCompletionSource<Void> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                source.setResult(null);
            }
        };
        createRequest("wallSetMuteValue")
                .setEventAttribute(GSConstants.VALUE, value)
                .send(consumer);
        return source.getTask();
    }

    private UserInfo getCurrentUser() {
        return Model.getInstance().getUserInfo();
    }

    @Override
    public void onGSScriptMessage(String type, Map<String, Object> data) {
        switch (type) {
            case "Wall":
                parseWallMessage(data);
                break;
            case "sharingCountIncremented":
                parseShareCountMessage(data);
            default:
                break;
        }
    }

    private void parseShareCountMessage(Map<String, Object> data) {
        String itemType = (String) data.get(GSConstants.ITEM_TYPE);
        if (itemType != null) {
            switch (itemType) {
                case GSConstants.WALL_POST:
                case GSConstants.NEWS:
                case GSConstants.RUMOUR:
                    WallBase post = WallBase.postFactory(data.get(GSConstants.ITEM), mapper, true);
                    if (post != null) {
                        EventBus.getDefault().post(new PostUpdateEvent(post));
                    }
                    break;
            }
        }
    }

    private void parseWallMessage(Map<String, Object> data) {
        String operation = (String) data.get(GSConstants.OPERATION);
        if (operation != null) {
            Object object = data.get(GSConstants.POST);
            WallBase post = WallBase.postFactory(object, mapper, true);
            if (post != null) {
                switch (operation) {
                    case GSConstants.OPERATION_LIKE:
                        int likeCount = post.getLikeCount();
                        String wallId = post.getWallId();
                        String postId = post.getPostId();
                        EventBus.getDefault().post(new WallLikeUpdateEvent(wallId, postId, likeCount));
                        break;
                    case GSConstants.OPERATION_COMMENT:
                        Object commentObject = data.get(GSConstants.COMMENT);
                        PostComment comment = mapper.convertValue(commentObject, new TypeReference<PostComment>() {
                        });
//                        if (Model.getInstance().isRealUser()) {
//                            if (comment.getPosterId().equals(Model.getInstance().getUserInfo().getUserId())) {
//                                return; // Its our own comment, ignore it
//                            }
//                        }
                        CommentUpdateEvent event = new CommentUpdateEvent(post);
                        event.setComment(comment);
                        EventBus.getDefault().post(event);
                        break;
                    case GSConstants.OPERATION_NEW_POST:
                    case GSConstants.OPERATION_UPDATE_POST:
                        EventBus.getDefault().post(new PostUpdateEvent(post));
                        break;
                    case GSConstants.OPERATION_DELETE_COMMENT:
                        Object deletedCommentObject = data.get(GSConstants.COMMENT);
                        PostComment deletedComment = mapper.convertValue(deletedCommentObject, new TypeReference<PostComment>() {
                        });
                        CommentDeleteEvent deleteCommentEvent = new CommentDeleteEvent(post, deletedComment);
                        EventBus.getDefault().post(deleteCommentEvent);
                        break;
                    case GSConstants.OPERATION_UPDATE_COMMENT:
                        Object updatedCommentObject = data.get(GSConstants.COMMENT);
                        PostComment updatedComment = mapper.convertValue(updatedCommentObject, new TypeReference<PostComment>() {
                        });
                        CommentUpdatedEvent updatedCommentEvent = new CommentUpdatedEvent(post);
                        updatedCommentEvent.setComment(updatedComment);
                        EventBus.getDefault().post(updatedCommentEvent);
                        break;
                    case GSConstants.OPERATION_DELTE_POST:
                        EventBus.getDefault().post(new PostDeletedEvent(post));
                        break;
                }
            }
        }
    }
}