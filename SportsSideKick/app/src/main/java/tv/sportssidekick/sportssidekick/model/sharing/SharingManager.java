package tv.sportssidekick.sportssidekick.model.sharing;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
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

import static tv.sportssidekick.sportssidekick.model.Model.createRequest;

/**
 * Created by Filip on 4/4/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class SharingManager implements FacebookCallback<Sharer.Result> {

    private static final String TAG = "SHARING";
    private final ObjectMapper mapper; // jackson's object mapper

    public static SharingManager getInstance() {
        if(instance == null){
            instance = new SharingManager();
        }
        return instance;
    }

    private SharingManager(){
        mapper  = new ObjectMapper();
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
        News
    }

    Shareable itemToShare;

    // NOTE: currently, iPhone presents the 'native' share dialog, whereas the iPad
    //       presents different views depending on the share target, so if a different
    //       type of presentation is needed, currently this is handled with a method override
    //       In future, it would be good to implement a presentable protocol that let's us have
    //       a better implementation than this.
    private void presentTwitter(Map<String,Object> response) {
        //TODO
    }

    private void presentFacebook(Map<String,Object> response){
        ShareLinkContent.Builder contentBuilder = new ShareLinkContent.Builder();
        if(response.containsKey("url")){
            String urlString = (String) response.get("url");
            Uri shareUrl =  Uri.parse(urlString);
            contentBuilder.setContentUrl(shareUrl);
        }
        if(response.containsKey("image")){
            String image = (String) response.get("image");
            Uri imageUrl =  Uri.parse(image);
            contentBuilder.setImageUrl(imageUrl);
        }
        if(response.containsKey("title")){
            String title = (String) response.get("title");
            contentBuilder.setContentTitle(title);
        }
        EventBus.getDefault().post(contentBuilder.build());
    }

    // This is for iPhone, iPad uses the above methods.
    private void presentNative(Map<String,Object> response, View sender){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        if(response.containsKey("url")){
            String urlString = (String) response.get("url");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, urlString);

        }
        if(response.containsKey("title")){
            String title = (String) response.get("title");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT,title);
        }
        EventBus.getDefault().post(new NativeShareEvent(sharingIntent));

    }

    public Task<Map<String,Object>> getUrl(Map<String,Object> item, ItemType type){
        final TaskCompletionSource<Map<String,Object>> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Map<String,Object> data = response.getScriptData().getBaseData();
                    source.setResult(data);
                } else {
                    source.setException(new Exception());
                }
            }
        };
        GSData data = new GSData(item);
        createRequest("sharingGetUrl")
                .setEventAttribute("itemType", type.name())
                .setEventAttribute("item", data)
                .send(consumer);
        return source.getTask();
    }

    public void share(final Shareable item, final boolean isNative, final ShareTarget shareTarget, final View sender){
        Map<String, Object> itemAsMap = mapper.convertValue(item, new TypeReference<Map<String, Object>>(){});
        itemToShare = null;
        if(item.getItemType()==null){
            Log.e(TAG, "This item is not inteded for sharing, yet!");
        }
        getUrl(itemAsMap,item.getItemType()).addOnCompleteListener(new OnCompleteListener<Map<String, Object>>() {
            @Override
            public void onComplete(@NonNull Task<Map<String, Object>> task) {
                if(task.isSuccessful()){
                    Map<String, Object> response = task.getResult();
                    if(response == null){
                        return;
                    }
                    itemToShare = item;
                    // probably going to be a user on an iPhone
                    if(isNative && sender!=null){
                        presentNative(response,sender);
                        return;
                    }
                    if(shareTarget == null){
                        return;
                    }
                    switch(shareTarget){
                        case facebook:
                            presentFacebook(response);
                            break;
                        case twitter:
                            presentTwitter(response);
                            break;
                        case mail:
                            break;
                        case messenger:
                            break;
                        case airdrop:
                            break;
                        case weibo:
                            break;
                        case other:
                            break;
                    }
                }
            }
        });
    }

    // After a successful share from the app, we increment the share count for the object shared on it's sharetarget
    // These are all held as separate counters on the db, but an overall total is returned as the UI doesn't need
    // that level of detail
    public Task<Map<String,Object>> increment(Shareable item,ShareTarget shareTarget) {
        final TaskCompletionSource<Map<String,Object>> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Map<String,Object> data = response.getScriptData().getBaseData();
                    if(data==null){
                        source.setException(new Exception());
                        return;
                    }
                    if(data.containsKey("success")){
                        if(!(Boolean)data.get("success")){
                            source.setException(new Exception());
                            return;
                        }
                    }
                    if(!data.containsKey("item")){
                        source.setException(new Exception());
                        return;
                    }
                    source.setResult(data);
                } else {
                    source.setException(new Exception());
                }
            }
        };
        Map<String, Object> itemAsMap = mapper.convertValue(item, new TypeReference<Map<String, Object>>(){});
        GSData data = new GSData(itemAsMap);
        createRequest("sharingCountIncrement")
                .setEventAttribute("shareType", shareTarget.ordinal())
                .setEventAttribute("itemType", item.getItemType().ordinal())
                .setEventAttribute("item", data)
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
        // TODO   title: "Error", message: "Failed to share to Facebook, please try again"
    }

}

