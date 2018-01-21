package base.app.data.user.notifications;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import base.app.ui.fragment.user.auth.LoginApi;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "Firebase InstanceID Service";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        LoginApi.getInstance().setFirebaseToken(refreshedToken);
    }

}