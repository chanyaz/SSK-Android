package base.app.ui.fragment.user.auth;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.GSData;
import com.gamesparks.sdk.api.autogen.GSRequestBuilder;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.iid.FirebaseInstanceId;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import base.app.Keys;
import base.app.data.user.GSMessageHandlerAbstract;
import base.app.data.user.MessageHandler;
import base.app.data.user.User;
import base.app.data.user.UserEvent;
import base.app.data.user.purchases.PurchaseModel;
import base.app.ui.fragment.content.wall.SessionType;
import base.app.util.commons.FileUploader;
import base.app.util.commons.GSAndroidPlatform;
import base.app.util.commons.GSConstants;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static base.app.ClubConfig.CLUB_ID;
import static base.app.data.TypeConverterKt.toUser;
import static base.app.ui.fragment.content.wall.SessionType.Anonymous;
import static base.app.ui.fragment.content.wall.SessionType.Authenticated;
import static base.app.ui.fragment.user.auth.LoginApi.LoggedInUserType.NONE;
import static base.app.ui.fragment.user.auth.LoginApi.LoggedInUserType.REAL;
import static base.app.util.commons.GSConstants.CLUB_ID_TAG;
import static io.reactivex.Observable.create;
import static io.reactivex.Observable.just;

public class LoginApi {

    private static final String TAG = "MODEL";
    private final ObjectMapper mapper;
    private String firebaseToken;

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
        registerForPushNotifications();
    }

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


    private static LoginApi instance;

    public static LoginApi getInstance() {
        if (instance == null) {
            instance = new LoginApi();
        }
        return instance;
    }

    public Observable<SessionType> getSessionState() {
        if (GSAndroidPlatform.getInstance().isAuthenticated()) {
            return just(Authenticated);
        } else {
            return just(Anonymous);
        }
    }

    private User currentUser;

    public User getUser() {
        if (currentUser != null) {
            return currentUser;
        }
        return null;
    }

    public boolean isLoggedIn() {
        return getLoggedInUserType() == LoginApi.LoggedInUserType.REAL;
    }

    public LoggedInUserType getLoggedInUserType() {
        return loggedInUserType;
    }

    private LoggedInUserType loggedInUserType = NONE;

    public void setLoggedInUserType(LoggedInUserType type) {
        loggedInUserType = type;
        Log.d(TAG, "Logged in user type: " + loggedInUserType.name());
        switch (type) {
            case NONE:
                EventBus.getDefault().post(new UserEvent(UserEvent.Type.onLogout));
                break;
            case ANONYMOUS:
                EventBus.getDefault().post(new UserEvent(UserEvent.Type.onLoginAnonymously));
                registerForPushNotifications();
                break;
            case REAL:
                EventBus.getDefault().post(new UserEvent(UserEvent.Type.onLogin, currentUser));
                registerForPushNotifications();
                break;
        }
    }

    private HashMap<String, User> userCache;

    private LoginApi() {
        userCache = new HashMap<>();
        mapper = new ObjectMapper();
        loggedInUserType = NONE;
    }

    public void setMessageHandlerDelegate(GSMessageHandlerAbstract delegate) {
        MessageHandler.getInstance().addDelegate(delegate);
    }

    public void registerForPushNotifications() {
        Log.d(TAG, "Registering for push notifications");
        firebaseToken = FirebaseInstanceId.getInstance().getToken();
        GSAndroidPlatform.getInstance().getRequestBuilder().createPushRegistrationRequest()
                .setDeviceOS("ANDROID")
                .setPushId(firebaseToken)
                .send(new GSEventConsumer<GSResponseBuilder.PushRegistrationResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.PushRegistrationResponse pushRegistrationResponse) {
                        if (!pushRegistrationResponse.hasErrors()) {
                            String registrationId = pushRegistrationResponse.getRegistrationId();
                            Log.d(TAG, "Registration id is:" + registrationId);
                            GSData scriptData = pushRegistrationResponse.getScriptData();
                        } else {
                            Log.e(TAG, "There was an error at registerForPushNotifications call");
                        }

                    }
                });
    }

    public void unRegisterFromPushNotifications() {
        GSAndroidPlatform.getInstance().getRequestBuilder().createPushRegistrationRequest()
                .setDeviceOS("ANDROID")
                .setPushId(null)
                .send(new GSEventConsumer<GSResponseBuilder.PushRegistrationResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.PushRegistrationResponse pushRegistrationResponse) {
                        if (!pushRegistrationResponse.hasErrors()) {
                            String registrationId = pushRegistrationResponse.getRegistrationId();
                            Log.d(TAG, "Registration id is:" + registrationId);
                            GSData scriptData = pushRegistrationResponse.getScriptData();
                        } else {
                            Log.e(TAG, "There was an error at registerForPushNotifications call");
                        }

                    }
                });
    }

    public void markWallTipComplete(String tipId) {
        final TaskCompletionSource<User> source = new TaskCompletionSource<>();
        GSRequestBuilder.LogEventRequest request = GSAndroidPlatform.getInstance().getRequestBuilder().createLogEventRequest();
        request.setEventKey("usersMarkTipComplete");
        request.setEventAttribute("tipId", tipId);
        request.send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (response != null) {
                    if (!response.hasErrors() && response.getScriptData().getObject(GSConstants.USER_INFO) != null) {
                        Map<String, Object> data = response.getScriptData().getObject(GSConstants.USER_INFO).getBaseData();
                        User user = mapper.convertValue(data, User.class);
                        userCache.put(user.getUserId(), user);
                        if (user.getUserId().equals(currentUser.getUserId())) {
                            currentUser = user;
                            EventBus.getDefault().post(new UserEvent(UserEvent.Type.onDetailsUpdated, currentUser));
                        }
                        source.setResult(user);
                        return;
                    }
                }
                source.setException(new Exception("No user!"));
            }
        });
    }

    private String androidId;

    public Observable<Boolean> initialize(final Context context) {
        androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        GSAndroidPlatform.initialise(context, Keys.GS_API_KEY, Keys.GS_API_SECRET);

        return create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> emitter) throws Exception {
                GSAndroidPlatform.getInstance().setOnAvailable(new GSEventConsumer<Boolean>() {
                    @Override
                    public void onEvent(Boolean available) {

                        emitter.onNext(available);
                        emitter.onComplete();

                        if (available) {

                            if (GSAndroidPlatform.getInstance().isAuthenticated()) {
                                getProfileData(completeLogin);
                                PurchaseModel.getInstance().updateProductList();
                            }
                        }
                    }
                });
            }
        });
    }

    private GSEventConsumer<GSResponseBuilder.AuthenticationResponse> onAuthenticated = new GSEventConsumer<GSResponseBuilder.AuthenticationResponse>() {
        @Override
        public void onEvent(GSResponseBuilder.AuthenticationResponse authenticationResponse) {
            if (authenticationResponse != null) {
                if (authenticationResponse.hasErrors()) {
                    Log.d(TAG, "AuthenticationResponse: " + authenticationResponse.toString());
                    EventBus.getDefault().post(new UserEvent(UserEvent.Type.onLoginError));
                } else {
                    getProfileData(completeLogin);
                }
            }
        }
    };

    private GSEventConsumer<GSResponseBuilder.AccountDetailsResponse> completeLogin = new GSEventConsumer<GSResponseBuilder.AccountDetailsResponse>() {
        @Override
        public void onEvent(GSResponseBuilder.AccountDetailsResponse response) {
            if (response != null) {
                if (!response.hasErrors()) {
                    if (!isLoggedIn()) {
                        toUser(response);
                        String dn = response.getDisplayName();
                        setLoggedInUserType(dn != null && !"".equals(dn) && !" ".equals(dn) ? LoggedInUserType.REAL : LoggedInUserType.ANONYMOUS);
                    } else {
                        setLoggedInUserType(REAL);
                    }
                }
            }
        }
    };

    private void getProfileData(GSEventConsumer<GSResponseBuilder.AccountDetailsResponse> completion) {
        GSRequestBuilder.AccountDetailsRequest request = GSAndroidPlatform.getInstance().getRequestBuilder().createAccountDetailsRequest();
        HashMap<String, Object> scriptData = new HashMap<>();
        scriptData.put(CLUB_ID_TAG, CLUB_ID);
        request.getBaseData().put("scriptData", scriptData);
        if (completion != null) {
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

    public Observable<GSResponseBuilder.AccountDetailsResponse> getProfileData() {
        return create(new ObservableOnSubscribe<GSResponseBuilder.AccountDetailsResponse>() {
            @Override
            public void subscribe(final ObservableEmitter<GSResponseBuilder.AccountDetailsResponse> emitter) throws Exception {

                GSRequestBuilder.AccountDetailsRequest request = GSAndroidPlatform.getInstance().getRequestBuilder().createAccountDetailsRequest();
                HashMap<String, Object> scriptData = new HashMap<>();
                scriptData.put(CLUB_ID_TAG, CLUB_ID);
                request.getBaseData().put("scriptData", scriptData);

                GSEventConsumer<GSResponseBuilder.AccountDetailsResponse> consumer = new GSEventConsumer<GSResponseBuilder.AccountDetailsResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.AccountDetailsResponse response) {
                        if (!response.hasErrors()) {
                            emitter.onNext(response);
                            emitter.onComplete();
                        } else {
                            emitter.onError(new Throwable(response.toString()));
                        }
                    }
                };
                request.send(consumer);
            }
        });
    }

    private void onAccountDetails(GSResponseBuilder.AccountDetailsResponse response) {
        if (response != null) {
            if (response.hasErrors()) {
                EventBus.getDefault().post(new UserEvent(UserEvent.Type.onDetailsUpdateError));
            } else {
                toUser(response);
            }
        }
    }

    public void registrationRequest(String displayName, String password, String email, HashMap<String, Object> userDetails) {
        final GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.getInstance().getRequestBuilder().createChangeUserDetailsRequest()
                .setDisplayName(displayName)
                .setNewPassword(password)
                .setLanguage(Locale.getDefault().getLanguage())
                .setUserName(email);

        if (userDetails != null) {
            userDetails.put("action", "register");
            userDetails.put(CLUB_ID_TAG, CLUB_ID);
            Map<String, Object> map = request.getBaseData();
            map.put("scriptData", userDetails);
        }
        request.send(new GSEventConsumer<GSResponseBuilder.ChangeUserDetailsResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.ChangeUserDetailsResponse response) {
                if (response != null) {
                    if (response.hasErrors()) {
                        UserEvent.Type errorType = UserEvent.Type.onRegisterError;
                        Map responseData = response.getBaseData();
                        Object errorObject = responseData.get("error");
                        Error error = new Error(errorObject.toString());
                        UserEvent event = new UserEvent(errorType, error);
                        EventBus.getDefault().post(event);
                    } else {
                        getProfileData(new GSEventConsumer<GSResponseBuilder.AccountDetailsResponse>() {
                            @Override
                            public void onEvent(GSResponseBuilder.AccountDetailsResponse response) {
                                if (!response.hasErrors()) {
                                    toUser(response);
                                    setLoggedInUserType(REAL);
                                    EventBus.getDefault().post(new UserEvent(UserEvent.Type.onRegister));
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void registerFromFacebook(String token, String email, HashMap<String, Object> userData) {
        final GSRequestBuilder.FacebookConnectRequest request = GSAndroidPlatform.getInstance().getRequestBuilder().createFacebookConnectRequest();
        if (userData != null) {
            userData.put("initial_email", email);
            userData.put(CLUB_ID_TAG, CLUB_ID);
            Map<String, Object> map = request.getBaseData();
            map.put("scriptData", userData);
        }
        request.setAccessToken(token)
                .setDoNotLinkToCurrentPlayer(false)
                .setSwitchIfPossible(true)
                .send(handleFBAuth);
    }

    private GSEventConsumer<GSResponseBuilder.AuthenticationResponse> handleFBAuth = new GSEventConsumer<GSResponseBuilder.AuthenticationResponse>() {
        @Override
        public void onEvent(GSResponseBuilder.AuthenticationResponse authenticationResponse) {
            if (authenticationResponse != null) {
                if (authenticationResponse.hasErrors()) {
                    Log.d(TAG, "Facebook AuthenticationResponse: " + authenticationResponse.toString());
                    EventBus.getDefault().post(new UserEvent(UserEvent.Type.onLoginError));
                } else {
                    boolean isNewPlayer = authenticationResponse.getNewPlayer();
                    if (isNewPlayer) {
                        onRegisteredFB.onEvent(authenticationResponse);
                    } else {
                        onAuthenticatedFB.onEvent(authenticationResponse);
                    }

                }
            }
        }
    };

    private GSEventConsumer<GSResponseBuilder.AuthenticationResponse> onAuthenticatedFB = new GSEventConsumer<GSResponseBuilder.AuthenticationResponse>() {
        @Override
        public void onEvent(GSResponseBuilder.AuthenticationResponse authenticationResponse) {
            if (authenticationResponse != null) {
                if (authenticationResponse.hasErrors()) {
                    Log.d(TAG, "Facebook onRegisteredFB AuthenticationResponse: " + authenticationResponse.toString());
                    EventBus.getDefault().post(new UserEvent(UserEvent.Type.onLoginError));
                } else {
                    getProfileData(completeLogin);
                }
            }
        }
    };


    private GSEventConsumer<GSResponseBuilder.AuthenticationResponse> onRegisteredFB = new GSEventConsumer<GSResponseBuilder.AuthenticationResponse>() {
        @Override
        public void onEvent(GSResponseBuilder.AuthenticationResponse authenticationResponse) {
            if (authenticationResponse != null) {
                if (authenticationResponse.hasErrors()) {
                    Log.d(TAG, "Facebook onRegisteredFB AuthenticationResponse: " + authenticationResponse.toString());
                    EventBus.getDefault().post(new UserEvent(UserEvent.Type.onLoginError));
                } else {
                    // AT THIS STAGE, WE NEED TO COMPLETE THE TRADITIONAL REGISTRATION STAGE
                    // BUT WE SHOULD ALSO UPDATE THE USER DATA

                    GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.getInstance().getRequestBuilder().createChangeUserDetailsRequest();
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("action", "register");
                    data.put(CLUB_ID_TAG, CLUB_ID);

                    String firstName = authenticationResponse.getScriptData().getString("firstName");


                    Map<String, Object> map = request.getBaseData();
                    map.put("scriptData", data);

                    request.setUserName("");
                    request.send(onRegisteredFBCompleted);
                    //TODO - from swift to java
//                    print(self.userData)
//                    let request:GSChangeUserDetailsRequest = GSChangeUserDetailsRequest()
//                    request.timeout = 60
//                    var _data = [AnyHashable:Any]()
//                    _data["action"] = "register"
//                    _data["club_id"] = self.clubId
//
//                    if let firstName = self.userData["firstName"] {
//                        _data["firstName"] = firstName
//                    }
//                    if let lastName = self.userData["lastName"] {
//                        _data["lastName"] = lastName
//                    }
//                    if let phone = self.userData["phone"] {
//                        _data["phone"] = phone
//                    }
//                    if let avatarUrl = self.userData["avatarUrl"] {
//                        _data["avatarUrl"] = avatarUrl
//                    }
//
//                    var initialEmail:String = ""
//
//                    let scriptData = response!.getScriptData()
//                    if let email = scriptData?["initial_email"] as? String{
//                        initialEmail = email
//
//                        request.setUserName(initialEmail)
//                        request.setScriptData(_data)
//                        request.setCallback(self.onRegisteredFBCompleted)
//                        self.getInstance.send(request)
//                    }
                }
            }
        }
    };

    private GSEventConsumer<GSResponseBuilder.ChangeUserDetailsResponse> onRegisteredFBCompleted = new GSEventConsumer<GSResponseBuilder.ChangeUserDetailsResponse>() {
        @Override
        public void onEvent(GSResponseBuilder.ChangeUserDetailsResponse response) {
            if (response != null) {
                if (response.hasErrors()) {
                    // We don't need to error if the request was successful, as that means we logged the user in!
                    GSData scriptData = response.getScriptData();
                    boolean successful = (boolean) scriptData.getBaseData().get("success");
                    if (!successful) {
                        UserEvent.Type errorType = UserEvent.Type.onRegisterError;
                        Map responseData = response.getBaseData();
                        Object errorObject = responseData.get("error");
                        Error error = new Error(errorObject.toString());
                        UserEvent event = new UserEvent(errorType, error);
                        EventBus.getDefault().post(event);
                        return;
                    }
                }
                getProfileData(new GSEventConsumer<GSResponseBuilder.AccountDetailsResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.AccountDetailsResponse response) {
                        if (!response.hasErrors()) {
                            toUser(response);
                            setLoggedInUserType(REAL);
                            EventBus.getDefault().post(new UserEvent(UserEvent.Type.onRegister));
                        }
                    }
                });
            }
        }
    };

    public Observable startSession(SessionType type) {
        if (type == Authenticated) {
            return Observable.just(true);
        }
        return create(new ObservableOnSubscribe<GSResponseBuilder.AuthenticationResponse>() {
            @Override
            public void subscribe(final ObservableEmitter<GSResponseBuilder.AuthenticationResponse> emitter) throws Exception {
                GSRequestBuilder.DeviceAuthenticationRequest request = GSAndroidPlatform.getInstance().getRequestBuilder().createDeviceAuthenticationRequest();
                HashMap<String, Object> scriptData = new HashMap<>();
                scriptData.put(CLUB_ID_TAG, CLUB_ID);
                request.getBaseData().put("scriptData", scriptData);
                request.setDeviceId(androidId);
                request.setDeviceModel(Build.MANUFACTURER);
                request.setDeviceName(Build.MODEL);
                request.setDeviceOS("ANDROID");
                request.send(new GSEventConsumer<GSResponseBuilder.AuthenticationResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.AuthenticationResponse response) {
                        if (!response.hasErrors()) {
                            emitter.onNext(response);
                            emitter.onComplete();
                        } else {
                            emitter.onError(new Throwable(response.toString()));
                        }
                    }
                });
            }
        });
    }

    public Observable<GSResponseBuilder.AuthenticationResponse> authorize(final String email, final String password) {
        return create(new ObservableOnSubscribe<GSResponseBuilder.AuthenticationResponse>() {
            @Override
            public void subscribe(final ObservableEmitter<GSResponseBuilder.AuthenticationResponse> emitter) throws Exception {
                GSAndroidPlatform.getInstance().getRequestBuilder().createAuthenticationRequest()
                        .setUserName(email)
                        .setPassword(password)
                        .send(new GSEventConsumer<GSResponseBuilder.AuthenticationResponse>() {
                            @Override
                            public void onEvent(GSResponseBuilder.AuthenticationResponse response) {
                                if (!response.hasErrors()) {
                                    emitter.onNext(response);
                                    emitter.onComplete();
                                } else {
                                    emitter.onError(new Throwable(response.toString()));
                                }
                            }
                        });
            }
        });
    }

    public void logout() {
        GSAndroidPlatform.getInstance().getRequestBuilder().createEndSessionRequest().send(new GSEventConsumer<GSResponseBuilder.EndSessionResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.EndSessionResponse endSessionResponse) {
                if (endSessionResponse != null) {
                    if (endSessionResponse.hasErrors()) {
                        Log.d(TAG, "LoginApi.onSessionEnded() -> Error ending session!");
                    } else {
                        clearUser();
                        setLoggedInUserType(NONE);
                        startSession(Anonymous);
                    }
                }
            }
        });
    }

    public void resetPassword(String email) {
        GSRequestBuilder.LogEventRequest request = GSAndroidPlatform.getInstance().getRequestBuilder().createLogEventRequest();
        request.setEventKey("passwordRecoveryRequest");
        request.setEventAttribute(GSConstants.EMAIL, email);
        request.send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (response != null) {
                    if (response.hasErrors()) {
                        EventBus.getDefault().post(new UserEvent(UserEvent.Type.onPasswordResetRequestError));
                    } else {
                        EventBus.getDefault().post(new UserEvent(UserEvent.Type.onPasswordResetRequest));
                    }
                }
            }
        });
    }

    /**
     * RETURN ALL THE OFFICIAL USER ACCOUNTS! (PLAYERS and SPECIALS)
     */
    public Task<List<User>> getOfficialAccounts(int offset) {
        final TaskCompletionSource<List<User>> source = new TaskCompletionSource<>();
        GSRequestBuilder.LogEventRequest request = GSAndroidPlatform.getInstance().getRequestBuilder().createLogEventRequest();
        request.setEventKey("usersGetSpecialUsers");
        request.setEventAttribute(GSConstants.OFFSET, offset);
        request.setEventAttribute(GSConstants.OFFSET, offset);
        request.setEventAttribute(CLUB_ID_TAG, CLUB_ID);
        request.send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    // Parse response
                    Object object = response.getScriptData().getBaseData().get("usersInfo");
                    List<User> officialAccounts = mapper.convertValue(object, new TypeReference<List<User>>() {
                    });
                    source.setResult(officialAccounts);
                }
            }
        });
        return source.getTask();
    }

    public void setEmail(String email) {
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.getInstance().getRequestBuilder().createChangeUserDetailsRequest();
        request.setUserName(email);
        request.send(onDetailsUpdated);
    }

    public void setPassword(String password, String oldPassword) {
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.getInstance().getRequestBuilder().createChangeUserDetailsRequest();
        request.setNewPassword(password);
        request.setOldPassword(oldPassword);
        request.send(onDetailsUpdated);
    }

    public void setLanguage(String language) {
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.getInstance().getRequestBuilder().createChangeUserDetailsRequest();
        request.setLanguage(language);
        request.send(onDetailsUpdated);
    }

    public void setDisplayName(String displayName) {
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.getInstance().getRequestBuilder().createChangeUserDetailsRequest();
        request.setDisplayName(displayName);
        request.send(onDetailsUpdated);
    }

    public void setUserState(UserState state) {
        GSRequestBuilder.LogEventRequest request = GSAndroidPlatform.getInstance().getRequestBuilder().createLogEventRequest();
        request.setEventKey("setUserState");
        request.setEventAttribute(GSConstants.STATE, state.toString());
        request.send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                setState(response);
            }
        });

    }

    public void setProfileImageUrl(String profileImageUrl, boolean isCircular) {
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.getInstance().getRequestBuilder().createChangeUserDetailsRequest();
        String key = isCircular ? "circularAvatarUrl" : "avatarUrl";
        HashMap<String, Object> scriptData = new HashMap<>();
        scriptData.put(key, profileImageUrl);
        request.getBaseData().put("scriptData", scriptData);
        request.send(onDetailsUpdated);
    }

    public void setDetails(Map<String, String> details) {
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.getInstance().getRequestBuilder().createChangeUserDetailsRequest();
        if (!details.get(GSConstants.EMAIL).equals(currentUser.getEmail())) {
            request.setUserName(details.get(GSConstants.EMAIL));
        }

        String newNicName = details.get(GSConstants.NICNAME);
        if (details.containsKey(GSConstants.NICNAME) && !TextUtils.isEmpty(newNicName)) {
            if (!newNicName.equals(currentUser.getNicName())) {
                request.setDisplayName(details.get(GSConstants.NICNAME));
            }
        }

        HashMap<String, Object> scriptData = new HashMap<>();

        scriptData.put(GSConstants.FIRST_NAME, details.get(GSConstants.FIRST_NAME));
        scriptData.put(GSConstants.LAST_NAME, details.get(GSConstants.LAST_NAME));
        scriptData.put(GSConstants.PHONE, details.get(GSConstants.PHONE));
        request.getBaseData().put("scriptData", scriptData);
        request.send(onDetailsUpdated);
    }

    protected void setUser(User user) {
        userCache.put(user.getUserId(), user);
        EventBus.getDefault().post(new UserEvent(UserEvent.Type.onDetailsUpdated, currentUser));
    }

    private GSEventConsumer<GSResponseBuilder.ChangeUserDetailsResponse> onDetailsUpdated = new GSEventConsumer<GSResponseBuilder.ChangeUserDetailsResponse>() {
        @Override
        public void onEvent(GSResponseBuilder.ChangeUserDetailsResponse response) {
            if (response != null) {
                if (response.hasErrors()) {
                    EventBus.getDefault().post(new UserEvent(UserEvent.Type.onDetailsUpdateError));
                } else {
                    getProfileData(null);
                }
            }
        }
    };

    private void setState(GSResponseBuilder.LogEventResponse response) {
        if (response != null) {
            if (response.hasErrors()) {
                EventBus.getDefault().post(new UserEvent(UserEvent.Type.onStateUpdateError));
                return;
            }
            if (response.getScriptData() == null) {
                Log.d(TAG, "GSModel.setState() -> No scriptData returned");
                return;
            }
            if (response.getScriptData().getBaseData() == null) {
                Log.d(TAG, "GSModel.setState() -> No base data returned");
                return;
            }
            String state = (String) response.getScriptData().getBaseData().get(GSConstants.STATE);
            if (response.getScriptData().getBaseData().get(GSConstants.STATE) == null) {
                Log.d(TAG, "GSModel.setState() -> No state data returned");
                return;
            }
            currentUser.setUserState(UserState.valueOf(state));
            EventBus.getDefault().post(new UserEvent(UserEvent.Type.onStateUpdated, currentUser.getUserState()));
        }
    }

    private void clearUser() {
        currentUser = null;
    }

    /** --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- **/
    /** --- --- --- --- --- --- --- -     USERS         --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- **/
    /** --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- **/


    /**
     * this func return the userInfo related to the given user ID, it first
     * tryes to retrieve the info from cache if not found it will be fetched from DB
     * so the notification my be fired before this call returns.
     *
     * @param userId user id
     * @return Task<UserInfo> task that will be run on complete
     **/
    public Task<User> getUserInfoById(String userId) {
        final TaskCompletionSource<User> source = new TaskCompletionSource<>();
        User info = getCachedUserInfoById(userId);
        if (info != null) {
            source.setResult(info);
            return source.getTask();
        } else {
            return refreshUserInfo(userId);
        }
    }

    public Task<User> refreshUserInfo(String userId) {
        final TaskCompletionSource<User> source = new TaskCompletionSource<>();
        GSRequestBuilder.LogEventRequest request = GSAndroidPlatform.getInstance().getRequestBuilder().createLogEventRequest();
        request.setEventKey("getUserInfoById");
        request.setEventAttribute(GSConstants.USER_ID, userId);
        request.send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors() && response.getScriptData().getObject(GSConstants.USER_INFO) != null) {
                    Map<String, Object> data = response.getScriptData().getObject(GSConstants.USER_INFO).getBaseData();
                    User user = mapper.convertValue(data, User.class);
                    userCache.put(user.getUserId(), user);
                    source.setResult(user);
                } else {
                    Log.e(TAG, "There was an error at refreshUserInfo call");
                    source.setException(new Exception("GameSparks error!"));
                }
            }
        });
        return source.getTask();
    }

    public List<User> getCachedUserInfoById(Collection<String> userIds) {
        List<User> users = new ArrayList<>();
        for (String id : userIds) {
            User info = getCachedUserInfoById(id);
            if (info != null) {
                users.add(info);
            }
        }
        return users;
    }

    public User getCachedUserInfoById(String userId) {
        if (userCache.containsKey(userId)) {
            return userCache.get(userId);
        } else {
            return null;
        }
    }


    /** --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- **/
    /** --- --- --- --- --- --- --- --            FILES                     --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- **/
    /**
     * --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---
     **/

    private String getUserIdForImageName() {
        if (currentUser != null) {
            return currentUser.getUserId();
        } else {
            return "unknown";
        }
    }

    public void uploadChatVideoRecording(String filepath, File filesDir, final TaskCompletionSource<String> completion) {
        String filename =
                "video_" +
                        getUserIdForImageName() +
                        System.currentTimeMillis() +
                        ".mov";
        FileUploader.getInstance().upload(filename, filepath, completion);
    }

    public void uploadChatVideoRecordingThumbnail(String filepath, File filesDir, final TaskCompletionSource<String> completion) {
        String filename = "video_thumb_" + currentUser.getUserId() + System.currentTimeMillis() + ".jpg";
        FileUploader.getInstance().uploadThumbnail(filename, filepath, filesDir, completion);
    }

    public void uploadImageForProfile(String filepath, File filesDir, final TaskCompletionSource<String> completion) {
        String filename =
                "photo_" +
                        getUserIdForImageName() +
                        System.currentTimeMillis() +
                        ".png";
        FileUploader.getInstance().uploadCircularProfileImage(filename, filepath, filesDir, completion);
    }

    public void uploadImageForWallPost(String filepath, File filesDir, final TaskCompletionSource<String> completion) {
        String filename =
                "post_photo_" +
                        getUserIdForImageName() +
                        System.currentTimeMillis() +
                        ".jpg";
        FileUploader.getInstance().uploadCompressedImage(filename, filepath, filesDir, completion);
    }

    public Observable<String> uploadImage(final File image) {
        return create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                String filename = "post_photo_" +
                        getUserIdForImageName() +
                        System.currentTimeMillis() +
                        ".jpg";
                TaskCompletionSource<String> source = new TaskCompletionSource<>();
                source.getTask().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            String uploadedImageUrl = task.getResult();
                            emitter.onNext(uploadedImageUrl);
                            emitter.onComplete();
                        } else {
                            emitter.onError(task.getException());
                        }
                    }
                });
                FileUploader.getInstance().uploadImage(image, filename, source);
            }
        });
    }

    public void uploadImageForStats(String filepath, File filesDir, final TaskCompletionSource<String> completion) {
        String filename =
                "post_photo_" +
                        getUserIdForImageName() +
                        System.currentTimeMillis() +
                        ".jpg";
        FileUploader.getInstance().uploadCompressedImage(filename, filepath, filesDir, completion);
    }

    public void uploadWallPostVideoRecording(String filepath, File filesDir, final TaskCompletionSource<String> completion) {
        String filename =
                "post_video_" +
                        getUserIdForImageName() +
                        System.currentTimeMillis() +
                        ".mov";
        FileUploader.getInstance().uploadCompressedImage(filename, filepath, filesDir, completion);
    }

    public void uploadWallPostVideoRecordingThumbnail(String filepath, File filesDir, final TaskCompletionSource<String> completion) {
        String filename = "post_video_thumb_" + currentUser.getUserId() + System.currentTimeMillis() + ".jpg";
        FileUploader.getInstance().uploadThumbnail(filename, filepath, filesDir, completion);
    }

    public void uploadImageForChatMessage(String filepath, File filesDir, final TaskCompletionSource<String> completion) {
        String filename =
                "photo_" +
                        getUserIdForImageName() +
                        System.currentTimeMillis() +
                        ".jpg";
        FileUploader.getInstance().uploadCompressedImage(filename, filepath, filesDir, completion);
    }

    public void uploadImageForCreateChat(String filepath, File filesDir, final TaskCompletionSource<String> completion) {
        String filename =
                "photo_" +
                        getUserIdForImageName() +
                        System.currentTimeMillis() +
                        ".jpg";
        FileUploader.getInstance().uploadCompressedImage(filename, filepath, filesDir, completion);
    }

    public void uploadImageForEditChat(String filepath, File filesDir, final TaskCompletionSource<String> completion) {
        String filename =
                "photo_" +
                        getUserIdForImageName() +
                        System.currentTimeMillis() +
                        ".jpg";
        FileUploader.getInstance().uploadCompressedImage(filename, filepath, filesDir, completion);
    }

    public void uploadAudioRecordingForChat(String filepath, File filesDir, final TaskCompletionSource<String> completion) {
        String filename =
                "voiceRecording_" +
                        getUserIdForImageName() +
                        System.currentTimeMillis() +
                        ".caf";
        FileUploader.getInstance().upload(filename, filepath, completion);
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
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static GSRequestBuilder.LogEventRequest createRequest(String key) {
        return GSAndroidPlatform.getInstance().getRequestBuilder().createLogEventRequest().setEventKey(key);
    }
}
