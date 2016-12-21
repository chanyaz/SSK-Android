package tv.sportssidekick.sportssidekick.model;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.greenrobot.eventbus.EventBus;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tv.sportssidekick.sportssidekick.model.im.ImModel;
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

    public UserInfo getUserInfo() {
        return userInfo;
    }

    private UserInfo userInfo;
    private HashMap<String, UserInfo> userCache;
    private DatabaseReference userInfoRef;
    //private DatabaseReference onlineUserIndexRef;

    private StorageReference storageRef;
    FirebaseAuth mAuth;
    private Model() {
        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance();
        userCache = new HashMap<>();
        userInfoRef = ref.getReference("usersInfo");
        //onlineUserIndexRef = ref.getReference("onlineUsersIndex");
        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        String storageURL = firebaseRemoteConfig.getString("storageURL");
        if(TextUtils.isEmpty(storageURL)){
            storageURL = "gs://sportssidekickdev.appspot.com";
        }
        storageRef = storage.getReferenceFromUrl(storageURL);
    }

    private FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            final FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged: signed_in:" + user.getUid());
                final String userId = user.getUid();
                ImModel.getInstance().getAllPublicChats();
                getAllUsersInfo();
                // Attach a listener to read the data at our User Info reference
                userInfoRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userInfo = dataSnapshot.getValue(UserInfo.class);
                        userInfo.setUserId(userId);
                        EventBus.getDefault().post(new FirebaseEvent("Login successful!", FirebaseEvent.Type.LOGIN_SUCCESSFUL, userInfo));
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        EventBus.getDefault().post(new FirebaseEvent("Login failed (canceled)!", FirebaseEvent.Type.LOGIN_FAILED, null));
                    }
                });
            } else {
                EventBus.getDefault().post(new FirebaseEvent("Signed out.", FirebaseEvent.Type.SIGNED_OUT, null));
            }
        }
    };

    public void attachAuthStateListener(){
        mAuth.addAuthStateListener(authStateListener);
    }

    public void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                EventBus.getDefault().post(new FirebaseEvent("Login failed (Error)!", FirebaseEvent.Type.LOGIN_FAILED, null));
            }
        });
    }

    public FirebaseDatabase getDatabase(){
        return ref;
    }

    private void getAllUsersInfo(){
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
                ImModel.getInstance().reload(userInfo.getUserId());
                EventBus.getDefault().post(new FirebaseEvent("All users downloaded", FirebaseEvent.Type.ALL_DATA_ACQUIRED, usersInfo));
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
            // getUserInfoById(userId); // TODO Requested, what happens when null is returned?
            return null;
        } else {
            return info;
        }
    }

    /////////////
    /// FILES ///
    /////////////

    public void saveDataFile(String filename, InputStream stream){
        StorageReference filesRef = storageRef.child("images").child(filename);
        UploadTask uploadTask = filesRef.putStream(stream);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                EventBus.getDefault().post(new FirebaseEvent("File uploaded!", FirebaseEvent.Type.FILE_UPLOADED, downloadUrl.toString()));
            }
        });

    }
}
