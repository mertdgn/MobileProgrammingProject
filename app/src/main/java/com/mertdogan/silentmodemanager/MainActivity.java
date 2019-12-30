package com.mertdogan.silentmodemanager;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    SQLiteLayer db;
    public ArrayList<SilentModeSetting> savedSettings;
    ListView lv;
    static ArrayAdapter<SilentModeSetting> adaptor;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    PendingIntent pendingIntent2;


    static Location currentLocation = null;
    static double distance;
    public static ArrayList<Location> targetLocations = new ArrayList<>();
    static MainActivity instance;
    public static MainActivity getInstance(){
        return instance;
    }
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
        }

        instance = this;
        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                updateLocation();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(MainActivity.this,"You must give permission", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

            }
        }).check();
        db = new SQLiteLayer(this);
        lv = (ListView) findViewById(R.id.listView);

        savedSettings = makeList();

        adaptor = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, savedSettings);
        lv.setAdapter(adaptor);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SilentModeSetting item = savedSettings.get(i);
                if (item.getSetType() == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putDouble("latitude", item.getLocation().latitude);
                    bundle.putDouble("longitude", item.getLocation().longitude);
                    bundle.putString("title", item.getTitle());
                    bundle.putInt("id", item.getIdLoc());
                    targetLocations=new ArrayList<>();
                    Intent intent = new Intent(MainActivity.this, editLocationBased.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

                if (item.getSetType() == 0) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", item);
                    ArrayList<Integer> ids = getIds();
                    deleteSets(ids);
                    Intent intent = new Intent(MainActivity.this, editSettingActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        });

        ((Button) findViewById(R.id.setButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, setButtonActivity.class);
                startActivity(i);
            }
        });


        /*alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE)+1);
        cal.set(Calendar.SECOND, 0);
        Intent locIntent= new Intent(MainActivity.this, LocationBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, locIntent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),1000, pendingIntent);*/

        for (final SilentModeSetting s : savedSettings) {
            if (s.getSetType() == 0) {
                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Calendar calendarStart = Calendar.getInstance();
                Calendar calendarEnd = Calendar.getInstance();


                calendarStart.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s.getStartTime().substring(0, 2)));
                calendarStart.set(Calendar.MINUTE, Integer.parseInt(s.getStartTime().substring(s.getStartTime().length() - 2)));
                calendarStart.set(Calendar.SECOND, 0);
                String mode = s.getMode();
                Intent myIntent;
                if (calendarStart.getTime().after(Calendar.getInstance().getTime())) {

                    if (mode.equals("Do Not Disturb"))
                        myIntent = new Intent(MainActivity.this, VibrateBroadcastReceiver.class);
                    else if (mode.equals("Silent"))
                        myIntent = new Intent(MainActivity.this, SilenceBroadcastReceiver.class);
                    else
                        myIntent = new Intent(MainActivity.this, SilenceBroadcastReceiver.class);

                    pendingIntent = PendingIntent.getBroadcast(MainActivity.this, s.getId(), myIntent, 0);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarStart.getTimeInMillis(), pendingIntent);
                }


                calendarEnd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s.getEndTime().substring(0, 2)));
                calendarEnd.set(Calendar.MINUTE, Integer.parseInt(s.getEndTime().substring(s.getEndTime().length() - 2)));
                calendarEnd.set(Calendar.SECOND, 0);
                if (calendarEnd.getTime().after(Calendar.getInstance().getTime())) {
                    Intent myIntent1 = new Intent(MainActivity.this, NormalBroadcastReceiver.class);
                    pendingIntent2 = PendingIntent.getBroadcast(MainActivity.this, -(s.getId()), myIntent1, 0);
                    if (!isConflicted(savedSettings, s)) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarEnd.getTimeInMillis(), pendingIntent2);
                    } else {
                        alarmManager.cancel(pendingIntent2);
                    }


                }
            }

            if (s.getSetType() == 1) {

            double latitude=s.getLocation().latitude;
            double longitude=s.getLocation().longitude;
            Location destLocation=new Location("destLocation");
            destLocation.setLatitude(latitude);
            destLocation.setLongitude(longitude);
            targetLocations.add(destLocation);
        }

      }

        ((Button) findViewById(R.id.locationBasedButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, setLocationBased.class);
                startActivity(i);
            }
        });


    }

    private void updateLocation() {
        buildLocationRequest();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
    }


    private void buildLocationRequest() {
        locationRequest=new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(0f);
    }


    public ArrayList<SilentModeSetting> makeList() {
        Cursor cursor = db.viewData();
        Cursor cursor2 = db.viewDataLoc();
        ArrayList<SilentModeSetting> smsettings = new ArrayList<SilentModeSetting>();
        if (cursor.getCount()==0 && cursor2.getCount()==0) {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        } else {

            while (cursor.moveToNext()) {
                String startTime = cursor.getString(2);
                String endTime = cursor.getString(3);
                String s = cursor.getString(4).replace("[","").replace("]","").replace(",","");
                Scanner scanner = new Scanner(s);
                List<Integer> days = new ArrayList<Integer>();
                while (scanner.hasNextInt()) {
                    days.add(scanner.nextInt());
                }

                String mode = cursor.getString(5);
                String title = cursor.getString(1);
                SilentModeSetting sms = new SilentModeSetting(startTime, endTime, days, mode, title);
                int id = cursor.getInt(0);
                int setType = cursor.getInt(6);
                sms.setId(id);
                sms.setSetType(setType);
                smsettings.add(sms);
            }
            cursor.close();

           while(cursor2.moveToNext()){
                String titleLoc = cursor2.getString(1);

                String[] latLong =  cursor2.getString(2).replace("lat/lng: (", "").replace(")","").split(",");
                double latitude = Double.parseDouble(latLong[0]);
                double longitude = Double.parseDouble(latLong[1]);
                LatLng location = new LatLng(latitude, longitude);
                SilentModeSetting sms = new SilentModeSetting(location,titleLoc);
                int idLoc = cursor2.getInt(0);
                int setType = cursor2.getInt(3);
                sms.setIdLoc(idLoc);
                sms.setSetType(setType);
                smsettings.add(sms);
            }
            cursor2.close();
        }

        return smsettings;
    }

    public ArrayList<Integer> getIds(){
        Cursor cursor = db.viewData();
        ArrayList<Integer> ids = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id=cursor.getInt(0);
            ids.add(id);
        }
        return ids;
    }

    public boolean isConflicted( ArrayList<SilentModeSetting> arr, SilentModeSetting obj){
        Boolean check = false;
        Calendar calStart = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        Calendar calEndObj = Calendar.getInstance();
        calEndObj.set(Calendar.HOUR_OF_DAY, Integer.parseInt(obj.getEndTime().substring(0,2)));
        calEndObj.set(Calendar.MINUTE, Integer.parseInt(obj.getEndTime().substring(obj.getEndTime().length() - 2)));
        calEndObj.set(Calendar.SECOND, 0);
        for(SilentModeSetting s: arr){
            if(s.getSetType()==0){
            calStart.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s.getStartTime().substring(0,2)));
            calStart.set(Calendar.MINUTE, Integer.parseInt(s.getStartTime().substring(s.getStartTime().length() - 2)));
            calStart.set(Calendar.SECOND, 0);
            calEnd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s.getEndTime().substring(0,2)));
            calEnd.set(Calendar.MINUTE, Integer.parseInt(s.getEndTime().substring(s.getEndTime().length() - 2)));
            calEnd.set(Calendar.SECOND, 0);
            if(calEndObj.getTime().after(calStart.getTime()) && calEndObj.getTime().before(calEnd.getTime()))
               check = true;
            }
        }
            return check;
    }

    public void deleteSets(ArrayList<Integer> ids){

        ids.add(0);
        for(int i : ids) {
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, i, new Intent(MainActivity.this, SilenceBroadcastReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, i, new Intent(MainActivity.this, VibrateBroadcastReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, i, new Intent(MainActivity.this, NormalBroadcastReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
        }

    }


    public PendingIntent getPendingIntent() {

        Intent intent = new Intent(this, LocationBroadcastReceiver.class);
        intent.setAction(LocationBroadcastReceiver.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void setPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
    }

    public void showDistance(final Double distance){
        MainActivity.this.runOnUiThread(new Runnable(){
            @Override
            public void run(){
                Toast.makeText(MainActivity.this, String.valueOf(distance), Toast.LENGTH_SHORT).show();
            }
        });

    }
}

