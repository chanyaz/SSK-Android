/**
 * 
 */
package base.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Base64;

import com.gamesparks.sdk.GS;
import com.gamesparks.sdk.IGSPlatform;

import java.io.File;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Giuseppe Perniola
 *
 */
public class GSAndroidPlatform implements IGSPlatform
{
	private static final String GS_CURR_STATUS = "GSCurrStatus";

    private static GS gs;
    private Handler mainHandler;
    private Context ctx;

	public static GS initialise(final Context ctx, String apiKey, String secret, String credential, boolean liveMode, boolean autoUpdate){
		if (gs == null){
			GSAndroidPlatform gsAndroidPlatform = new GSAndroidPlatform(ctx);
			gs = new GS(apiKey, secret, credential, liveMode, autoUpdate, gsAndroidPlatform);
		}
		return gs;
	}
	
	private GSAndroidPlatform(Context ctx){
		this.ctx = ctx;
		mainHandler = new Handler(ctx.getMainLooper());
	}
	
	public static GS gs(){
		return gs;
	}

	@Override
	public File getWritableLocation(){
		return ctx.getFilesDir();
	}
	
	public void storeValue(String key, String value){
		try{
			SharedPreferences 			settings = ctx.getSharedPreferences(GS_CURR_STATUS, Context.MODE_PRIVATE);
			SharedPreferences.Editor 	editor = settings.edit();
			editor.putString(key, value);
			editor.apply();
		}
		catch (Exception e){
			
		}
	}
	
	public String loadValue(String key){
		try{
			SharedPreferences settings = ctx.getSharedPreferences(GS_CURR_STATUS, Context.MODE_PRIVATE);
			return settings.getString(key, "");
		}
		catch (Exception e){
			return "";
		}
	}

	@Override
	public void executeOnMainThread(Runnable job){
		if (mainHandler != null){
			mainHandler.post(job);
		}
	}

	@Override
	public String getPlayerId()
	{
		return loadValue("playerId");
	}

	@Override
	public String getAuthToken()
	{
		return loadValue("authToken");
	}

	@Override
	public void setPlayerId(String value)
	{
		storeValue("playerId", value);
	}

	@Override
	public void setAuthToken(String value)
	{
		storeValue("authToken", value);
	}
	
	@Override
	public Object getHmac(String nonce, String secret){
		try{
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
			sha256_HMAC.init(secret_key);
			return Base64.encodeToString(sha256_HMAC.doFinal(nonce.getBytes("UTF-8")), Base64.NO_WRAP);
		}
		catch (Exception e){
			return null;
		}
	}
	@Override
	public void logMessage(String msg)
	{
		System.out.println(msg);
	}

	@Override
	public void logError(Throwable t)
	{
		System.out.println(t.getMessage());
	}
}
