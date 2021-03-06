package base.app.util.commons;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Base64;

import com.gamesparks.sdk.GS;
import com.gamesparks.sdk.IGSPlatform;
import com.gamesparks.sdk.api.GSData;

import java.io.File;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Giuseppe Perniola
 */
public class GSAndroidPlatform implements IGSPlatform {

    private static final String GS_CURR_STATUS = "GSCurrStatus";

    private static GS instance;
    private Handler mainHandler;
    private Context context;

    public static void initialise(final Context ctx, String apiKey, String secret) {
        if (instance == null) {
            GSAndroidPlatform gsAndroidPlatform = new GSAndroidPlatform(ctx);
            instance = new GS(apiKey, secret, null, false, true, gsAndroidPlatform);
        }
    }

    private GSAndroidPlatform(Context context) {
        this.context = context;
        mainHandler = new Handler(context.getMainLooper());
    }

    public static GS getInstance() {
        return instance;
    }

    @Override
    public File getWritableLocation() {
        return context.getFilesDir();
    }

    private void storeValue(String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(GS_CURR_STATUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String loadValue(String key) {
        try {
            SharedPreferences settings = context.getSharedPreferences(GS_CURR_STATUS, Context.MODE_PRIVATE);
            return settings.getString(key, "");
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void executeOnMainThread(Runnable job) {
        if (mainHandler != null) {
            mainHandler.post(job);
        }
    }

    @Override
    public String getDeviceId() {
        return null;
    }

    @Override
    public String getDeviceOS() {
        return null;
    }

    @Override
    public String getPlatform() {
        return null;
    }

    @Override
    public String getSDK() {
        return null;
    }

    @Override
    public String getDeviceType() {
        return null;
    }

    @Override
    public GSData getDeviceStats() {
        return null;
    }

    @Override
    public String getPlayerId() {
        return loadValue("playerId");
    }

    @Override
    public String getAuthToken() {
        return loadValue("authToken");
    }

    @Override
    public void setPlayerId(String value) {
        storeValue("playerId", value);
    }

    @Override
    public void setAuthToken(String value) {
        storeValue("authToken", value);
    }

    @Override
    public Object getHmac(String nonce, String secret) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            return Base64.encodeToString(sha256_HMAC.doFinal(nonce.getBytes("UTF-8")), Base64.NO_WRAP);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void logMessage(String msg) {
        System.out.println(msg);
    }

    @Override
    public void logError(Throwable t) {
        System.out.println(t.getMessage());
    }
}
