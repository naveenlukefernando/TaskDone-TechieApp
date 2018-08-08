package com.pdm.taskdone;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class activity_Welcome extends FragmentActivity implements OnMapReadyCallback

{

    private GoogleMap mMap;

    FusedLocationProviderClient fusedLocationProviderClient;
    com.google.android.gms.location.LocationCallback locationCallback;


    //play services init
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQEUST = 7001;

    private LocationRequest mlocationRequest;
    private GoogleApiClient mgoogleApiClient;
    private Location mLastlocation;

    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    DatabaseReference workers;
    GeoFire mGeoFire;

    Marker mCurrent;

    MaterialAnimatedSwitch location_switch;
    SupportMapFragment msupportMapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

       fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //init map

        location_switch = (MaterialAnimatedSwitch) findViewById(R.id.location_switch);
        location_switch.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean isOnline) {

                if (isOnline) {

                    if (ActivityCompat.checkSelfPermission(activity_Welcome.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(activity_Welcome.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }

                    buildLocationCallBack();
                    buildLocationRequest();

                    fusedLocationProviderClient.requestLocationUpdates(mlocationRequest, locationCallback, Looper.myLooper());
                    displayLocation();
                    Toast.makeText(activity_Welcome.this, "You are Online", Toast.LENGTH_SHORT).show();

                } else {

                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);

                    mCurrent.remove();
                    Toast.makeText(activity_Welcome.this, "You are Offline", Toast.LENGTH_SHORT).show();


                }
            }
        });


        // Geofire

        workers = FirebaseDatabase.getInstance().getReference("Worker");
        mGeoFire = new GeoFire(workers);

        setUplocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    buildLocationCallBack();
                    buildLocationRequest();


                        if (location_switch.isChecked()) {
                            displayLocation();
                        }


                }
        }
    }

    private void setUplocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //request runtime permission
            ActivityCompat.requestPermissions(this, new String[]{

                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);

        } else

        {

            buildLocationRequest();
            buildLocationCallBack();


        }

        {
            if (location_switch.isChecked()) {
                displayLocation();
            }

        }

    }

    private void buildLocationCallBack() {

        locationCallback = new com.google.android.gms.location.LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                for (Location location : locationResult.getLocations()) {
                    mLastlocation = location;

                }
            }
        };
    };

    @SuppressLint("RestrictedApi")
    private void buildLocationRequest() {

        mlocationRequest = new LocationRequest();
        mlocationRequest.setInterval(UPDATE_INTERVAL);
        mlocationRequest.setFastestInterval(FATEST_INTERVAL);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mlocationRequest.setSmallestDisplacement(DISPLACEMENT);


    }


    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        mLastlocation = location;

                    }
                });

        if (mLastlocation != null) {

            if (location_switch.isChecked()) {

                final double latitude = mLastlocation.getLatitude();
                final double longitude = mLastlocation.getLongitude();

                // update the database

                mGeoFire.setLocation(FirebaseAuth.getInstance().getUid(), new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                        // adding the marker
                        if (mCurrent != null) {
                            mCurrent.remove();  // this will removing current marker.
                            mCurrent = mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.worker_pin_man_marker))
                                    .position(new LatLng(latitude, longitude))
                                    .title("You"));


                            // map camera movement
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));

                            //Animation draw to location
                            rotateMaker(mCurrent, -360, mMap);
                            {


                            }
                        }


                    }
                });

            } else {
                Log.d("Task Done", "Error! Cant getting your location");

            }
        }

    }

    private void rotateMaker(final Marker mCurrent, final float i, GoogleMap mMap) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = mCurrent.getRotation();
        final long duration = 1500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {

                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                float rot = t * i + (1 - t) * startRotation;
                mCurrent.setRotation(-rot > 180 ? rot / 2 : rot);

            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
           mMap = googleMap;
//            Marker marker_new;



        mCurrent = mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.worker_pin_man_marker))
                    .position(new LatLng(6.914789, 79.973159))
                    .title("You"));


            // map camera movement
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(6.914789, 79.973159), 15.0f));

            //Animation draw to location
            rotateMaker(mCurrent, -360, mMap);



        //update location

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        buildLocationRequest();
        buildLocationCallBack();


        fusedLocationProviderClient.requestLocationUpdates(mlocationRequest, locationCallback, Looper.myLooper());
    }



}
