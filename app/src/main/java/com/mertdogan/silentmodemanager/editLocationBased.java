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

public class editLocationBased extends FragmentActivity implements OnMapReadyCallback {


    LatLng latlng;
    MarkerOptions markerOptions;
    SQLiteLayer db;
    EditText titleText;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    private static final int REQUEST_CODE = 101;
    Bundle bundle;
    public static String location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location_based);

        Intent intent=this.getIntent();
        bundle = intent.getExtras();
        db=new SQLiteLayer(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(editLocationBased.this);
        fetchLastLocation();
        titleText = (EditText) findViewById(R.id.titleTextLoc);
        titleText.setText(bundle.getString("title"));

        ((Button) findViewById(R.id.deleteButtonLocEdit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteDataLoc(bundle.getInt("id"));
                Toast.makeText(editLocationBased.this, "Deleted", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(editLocationBased.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        ((Button) findViewById(R.id.cancelButtonLoc)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(editLocationBased.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        ((Button) findViewById(R.id.applyButtonLocEdit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!titleText.getText().toString().matches("")) {
                    if (db.updateDataLoc(bundle.getInt("id"), location, titleText.getText().toString(), 1)) {
                        Toast.makeText(editLocationBased.this, "Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(editLocationBased.this, "Not Updated", Toast.LENGTH_SHORT).show();
                    }

                    Intent i = new Intent(editLocationBased.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                else{
                    Toast.makeText(editLocationBased.this, "You have to enter a title", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        latlng = new LatLng(bundle.getDouble("latitude"), bundle.getDouble("longitude"));
        this.location=latlng.toString();
        Toast.makeText(getApplicationContext(), latlng.toString(), Toast.LENGTH_SHORT).show();
        markerOptions = new MarkerOptions().position(latlng).title("You picked here");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 5));
        googleMap.addMarker(markerOptions);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                Toast.makeText(getApplicationContext(), point.toString(), Toast.LENGTH_SHORT).show();
                googleMap.clear();
                markerOptions = new MarkerOptions().position(point).title("You picked here");

                googleMap.addMarker(markerOptions);
                editLocationBased.location = point.toString();
            }

        });

    }

    private void fetchLastLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation = location;
                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude()+""+ currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragmentEdit);
                    supportMapFragment.getMapAsync(editLocationBased.this);

                }
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
}
