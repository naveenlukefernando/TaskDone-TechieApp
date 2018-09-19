package com.pdm.taskdone;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pdm.taskdone.Common.Common;
import com.pdm.taskdone.Helper.DirectionsJSONParser;
import com.pdm.taskdone.Model.FCMResponse;
import com.pdm.taskdone.Model.Notification;
import com.pdm.taskdone.Model.Sender;
import com.pdm.taskdone.Model.Token;
import com.pdm.taskdone.Remote.IFCMService;
import com.pdm.taskdone.Remote.IGoogleAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.pdm.taskdone.Common.Common.mLastlocation;

public class worker_tracking extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    double client_Lat,client_Lng;

    String clientID;


    //play services init
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQEUST = 7001;

    private LocationRequest mlocationRequest;
    private GoogleApiClient mgoogleApiClient;


    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    DatabaseReference workers;
    GeoFire mGeoFire;

    IGoogleAPI mService;
    IFCMService mFCMService;

    GeoFire geoFire;

    private Circle client_Marker ;
    private Marker worker_Marker;

    private Polyline direction;


    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    FusedLocationProviderClient fusedLocationProviderClient;
    com.google.android.gms.location.LocationCallback locationCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (getIntent() != null)
        {
            client_Lat = getIntent().getDoubleExtra("lat",-1.0);
            client_Lng = getIntent().getDoubleExtra("lng",-1.0);
            clientID = getIntent().getStringExtra("clientID");

        }

        mService = Common.getGoogleAPI();
        mFCMService = Common.getIFCMService();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

//        FirebaseDatabase.getInstance().goOnline(); // set Online
//
//        if (ActivityCompat.checkSelfPermission(worker_tracking.this,
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(worker_tracking.this,
//                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            return;
//        }
//
//        buildLocationCallBack();
//        buildLocationRequest();
//
//        fusedLocationProviderClient.requestLocationUpdates(mlocationRequest, locationCallback, Looper.myLooper());
//        displayLocation();




        setUplocation();




    }



    private void setUplocation() {


        Log.d("SETUPLOCATION","naveeennnnnnn");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            //request runtime permission
            ActivityCompat.requestPermissions(this, new String[]{

                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);

        } else

            buildLocationRequest();
        buildLocationCallBack();


        {

                displayLocation();


        }

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


                        if (mLastlocation != null) {



                                final double latitude = mLastlocation.getLatitude();
                                final double longitude = mLastlocation.getLongitude();

                                if (worker_Marker != null)
                                    worker_Marker.remove();

                                worker_Marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude))
                                .title("You")
                                .icon(BitmapDescriptorFactory.defaultMarker()));


                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),17.0f));

                                if (direction != null)
                                    direction.remove(); // remving old direction

                            getDirection();



//                            else {
//                                Log.d("Task Done", "Error! Cant getting your location");
//
//                            }
                        }

                    }
                });


    }

    private void getDirection() {

         LatLng currentPosition = new LatLng(mLastlocation.getLatitude(), mLastlocation.getLongitude());




        String requestApi = null;

        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&"+
                    "origin="+currentPosition.latitude+","+currentPosition.longitude+"&"+
                    "destination="+client_Lat+","+client_Lng+"&"+
                    "key="+getResources().getString(R.string.google_direction_api);

            Log.d("Yeah !!!! ", requestApi);  //print URL for debug

            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            try {

                                new ParserTask().execute(response.body().toString());



                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("fail",""+e);
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(worker_tracking.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();

        }


    }


    @SuppressLint("RestrictedApi")
    private void buildLocationRequest() {

        mlocationRequest = new LocationRequest();
        mlocationRequest.setInterval(UPDATE_INTERVAL);
        mlocationRequest.setFastestInterval(FATEST_INTERVAL);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mlocationRequest.setSmallestDisplacement(DISPLACEMENT);


    }

    private void buildLocationCallBack() {

        locationCallback = new com.google.android.gms.location.LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                for (Location location : locationResult.getLocations()) {
                    mLastlocation = location;

                }
                displayLocation();
            }
        };
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        client_Marker = mMap.addCircle(new CircleOptions()
        .center(new LatLng(client_Lat,client_Lng))
        .radius(50) // => radius is 50m
        .strokeColor(Color.BLUE)
        .fillColor(0x220000FF)
        .strokeWidth(5.0f));


        //Create GeoFensing with radius is 50m
        geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference(Common.worker_location_GPS));
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(client_Lat,client_Lng),0.05f);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                // we need client id to send notification
                // so , we will pass from previous activuty
                sendArrivedNotification (clientID);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });




        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d("UPDATE 1 ON MAP","UPDATEING IN MAP trackingggg");

            return;
        }

        Log.d("BUILDLOCATION 2 CALLING","trackinggggg");

        buildLocationRequest();
        buildLocationCallBack();



        fusedLocationProviderClient.requestLocationUpdates(mlocationRequest, locationCallback, Looper.myLooper());



    }

    private void sendArrivedNotification(String clientID) {

        Log.d("Send notification","sent sent ");
        Token token = new Token(clientID);
        // send notification with title is arrived and body is string
        Notification notification = new Notification("Arrived",String.format("The worker %s has arrived to your location",Common.currentUser.getName()));
        Sender sender = new Sender(token.getToken(),notification);

        mFCMService.sendMessage(sender).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                if (response.body().success != 1)
                {
                    Toast.makeText(worker_tracking.this,"Failed",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {

            }
        });


    }

    private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>>
    {
        ProgressDialog mDialog = new ProgressDialog(worker_tracking.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mDialog.setMessage("Please waiting...");
            mDialog.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jObject;

            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(strings[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);


            } catch (JSONException e) {

                e.printStackTrace();
            }

            return routes;
        }


        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            mDialog.dismiss();
            ArrayList points = null;
            PolylineOptions polylineOptions = null;

            for (int i=0; i<lists.size();i++)
            {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                List<HashMap<String,String>> path = lists.get(i);

                for (int j=0; j<path.size();j++)
                {
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));

                    LatLng position = new LatLng(lat,lng);

                    points.add(position);
                }

                polylineOptions.addAll(points);
                polylineOptions.width(10);
                polylineOptions.color(Color.RED);
                polylineOptions.geodesic(true);


            }

            direction = mMap.addPolyline(polylineOptions);


        }
    }


}
