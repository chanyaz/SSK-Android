package tv.sportssidekick.sportssidekick.model;

import java.util.List;

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
    private List<String> friendshipRequests;
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
        setOnline(userInfo.isOnline());
        setFriendshipRequests(userInfo.getFriendshipRequests());
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

    public boolean isOnline() {
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

    public List<String> getFriendshipRequests() {
        return friendshipRequests;
    }

    public UserInfo setFriendshipRequests(List<String> friendshipRequests) {
        this.friendshipRequests = friendshipRequests;
        return this;
    }
}
