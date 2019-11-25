package com.eis.phoneringer.RingCommands;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/**
 * Class that interfaces with the RingtoneManager class of the Android library
 *
 * @author Alessandra Tonin, Luca Crema, Alberto Ursino
 */
public class RingtoneHandler {

    private static Ringtone defaultRingtone;
    private static RingtoneHandler ringtoneHandler = new RingtoneHandler();

    /**
     * Private constructor
     */
    private RingtoneHandler() {
    }

    /**
     * @return the singleton
     */
    public static RingtoneHandler getInstance() {
        return ringtoneHandler;
    }

    /**
     * Get the tone of the specified type
     *
     * @param ctx the current application context
     * @return the requested tone
     */
    public Ringtone getDefaultTone(Context ctx, int type) {
        return RingtoneManager.getRingtone(ctx, getDefaultToneUri(type));
    }

    /**
     * Get the default ringtone
     *
     * @param ctx the current application context
     * @return the actual ringtone
     */
    public Ringtone getDefaultRingtone(Context ctx) {
        if (defaultRingtone == null)
            defaultRingtone = getDefaultTone(ctx, RingtoneManager.TYPE_RINGTONE);
        return defaultRingtone;
    }

    /**
     * Get the default alarm tone
     *
     * @param ctx the current application context
     * @return the actual alarm tone
     */
    public Ringtone getDefaultAlarmTone(Context ctx) {
        return getDefaultTone(ctx, RingtoneManager.TYPE_ALARM);
    }

    /**
     * Plays a ringtone (overload di ringtonePlay(Ringtone ringtone, final int USAGE_CODE))
     *
     * @param ringtone the default ringtone
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void playRingtone(Ringtone ringtone) {
        playRingtone(ringtone, AudioAttributes.USAGE_ALARM);
    }

    /**
     * Stops the playing sound
     *
     * @param ringtone the playing sound (ringtone or alarm tone)
     */
    public void stopRingtone(Ringtone ringtone) {
        ringtone.stop();
    }

    /**
     * User can decide in which mode he wants to play the ringtone
     *
     * @param ringtone   the sound to be played
     * @param USAGE_CODE from AudioAttributes
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void playRingtone(@NonNull Ringtone ringtone, final int USAGE_CODE) {
        ringtone.setAudioAttributes(new AudioAttributes.Builder().setUsage(USAGE_CODE).build());
        ringtone.play();

    }

    /**
     * @param type taken from RingtoneManager API
     * @return the Uri of the specified type tone
     */
    private Uri getDefaultToneUri(int type) {
        return RingtoneManager.getDefaultUri(type);
    }
}
