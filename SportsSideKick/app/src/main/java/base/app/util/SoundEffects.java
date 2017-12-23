package base.app.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.util.SparseIntArray;

import base.app.R;

/**
 * Created by Filip on 2/6/2017.
 */

public class SoundEffects {

    public static final int ERROR = 1; // for "errors"
    public static final int ROLL_OVER = 2; // "close" states (closing a popup / coming back from a wall post etc
    public static final int SOFT = 3; // in any instances where selecting a users profile
    public static final int SUBTLE = 4; //  "open" states (opening a popup / opening a wall post / pressing a button)
    public static final int AUDIO_CALL = 5; //  play a sound for a video call

    private static final int PRIORITY = 1;
    private static final int NO_LOOP = 0;
    private static final float NORMAL_PLAYBACK_RATE = 1.0f;
    private static final String TAG = "SOUND EFFECTS";
    private static SoundEffects instance;

    public static SoundEffects getDefault(){
        if(instance==null) {
            instance = new SoundEffects();
        }
        return instance;
    }

    private SoundPool soundPool;
    private SparseIntArray soundPoolMap;

    private int volume;

    public void initialize(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 100);
        }

        soundPoolMap = new SparseIntArray();
        soundPoolMap.put(ERROR, soundPool.load(context, R.raw.error_sound, 1));
        soundPoolMap.put(ROLL_OVER, soundPool.load(context, R.raw.roll_over_button_sound, 1));
        soundPoolMap.put(SOFT, soundPool.load(context, R.raw.soft_click_button_sound, 1));
        soundPoolMap.put(SUBTLE, soundPool.load(context, R.raw.subtle_button_sound, 1));
        soundPoolMap.put(AUDIO_CALL, soundPool.load(context, R.raw.audio_call, 2));
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        volume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
    }
    public void playSound(int soundId) {
        Log.d(TAG, "Playing sound with id:" + soundPoolMap.get(soundId));
        soundPool.play(soundPoolMap.get(soundId), volume, volume, PRIORITY, NO_LOOP, NORMAL_PLAYBACK_RATE);
    }
}
