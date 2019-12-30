package com.mertdogan.silentmodemanager;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

public class SilenceBroadcastReceiver extends BroadcastReceiver {
    NotificationManager nm;
    @Override
    public void onReceive(Context context, Intent intent) {
// TODO Auto-generated method stub
        AudioManager audio = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        //audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        //nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        //nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
        //audio.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
        Log.d("MODE", "is silence");
        }
}
