package tv.sportssidekick.sportssidekick.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gamesparks.sdk.api.autogen.GSTypes;

import java.util.Date;
import java.util.List;

/**
 * Created by Djordje Krutil on 6.12.2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com eliav
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo {

   public static enum UserType {
        fan, player, special
    }

    private UserType userType = UserType.fan;

    private String userId;
    private String email;
    private String nicName;
    private String language;
    private String phone;
    private String firstName;
    private String lastName;
    private Date subscribedDate;
    private String location;
    private String authToken;

    private String avatarUrl;
    private String circularAvatarUrl;

    private String fantasyUUID;
    private String fantasyToken;

    private boolean isOnline;
    private Model.UserState userState;

    //TODO Hook up with GS
    private int wallPosts=0;
    private int comments=0;
    private int likes=0;
    private int videosWatched=0;
    private int chats=0;
    private int videoChats=0;
    private int publicChats=0;
    private int matchesHome=0;
    private int matchesAway=0;

    private int capsValue=0;
    private int currency=0;
    private int level=20;
    private float progress=0.65f;
    private int wallPins= 11;
    private int friendsCount = 11;
    // --

    // read only
    private boolean aFriend;
    private boolean isFriendPendingRequest;
    private boolean followsMe;
    private boolean iFollowHim;
    private int followingCount = 0; //read only
    private int followersCount = 0; //read only

    List<String> messagingTokens;

    public void setUserState(Model.UserState newState){
        this.userState = newState;
    }

    public Model.UserState getUserState(){
        if(isOnline){
            if(userState!=null){
                return userState;
            } else {
                return Model.UserState.online;
            }
        } else {
            return Model.UserState.offline;
        }
    }

    public String getLocationAsString(GSTypes.Location location){
        String result = "";
        if(location!=null){
            result += location.getCountry();
        }
        return result;
    }

    public void setEqualsTo(UserInfo userInfo) {
        setUserId(userInfo.getUserId());
        setFirstName(userInfo.getFirstName());
        setLastName(userInfo.getLastName());
        setNicName(userInfo.getNicName());
        setPhone(userInfo.getPhone());
        setAvatarUrl(userInfo.getAvatarUrl());
        setCircularAvatarUrl(userInfo.getCircularAvatarUrl());
        setLanguage(userInfo.getLanguage());
        setFantasyUUID(userInfo.getFantasyUUID());
        setFantasyToken(userInfo.getFantasyToken());
        setOnline(userInfo.getIsOnline());
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getSubscribedDate() {
        return subscribedDate;
    }

    public void setSubscribedDate(Date subscribedDate) {
        this.subscribedDate = subscribedDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCircularAvatarUrl() {
        return circularAvatarUrl;
    }

    public void setCircularAvatarUrl(String circularAvatarUrl) {
        this.circularAvatarUrl = circularAvatarUrl;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFantasyToken() {
        return fantasyToken;
    }

    public void setFantasyToken(String fantasyToken) {
        this.fantasyToken = fantasyToken;
    }

    public boolean getIsOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public UserInfo(String firstName, String lastName, String nicName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nicName = nicName;
        this.phone = phone;
    }

    public UserInfo() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNicName() {
        return nicName;
    }

    public void setNicName(String nicName) {
        this.nicName = nicName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFantasyUUID() {
        return fantasyUUID;
    }

    public void setFantasyUUID(String fantasyUUID) {
        this.fantasyUUID = fantasyUUID;
    }

    public List<String> getMessagingTokens() {
        return messagingTokens;
    }

    public void setMessagingTokens(List<String> messagingTokens) {
        this.messagingTokens = messagingTokens;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserInfo)) return false;

        UserInfo userInfo = (UserInfo) o;

        if (isOnline != userInfo.isOnline) return false;
        if (userId != null ? !userId.equals(userInfo.userId) : userInfo.userId != null)
            return false;
        if (firstName != null ? !firstName.equals(userInfo.firstName) : userInfo.firstName != null)
            return false;
        if (lastName != null ? !lastName.equals(userInfo.lastName) : userInfo.lastName != null)
            return false;
        if (nicName != null ? !nicName.equals(userInfo.nicName) : userInfo.nicName != null)
            return false;
        if (phone != null ? !phone.equals(userInfo.phone) : userInfo.phone != null) return false;
        if (avatarUrl != null ? !avatarUrl.equals(userInfo.avatarUrl) : userInfo.avatarUrl != null)
            return false;
        if (circularAvatarUrl != null ? !circularAvatarUrl.equals(userInfo.circularAvatarUrl) : userInfo.circularAvatarUrl != null)
            return false;
        if (language != null ? !language.equals(userInfo.language) : userInfo.language != null)
            return false;
        if (fantasyUUID != null ? !fantasyUUID.equals(userInfo.fantasyUUID) : userInfo.fantasyUUID != null)
            return false;
        if (fantasyToken != null ? !fantasyToken.equals(userInfo.fantasyToken) : userInfo.fantasyToken != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (nicName != null ? nicName.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (avatarUrl != null ? avatarUrl.hashCode() : 0);
        result = 31 * result + (circularAvatarUrl != null ? circularAvatarUrl.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (fantasyUUID != null ? fantasyUUID.hashCode() : 0);
        result = 31 * result + (fantasyToken != null ? fantasyToken.hashCode() : 0);
        result = 31 * result + (isOnline ? 1 : 0);
        return result;
    }
}
