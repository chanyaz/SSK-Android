package tv.sportssidekick.sportssidekick.model.friendship;

import android.text.TextUtils;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.ArrayList;
import java.util.List;

import tv.sportssidekick.sportssidekick.model.user.UserInfo;

/**
 * Created by Filip on 12/27/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class PeopleSearchManager {

    private static PeopleSearchManager instance;

    public static PeopleSearchManager getInstance(){
        if(instance==null){
            instance = new PeopleSearchManager();
        }
        return instance;
    }

    private PeopleSearchManager(){

    }

    private String lastSearchKey;

    /**
     * SearchPeople - get a matching list of SSK users that matches the search string
     * @param  searchString search string
     *
     */

    public Task<List<UserInfo>> searchPeople(String searchString){
        lastSearchKey = searchString.toLowerCase();
        final TaskCompletionSource<List<UserInfo>> source = new TaskCompletionSource<>();
        final List<UserInfo> usersInfo = new ArrayList<>();

        if(TextUtils.isEmpty(searchString)){
            source.setResult(usersInfo);
            return source.getTask();
        }
        // TODO Rewrite to GS
//        Model.getInstance().getRef()
//                .getReference("userSearchKeys")
//                .orderByChild("firstNameL")
//                .startAt(lastSearchKey)
//                .endAt(lastSearchKey + "\\u{f8ff}")
//                .limitToFirst(10)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        final List<Task<UserInfo>> tasks = new ArrayList<>();
//                        for (DataSnapshot child : dataSnapshot.getChildren()) {
//                            String uid = child.getKey();
//                            Task<UserInfo> task = Model.getInstance().getUserInfoById(uid);
//                            tasks.add(task);
//                        }
//                        Tasks.whenAll(tasks).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                List<UserInfo> userInfoList = new ArrayList<>();
//                                for(Task t : tasks){
//                                    userInfoList.add((UserInfo) t.getResult());
//                                }
//                                usersInfo.addAll(userInfoList);
//                                source.setResult(usersInfo);
//                            }
//                        });
//                    }
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) { }
//                });
//
//        Model.getInstance().getRef()
//                .getReference("userSearchKeys")
//                .orderByChild("lastNameL")
//                .startAt(lastSearchKey)
//                .endAt(lastSearchKey + "\\u{f8ff}")
//                .limitToFirst(10)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        final List<Task<UserInfo>> tasks = new ArrayList<>();
//                        for (DataSnapshot child : dataSnapshot.getChildren()) {
//                            String uid = child.getKey();
//                            Task<UserInfo> task = Model.getInstance().getUserInfoById(uid);
//                            tasks.add(task);
//                        }
//                        Tasks.whenAll(tasks).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                List<UserInfo> userInfoList = new ArrayList<>();
//                                for(Task t : tasks){
//                                    userInfoList.add((UserInfo) t.getResult());
//                                }
//                                usersInfo.addAll(userInfoList);
//                                source.setResult(usersInfo);
//                            }
//                        });
//                    }
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) { }
//                });
//
//        Model.getInstance().getRef()
//                .getReference("userSearchKeys")
//                .orderByChild("nicNameL")
//                .startAt(lastSearchKey)
//                .endAt(lastSearchKey + "\\u{f8ff}")
//                .limitToFirst(10)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        final List<Task<UserInfo>> tasks = new ArrayList<>();
//                        for (DataSnapshot child : dataSnapshot.getChildren()) {
//                            String uid = child.getKey();
//                            Task<UserInfo> task = Model.getInstance().getUserInfoById(uid);
//                            tasks.add(task);
//                        }
//                        Tasks.whenAll(tasks).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                List<UserInfo> userInfoList = new ArrayList<>();
//                                for(Task t : tasks){
//                                    userInfoList.add((UserInfo) t.getResult());
//                                }
//                                usersInfo.addAll(userInfoList);
//                                source.setResult(usersInfo);
//                            }
//                        });
//                    }
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) { }
//                });

        return source.getTask();
    }
}
