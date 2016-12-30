package tv.sportssidekick.sportssidekick.model;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tv.sportssidekick.sportssidekick.model.im.ImModel;
import tv.sportssidekick.sportssidekick.service.FirebaseEvent;

public class Model {

    private static final String TAG = "MODEL";
    private static Model instance;

    public FirebaseDatabase getRef() {
        return ref;
    }

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

    public DatabaseReference getUserInfoRef() {
        return userInfoRef;
    }

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
                getAllUsersInfo().addOnSuccessListener(userInfos -> {
                    ImModel.getInstance().reload(userInfo.getUserId());
                    EventBus.getDefault().post(new FirebaseEvent("All user data downloaded", FirebaseEvent.Type.ALL_DATA_ACQUIRED, userInfos));
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



    private Task<List<UserInfo>> getAllUsersInfo(){
        TaskCompletionSource<List<UserInfo>> source = new TaskCompletionSource<>();
        userInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<UserInfo> usersInfo = new ArrayList<>();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    UserInfo userInfo = child.getValue(UserInfo.class);
                    UserInfo cachedInfo = getCachedUserInfoById(userInfo.getUserId());
                    if(cachedInfo!=null){
                        cachedInfo.setEqualsTo(userInfo);
                    } else {
                        userCache.put(userInfo.getUserId(),userInfo);
                    }
                    usersInfo.add(cachedInfo);

                }
                source.setResult(usersInfo);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        return source.getTask();
    }



    /**
     * this func return the userInfo related to the given user ID, it first
     * tryes to retrieve the info from cache if not found it will be fetched from DB
     * so the notification my be fired before this call returns.
     *
     * @param  userId user id
     * @return Task<UserInfo> task that will be run on complete
     *
     **/
    public Task<UserInfo> getUserInfoById(String userId) {
        TaskCompletionSource<UserInfo> source = new TaskCompletionSource<>();
        UserInfo info = getCachedUserInfoById(userId);
        if(info!=null){
            source.setResult(info);
            return source.getTask();
        } else {
            userInfoRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserInfo info = parseAndCacheUserInfo(dataSnapshot);
                    source.setResult(info);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    source.setException(new Exception("Database Error"));
                }
            });
            return source.getTask();
        }
    }

    private UserInfo parseAndCacheUserInfo(DataSnapshot dataSnapshot) {
        UserInfo info = dataSnapshot.getValue(UserInfo.class);
        info.setUserId(dataSnapshot.getKey());
        UserInfo cachedInfo = getCachedUserInfoById(info.getUserId());
        if (cachedInfo != null) {
            cachedInfo.setEqualsTo(info);
        } else {
            userCache.put(info.getUserId(), info);
        }
        return info;
    }

    /**
     * this func return the userInfo related to the given user ID, it first
     * tryes to retrieve the info from cache if not found it will be fetched from DB
     * so the notification my be fired before this call returns.
     *
     * @callback  to receive the userInfo
     * @return Task<UserInfo> task that will be run on complete
     **/
    public Task<UserInfo> fetchOnceUserInfoById(String userId) {
        UserInfo info = getCachedUserInfoById(userId);
        TaskCompletionSource<UserInfo> source = new TaskCompletionSource<>();
        if(info!=null){
            source.setResult(info);
            return source.getTask();
        } else {
            userInfoRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserInfo info = parseAndCacheUserInfo(dataSnapshot);
                    source.setResult(info);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {  }
            });
        }
        return source.getTask();
    }



    public UserInfo getCachedUserInfoById(String userId) {
        if(userCache.containsKey(userId)){
            return userCache.get(userId);
        }
        return null;
    }



    /////////////
    /// FILES ///
    /////////////

    //user_photo_square_jtRp5Be3N3OuV1IH7m7FxfMhT6Q21480609791.59482.png
    //user_photo_rounded_H9g3RugXkzLcvqCn1aKSnWm9KIC31480012799.68699.png
    //_groupChatAvatar_1481051718.21099.png


    //video_sLqHBMbL3BQNgddTK0a4wmPfuA531480240655.74631.mov
    public void uploadVideoRecording(String filepath){
        String filename =
                "video_" +
                userInfo.getUserId() +
                System.currentTimeMillis() +
                ".mov";
        try {
            InputStream inputStream = new FileInputStream(filepath);
            saveDataFile(filename,inputStream, FirebaseEvent.Type.VIDEO_FILE_UPLOADED);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //video_thumb_sLqHBMbL3BQNgddTK0a4wmPfuA531480240556.36911.jpg
    public void uploadVideoRecordingThumbnail(String filepath){

        String filename = "video_thumb_" + userInfo.getUserId() +  System.currentTimeMillis() + ".jpg";
        Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(filepath, MediaStore.Video.Thumbnails.MINI_KIND);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmThumbnail.compress(Bitmap.CompressFormat.JPEG, 70, bos);
        byte[] bitmapdata = bos.toByteArray();
        ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);

        saveDataFile(filename,bs, FirebaseEvent.Type.VIDEO_IMAGE_FILE_UPLOADED);
    }

    //photo_sLqHBMbL3BQNgddTK0a4wmPfuA531480082543.52176.jpg
    public void uploadImageForMessage(String filepath){
        String filename =
                "photo_" +
                userInfo.getUserId() +
                System.currentTimeMillis() +
                ".jpg";
        try {
            InputStream inputStream = new FileInputStream(filepath);
            saveDataFile(filename,inputStream, FirebaseEvent.Type.MESSAGE_IMAGE_FILE_UPLOADED);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //voiceRecording_jtRp5Be3N3OuV1IH7m7FxfMhT6Q21481227362.30166.caf
    public void uploadAudioRecording(String filepath){
        String filename =
                "voiceRecording_" +
                userInfo.getUserId() +
                System.currentTimeMillis() +
                ".caf";
        try {
            InputStream inputStream = new FileInputStream(filepath);
            saveDataFile(filename,inputStream, FirebaseEvent.Type.AUDIO_FILE_UPLOADED);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getAudioFileName() {
        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
        filepath += "/audiorecord.aac";
        return filepath;
    }

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return  cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void saveDataFile(String filename, InputStream stream, FirebaseEvent.Type type){
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
                if(downloadUrl!=null){
                    EventBus.getDefault().post(new FirebaseEvent("File uploaded!", type, downloadUrl.toString()));
                } else {
                    EventBus.getDefault().post(new FirebaseEvent("Something went wrong, file not uploaded!", type, null));
                }
            }
        });
    }


}
