package base.app.data.user;

import base.app.ui.fragment.user.auth.LoginApi;

/**
 * Created by Filip on 4/25/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class UserEvent {

    User user;
    Error error;
    LoginApi.UserState userState;


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

    public UserEvent(Type type, User user) {
        this.user = user;
        this.type = type;
    }

    public UserEvent(Type type, Error error) {
        this.error = error;
        this.type = type;
    }

    public UserEvent(Type type, LoginApi.UserState userState) {
        this.userState = userState;
        this.type = type;
    }

    public Type getType() {
        return type;
    }
    public User getUser() {
        return user;
    }

    public Error getError() {
        return error;
    }

    public LoginApi.UserState getUserState() {
        return userState;
    }
}
