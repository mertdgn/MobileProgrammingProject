package com.mertdogan.silentmodemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.AudioManager;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;

public class LocationBroadcastReceiver extends BroadcastReceiver {

    static double distance;
    public static final String ACTION_PROCESS_UPDATE ="com.mertdogan.silentmodemanager.UPDATE_LOCATION";
    @Override
    public void onReceive(Context context, Intent intent) {
       /* Intent service = new Intent(context, BackgroundService.class);
        context.startService(service);*/

        if(intent != null){
            final String action = intent.getAction();

            if(ACTION_PROCESS_UPDATE.equals(action)){
                LocationResult result = LocationResult.extractResult(intent);
                if(result!=null){
                    Location location = result.getLastLocation();
                    for(Location l : MainActivity.targetLocations){
                        distance = location.distanceTo(l);
                        AudioManager audio = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
                        if(distance < 20){
                            audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        }
                        else{
                            audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        }
                        try{
                            MainActivity.getInstance().showDistance(distance);
                        }catch (Exception e) {
                            Toast.makeText(context, String.valueOf(distance), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }

    }

}
