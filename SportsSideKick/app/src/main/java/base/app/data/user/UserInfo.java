package base.app.data.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gamesparks.sdk.api.autogen.GSTypes;

import java.util.List;

import base.app.data.Model;

/**
 * Created by Djordje Krutil on 6.12.2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com eliav
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo {

    public enum UserType {
        fan, player, special
    }

    private UserType userType = UserType.fan;
    private String userId;

    @JsonProperty("userName")
    private String email;
    @JsonProperty("displayName")
    private String nicName;
    private String language;
    private String phone;
    private String firstName;
    private String lastName;
    @JsonProperty("subscribed")
    private Double subscribedDate;
    private Location location;
    private String authToken;

    private String avatarUrl;
    private String circularAvatarUrl;

    private String fantasyUUID;
    private String fantasyToken;

    private boolean isOnline;
    private boolean wallMute = false;
    private Model.UserState userState;

    //TODO @Filip Hook up with GS when backend is avaliable
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
    private int wallPins= 0;
    private int friendsCount = 0;
    private int requestedUserFriendsCount = 0;
    // --

    // read only
    private boolean aFriend;
    @JsonProperty("isFriendPendingRequest")
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    @JsonProperty("userName")
    public String getEmail() {
        return email;
    }

    @JsonProperty("userName")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("subscribed")
    public Double getSubscribedDate() {
        return subscribedDate;
    }

    @JsonProperty("subscribed")
    public void setSubscribedDate(Double subscribedDate) {
        this.subscribedDate = subscribedDate;
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

    @JsonProperty("displayName")
    public String getNicName() {
        return nicName;
    }

    @JsonProperty("displayName")
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

    public boolean isOnline() {
        return isOnline;
    }

    public int getWallPosts() {
        return wallPosts;
    }

    public void setWallPosts(int wallPosts) {
        this.wallPosts = wallPosts;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getVideosWatched() {
        return videosWatched;
    }

    public void setVideosWatched(int videosWatched) {
        this.videosWatched = videosWatched;
    }

    public int getChats() {
        return chats;
    }

    public void setChats(int chats) {
        this.chats = chats;
    }

    public int getVideoChats() {
        return videoChats;
    }

    public void setVideoChats(int videoChats) {
        this.videoChats = videoChats;
    }

    public int getPublicChats() {
        return publicChats;
    }

    public void setPublicChats(int publicChats) {
        this.publicChats = publicChats;
    }

    public int getMatchesHome() {
        return matchesHome;
    }

    public void setMatchesHome(int matchesHome) {
        this.matchesHome = matchesHome;
    }

    public int getMatchesAway() {
        return matchesAway;
    }

    public void setMatchesAway(int matchesAway) {
        this.matchesAway = matchesAway;
    }

    public int getCapsValue() {
        return capsValue;
    }

    public void setCapsValue(int capsValue) {
        this.capsValue = capsValue;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public int getWallPins() {
        return wallPins;
    }

    public void setWallPins(int wallPins) {
        this.wallPins = wallPins;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public boolean isaFriend() {
        return aFriend;
    }

    public void setaFriend(boolean aFriend) {
        this.aFriend = aFriend;
    }

    @JsonProperty("isFriendPendingRequest")
    public boolean isFriendPendingRequest() {
        return isFriendPendingRequest;
    }

    @JsonProperty("isFriendPendingRequest")
    public void setFriendPendingRequest(boolean friendPendingRequest) {
        isFriendPendingRequest = friendPendingRequest;
    }

    public boolean isFollowsMe() {
        return followsMe;
    }

    public void setFollowsMe(boolean followsMe) {
        this.followsMe = followsMe;
    }

    public boolean isiFollowHim() {
        return iFollowHim;
    }

    public void setiFollowHim(boolean iFollowHim) {
        this.iFollowHim = iFollowHim;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getRequestedUserFriendsCount() {
        return requestedUserFriendsCount;
    }

    public void setRequestedUserFriendsCount(int requestedUserFriendsCount) {
        this.requestedUserFriendsCount = requestedUserFriendsCount;
    }

    @JsonProperty("wallMute")
    public boolean isWallMute() {
        return wallMute;
    }

    @JsonProperty("wallMute")
    public void setWallMute(boolean wallMute) {
        this.wallMute = wallMute;
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
        return fantasyToken != null ? fantasyToken.equals(userInfo.fantasyToken) : userInfo.fantasyToken == null;
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
