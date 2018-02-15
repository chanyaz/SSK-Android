package base.app.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import base.app.Keys;
import base.app.data.facebook.FacebookModel;
import base.app.data.purchases.PurchaseModel;
import base.app.data.user.GSMessageHandlerAbstract;
import base.app.data.user.MessageHandler;
import base.app.data.user.UserEvent;
import base.app.data.user.UserInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static base.app.ClubConfig.CLUB_ID;
import static base.app.data.GSConstants.CLUB_ID_TAG;
import static base.app.data.Model.LoggedInUserType.NONE;
import static base.app.data.Model.LoggedInUserType.REAL;

public class Model {

    private static final String TAG = "MODEL";
    private final ObjectMapper mapper; // jackson's object mapper
    private String firebaseToken;
    private AccessToken mFacebookToken;

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

    private static Model instance;

    public static Model getInstance() {
        if (instance == null) {
            instance = new Model();
        }
        return instance;
    }

    private UserInfo currentUserInfo;

    public UserInfo getUserInfo() {
        return currentUserInfo;
    }

    public boolean isRealUser() {
        return getLoggedInUserType() == Model.LoggedInUserType.REAL;
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
                EventBus.getDefault().post(new UserEvent(UserEvent.Type.onLogin, currentUserInfo));
                registerForPushNotifications();
                break;
        }
    }

    private HashMap<String, UserInfo> userCache;

    private Model() {
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
        GSAndroidPlatform.gs().getRequestBuilder().createPushRegistrationRequest()
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
        GSAndroidPlatform.gs().getRequestBuilder().createPushRegistrationRequest()
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
        final TaskCompletionSource<UserInfo> source = new TaskCompletionSource<>();
        GSRequestBuilder.LogEventRequest request = GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest();
        request.setEventKey("usersMarkTipComplete");
        request.setEventAttribute("tipId", tipId);
        request.send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (response != null) {
                    if (!response.hasErrors() && response.getScriptData().getObject(GSConstants.USER_INFO) != null) {
                        Map<String, Object> data = response.getScriptData().getObject(GSConstants.USER_INFO).getBaseData();
                        UserInfo userInfo = mapper.convertValue(data, UserInfo.class);
                        userCache.put(userInfo.getUserId(), userInfo);
                        if (userInfo.getUserId().equals(currentUserInfo.getUserId())) {
                            currentUserInfo = userInfo;
                            EventBus.getDefault().post(new UserEvent(UserEvent.Type.onDetailsUpdated, currentUserInfo));
                        }
                        source.setResult(userInfo);
                        return;
                    }
                }
                source.setException(new Exception("No user!"));
            }
        });
    }

    private String androidId;

    public void initialize(final Context context) {
        androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        GSAndroidPlatform.initialise(context, Keys.GS_API_KEY, Keys.GS_API_SECRET);
        GSAndroidPlatform.gs().setOnAvailable(new GSEventConsumer<Boolean>() {
            @Override
            public void onEvent(Boolean available) {
                Log.d(TAG, "GS Available: " + available);
                if (available) {
                    if (!GSAndroidPlatform.gs().isAuthenticated()) {
                        Log.d(TAG, "isAuthenticated(): connected but not authenticated, logging in anonymously");
                        login();
                    } else {
                        PurchaseModel.getInstance().updateProductList();
                        // Same entry point as onAuthenticationCheck - escaping from dead loop!
                        Log.d(TAG, "isAuthenticated(): authenticated, do nothing.");
                        getAccountDetails(completeLogin);
                    }
                }
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
                    getAccountDetails(completeLogin);
                }
            }
        }
    };

    private GSEventConsumer<GSResponseBuilder.AccountDetailsResponse> completeLogin = new GSEventConsumer<GSResponseBuilder.AccountDetailsResponse>() {
        @Override
        public void onEvent(GSResponseBuilder.AccountDetailsResponse response) {
            if (response != null) {
                if (!response.hasErrors()) {
                    if (!isRealUser()) {
                        setUser(response);
                        String dn = response.getDisplayName();
                        setLoggedInUserType(dn != null && !"".equals(dn) && !" ".equals(dn) ? LoggedInUserType.REAL : LoggedInUserType.ANONYMOUS);
                    } else {
                        setLoggedInUserType(REAL);
                    }
                }
            }
        }
    };

    private void getAccountDetails(GSEventConsumer<GSResponseBuilder.AccountDetailsResponse> completion) {
        GSRequestBuilder.AccountDetailsRequest request = GSAndroidPlatform.gs().getRequestBuilder().createAccountDetailsRequest();
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

    private void onAccountDetails(GSResponseBuilder.AccountDetailsResponse response) {
        if (response != null) {
            if (response.hasErrors()) {
                EventBus.getDefault().post(new UserEvent(UserEvent.Type.onDetailsUpdateError));
            } else {
                setUser(response);
            }
        }
    }

    public void registrationRequest(String displayName, String password, String email, HashMap<String, Object> userDetails) {
        final GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.gs().getRequestBuilder().createChangeUserDetailsRequest()
                .setDisplayName(displayName)
                .setNewPassword(password)
                .setLanguage(Locale.getDefault().getLanguage())
                .setUserName(email);

        if (userDetails != null) {
            userDetails.put("action", "register");
            userDetails.put(CLUB_ID_TAG, CLUB_ID_TAG);
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
                        org.json.simple.JSONObject errorObject = (org.json.simple.JSONObject) responseData.get("error");
                        Error error;
                        if (errorObject.get("message") != null) {
                            error = new Error(errorObject.get("message").toString());
                        } else {
                            error = new Error(errorObject.toString());
                        }
                        UserEvent event = new UserEvent(errorType, error);
                        EventBus.getDefault().post(event);
                    } else {
                        getAccountDetails(new GSEventConsumer<GSResponseBuilder.AccountDetailsResponse>() {
                            @Override
                            public void onEvent(GSResponseBuilder.AccountDetailsResponse response) {
                                if (!response.hasErrors()) {
                                    setUser(response);
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
                    pullFreshFacebookProfile(authenticationResponse, new GSEventConsumer<GSResponseBuilder.ChangeUserDetailsResponse>() {
                        @Override
                        public void onEvent(GSResponseBuilder.ChangeUserDetailsResponse changeUserDetailsResponse) {
                            if (!changeUserDetailsResponse.hasErrors()) {
                                getAccountDetails(completeLogin);
                            } else {
                                Log.e("Model", "Failed to update user details");
                            }
                        }
                    });
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
                    // At this stage, we need to complete the traditional registration stage
                    // but we should also update the user data
                    pullFreshFacebookProfile(authenticationResponse, null);
                }
            }
        }
    };

    private void pullFreshFacebookProfile(
            final GSResponseBuilder.AuthenticationResponse authenticationResponse,
            final GSEventConsumer<GSResponseBuilder.ChangeUserDetailsResponse> consumerFromLogin) {

        GraphRequest graphRequest = GraphRequest.newMeRequest(
                mFacebookToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        final HashMap<String, Object> userData = new Gson().fromJson(
                                object.toString(), new TypeToken<HashMap<String, Object>>() {
                                }.getType()
                        );

                        GSRequestBuilder.ChangeUserDetailsRequest request
                                = GSAndroidPlatform.gs().getRequestBuilder().createChangeUserDetailsRequest();
                        HashMap<String, Object> data = new HashMap<>();
                        if (consumerFromLogin == null) {
                            data.put("action", "register");
                        }
                        data.put(CLUB_ID_TAG, CLUB_ID);

                        GSData responseData = authenticationResponse.getScriptData();
                        String initialEmail = responseData.getString("initial_email");

                        data.put(GSConstants.FIRST_NAME, userData.get("first_name"));
                        data.put(GSConstants.LAST_NAME, userData.get("last_name"));
                        data.put(GSConstants.PHONE, userData.get("phone"));
                        data.put(GSConstants.EMAIL, initialEmail);
                        if (userData.get("picture") != null) {
                            try {
                                String imageInfo = userData.get("picture").toString();
                                int indexStart = imageInfo.indexOf("url=") + 4;
                                int indexEnd = imageInfo.indexOf("width=") - 2;
                                String avatarUrl = imageInfo.substring(indexStart, indexEnd);
                                data.put("avatarUrl", avatarUrl);
                                data.put("circularAvatarUrl", avatarUrl);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        Map<String, Object> map = request.getBaseData();
                        map.put("scriptData", data);

                        if (consumerFromLogin == null) {
                            request.setUserName(initialEmail);
                            request.send(onRegisteredFBCompleted);
                        } else {
                            request.send(consumerFromLogin);
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, first_name, last_name, picture.type(large), email, friends, birthday, age_range, location, gender");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

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
                getAccountDetails(new GSEventConsumer<GSResponseBuilder.AccountDetailsResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.AccountDetailsResponse response) {
                        if (!response.hasErrors()) {
                            setUser(response);
                            setLoggedInUserType(REAL);
                            EventBus.getDefault().post(new UserEvent(UserEvent.Type.onRegister));
                        }
                    }
                });
            }
        }
    };

    public void login() {
        GSRequestBuilder.DeviceAuthenticationRequest request =
                GSAndroidPlatform.gs().getRequestBuilder().createDeviceAuthenticationRequest();
        HashMap<String, Object> scriptData = new HashMap<>();
        scriptData.put(CLUB_ID_TAG, CLUB_ID);
        request.getBaseData().put("scriptData", scriptData);
        request.setDeviceId(androidId);
        request.setDeviceModel(Build.MANUFACTURER);
        request.setDeviceName(Build.MODEL);
        request.setDeviceOS("ANDROID");
        request.send(onAuthenticated);
    }

    public void login(String email, String password) {
        GSAndroidPlatform.gs().getRequestBuilder().createAuthenticationRequest()
                .setUserName(email)
                .setPassword(password)
                .send(onAuthenticated);
    }

    public void loginFromFacebook(AccessToken token, String email, HashMap<String, Object> userData) {
        mFacebookToken = token;

        final GSRequestBuilder.FacebookConnectRequest request = GSAndroidPlatform.gs().getRequestBuilder()
                .createFacebookConnectRequest();
        if (userData != null) {
            userData.put("initial_email", email);
            userData.put(CLUB_ID_TAG, CLUB_ID);
            Map<String, Object> map = request.getBaseData();
            map.put("scriptData", userData);
        }
        request.setAccessToken(token.getToken())
                .setDoNotLinkToCurrentPlayer(true)
                .setSwitchIfPossible(false)
                .send(handleFBAuth);
    }

    public void logout() {
        FacebookModel.getInstance().logout(); // Facebook logout

        GSAndroidPlatform.gs().getRequestBuilder().createEndSessionRequest()
                .send(new GSEventConsumer<GSResponseBuilder.EndSessionResponse>() {
                    @Override
                    public void onEvent(GSResponseBuilder.EndSessionResponse endSessionResponse) {
                        if (endSessionResponse != null) {
                            if (endSessionResponse.hasErrors()) {
                                Log.d(TAG, "Model.onSessionEnded() -> Error ending session!");
                            } else {
                                clearUser();
                                setLoggedInUserType(NONE);
                                login();
                            }
                        }
                    }
                });
    }

    public void resetPassword(String email) {
        GSRequestBuilder.LogEventRequest request = GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest();
        request.setEventKey("passwordRecoveryRequest");
        request.setEventAttribute(GSConstants.EMAIL, email);
        request.send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (response.hasErrors()) {
                    EventBus.getDefault().post(new UserEvent(UserEvent.Type.onPasswordResetRequestError));
                } else {
                    EventBus.getDefault().post(new UserEvent(UserEvent.Type.onPasswordResetRequest));
                }
            }
        });
    }

    /**
     * RETURN ALL THE OFFICIAL USER ACCOUNTS! (PLAYERS and SPECIALS)
     */
    public Task<List<UserInfo>> getOfficialAccounts(int offset) {
        final TaskCompletionSource<List<UserInfo>> source = new TaskCompletionSource<>();
        GSRequestBuilder.LogEventRequest request = GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest();
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
                    List<UserInfo> officialAccounts = mapper.convertValue(object, new TypeReference<List<UserInfo>>() {
                    });
                    source.setResult(officialAccounts);
                }
            }
        });
        return source.getTask();
    }

    public void setEmail(String email) {
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.gs().getRequestBuilder().createChangeUserDetailsRequest();
        request.setUserName(email);
        request.send(onDetailsUpdated);
    }

    public void setPassword(String password, String oldPassword) {
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.gs().getRequestBuilder().createChangeUserDetailsRequest();
        request.setNewPassword(password);
        request.setOldPassword(oldPassword);
        request.send(onDetailsUpdated);
    }

    public void setLanguage(String language) {
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.gs().getRequestBuilder().createChangeUserDetailsRequest();
        request.setLanguage(language);
        request.send(onDetailsUpdated);
    }

    public void setDisplayName(String displayName) {
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.gs().getRequestBuilder().createChangeUserDetailsRequest();
        request.setDisplayName(displayName);
        request.send(onDetailsUpdated);
    }

    public void setUserState(UserState state) {
        GSRequestBuilder.LogEventRequest request = GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest();
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
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.gs().getRequestBuilder()
                .createChangeUserDetailsRequest();
        String key = isCircular ? "circularAvatarUrl" : "avatarUrl";
        HashMap<String, Object> scriptData = new HashMap<>();
        scriptData.put(key, profileImageUrl);
        request.getBaseData().put("scriptData", scriptData);
        request.send(onDetailsUpdated);
    }

    public void setDetails(Map<String, String> details) {
        GSRequestBuilder.ChangeUserDetailsRequest request = GSAndroidPlatform.gs().getRequestBuilder().createChangeUserDetailsRequest();
        if (!details.get(GSConstants.EMAIL).equals(currentUserInfo.getEmail())) {
            request.setUserName(details.get(GSConstants.EMAIL));
        }

        String newNicName = details.get(GSConstants.NICNAME);
        if (details.containsKey(GSConstants.NICNAME) && !TextUtils.isEmpty(newNicName)) {
            if (!newNicName.equals(currentUserInfo.getNicName())) {
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

    private void setUser(GSResponseBuilder.AccountDetailsResponse response) {
        String userId = response.getUserId();
        if (userId == null) {
            Log.d(TAG, "GSModel.setUser() -> Couldn't retrieve User ID! Aborting!!");
            return;
        }
        GSData scriptData = response.getScriptData();
        Map<String, Object> data;
        UserInfo info;
        if (scriptData != null) {
            data = scriptData.getBaseData();
            info = mapper.convertValue(data, UserInfo.class);
        } else {
            info = mapper.convertValue(response, UserInfo.class);
        }
        currentUserInfo = info;
        userCache.put(info.getUserId(), info);
        EventBus.getDefault().post(new UserEvent(UserEvent.Type.onDetailsUpdated, currentUserInfo));
    }

    private GSEventConsumer<GSResponseBuilder.ChangeUserDetailsResponse> onDetailsUpdated = new GSEventConsumer<GSResponseBuilder.ChangeUserDetailsResponse>() {
        @Override
        public void onEvent(GSResponseBuilder.ChangeUserDetailsResponse response) {
            if (response != null) {
                if (response.hasErrors()) {
                    EventBus.getDefault().post(new UserEvent(UserEvent.Type.onDetailsUpdateError));
                } else {
                    getAccountDetails(null);
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
            currentUserInfo.setUserState(UserState.valueOf(state));
            EventBus.getDefault().post(new UserEvent(UserEvent.Type.onStateUpdated, currentUserInfo.getUserState()));
        }
    }

    private void clearUser() {
        currentUserInfo = null;
        userCache.clear();
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
    public Task<UserInfo> getUserInfoById(String userId) {
        final TaskCompletionSource<UserInfo> source = new TaskCompletionSource<>();
        UserInfo info = getCachedUserInfoById(userId);
        if (info != null) {
            source.setResult(info);
            return source.getTask();
        } else {
            return refreshUserInfo(userId);
        }
    }

    public Task<UserInfo> refreshUserInfo(String userId) {
        final TaskCompletionSource<UserInfo> source = new TaskCompletionSource<>();
        GSRequestBuilder.LogEventRequest request = GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest();
        request.setEventKey("getUserInfoById");
        request.setEventAttribute(GSConstants.USER_ID, userId);
        request.send(new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    if (response.getScriptData().getObject(GSConstants.USER_INFO) != null) {
                        Map<String, Object> data = response.getScriptData().getObject(GSConstants.USER_INFO).getBaseData();
                        UserInfo userInfo = mapper.convertValue(data, UserInfo.class);
                        userCache.put(userInfo.getUserId(), userInfo);
                        source.setResult(userInfo);
                    }
                } else {
                    Log.e(TAG, "There was an error at refreshUserInfo call");
                    source.setException(new Exception("GameSparks error!"));
                }
            }
        });
        return source.getTask();
    }

    public List<UserInfo> getCachedUserInfoById(Collection<String> userIds) {
        List<UserInfo> userInfos = new ArrayList<>();
        for (String id : userIds) {
            UserInfo info = getCachedUserInfoById(id);
            if (info != null) {
                userInfos.add(info);
            }
        }
        return userInfos;
    }

    public UserInfo getCachedUserInfoById(String userId) {
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
        if (currentUserInfo != null) {
            return currentUserInfo.getUserId();
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
        String filename = "video_thumb_" + currentUserInfo.getUserId() + System.currentTimeMillis() + ".jpg";
        FileUploader.getInstance().uploadThumbnail(filename, filepath, filesDir, completion);
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
        return Observable.create(new ObservableOnSubscribe<String>() {
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
        String filename = "post_video_thumb_" + currentUserInfo.getUserId() + System.currentTimeMillis() + ".jpg";
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

    public void uploadImage(String filepath, File filesDir, final TaskCompletionSource<String> completion) {
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

    public static String getAudioFilePath(Context context)  {
        String imageFileName = "recorded_audio";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File audioFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".wav",         /* suffix */
                    storageDir      /* directory */
            );
            return audioFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
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
        return GSAndroidPlatform.gs().getRequestBuilder().createLogEventRequest().setEventKey(key);
    }
}
