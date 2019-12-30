package com.mertdogan.silentmodemanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class setLocationBased extends FragmentActivity implements OnMapReadyCallback {

    static Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    LatLng latlng;
    MarkerOptions markerOptions;
    SQLiteLayer db;
    EditText titleText;
    public static String location;
    private static final int REQUEST_CODE = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location_based);
        db=new SQLiteLayer(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(setLocationBased.this);
        fetchLastLocation();

        titleText=(EditText) findViewById(R.id.titleTextLoc);

        ((Button) findViewById(R.id.cancelButtonLoc)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(setLocationBased.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        ((Button) findViewById(R.id.applyButtonLoc)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!titleText.getText().toString().matches("")) {
                    if (db.insertDataLoc(location, titleText.getText().toString(), 1)) {
                        Toast.makeText(setLocationBased.this, "Added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(setLocationBased.this, "Not Added", Toast.LENGTH_SHORT).show();
                    }

                    Intent i = new Intent(setLocationBased.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                else{
                    Toast.makeText(setLocationBased.this, "You have to enter a title", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchLastLocation() {

        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation = location;
                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude()+""+ currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
                    supportMapFragment.getMapAsync(setLocationBased.this);

                }
            }
        });
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        latlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        markerOptions = new MarkerOptions().position(latlng).title("You are here");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 5));
        googleMap.addMarker(markerOptions);
        if(this.location==null)
            this.location=latlng.toString();
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                Toast.makeText(getApplicationContext(), point.toString(), Toast.LENGTH_SHORT).show();
                googleMap.clear();
                markerOptions = new MarkerOptions().position(point).title("You picked here");
                googleMap.addMarker(markerOptions);
                setLocationBased.location = point.toString();
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE:
                if(grantResults.length>0 && grantResults.length==PackageManager.PERMISSION_GRANTED){
                    fetchLastLocation();
                }
                break;
        }
    }

    public static Location getCurrentLocation(){
        return currentLocation;
    }

}



