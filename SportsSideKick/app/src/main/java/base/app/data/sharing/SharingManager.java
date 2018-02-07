package base.app.data.sharing;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

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

import base.app.data.wall.WallNews;

import static base.app.ClubConfig.CLUB_ID;
import static base.app.data.GSConstants.CLUB_ID_TAG;
import static base.app.data.Model.createRequest;

/**
 * Created by Filip on 4/4/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class SharingManager implements FacebookCallback<Sharer.Result> {

    private static final String TAG = "SHARING";
    private final ObjectMapper mapper; // jackson's object mapper

    public static SharingManager getInstance() {
        if (instance == null) {
            instance = new SharingManager();
        }
        return instance;
    }

    private SharingManager() {
        mapper = new ObjectMapper();
    }

    private static SharingManager instance;

    // A bit hacky, but we can only share one thing at a time, so it should never be an issue. Right?

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
        NewsShare
    }

    Shareable itemToShare;

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
        createRequest("sharingGetUrl")
                .setEventAttribute("itemType", type.name())
                .setEventAttribute("item", data)
                .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
                .send(consumer);
        return source.getTask();
    }

    public void share(final Shareable item, final ShareTarget shareTarget, final View sender) {
        share(null, item, shareTarget, sender);
    }

    public void share(final Context context, final Shareable item, final ShareTarget shareTarget, final View sender) {
        Map<String, Object> itemAsMap = mapper.convertValue(item, new TypeReference<Map<String, Object>>() {
        });
       //TODO Temporarily fix
        if (item instanceof WallNews)
            if (((WallNews) item).getUrl() != null)
                if (!((WallNews) item).getUrl().equals("") && ((WallNews) item).getWallId().equals("")) {
                    socialNetworkSelector(context, itemAsMap, shareTarget);
                    return;
                }
        itemToShare = null;
        if (item.getItemType() == null) {
            Log.e(TAG, "This item is not inteded for sharing, yet!");
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
                } else {
                    //TODO @Filip NOT SUCCESSFUL - What we can do with this?
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

}
