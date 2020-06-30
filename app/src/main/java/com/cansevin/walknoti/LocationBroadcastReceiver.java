package com.cansevin.walknoti;

import java.util.List;

import com.huawei.hms.location.ActivityConversionData;
import com.huawei.hms.location.ActivityConversionResponse;
import com.huawei.hms.location.ActivityIdentificationData;
import com.huawei.hms.location.ActivityIdentificationResponse;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationResult;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

public class LocationBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_PROCESS_LOCATION = "com.huawei.hms.location.ACTION_PROCESS_LOCATION";

    private static final String TAG = "LocationReceiver";

    public static boolean isListenActivityIdentification = false;

    public static boolean isListenActivityConversion = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            StringBuilder sb = new StringBuilder();
            if (ACTION_PROCESS_LOCATION.equals(action)) {
                // Processing LocationResult information
                Log.i(TAG, "null != intent");
                String messageBack = "";
                ActivityConversionResponse activityTransitionResult = ActivityConversionResponse.getDataFromIntent(intent);
                if (activityTransitionResult != null && isListenActivityConversion == true) {
                    List<ActivityConversionData> list = activityTransitionResult.getActivityConversionDatas();
                    for (int i = 0; i < list.size(); i++) {
                        Log.i(TAG, "activityTransitionEvent[" + i + "]" + list.get(i));
                        messageBack += list.get(i) + "\n";
                    }
                }

                ActivityIdentificationResponse activityRecognitionResult = ActivityIdentificationResponse.getDataFromIntent(intent);
                if (activityRecognitionResult != null && isListenActivityIdentification == true) {
                    List<ActivityIdentificationData> list = activityRecognitionResult.getActivityIdentificationDatas();
                }
                if (LocationResult.hasResult(intent)) {
                    LocationResult result = LocationResult.extractResult(intent);
                    if (result != null) {
                        List<Location> locations = result.getLocations();
                        if (!locations.isEmpty()) {
                            sb.append("requestLocationUpdatesWithIntent[Longitude,Latitude,Accuracy]:\r\n");
                            for (Location location : locations) {
                                sb.append(location.getLongitude())
                                    .append(",")
                                    .append(location.getLatitude())
                                    .append(",")
                                    .append(location.getAccuracy())
                                    .append("\r\n");
                            }
                        }
                    }
                }

               // Processing LocationAvailability information
                if (LocationAvailability.hasLocationAvailability(intent)) {
                    LocationAvailability locationAvailability =
                        LocationAvailability.extractLocationAvailability(intent);
                    if (locationAvailability != null) {
                        sb.append("[locationAvailability]:" + locationAvailability.isLocationAvailable() + "\r\n");
                    }
                }
            }
        }
    }
    public static void addConversionListener(){
        isListenActivityConversion = true;
    }
    public static void removeConversionListener(){
        isListenActivityConversion = false;
    }
    public static void addIdentificationListener(){
        isListenActivityIdentification = true;
    }
    public static void removeIdentificationListener(){
        isListenActivityIdentification = false;
    }
}
