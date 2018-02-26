package base.app.data.wall.sharing;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.GSData;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import static base.app.ClubConfig.CLUB_ID;
import static base.app.data.Model.createRequest;
import static base.app.util.GSConstants.CLUB_ID_TAG;
import static base.app.util.commons.AnalyticsTrackerKt.trackNewsShared;
import static base.app.util.commons.AnalyticsTrackerKt.trackPostShared;
import static base.app.util.commons.AnalyticsTrackerKt.trackSocialShared;

/**
 * Created by Filip on 4/4/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class SharingManager implements FacebookCallback<Sharer.Result> {

    private static final String TAG = "SHARING";
    private static SharingManager instance;
    private final ObjectMapper mapper; // jackson's object mapper
    Shareable itemToShare;

    private SharingManager() {
        mapper = new ObjectMapper();
    }

    // A bit hacky, but we can only share one thing at a time, so it should never be an issue. Right?

    public static SharingManager getInstance() {
        if (instance == null) {
            instance = new SharingManager();
        }
        return instance;
    }

    public Task<Map<String, Object>> getUrl(Map<String, Object> item, ItemType type) {
        final TaskCompletionSource<Map<String, Object>> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Map<String, Object> data = response.getScriptData().getBaseData();
                    source.setResult(data);
                } else {
                    source.setException(new Exception("There was an error while trying to get a share url."));
                }
            }
        };
        GSData data = new GSData(item);
        if (item.containsKey("strap")) {
            type = ItemType.Social;
        }
        createRequest("sharingGetUrl")
                .setEventAttribute("itemType", type.name())
                .setEventAttribute("item", data)
                .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
                .send(consumer);
        return source.getTask();
    }

    private void presentNative(Map<String, Object> response) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        if (response.containsKey("url")) {
            String urlString = (String) response.get("url");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, urlString);

        }
        if (response.containsKey("title")) {
            String title = (String) response.get("title");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        }
        EventBus.getDefault().post(new NativeShareEvent(sharingIntent));
    }

    public void share(final Shareable item) {
        share(null, item);
    }

    public void share(final Context context, final Shareable item) {
        Map<String, Object> itemAsMap = mapper.convertValue(item, new TypeReference<Map<String, Object>>() {
        });
        itemToShare = null;
        if (item.getItemType() == null) {
            Log.e(TAG, "This item is not intended for sharing yet!");
        }
        getUrl(itemAsMap, item.getItemType()).addOnCompleteListener(new OnCompleteListener<Map<String, Object>>() {
            @Override
            public void onComplete(@NonNull Task<Map<String, Object>> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> response = task.getResult();
                    if (response == null) {
                        return;
                    }
                    itemToShare = item;

                    presentNative(response);

                    if (item.getItemType() == ItemType.WallPost) {
                        trackPostShared();
                    } else if (item.getItemType() == ItemType.News) {
                        trackNewsShared();
                    } else if (item.getItemType() == ItemType.Social) {
                        trackSocialShared();
                    }
                } else {
                    Toast.makeText(context, "Failed to share item", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void socialNetworkSelector(Context context, Map<String, Object> response, ShareTarget shareTarget) {
        presentNative(response);
    }

    // After a successful share from the app, we increment the share count for the object shared on it's sharetarget
    // These are all held as separate counters on the db, but an overall total is returned as the UI doesn't need
    // that level of detail
    public Task<Map<String, Object>> increment(Shareable item, ShareTarget shareTarget) {
        final TaskCompletionSource<Map<String, Object>> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Map<String, Object> data = response.getScriptData().getBaseData();
                    if (data == null) {
                        source.setException(new Exception("There was an error while trying to increment the share count."));
                        return;
                    }
                    if (data.containsKey("success")) {
                        if (!(Boolean) data.get("success")) {
                            source.setException(new Exception("There was an error while trying to increment the share count."));
                            return;
                        }
                    }
                    if (!data.containsKey("item")) {
                        source.setException(new Exception("There was an error while trying to increment the share count."));
                        return;
                    }
                    source.setResult(data);
                } else {
                    source.setException(new Exception("There was an error while trying to increment the share count."));
                }
            }
        };
        Map<String, Object> itemAsMap = mapper.convertValue(item, new TypeReference<Map<String, Object>>() {
        });
        GSData data = new GSData(itemAsMap);
        createRequest("sharingCountIncrement")
                .setEventAttribute("shareType", shareTarget.ordinal())
                .setEventAttribute("itemType", item.getItemType().ordinal())
                .setEventAttribute("item", data)
                .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
                .send(consumer);

        return source.getTask();
    }

    @Override
    public void onSuccess(Sharer.Result result) {
        itemToShare.incrementShareCount(ShareTarget.facebook);
    }

    @Override
    public void onCancel() {
        // - NOOP
    }

    @Override
    public void onError(FacebookException error) {
        // TODO @Filip - title: "Error", message: "Failed to share to Facebook, please try again"
    }

    // If we have other share targets, expans this list and the shareTargetMap below
    public enum ShareTarget {
        facebook,
        twitter,
        mail,
        messenger,
        airdrop,
        weibo,
        other
    }

    // As more types of object become sharable, expand this type enum to account for this.
    // These are used to match types on Cloud Code so we can pull from the correct collection.
    public enum ItemType {
        WallPost,
        News,
        Social
    }

}

