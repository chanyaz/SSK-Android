package tv.sportssidekick.sportssidekick.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tv.sportssidekick.sportssidekick.service.FirebaseEvent;

public class Model {

    private static final String TAG = "MODEL";
    private static Model instance;

    private FirebaseDatabase ref;

    public static Model getInstance(){
        if(instance==null){
            instance = new Model();
        }
        return instance;
    }
    private FirebaseAuth.AuthStateListener authStateListener;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    private UserInfo userInfo;
    HashMap<String, UserInfo> userCache;
    DatabaseReference userInfoRef;
    DatabaseReference onlineUserIndexRef;

    private Model() {
        ref = FirebaseDatabase.getInstance();
        userCache = new HashMap<>();
        userInfoRef = ref.getReference("usersInfo");
        onlineUserIndexRef = ref.getReference("onlineUsersIndex");

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // Attach a listener to read the data at our User Info reference
                    String userId = user.getUid();
                    ref.getReference("usersInfo").child(userId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG, "The read is successful: " + dataSnapshot);
                            userInfo = dataSnapshot.getValue(UserInfo.class);
                            userInfo.setUserId(dataSnapshot.getKey());
                            EventBus.getDefault().post(userInfo);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, "The read failed: " + databaseError.getCode());
                        }
                    });
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };


    }

    public FirebaseDatabase getDatabase(){
        return ref;
    }

    public FirebaseAuth.AuthStateListener getAuthStateListener() {
        return authStateListener;
    }

    // TODO DO we really need this?
    public void getAllUsersInfo(){
        userInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<UserInfo> usersInfo = new ArrayList<>();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    UserInfo userInfo = child.getValue(UserInfo.class);
                    UserInfo cachedInfo = userCache.get(userInfo.getUserId());
                    if(cachedInfo!=null){
                        cachedInfo.setEqualsTo(userInfo);
                    } else {
                        userCache.put(userInfo.getUserId(),userInfo);
                    }
                    usersInfo.add(cachedInfo);

                }
                EventBus.getDefault().post(new FirebaseEvent("All users downloaded", FirebaseEvent.Type.ALL_USERS_ACQUIRED, usersInfo));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    /**
     * this func return the userInfo related to the given user ID, it first
     * tryes to retrieve the info from cache if not found it will be fetched from DB
     * so the notification my be fired before this call returnes.
     *
     * @param  userId user id
     * @return void
     **/
    public void getUserInfoById(String userId) {
        UserInfo info = userCache.get(userId);
        if(info!=null){
            EventBus.getDefault().post(new FirebaseEvent("User Info found in cache.", FirebaseEvent.Type.USER_INFO_BY_ID, info));
        } else {
            DatabaseReference thisUserInfoRef = userInfoRef.child(userId);
            thisUserInfoRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserInfo uInfo = dataSnapshot.getValue(UserInfo.class);
                    uInfo.setUserId(dataSnapshot.getKey());
                    UserInfo cachedInfo = userCache.get(uInfo.getUserId());
                    if(cachedInfo!=null){
                        cachedInfo.setEqualsTo(uInfo);
                    } else {
                        userCache.put(uInfo.getUserId(), uInfo);
                    }
                    EventBus.getDefault().post(new FirebaseEvent("User Info downloaded", FirebaseEvent.Type.USER_INFO_BY_ID, uInfo));
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    // TODO Hide this to internal method
    public UserInfo getCachedUserInfoById(String userId) {
        UserInfo info = userCache.get(userId);
        if(info==null){
            getUserInfoById(userId); // TODO Requested, what happens when null is returned?
            return null;
        } else {
            return info;
        }
    }
}
