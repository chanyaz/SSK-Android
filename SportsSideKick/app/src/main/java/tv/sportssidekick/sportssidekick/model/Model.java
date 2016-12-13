package tv.sportssidekick.sportssidekick.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

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

    private Model() {
        ref = FirebaseDatabase.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // Attach a listener to read the data at our User Info reference
                    String userId = user.getUid();
                    Log.d(TAG, "The userId: " + userId);
                    ref.getReference("usersInfo").child(userId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            userInfo = dataSnapshot.getValue(UserInfo.class);
                            userInfo.loadKey(dataSnapshot.getRef());
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

    public UserInfo getCachedUserInfoById(String id) {
        return null;
        // TODO INCOMPLETE!
    }
}
