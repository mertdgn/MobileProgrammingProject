package com.mertdogan.silentmodemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

public class NormalBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context arg0, Intent intent) {
// TODO Auto-generated method stub
        AudioManager audio = (AudioManager)arg0.getSystemService(Context.AUDIO_SERVICE);

        audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        Log.d("MODE", "is normal");
    }
}



