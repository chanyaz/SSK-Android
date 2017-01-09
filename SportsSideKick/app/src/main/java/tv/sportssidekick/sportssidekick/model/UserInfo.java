package tv.sportssidekick.sportssidekick.model;

import java.util.HashMap;

/**
 * Created by Djordje Krutil on 6.12.2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class UserInfo extends FirebseObject {

    private String userId;
    private String firstName;
    private String lastName;
    private String nicName;
    private String phone;
    private String avatarUrl;
    private String circularAvatarUrl;
    private String language;
    private String fantasyUUID;
    private String fantasyToken;
    private HashMap<String, Boolean> following;
    private HashMap<String, Boolean> followers;
    private HashMap<String, Boolean> tokens;
    private HashMap<String, Long> friendshipRequests;
    private boolean isOnline;

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
        setFriendshipRequests(userInfo.getFriendshipRequests());
        setFollowing(userInfo.getFollowing());
        setTokens(userInfo.getTokens());
        setFollowers(userInfo.getFollowers());
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

    public HashMap<String, Long> getFriendshipRequests() {
        return friendshipRequests;
    }

    public void setFriendshipRequests(HashMap<String, Long> friendshipRequests) {
        this.friendshipRequests = friendshipRequests;
    }

    public HashMap<String, Boolean> getFollowing() {
        return following;
    }

    public UserInfo setFollowing(HashMap<String, Boolean> following) {
        this.following = following;
        return this;
    }

    public HashMap<String, Boolean> getFollowers() {
        return followers;
    }

    public UserInfo setFollowers(HashMap<String, Boolean> followers) {
        this.followers = followers;
        return this;
    }

    public HashMap<String, Boolean> getTokens() {
        return tokens;
    }

    public UserInfo setTokens(HashMap<String, Boolean> tokens) {
        this.tokens = tokens;
        return this;
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
        if (following != null ? !following.equals(userInfo.following) : userInfo.following != null)
            return false;
        if (followers != null ? !followers.equals(userInfo.followers) : userInfo.followers != null)
            return false;
        if (tokens != null ? !tokens.equals(userInfo.tokens) : userInfo.tokens != null)
            return false;
        return friendshipRequests != null ? friendshipRequests.equals(userInfo.friendshipRequests) : userInfo.friendshipRequests == null;
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
        result = 31 * result + (following != null ? following.hashCode() : 0);
        result = 31 * result + (followers != null ? followers.hashCode() : 0);
        result = 31 * result + (tokens != null ? tokens.hashCode() : 0);
        result = 31 * result + (friendshipRequests != null ? friendshipRequests.hashCode() : 0);
        result = 31 * result + (isOnline ? 1 : 0);
        return result;
    }
}
