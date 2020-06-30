package com.cansevin.walknoti;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.ActivityIdentification;
import com.huawei.hms.location.ActivityIdentificationService;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;

public class LocationActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private SettingsClient settingsClient;
    LocationCallback mLocationCallback;
    private PendingIntent pendingIntent;
    private ActivityIdentificationService activityIdentificationService;
    private Double lLat,lLon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        settingsClient = LocationServices.getSettingsClient(this);
        CheckPermission();
        CheckLocationSettings();
        pendingIntent = getPendingIntent();
        //LocationCall();
    }

    private void SetMaps() {
        Intent maps = new Intent(this,MapsActivity.class);
        maps.putExtra("lat",lLat);
        maps.putExtra("lon",lLon);
        startActivity(maps);
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationBroadcastReceiver.class);
        intent.setAction(LocationBroadcastReceiver.ACTION_PROCESS_LOCATION);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void LocationCall() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    lLat = locationResult.getLastLocation().getLatitude();
                    lLon = locationResult.getLastLocation().getLongitude();
                }
            }
        };
        fusedLocationProviderClient
                .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                .addOnSuccessListener(aVoid -> {
                    //Processing when the API call is successful.
                })
                .addOnFailureListener(e -> {
                    //Processing when the API call fails.
                });
    }

    private void GetLastLocation(){
        Task<Location> task = fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location == null) {
                        return;
                    }
                })
                .addOnFailureListener(e -> {
                    //Exception handling logic.
                });
    }

    private void StopCallLocation(){
        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
                .addOnSuccessListener(aVoid -> {
                    //Location updates are stopped successfully.
                })
                .addOnFailureListener(e -> {
                    //Failed to stop location updates.
                });
    }

    private void CheckLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        mLocationRequest = new LocationRequest();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    lLat = locationResult.getLastLocation().getLatitude();
                    lLon = locationResult.getLastLocation().getLongitude();
                    SetMaps();
                }
            }
        };

        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(locationSettingsResponse -> fusedLocationProviderClient
                        .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                        .addOnSuccessListener(aVoid -> {

                        }))
                .addOnFailureListener(e -> {
                    //Settings do not meet targeting criteria
                    int statusCode = ((ApiException) e).getStatusCode();
                    if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        try {
                            ResolvableApiException rae = (ResolvableApiException) e;
                            Log.i("bla",e.toString());
                            rae.startResolutionForResult(LocationActivity.this, 0);
                        } catch (IntentSender.SendIntentException sie) {
                            Log.i("bla",sie.toString());
                        }
                    }
                });

    }

    private void CheckPermission() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] strings =
                        {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(this, strings, 1);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        "android.permission.ACCESS_BACKGROUND_LOCATION"};
                ActivityCompat.requestPermissions(this, strings, 2);
            }
        }
    }

}
