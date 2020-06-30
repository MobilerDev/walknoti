package com.cansevin.walknoti;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cansevin.walknoti.models.direction.DirectionsRequest;
import com.cansevin.walknoti.models.direction.DirectionsResponse;
import com.cansevin.walknoti.models.direction.LatLngData;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
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
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;
import com.huawei.hms.maps.model.PolylineOptions;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.LocationType;
import com.huawei.hms.site.api.model.NearbySearchRequest;
import com.huawei.hms.site.api.model.NearbySearchResponse;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;
import com.huawei.nearestcinema.model.Paths;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_WIFI_STATE;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Double lLat,lLon;
    private HuaweiMap hMap;
    private SearchService searchService;
    private Marker mMarker;
    private String apikey = "CV7coJwW5hLhLVTccQwNC3S/+lMos63/0ChEnYmqF2mQKkv4ap5DZvy6gX96h7P6WecZoGPDEeM5L5uEcbuiVSEnhqLo",name,address;
    private MapView mMapView;
    private PendingIntent pendingIntent;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private SettingsClient settingsClient;
    private NearbySearchRequest request;
    private Coordinate location;
    LocationCallback mLocationCallback;
    private DirectionsRequest directionsRequest;
    private RecyclerView redirection;
    public ActivityIdentificationService activityIdentificationService;
    private DirectionsViewModel directionsViewModel;
    PolylineOptions polylineOptions = new PolylineOptions();
    private LatLngData origin,destination;
    private TextView directionStepsMainInfo,directionStepsVia;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CheckPermissions();
        setContentView(R.layout.activity_maps);
        activityIdentificationService = ActivityIdentification.getService(this);
        pendingIntent = getPendingIntent();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        settingsClient = LocationServices.getSettingsClient(this);
        redirection = findViewById(R.id.directionStepsRecyclerView);
        directionStepsMainInfo = findViewById(R.id.directionStepsMainInfo);
        directionStepsVia = findViewById(R.id.directionStepsVia);
        requestActivityUpdates(5000);
        String encodedApiKey = null;
        try {
            encodedApiKey = URLEncoder.encode(apikey, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        searchService = SearchServiceFactory.create(this,encodedApiKey);
        directionsViewModel = new DirectionsViewModel();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        redirection.setLayoutManager(linearLayoutManager);
        mMapView = findViewById(R.id.mapView);
        CheckLocationSettings();
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle("MapViewBundleKey");
        }
        getFirstPlace();
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
    }

    @RequiresPermission(allOf = {ACCESS_FINE_LOCATION, ACCESS_WIFI_STATE})
    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
            Log.d("onMapReady", huaweiMap.toString());
            hMap = huaweiMap;
            hMap.setMyLocationEnabled(true);// Enable the my-location overlay.
            hMap.getUiSettings().setCompassEnabled(true);
            hMap.getUiSettings().setMyLocationButtonEnabled(true);// Enable the my-location icon.
            hMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
            hMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.347142, -6.269250), 15));
    }

    class CustomInfoWindowAdapter implements HuaweiMap.InfoWindowAdapter {
        private final View mWindow;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            TextView txtvTitle = mWindow.findViewById(R.id.infoTile);
            TextView txtvAddress = mWindow.findViewById(R.id.infoAddress);
            TextView txtvdirection = mWindow.findViewById(R.id.infoGetDirections);

            txtvTitle.setText(name);
            txtvAddress.setText(address);

            txtvdirection.setOnClickListener(v -> {
                observeDirection();
            });

            return mWindow;
        }
    }


    private void observeDirection(){
        directionsViewModel.
                getDirectionsResponse().observe(this, this::SetBottomSheetDirections);
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationBroadcastReceiver.class);
        intent.setAction(LocationBroadcastReceiver.ACTION_PROCESS_LOCATION);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private void SetBottomSheetDirections(DirectionsResponse directionsResponse){
        String durationTxt = directionsResponse.getRoutes().get(0).getPaths().get(0).getDurationText()+" "+
                directionsResponse.getRoutes().get(0).getPaths().get(0).getDistanceText();
        directionStepsMainInfo.setText(durationTxt);
        directionStepsVia.setText(directionsResponse.getRoutes().get(0).getPaths().get(0).getSteps().get(0).getRoadName());
        DirectionsAdapter directionsAdapter = new DirectionsAdapter(this,directionsResponse.getRoutes().get(0).getPaths().get(0).getSteps());
        redirection.setAdapter(directionsAdapter);
        addPolyLines(directionsResponse.getRoutes().get(0).getPaths().get(0));
    }

    public void addPolyLines(Paths path){
        polylineOptions.add(new LatLng(path.getStartLocation().getLat(),path.getStartLocation().getLng()));

        for(int a=0;a < path.getSteps().size();a++){
            for(int b=0;b < path.getSteps().get(a).getPolyline().size();b++){
                polylineOptions.add(new LatLng(path.getSteps().get(a).getPolyline().get(b).getLat(),
                        path.getSteps().get(a).getPolyline().get(b).getLng()));
            }
        }

        polylineOptions.add(new LatLng(path.getEndLocation().getLat(),path.getEndLocation().getLng()));
        polylineOptions.color(Color.RED);
        polylineOptions.width(10f);
        hMap.addPolyline(polylineOptions);
    }



    private void addMarker() {
        if (null != mMarker) {
            mMarker.remove();
        }
        MarkerOptions options = new MarkerOptions().position(new LatLng(lLat, lLon));
                 options.icon(BitmapDescriptorFactory.fromResource(R.drawable.bottle128));
        mMarker = hMap.addMarker(options);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void CheckPermissions(){
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
                            rae.startResolutionForResult(MapsActivity.this, 0);
                        } catch (IntentSender.SendIntentException sie) {
                            Log.i("bla",sie.toString());
                        }
                    }
                });

    }


    public void requestActivityUpdates(long detectionIntervalMillis) {
        try {
            if(pendingIntent != null){
                removeActivityUpdates();
            }
            pendingIntent = getPendingIntent();
            LocationBroadcastReceiver.addIdentificationListener();
            activityIdentificationService.createActivityIdentificationUpdates(detectionIntervalMillis, pendingIntent)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("request", "createActivityIdentificationUpdates onSuccess");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Log.e("request", "createActivityIdentificationUpdates onFailure:" + e.getMessage());
                        }
                    });
        } catch (Exception e) {
            Log.e("request", "createActivityIdentificationUpdates exception:" + e.getMessage());
        }
    }

    public void removeActivityUpdates() {
        try {
            LocationBroadcastReceiver.removeIdentificationListener();
            Log.i("remove", "start to removeActivityUpdates");
            activityIdentificationService.deleteActivityIdentificationUpdates(pendingIntent)
                    .addOnSuccessListener(aVoid -> Log.i("remove", "deleteActivityIdentificationUpdates onSuccess"))
                    .addOnFailureListener(e -> Log.e("remove", "deleteActivityIdentificationUpdates onFailure:" + e.getMessage()));
        } catch (Exception e) {
            Log.e("remove", "removeActivityUpdates exception:" + e.getMessage());
        }
    }

    private void getFirstPlace(){
        request = new NearbySearchRequest();
        location = new Coordinate(53.347142, -6.269250);
        request.setLocation(location);
        request.setRadius(6000);
        request.setPoiType(LocationType.GROCERY_OR_SUPERMARKET);
        request.setLanguage("en");
        request.setPageIndex(1);
        request.setPageSize(2);
        SearchResultListener<NearbySearchResponse> resultListener = new SearchResultListener<NearbySearchResponse>() {
            @Override
            public void onSearchResult(NearbySearchResponse results) {
                List<Site> sites = results.getSites();
                if (results.getTotalCount() <= 0 || sites == null || sites.size() <= 0) {
                    return;
                }
                Site site = results.getSites().get(0);
                Log.i("siteid", String.format("siteId:'%s', name: %s\r\n distance:%f poi:%s coordinate:%s",site.getSiteId(),
                        site.getName(),site.getDistance(),site.getPoi().toString(),site.getLocation().getLat(),site.getLocation().getLng()));
                lLat = site.getLocation().getLat();
                lLon = site.getLocation().getLng();
                name = site.getName();
                address = site.getFormatAddress();
                origin = new LatLngData(53.347142, -6.269250);
                destination = new LatLngData(lLat, lLon);
                directionsRequest = new DirectionsRequest(origin, destination);
                directionsViewModel.directions("walking", directionsRequest);
                addMarker();
                /*
                for (Site site : sites) {
                    Log.i("siteid", String.format("siteId: '%s', name: %s\r\n distance:%f", site.getSiteId(), site.getName(),site.getDistance()));
                    mName.add(site.getName());
                    mAddress.add(site.getFormatAddress());
                    mDistance.add(String.valueOf(site.getDistance()));
                    mHour.add(site.getPoi().toString());
                    mWeb.add(site.getSiteId());
                }
                getPlace();*/
            }
            @Override
            public void onSearchError(SearchStatus status) {
                Log.i("siteid", "Error : " + status.getErrorCode() + " " + status.getErrorMessage());
            }
        };
        searchService.nearbySearch(request, resultListener);
    }
}
