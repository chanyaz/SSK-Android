package base.app.data.wall;

import android.support.annotation.Nullable;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.GSData;
import com.gamesparks.sdk.api.autogen.GSRequestBuilder;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.pixplicity.easyprefs.library.Prefs;

import org.greenrobot.eventbus.EventBus;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import base.app.data.DateUtils;
import base.app.data.FileUploader;
import base.app.data.GSConstants;
import base.app.data.Model;
import base.app.data.TypeMapper;
import base.app.data.user.GSMessageHandlerAbstract;
import base.app.data.user.UserInfo;
import base.app.util.events.comment.CommentDeleteEvent;
import base.app.util.events.comment.CommentUpdateEvent;
import base.app.util.events.comment.CommentUpdatedEvent;
import base.app.util.events.comment.GetCommentsCompleteEvent;
import base.app.util.events.post.GetPostByIdEvent;
import base.app.util.events.post.ItemUpdateEvent;
import base.app.util.events.post.PostCommentCompleteEvent;
import base.app.util.events.post.PostDeletedEvent;
import base.app.util.events.post.WallLikeUpdateEvent;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static base.app.ClubConfig.CLUB_ID;
import static base.app.data.GSConstants.CLUB_ID_TAG;
import static base.app.data.Model.createRequest;
import static base.app.data.TypeMapper.ItemType;
import static base.app.data.TypeMapper.postFactory;
import static base.app.util.commons.Utility.CHOSEN_LANGUAGE;

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
        mapper = new ObjectMapper().registerModule(new KotlinModule());
        Model.getInstance().setMessageHandlerDelegate(this);
    }

    /**
     * starts getting all posts related to that user, all posts that where posted by this user
     * and all posts posted by the people this user follows
     * you need to listen to mbPostUpdate events which will return all old posts + new posts
     * + updated posts
     */
    public void loadWallPosts(final TaskCompletionSource<List<BaseItem>> completion) {
        final UserInfo userInfo = Model.getInstance().getUserInfo();

        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                List<BaseItem> wallItems = new ArrayList<>();
                if (!response.hasErrors()) {
                    JSONArray jsonArrayOfPosts = (JSONArray)
                            response.getScriptData().getBaseData().get(GSConstants.ITEMS);
                    for (Object postAsJson : jsonArrayOfPosts) {
                        BaseItem post = postFactory(postAsJson, mapper, true);
                        wallItems.add(post);
                    }
                }
                completion.setResult(wallItems);
            }
        };

        GSRequestBuilder.LogEventRequest request = createRequest("wallGetItems")
                .setEventAttribute(GSConstants.USER_ID, userInfo.getUserId())
                .setEventAttribute(GSConstants.LANGUAGE, Prefs.getString(CHOSEN_LANGUAGE, "en"));
        request.send(consumer);
    }

    /**
     * Posting a new blog on this user wall
     */
    public Observable<Post> createPost(final Post post) {
        return Observable.create(new ObservableOnSubscribe<Post>() {
            @Override
            public void subscribe(final ObservableEmitter<Post> emitter) throws Exception {
                post.setId(DateUtils.currentTimeToFirebaseDate() + FileUploader.generateRandName(10));
                post.setWallId(getCurrentUser().getUserId());

                GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.LogEventResponse response) {
                        if (!response.hasErrors()) {
                            Object object = response.getScriptData().getBaseData().get(GSConstants.POST);
                            postFactory(object, mapper, true);
                            emitter.onNext(post);
                            emitter.onComplete();
                        } else {
                            emitter.onError(new Throwable(response.getBaseData().get("message").toString()));
                        }
                    }
                };
                Map<String, Object> map = mapper.convertValue(post, new TypeReference<Map<String, Object>>() {
                });
                map.put("type", ItemType.Post.ordinal() + 1);
                GSData data = new GSData(map);
                createRequest("wallPostToWall")
                        .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
                        .setEventAttribute(GSConstants.POST, data)
                        .send(consumer);
            }
        });
    }

    public Task<Void> deletePost(final Post post) {
        final TaskCompletionSource<Void> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    EventBus.getDefault().post(new PostDeletedEvent(post));
                }
                source.setResult(null);
            }
        };
        Map<String, Object> map = mapper.convertValue(post, new TypeReference<Map<String, Object>>() {
        });
        map.put("type", ItemType.Post.ordinal() + 1);
        GSData data = new GSData(map);

        createRequest("wallDeletePost")
                .setEventAttribute(GSConstants.POST, data)
                .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
                .send(consumer);

        return source.getTask();
    }

    public Task<Void> updatePost(final BaseItem post) {
        final TaskCompletionSource<Void> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.POST);
                    Post post = postFactory(object, mapper, true);
                    EventBus.getDefault().post(new ItemUpdateEvent(post));
                }
                source.setResult(null);
            }
        };
        Map<String, Object> map = mapper.convertValue(post, new TypeReference<Map<String, Object>>() {
        });
        map.put("type", ItemType.Post.ordinal() + 1);
        GSData data = new GSData(map);

        createRequest("wallUpdatePost")
                .setEventAttribute(GSConstants.POST, data)
                .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
                .send(consumer);
        return source.getTask();
    }

    // user logged out so clearing all content.
    public void clear() {
        TypeMapper.getCache().clear();
    }

    Task<Void> setLikeCount(final Post post, final boolean val) {
        int increase = val ? 1 : -1;
        final TaskCompletionSource<Void> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.POST);
                    Post post = postFactory(object, mapper, true);
                    EventBus.getDefault().post(new ItemUpdateEvent(post));
                } else {
                    EventBus.getDefault().post(new ItemUpdateEvent(null));
                }
                source.setResult(null);
            }
        };
        createRequest("wallUpdatePostLike")
                .setEventAttribute(GSConstants.WALL_ID, post.getWallId())
                .setEventAttribute(GSConstants.POST_ID, post.getId())
                .setEventAttribute(GSConstants.INCREASE, increase)
                .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
                .send(consumer);
        return source.getTask();
    }

    /**
     * get all the Comments For the given Post
     */
    private static final int DEFAULT_COMMENTS_PAGE = 10;

    public void getCommentsForPost(Post post) {
        getCommentsForPost(post, 0);
    }

    public void getCommentsForPost(BaseItem post, int fetchedCount) {
        int pageSize = DEFAULT_COMMENTS_PAGE;
        if (fetchedCount >= post.getCommentsCount()) {
            return;
        }
        int offset = 0;
        pageSize = post.getCommentsCount();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.COMMENTS);
                    List<Comment> comments = mapper.convertValue(object, new TypeReference<List<Comment>>() {
                    });
                    EventBus.getDefault().post(new GetCommentsCompleteEvent(comments));
                }
            }
        };
        createRequest("wallGetPostComments")
                .setEventAttribute(GSConstants.WALL_ID, post.getWallId())
                .setEventAttribute(GSConstants.POST_ID, post.getId())
                .setEventAttribute(GSConstants.OFFSET, offset)
                .setEventAttribute(GSConstants.ENTRY_COUNT, pageSize)
                .send(consumer);
    }

    /**
     * post the comment on the given post, once the comment is successfully stored in DB
     * the post comments count will be increased by 1
     */
    public void postComment(final Comment comment) {
        comment.setId(DateUtils.currentTimeToFirebaseDate() + FileUploader.generateRandName(10));
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object commentObj = response.getScriptData().getBaseData().get(GSConstants.COMMENT);
                    Comment comment = mapper.convertValue(commentObj, new TypeReference<Comment>() {
                    });
                    Object postObj = response.getScriptData().getBaseData().get(GSConstants.POST);
                    Post post = postFactory(postObj, mapper, true);

                    EventBus.getDefault().post(new PostCommentCompleteEvent(comment, post));
                    EventBus.getDefault().post(new ItemUpdateEvent(post));
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

    public void deletePostComment(Comment comment) {
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

    public Task<Void> updatePostComment(Comment comment) {
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
     * get post by its id (used for loading from notification)
     */
    private void loadPost(String postId,
                          @Nullable GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer) {
        if (consumer == null) {
            consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
                @Override
                public void onEvent(GSResponseBuilder.LogEventResponse response) {
                    if (!response.hasErrors()) {
                        Object object = response.getScriptData().getBaseData().get(GSConstants.POST);
                        Post post = postFactory(object, mapper, true);
                        EventBus.getDefault().post(new GetPostByIdEvent(post));
                    }
                }
            };
        }
        createRequest("wallGetPost")
                .setEventAttribute(GSConstants.POST_ID, postId)
                .send(consumer);
    }

    public void loadPost(String postId) {
        loadPost(postId, null);
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
                    BaseItem post = postFactory(data.get(GSConstants.ITEM), mapper, true);
                    if (post != null) {
                        EventBus.getDefault().post(new ItemUpdateEvent(post));
                    }
                    break;
            }
        }
    }

    private void parseWallMessage(Map<String, Object> data) {
        String operation = (String) data.get(GSConstants.OPERATION);
        if (operation != null) {
            Object object = data.get(GSConstants.POST);
            Post post = postFactory(object, mapper, true);
            if (post != null) {
                switch (operation) {
                    case GSConstants.OPERATION_LIKE:
                        int likeCount = post.getLikeCount();
                        String wallId = post.getWallId();
                        String postId = post.getId();
                        EventBus.getDefault().post(new WallLikeUpdateEvent(wallId, postId, likeCount));
                        break;
                    case GSConstants.OPERATION_COMMENT:
                        Object commentObject = data.get(GSConstants.COMMENT);
                        Comment comment = mapper.convertValue(commentObject, new TypeReference<Comment>() {
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
                        EventBus.getDefault().post(new ItemUpdateEvent(post));
                        break;
                    case GSConstants.OPERATION_DELETE_COMMENT:
                        Object deletedCommentObject = data.get(GSConstants.COMMENT);
                        Comment deletedComment = mapper.convertValue(deletedCommentObject, new TypeReference<Comment>() {
                        });
                        CommentDeleteEvent deleteCommentEvent = new CommentDeleteEvent(post, deletedComment);
                        EventBus.getDefault().post(deleteCommentEvent);
                        break;
                    case GSConstants.OPERATION_UPDATE_COMMENT:
                        Object updatedCommentObject = data.get(GSConstants.COMMENT);
                        Comment updatedComment = mapper.convertValue(updatedCommentObject, new TypeReference<Comment>() {
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