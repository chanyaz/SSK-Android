package tv.sportssidekick.sportssidekick.model.sharing;

import android.view.View;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.HashMap;

/**
 * Created by Filip on 4/4/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class SharingManager {

    public static SharingManager getInstance() {
        if(instance == null){
            instance = new SharingManager();
        }
        return instance;
    }

    private SharingManager(){

    }

   private static SharingManager instance;

    // A bit hacky, but we can only share one thing at a time, so it should never be an issue. Right?
    private Shareable itemToShare;

    // If we have other share targets, expans this list and the shareTargetMap below
    public enum ShareTarget {
        facebook,
        twitter,
        mail,
        messenger
    }

    // As more types of object become sharable, expand this type enum to account for this.
    // These are used to match types on Cloud Code so we can pull from the correct collection.
    public enum ItemType {
        WallPost,
        News
    }

    public Task<HashMap<String,Object>> getUrl(HashMap<String,Object> item, ItemType type){
        final TaskCompletionSource<HashMap<String,Object>> source = new TaskCompletionSource<>();
        //TODO
        return source.getTask();
    }

    public void share(Shareable item, boolean isNative,ShareTarget shareTarget, View sender){

    }

    // After a successful share from the app, we increment the share count for the object shared on it's sharetarget
    // These are all held as separate counters on the db, but an overall total is returned as the UI doesn't need
    // that level of detail
    public Task<HashMap<String,Object>> increment(Shareable item,ShareTarget shareTarget) {
        final TaskCompletionSource<HashMap<String,Object>> source = new TaskCompletionSource<>();
        //TODO
        return source.getTask();
    }
}

