package tv.sportssidekick.sportssidekick.model.user;

import tv.sportssidekick.sportssidekick.model.Model;

/**
 * Created by Filip on 4/25/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class UserEvent {

    UserInfo userInfo;
    Error error;
    Model.UserState userState;


    Type type;

    public enum Type {
        onLogout,
        onLogin,
        onLoginAnonymously,
        onLoginError,
        // -- Register
        onRegister,
        onRegisterError,
        // -- Password reset
        onPasswordResetRequest,
        onPasswordResetRequestError,
        // -- Update details
        onDetailsUpdated,
        onDetailsUpdateError,
        // -- User State
        onStateUpdated,
        onStateUpdateError
    }

    public UserEvent(Type type) {
        this.type = type;
    }

    public UserEvent(Type type, UserInfo userInfo) {
        this.userInfo = userInfo;
        this.type = type;
    }

    public UserEvent(Type type, Error error) {
        this.error = error;
        this.type = type;
    }

    public UserEvent(Type type, Model.UserState userState) {
        this.userState = userState;
        this.type = type;
    }

    public Type getType() {
        return type;
    }
    public UserInfo getUserInfo() {
        return userInfo;
    }

    public Error getError() {
        return error;
    }

    public Model.UserState getUserState() {
        return userState;
    }
}
