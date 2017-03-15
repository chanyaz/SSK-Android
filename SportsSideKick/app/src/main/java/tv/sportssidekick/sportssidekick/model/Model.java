package tv.sportssidekick.sportssidekick.model;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesparks.sdk.GSEventConsumer;
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
import java.util.Map;

import tv.sportssidekick.sportssidekick.gs.AnalyticConstants;
import tv.sportssidekick.sportssidekick.model.im.ImsManager;
import tv.sportssidekick.sportssidekick.model.user.GSMessageHandlerAbstract;
import tv.sportssidekick.sportssidekick.model.user.MessageHandler;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.service.GSAndroidPlatform;
import tv.sportssidekick.sportssidekick.service.GameSparksEvent;

import static tv.sportssidekick.sportssidekick.model.Model.LoggedInUserType.NONE;
import static tv.sportssidekick.sportssidekick.model.Model.LoggedInUserType.REAL;

public class Model {

    private static final String TAG = "MODEL";
    private final ObjectMapper mapper; // jackson's object mapper

    enum LoggedInUserType {
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

    public LoggedInUserType getLoggedInUserType() {
        return loggedInUserType;
    }

    private LoggedInUserType loggedInUserType = NONE;

    public void setLoggedInUserType(LoggedInUserType type){
        loggedInUserType = type;
        Log.d(TAG, "Logged in user type: " + loggedInUserType.name());
        switch (type){
            case NONE:
//              UserEvents.onLogout.emit()  Event-TBA
                break;
            case ANONYMOUS:
//              UserEvents.onLoginAnonymously.emit() Event-TBA
                registerForPushNotifications();
                break;
            case REAL:
//              UserEvents.onLogin.emit(self.currentUserInfo!)
                registerForPushNotifications();
                break;
        }
    }

    private HashMap<String, UserInfo> userCache;

    private Model() {
        userCache = new HashMap<>();
        mapper  = new ObjectMapper();
        loggedInUserType = NONE;
    }

    public void setMessageHandlerDelegate(GSMessageHandlerAbstract delegate){
        MessageHandler.getInstance().addDelegate(delegate);
    }


    private String deviceToken; // TODO Not sure how this works!

    private void registerForPushNotifications(){
//        String authorizedEntity = PROJECT_ID; // Project id from Google Developer Console
//        String scope = "GCM"; // e.g. communicating using GCM, but you can use any
//        // URL-safe characters up to a maximum of 1000, or
//        // you can also leave it blank.
//        String token = InstanceID.getInstance(context).getToken(authorizedEntity,scope);
        deviceToken = ""; // TODO How to initialize?

        Log.d(TAG, "Registering for push notifications");
        GSAndroidPlatform.gs().getRequestBuilder().createPushRegistrationRequest()
            .setDeviceOS("android")
            .setPushId(deviceToken)
            .send(new GSEventConsumer<GSResponseBuilder.PushRegistrationResponse>() {
                @Override
                public void onEvent(GSResponseBuilder.PushRegistrationResponse pushRegistrationResponse) {
                    String registrationId = pushRegistrationResponse.getRegistrationId();
                    Log.d(TAG, "Registration id is:" + registrationId);
                    GSData scriptData = pushRegistrationResponse.getScriptData();
                }
            });
    }

    private String androidId;

    public void initialize(Context context){
        androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        GSAndroidPlatform.initialise(context, AnalyticConstants.API_KEY, AnalyticConstants.API_SECRET, null, false, true);
        GSAndroidPlatform.gs().setOnAvailable(new GSEventConsumer<Boolean>() {
            @Override
            public void onEvent(Boolean available) {
                if(available) {
                    Log.d(TAG, "GS Available: " + available);
                    if (!GSAndroidPlatform.gs().isAuthenticated()) {
                        Log.d(TAG, "isAuthenticated(): connected but not authenticated, logging in anonymously");
                        login();
                    } else {
                        Log.d(TAG, "isAuthenticated(): authenticated, do nothing.");
                    }
                }
            }
        });
        ImsManager.getInstance().setupMessageListeners();
    }

    private GSEventConsumer<GSResponseBuilder.AuthenticationResponse> onAuthenticated = new GSEventConsumer<GSResponseBuilder.AuthenticationResponse>() {
        @Override
        public void onEvent(GSResponseBuilder.AuthenticationResponse authenticationResponse) {
            if(authenticationResponse != null) {
                if (authenticationResponse.hasErrors()) {
                    Log.d(TAG, "AuthenticationResponse: " + authenticationResponse.toString());
//                  UserEvents.onLoginError.emit(nil); Event-TBA
                } else {
                   getAccountDetails(completeLogin);
                }
            }
        }
    };

    private GSEventConsumer<GSResponseBuilder.AccountDetailsResponse> completeLogin = new GSEventConsumer<GSResponseBuilder.AccountDetailsResponse>() {
        @Override
        public void onEvent(GSResponseBuilder.AccountDetailsResponse response) {
            if(response != null) {
                if (!response.hasErrors()) {
                    setUser(response);
                    String dn = response.getDisplayName();
                    setLoggedInUserType(dn !=null && "".equals(dn) && " ".equals(dn) ? LoggedInUserType.REAL : LoggedInUserType.ANONYMOUS);
                }
            }
        }
    };

    private void getAccountDetails(GSEventConsumer<GSResponseBuilder.AccountDetailsResponse> completion) {
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
               // UserEvents.onDetailsUpdateError.emit(nil) Event-TBA
            } else {
              setUser(response);
            }
        }
    }

    public void registrationRequest(String displayName, String password, String userName){
        GSAndroidPlatform.gs().getRequestBuilder().createRegistrationRequest()
                .setDisplayName(displayName)
                .setPassword(password)
                .setUserName(userName)
                .send(new GSEventConsumer<GSResponseBuilder.RegistrationResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.RegistrationResponse response) {
                        if(response!=null){
                            if(response.hasErrors()){
                                Log.d(TAG,"Registration Request error!");
//                                UserEvents.onRegisterError.emit(nil) Event-TBA
                            } else {
//                                UserEvents.onRegister.emit() Event-TBA
                                Log.d(TAG,"Registration Request successful!");
                                getAccountDetails(new GSEventConsumer<GSResponseBuilder.AccountDetailsResponse>() {
                                    @Override
                                    public void onEvent(GSResponseBuilder.AccountDetailsResponse response) {
                                        if(!response.hasErrors()){
                                            setUser(response);
                                            setLoggedInUserType(REAL);
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
    }


    public void login() {
        GSRequestBuilder.DeviceAuthenticationRequest request = GSAndroidPlatform.gs().getRequestBuilder().createDeviceAuthenticationRequest();
        request.setDeviceId(androidId);
        request.setDeviceModel(Build.MANUFACTURER);
        request.setDeviceName(Build.MODEL);
        request.setDeviceOS("ANDROID");
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
                        setLoggedInUserType(NONE);
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
//                        UserEvents.onPasswordResetRequestError.emit(nil) Event-TBA
                    } else {
//                        UserEvents.onPasswordResetRequest.emit() Event-TBA
                    }
                }
            }
        });
    }

    /**
     * RETURN ALL THE OFFICIAL USER ACCOUNTS! (PLAYERS and SPECIALS)
     */
    public Task<List<UserInfo>> getOfficialAccounts(int offset){
        final TaskCompletionSource<List<UserInfo>> source = new TaskCompletionSource<>();
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
                    List<UserInfo> officialAccounts = mapper.convertValue(object, new TypeReference<List<UserInfo>>(){});
                    source.setResult(officialAccounts);
                }
            }
        });
        return source.getTask();
    }

    public void setEmail(String email){
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.gs().getRequestBuilder().createChangeUserDetailsRequest();
        request.setUserName(email);
        request.send(onDetailsUpdated);
    }

    public void setPassword(String password, String oldPassword){
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.gs().getRequestBuilder().createChangeUserDetailsRequest();
        request.setNewPassword(password);
        request.setOldPassword(oldPassword);
        request.send(onDetailsUpdated);
    }

    public void setLanguage(String language){
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.gs().getRequestBuilder().createChangeUserDetailsRequest();
        request.setLanguage(language);
        request.send(onDetailsUpdated);
    }

    public void setDisplayName(String displayName){
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.gs().getRequestBuilder().createChangeUserDetailsRequest();
        request.setDisplayName(displayName);
        request.send(onDetailsUpdated);
    }

    public void setUserState(UserState state){
        GSRequestBuilder.LogEventRequest request = GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest();
        request.setEventKey("setUserState");
        request.setEventAttribute("state", state.toString());
        request.send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                setState(response);
            }
        });

    }

    public void setProfileImageUrl(String profileImageUrl, boolean isCircular){
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.gs().getRequestBuilder().createChangeUserDetailsRequest();
        String key = isCircular ? "circularAvatarUrl" : "avatarUrl";
        request.getBaseData().put(key,profileImageUrl);
        request.send(onDetailsUpdated);
    }

    public void setDetails(Map<String,String> details){
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.gs().getRequestBuilder().createChangeUserDetailsRequest();
        if(!details.get("email").equals(currentUserInfo.getEmail())){
            request.setUserName(details.get("email"));
        }
        if(!details.get("nicname").equals(currentUserInfo.getNicName())){
            request.setUserName(details.get("nicname"));
        }
        request.getBaseData().put("firstName",details.get("firstName"));
        request.getBaseData().put("lastName",details.get("lastName"));
        request.getBaseData().put("phone",details.get("phone"));
        request.send(onDetailsUpdated);
    }

    private void setUser(GSResponseBuilder.AccountDetailsResponse response) {
        String userId = response.getUserId();
        if(userId==null){
           Log.d(TAG,"GSModel.setUser() -> Couldn't retrieve User ID! Aborting!!");
            return;
        }
        this.currentUserInfo = mapper.convertValue(response.getScriptData(), UserInfo.class);
//        UserEvents.onDetailsUpdated.emit(self.currentUserInfo!)  Event-TBA
    }

    private GSEventConsumer<GSResponseBuilder.ChangeUserDetailsResponse> onDetailsUpdated = new GSEventConsumer<GSResponseBuilder.ChangeUserDetailsResponse>() {
        @Override
        public void onEvent(GSResponseBuilder.ChangeUserDetailsResponse response) {
            if(response!=null){
                if(response.hasErrors()){
//                    UserEvents.onDetailsUpdateError.emit(nil)  Event-TBA
                } else {
                    getAccountDetails(null);
                }
            }
        }
    };

    private void setState(GSResponseBuilder.LogEventResponse response) {
        if(response != null){
            if(response.hasErrors()) {
                // UserEvents.onStateUpdateError.emit(nil)  Event-TBA
                return;
            }
            if(response.getScriptData()==null){
                Log.d(TAG,"GSModel.setState() -> No scriptData returned");
                return;
            }
            if(response.getScriptData().getBaseData()==null){
                Log.d(TAG,"GSModel.setState() -> No base data returned");
                return;
            }
            String state = (String) response.getScriptData().getBaseData().get("state");
            if(response.getScriptData().getBaseData().get("state")==null){
                Log.d(TAG,"GSModel.setState() -> No state data returned");
                return;
            }
           currentUserInfo.setUserState(UserState.valueOf(state));
           // UserEvents.onStateUpdated.emit(state)  Event-TBA
        }
    }

    private void clearUser() {
        currentUserInfo = null;
    }

    /** --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- **/
    /** --- --- --- --- --- --- --- -     USERS         --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- **/
    /** --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- **/




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
            return refreshUserInfo(userId);
        }
    }

    private Task<UserInfo> refreshUserInfo(String userId){
        final TaskCompletionSource<UserInfo> source = new TaskCompletionSource<>();
        GSRequestBuilder.LogEventRequest request = GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest();
        request.setEventKey("getUserInfoById");
        request.setEventAttribute("userId", userId);
        request.send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                UserInfo userInfo = mapper.convertValue(response.getScriptData(), UserInfo.class);
                userCache.put(userInfo.getUserId(), userInfo);
                source.setResult(userInfo);
            }
        });
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
