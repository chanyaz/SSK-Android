package tv.sportssidekick.sportssidekick.model;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.android.GSAndroidPlatform;
import com.gamesparks.sdk.api.GSData;
import com.gamesparks.sdk.api.autogen.GSRequestBuilder;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import tv.sportssidekick.sportssidekick.gs.AnalyticConstants;
import tv.sportssidekick.sportssidekick.service.GameSparksEvent;

import static tv.sportssidekick.sportssidekick.model.Model.LoggedInUserType.NONE;

public class Model {

    private static final String TAG = "MODEL";
    private final ObjectMapper mapper; // jackson's object mapper

    public enum LoggedInUserType {
        NONE, ANONYMOUS, REAL
    }

    public enum UserState {
        online,
        away,
        busy,
        offline,
        auto
    }


    private static Model instance;
    public static Model getInstance(){
        if(instance==null){
            instance = new Model();
        }
        return instance;
    }

    private UserInfo currentUserInfo;
    public UserInfo getUserInfo() {
        return currentUserInfo;
    }

    private LoggedInUserType loggedInUserType = NONE;

    private HashMap<String, UserInfo> userCache;

    private Model() {
        userCache = new HashMap<>();
        mapper  = new ObjectMapper();
    }


    public static void registerForPushNotifications(String pushId){
//        String authorizedEntity = PROJECT_ID; // Project id from Google Developer Console
//        String scope = "GCM"; // e.g. communicating using GCM, but you can use any
//        // URL-safe characters up to a maximum of 1000, or
//        // you can also leave it blank.
//        String token = InstanceID.getInstance(context).getToken(authorizedEntity,scope);

        Log.d(TAG, "Registering for push notifications");
        GSAndroidPlatform.gs().getRequestBuilder().createPushRegistrationRequest()
                .setDeviceOS("android")
                .setPushId(pushId)
                .send(new GSEventConsumer<GSResponseBuilder.PushRegistrationResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.PushRegistrationResponse pushRegistrationResponse) {
                        String registrationId = pushRegistrationResponse.getRegistrationId();
                        Log.d(TAG, "Registration id is:" + registrationId);
                        GSData scriptData = pushRegistrationResponse.getScriptData();
                    }
                });
    }


    public void initialize(Context context){
        GSAndroidPlatform.initialise(context, AnalyticConstants.API_KEY, AnalyticConstants.API_SECRET, null, false, true);

        GSAndroidPlatform.gs().setOnAvailable(new GSEventConsumer<Boolean>() {
            @Override
            public void onEvent(Boolean available) {
                if(available) {
                    if (GSAndroidPlatform.gs().isAuthenticated()) {
                        Log.d(TAG, "onAvailable(): connected but not authenticated, logging in anonymously");
                        login();
                    }
                }
            }
        });
        GSAndroidPlatform.gs().setOnAuthenticated(onAuthenticatedConsumer);

        // TODO WHATS THIS FOR???
//        self.gs.setMessageListener(self.onMessage)

    }

    GSEventConsumer<GSResponseBuilder.AuthenticationResponse> onAuthenticated = new GSEventConsumer<GSResponseBuilder.AuthenticationResponse>() {
        @Override
        public void onEvent(GSResponseBuilder.AuthenticationResponse authenticationResponse) {
            //TODO
        }
    };

    GSEventConsumer<String> onAuthenticatedConsumer = new GSEventConsumer<String>() {
        @Override
        public void onEvent(String response) {
            if(response != null) {
                if (hasErrors(response)) {
//                    UserEvents.onLoginError.emit(nil); TODO Throw Login error!
                } else {
//                   getAccountDetails(completion: self.completeLogin); TODO call getAccountDetails ?
                }
            }
        }
    };

    private void getAccountDetails(GSEventConsumer completion) {
       GSRequestBuilder.AccountDetailsRequest request = GSAndroidPlatform.gs().getRequestBuilder().createAccountDetailsRequest();
        if(completion!=null){
            request.send(completion);
        } else {
            GSEventConsumer<GSResponseBuilder.AccountDetailsResponse> consumer = new GSEventConsumer<GSResponseBuilder.AccountDetailsResponse>() {
                @Override
                public void onEvent(GSResponseBuilder.AccountDetailsResponse accountDetailsResponse) {
                    onAccountDetails(accountDetailsResponse);
                }
            };
            request.send(consumer);
        }
    }

     private void onAccountDetails(GSResponseBuilder.AccountDetailsResponse response){
        if(response != null) {
            if (response.hasErrors()) {
               // UserEvents.onDetailsUpdateError.emit(nil) TODO Throw Details Update error!
            } else {
               // self.setUser(response: response)
            }
        }
    }

    public static void registrationRequest(String displayName, String password, String userName){
        GSAndroidPlatform.gs().getRequestBuilder().createRegistrationRequest()
                .setDisplayName(displayName)
                .setPassword(password)
                .setUserName(userName)
                .send(new GSEventConsumer<GSResponseBuilder.RegistrationResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.RegistrationResponse response) {
                        // TODO Implement!
//                        if response != nil {
//                            if self.hasErrored(response: response!) {
//                                UserEvents.onRegisterError.emit(nil)
//                                return
//                            }
//
//                            UserEvents.onRegister.emit()
//
//                            self.getAccountDetails() {
//                                response in
//
//                                if self.hasErrored(response: response!) {
//                                    return
//                                }
//
//                                self.setUser(response: response)
//                                self.loggedInUserType = .real
//                            }
//                        }
                    }
                });
    }


    public void login() {
        GSRequestBuilder.DeviceAuthenticationRequest request = GSAndroidPlatform.gs().getRequestBuilder().createDeviceAuthenticationRequest();
        request.setDeviceId("id"); // TODO Fill with proper info!
        request.setDeviceModel("model");
        request.setDeviceName("device name");
        request.setDeviceOS("Android");
        request.send(onAuthenticated);
    }

    public void login(String email, String password){
        GSAndroidPlatform.gs().getRequestBuilder().createAuthenticationRequest()
                .setUserName(email)
                .setPassword(password)
                .send(onAuthenticated);
    }

    public void logout() {
        GSAndroidPlatform.gs().getRequestBuilder().createEndSessionRequest().send(new GSEventConsumer<GSResponseBuilder.EndSessionResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.EndSessionResponse endSessionResponse) {
                if(endSessionResponse!=null){
                    if(endSessionResponse.hasErrors()){
                        Log.d(TAG,"Model.onSessionEnded() -> Error ending session!");
                    } else {
                        clearUser();
                        loggedInUserType = NONE;
                        login();
                    }
                }
            }
        });
    }

    public void resetPassword(String email){
        GSRequestBuilder.LogEventRequest request = GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest();
        request.setEventKey("passwordRecoveryRequest");
        request.setEventAttribute("email", email);
        request.send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if(response!=null){
                    if(response.hasErrors()){
//                        UserEvents.onPasswordResetRequestError.emit(nil) TODO Throw Reset Request Error error!
                    } else {
//                        UserEvents.onPasswordResetRequest.emit() TODO Throw Reset Request Error event!
                    }
                }
            }
        });
    }

    public void getOfficialAccounts(int offset){
        GSRequestBuilder.LogEventRequest request = GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest();
        request.setEventKey("usersGetSpecialUsers");
        request.setEventAttribute("offset", offset);
        request.setEventAttribute("entryCount", 50);

        request.send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    // Parse response
                    Object object = response.getScriptData().getBaseData().get("usersInfo");
                    List<UserInfo> chats = mapper.convertValue(object, new TypeReference<List<UserInfo>>(){});
                    //TODO Emit Chats or return trough task?
                }
            }
        });
    }

    public void setEmail(String email){
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.gs().getRequestBuilder().createChangeUserDetailsRequest();
        // TODO Implement update of user details
    }

    public void setPassword(String password, String oldPassword){
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.gs().getRequestBuilder().createChangeUserDetailsRequest();
        // TODO Implement update of user details
    }

    public void setLanguage(String language){
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.gs().getRequestBuilder().createChangeUserDetailsRequest();
        // TODO Implement update of user details
    }

    public void setDisplayName(String displayName){
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.gs().getRequestBuilder().createChangeUserDetailsRequest();
        // TODO Implement update of user details
    }

    public void setUserState(UserState state){
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.gs().getRequestBuilder().createChangeUserDetailsRequest();
        // TODO Implement update of user details
    }

    public void setProfileImageUrl(String profileImageUrl, boolean isCircular){
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.gs().getRequestBuilder().createChangeUserDetailsRequest();
        // TODO Implement update of user details
    }

    public void setDetails(){
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.gs().getRequestBuilder().createChangeUserDetailsRequest();
        // TODO Implement update of user details
    }

    private void setUser(GSResponseBuilder.AccountDetailsResponse response) {
        String userId = response.getUserId();
        if(userId==null){
           Log.d(TAG,"GSModel.setUser() -> Couldn't retrieve User ID! Aborting!!");
            return;
        }
        UserInfo userInfo = mapper.convertValue(response.getScriptData(), UserInfo.class);
        this.currentUserInfo = userInfo;
//        UserEvents.onDetailsUpdated.emit(self.currentUserInfo!)  TODO Throw Details Update error!
    }


    private void setState(GSResponseBuilder.LogEventResponse response) {
        if(response != null){
            if(response.hasErrors()) {
                // UserEvents.onStateUpdateError.emit(nil)  TODO Throw State Update error!
                return;
            }
            if(response.getScriptData()==null){
                Log.d(TAG,"GSModel.setState() -> No scriptData returned");
                return;
            }
            if(response.getScriptData().getBaseData()==null){
                Log.d(TAG,"GSModel.setState() -> No state data returned");
                return;
            }
            String state = (String) response.getScriptData().getBaseData().get("state");
            if(response.getScriptData().getBaseData().get("state")==null){
                Log.d(TAG,"GSModel.setState() -> No state data returned");
                return;
            }
           // currentUserInfo.setState(state); Add state to UserInfo and update it here!
           // UserEvents.onStateUpdated.emit(state)  TODO Throw State Update event!
        }
    }

    private void clearUser() {
        currentUserInfo = null;
    }

    // WTF ?!?
    private boolean hasErrors(String response){
//            if let _:[AnyHashable:Any] = response.getErrors() {
//                return true
//            }
        return false;
    }

    /** --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- **/
    /** --- --- --- --- --- --- --- -     USERS         --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- **/
    /** --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- **/
    private Task<List<UserInfo>> getAllUsersInfo(){
        final TaskCompletionSource<List<UserInfo>> source = new TaskCompletionSource<>();
        //TODO Rewrite to GS
//        userInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<UserInfo> usersInfo = new ArrayList<>();
//                for(DataSnapshot child : dataSnapshot.getChildren()){
//                    UserInfo userInfo = child.getValue(UserInfo.class);
//                    UserInfo cachedInfo = getCachedUserInfoById(userInfo.getUserId());
//                    if(cachedInfo!=null){
//                        cachedInfo.setEqualsTo(userInfo);
//                    } else {
//                        userCache.put(userInfo.getUserId(),userInfo);
//                    }
//                    usersInfo.add(cachedInfo);
//                }
//                source.setResult(usersInfo);
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        });
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
        final TaskCompletionSource<UserInfo> source = new TaskCompletionSource<>();
        UserInfo info = getCachedUserInfoById(userId);
        if(info!=null){
            source.setResult(info);
            return source.getTask();
        } else {
            //TODO Rewrite to GS
//            userInfoRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    UserInfo info = parseAndCacheUserInfo(dataSnapshot);
//                    source.setResult(info);
//                }
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    source.setException(new Exception("Database Error"));
//                }
//            });
            return source.getTask();
        }
    }

    private UserInfo parseAndCacheUserInfo(Object data) {
        //TODO Rewrite to GS
//        UserInfo info = dataSnapshot.getValue(UserInfo.class);
//        info.setUserId(dataSnapshot.getKey());
//        UserInfo cachedInfo = getCachedUserInfoById(info.getUserId());
//        if (cachedInfo != null) {
//            cachedInfo.setEqualsTo(info);
//        } else {
//            userCache.put(info.getUserId(), info);
//        }
//        return info;
        return null;
    }

    /**
     * this func return the userInfo related to the given user ID, it first
     * tryes to retrieve the info from cache if not found it will be fetched from DB
     * so the notification my be fired before this call returns.
     *
     * @param  userId of User to receive the userInfo
     * @return Task<UserInfo> task that will be run on complete
     **/
    public Task<UserInfo> fetchOnceUserInfoById(String userId) {
        final TaskCompletionSource<UserInfo> source = new TaskCompletionSource<>();
        UserInfo info = getCachedUserInfoById(userId);
        if(info!=null){
            source.setResult(info);
            return source.getTask();
        } else {
            //TODO Rewrite to GS
//            userInfoRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    UserInfo info = parseAndCacheUserInfo(dataSnapshot);
//                    source.setResult(info);
//                }
//                @Override
//                public void onCancelled(DatabaseError databaseError) {  }
//            });
        }
        return source.getTask();
    }

    public List<UserInfo> getCachedUserInfoById(Collection<String> userIds) {
        List<UserInfo> userInfos = new ArrayList<>();
        for(String id : userIds){
            UserInfo info = getCachedUserInfoById(id);
            if(info!=null){
                userInfos.add(info);
            }
        }
        return userInfos;
    }

    public UserInfo getCachedUserInfoById(String userId) {
        if(userCache.containsKey(userId)){
            return userCache.get(userId);
        }
        return null;
    }



    /** --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- **/
    /** --- --- --- --- --- --- --- --            FILES                     --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- **/
    /** --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- **/

    //user_photo_square_jtRp5Be3N3OuV1IH7m7FxfMhT6Q21480609791.59482.png
    //user_photo_rounded_H9g3RugXkzLcvqCn1aKSnWm9KIC31480012799.68699.png
    //_groupChatAvatar_1481051718.21099.png


    //video_sLqHBMbL3BQNgddTK0a4wmPfuA531480240655.74631.mov
    public void uploadVideoRecording(String filepath){
        String filename =
                "video_" +
                currentUserInfo.getUserId() +
                System.currentTimeMillis() +
                ".mov";
        try {
            InputStream inputStream = new FileInputStream(filepath);
            saveDataFile(filename,inputStream, GameSparksEvent.Type.VIDEO_FILE_UPLOADED);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //video_thumb_sLqHBMbL3BQNgddTK0a4wmPfuA531480240556.36911.jpg
    public void uploadVideoRecordingThumbnail(String filepath){

        String filename = "video_thumb_" + currentUserInfo.getUserId() +  System.currentTimeMillis() + ".jpg";
        Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(filepath, MediaStore.Video.Thumbnails.MINI_KIND);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmThumbnail.compress(Bitmap.CompressFormat.JPEG, 70, bos);
        byte[] bitmapdata = bos.toByteArray();
        ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);

        saveDataFile(filename,bs, GameSparksEvent.Type.VIDEO_IMAGE_FILE_UPLOADED);
    }

    //photo_sLqHBMbL3BQNgddTK0a4wmPfuA531480082543.52176.jpg
    public void uploadImageForMessage(String filepath){
        String filename =
                "photo_" +
                currentUserInfo.getUserId() +
                System.currentTimeMillis() +
                ".jpg";
        try {
            InputStream inputStream = new FileInputStream(filepath);
            saveDataFile(filename,inputStream, GameSparksEvent.Type.MESSAGE_IMAGE_FILE_UPLOADED);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //voiceRecording_jtRp5Be3N3OuV1IH7m7FxfMhT6Q21481227362.30166.caf
    public void uploadAudioRecording(String filepath){
        String filename =
                "voiceRecording_" +
                        currentUserInfo.getUserId() +
                System.currentTimeMillis() +
                ".caf";
        try {
            InputStream inputStream = new FileInputStream(filepath);
            saveDataFile(filename,inputStream, GameSparksEvent.Type.AUDIO_FILE_UPLOADED);

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

    private void saveDataFile(String filename, InputStream stream, final GameSparksEvent.Type type){
        //TODO Rewrite to GS
//        StorageReference filesRef = storageRef.child("images").child(filename);
//        UploadTask uploadTask = filesRef.putStream(stream);
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle unsuccessful uploads
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//            Uri downloadUrl = taskSnapshot.getDownloadUrl();
//            if(downloadUrl!=null){
//                EventBus.getDefault().post(new GameSparksEvent("File uploaded!", type, downloadUrl.toString()));
//            } else {
//                EventBus.getDefault().post(new GameSparksEvent("Something went wrong, file not uploaded!", type, null));
//            }
//            }
//        });
    }


}
