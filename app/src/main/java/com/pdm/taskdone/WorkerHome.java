package com.pdm.taskdone;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pdm.taskdone.Common.Common;
import com.pdm.taskdone.Model.Token;
import com.pdm.taskdone.Remote.IGoogleAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.pdm.taskdone.Common.Common.mLastlocation;

public class WorkerHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , OnMapReadyCallback {


    private GoogleMap mMap;

    //ini firebase storage
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    FusedLocationProviderClient fusedLocationProviderClient;
    com.google.android.gms.location.LocationCallback locationCallback;


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

    Marker mCurrent;

    MaterialAnimatedSwitch location_switch;
    SupportMapFragment msupportMapFragment;


    //Car Animation
    private List<LatLng> polyLineList;
    private Marker work_Marker;
    private float v;
    private double lat, lng;
    private Handler handler;
    private LatLng startPosition, endPosition, currentPosition;
    private int index, next;
    //    private Button btnGo;
    private PlaceAutocompleteFragment places;
    private String destination;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, greyPolyline;

    private IGoogleAPI mService;

    //presence system
    DatabaseReference onlineUserRef,currentUserRef;






    Runnable drawPathRunnable = new Runnable() {
        @Override
        public void run() {
            if (index < polyLineList.size() - 1) {
                index++;
                next = index + 1;

            }

            if (index < polyLineList.size() - 1) {

                startPosition = polyLineList.get(index);
                endPosition = polyLineList.get(next);
            }

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000);
            valueAnimator.setInterpolator(new LinearInterpolator());

            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {

                    v = valueAnimator.getAnimatedFraction();
                    lng = v * endPosition.longitude + (1 - v) * startPosition.longitude;
                    lat = v * endPosition.latitude + (1 - v) * startPosition.latitude;
                    LatLng newPos = new LatLng(lat, lng);
                    work_Marker.setPosition(newPos);

                    work_Marker.setAnchor(0.5f, 0.5f);
                    work_Marker.setRotation(getBearing(startPosition, newPos));

                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(newPos)
                                    .zoom(15.5f)
                                    .build()

                    ));
                }
            });

            valueAnimator.start();
            handler.postDelayed(this, 3000);

        }
    };



    private float getBearing(LatLng startPosition, LatLng endPosition) {

        double lat = Math.abs(startPosition.latitude - endPosition.latitude);
        double lng = Math.abs(startPosition.longitude - endPosition.longitude);

        if (startPosition.latitude < endPosition.latitude && startPosition.longitude < endPosition.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));

        else if (startPosition.latitude >= endPosition.latitude && startPosition.longitude < endPosition.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lat / lat))) + 90);

        else if (startPosition.latitude >= endPosition.latitude && startPosition.longitude >= endPosition.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);

        else if (startPosition.latitude < endPosition.latitude && startPosition.longitude >= endPosition.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);


        return -1;
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        //INITIZAIZE

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //Presence system (user can get online or offline )
        onlineUserRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        currentUserRef = FirebaseDatabase.getInstance().getReference(Common.worker_location_GPS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        onlineUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //remvong values from worker table when worker goes offline
                currentUserRef.onDisconnect().removeValue();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        //init firebase storage
         firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //init map

        location_switch = (MaterialAnimatedSwitch) findViewById(R.id.location_switch);


        location_switch.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean isOnline) {

                if (isOnline) {



                    FirebaseDatabase.getInstance().goOnline(); // set Online

                    if (ActivityCompat.checkSelfPermission(WorkerHome.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(WorkerHome.this,
                                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }

                    buildLocationCallBack();
                    buildLocationRequest();

                    fusedLocationProviderClient.requestLocationUpdates(mlocationRequest, locationCallback, Looper.myLooper());
                    displayLocation();
                    Toast.makeText(WorkerHome.this, "You are Online", Toast.LENGTH_SHORT).show();

                } else {

                    FirebaseDatabase.getInstance().goOffline(); // set disconnect when switch off

                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);

                    if (mCurrent != null)
                        mCurrent.remove();
                    mMap.clear();

                    if(handler != null)
                        handler.removeCallbacks(drawPathRunnable);
                    Toast.makeText(WorkerHome.this, "You are Offline", Toast.LENGTH_SHORT).show();


                }
            }
        });

        polyLineList = new ArrayList<>();


        //places api view

        places = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                if (location_switch.isChecked())
                {
                    destination = place.getAddress().toString();
                    destination = destination.replace(" ","+");

                    getDirection();
                }else
                {
                    Toast.makeText(WorkerHome.this,"Please change your status to ONLINE.",Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(WorkerHome.this,""+ status.toString(),Toast.LENGTH_SHORT).show();
            }
        });




        // Geofire

        workers = FirebaseDatabase.getInstance().getReference(Common.worker_location_GPS);
        mGeoFire = new GeoFire(workers);

        setUplocation();


        mService = com.pdm.taskdone.Common.Common.getGoogleAPI();

        updateFirebaseToken();



    }

 /// METHODS

    private void updateFirebaseToken() {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference(Common.token_table);

        Token token = new Token(FirebaseInstanceId.getInstance().getToken());
        tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(token);

    }


    private void getDirection() {

        currentPosition = new LatLng(mLastlocation.getLatitude(), mLastlocation.getLongitude());




        String requestApi = null;

        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&"+
                    "origin="+currentPosition.latitude+","+currentPosition.longitude+"&"+
                    "destination="+destination+"&"+
                    "key="+getResources().getString(R.string.google_direction_api);

            Log.d("Yeah !!!! ", requestApi);  //print URL for debug

            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                JSONArray jsonArray = jsonObject.getJSONArray("routes");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject route = jsonArray.getJSONObject(i);
                                    JSONObject poly = route.getJSONObject("overview_polyline");
                                    String polyline = poly.getString("points");
                                    polyLineList = decodePoly(polyline);

                                }



                                //adjuting bounds
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (LatLng latLng : polyLineList)
                                    builder.include(latLng);

                                LatLngBounds bounds = builder.build();
                                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
                                mMap.animateCamera(mCameraUpdate);

                                polylineOptions = new PolylineOptions();
                                polylineOptions.color(Color.GRAY);
                                polylineOptions.width(5);
                                polylineOptions.startCap(new SquareCap());
                                polylineOptions.endCap(new SquareCap());
                                polylineOptions.jointType(JointType.ROUND);
                                polylineOptions.addAll(polyLineList);

                                greyPolyline = mMap.addPolyline(polylineOptions);


                                blackPolylineOptions = new PolylineOptions();
                                blackPolylineOptions.color(Color.BLACK);
                                blackPolylineOptions.width(5);
                                blackPolylineOptions.startCap(new SquareCap());
                                blackPolylineOptions.endCap(new SquareCap());
                                blackPolylineOptions.jointType(JointType.ROUND);


                                blackPolyline = mMap.addPolyline(blackPolylineOptions);


                                mMap.addMarker(new MarkerOptions().position(polyLineList.get(polyLineList.size() - 1))
                                        .title("Client Location"));

                                //animation

                                ValueAnimator polyLineAnimator = ValueAnimator.ofInt(0, 100);
                                polyLineAnimator.setDuration(2000);
                                polyLineAnimator.setInterpolator(new LinearInterpolator());
                                polyLineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {

                                        List<LatLng> points = greyPolyline.getPoints();
                                        int percentValue = (int) valueAnimator.getAnimatedValue();
                                        int size = points.size();
                                        int newPoints = (int) (size * (percentValue / 100.0f));
                                        List<LatLng> p = points.subList(0, newPoints);
                                        blackPolyline.setPoints(p);

                                    }
                                });

                                polyLineAnimator.start();

                                work_Marker = mMap.addMarker(new MarkerOptions().position(currentPosition)
                                        .flat(true)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.navigation)));

                                handler = new Handler();
                                index = -1;
                                next = 1;
                                handler.postDelayed(drawPathRunnable, 3000);



                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("fail",""+e);
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(WorkerHome.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();

        }


    }

    private List decodePoly(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
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
            if (location_switch.isChecked()) {
                displayLocation();
            }

        }

    }

    private void buildLocationCallBack() {


        Log.d("success","BUILD LOCATION CAALL BACK");
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

    @SuppressLint("RestrictedApi")
    private void buildLocationRequest() {

        Log.d("buildlocation_request","Build success");

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

        Log.d("Display","displayLocation  is good");
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        mLastlocation = location;


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
                                                 //   .icon(BitmapDescriptorFactory.fromResource(R.drawable.navigation))
                                                    .position(new LatLng(latitude, longitude))
                                                    .title("Your Location"));


                                            // map camera movement
                                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));

                                            //Animation draw to location

                                        }


                                    }
                                });

                            } else {
                                Log.d("Task Done", "Error! Cant getting your location");

                            }
                        }

                    }
                });


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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//            Marker marker_new;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);



        mCurrent = mMap.addMarker(new MarkerOptions()
              //  .icon(BitmapDescriptorFactory.fromResource(R.drawable.navigation))
                .position(new LatLng(0,0))
                .title(""));


        // map camera movement
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 1.0f));

        //Animation draw to location
        rotateMaker(mCurrent, -360, mMap);

        mMap.clear();



        //update location





        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d("UPDATE 1 ON MAP","UPDATEING IN MAP");

            return;
        }

        Log.d("BUILDLOCATION 2 CALLING","CALLINGGGGGGGGGG1233445");

        buildLocationRequest();
        buildLocationCallBack();



        fusedLocationProviderClient.requestLocationUpdates(mlocationRequest, locationCallback, Looper.myLooper());





        }










    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.worker_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

            if (id == R.id.nav_workHistory) {
                // Handle the camera action
            }

            else if (id == R.id.nav_EditProfile) {

                showDialogUpdateInfo();

            }

            else if (id == R.id.nav_ChangePwd) {

                changeNewDialogPwd();

            }

            else if (id == R.id.nav_about) {

            }

            else if (id == R.id.nav_Signout) {

                signout();
            }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showDialogUpdateInfo() {


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(WorkerHome.this);
        alertDialog.setTitle("Edit Info");


        LayoutInflater inflater = this.getLayoutInflater();
        final View layout_edit = inflater.inflate(R.layout.worker_infoedit,null);

        TextInputLayout nameTxtEdit_lay = (TextInputLayout) layout_edit.findViewById(R.id.txtNameEdit_Lay);
        final TextInputEditText nameTxtEdit_txt = (TextInputEditText) layout_edit.findViewById(R.id.txtName_text);

        TextInputLayout phoneTxt_lay = (TextInputLayout) layout_edit.findViewById(R.id.txtPhoneEdit_Lay);
        final TextInputEditText phone_Txt_text = (TextInputEditText) layout_edit.findViewById(R.id.txtPhoneEdit_text);

        final ImageView image_upload = (ImageView) layout_edit.findViewById(R.id.addImage);


        image_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                chooseImage();

            }
        });

        alertDialog.setView(layout_edit);




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Common.PICK_IMAGE_REQUEST && requestCode == RESULT_OK
                && data !=  null && data.getData() != null )
        {
            Uri saveUri = data.getData();

            if (saveUri != null)
            {
                ProgressDialog mDialog = new ProgressDialog(this);
                mDialog.setMessage("Uploading");
                 mDialog.show();

                String imagename = UUID.randomUUID().toString(); //RANDOM NAME
                StorageReference imageFolder =



            }
        }



    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture:"),Common.PICK_IMAGE_REQUEST);





    }

    private void changeNewDialogPwd() {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(WorkerHome.this);
        alertDialog.setTitle("Change Password");
        alertDialog.setMessage("Please fill all information");

        LayoutInflater inflater = this.getLayoutInflater();
        final View layout_pwd = inflater.inflate(R.layout.layout_password_change,null);

        TextInputLayout currentPwd_Lay = (TextInputLayout) layout_pwd.findViewById(R.id.edt_password_layout);
        final TextInputEditText cuurentPwd_Edit = (TextInputEditText) layout_pwd.findViewById(R.id.edit_password_input);

        TextInputLayout newPwdLay = (TextInputLayout) layout_pwd.findViewById(R.id.edt_Newpassword_layout);
        final TextInputEditText newPwdTxt = (TextInputEditText) layout_pwd.findViewById(R.id.edit_RetypeNewpassword_input);

        TextInputLayout retypePwdLay = (TextInputLayout) layout_pwd.findViewById(R.id.retype_password_layout);
        final TextInputEditText retypePwdTxt = (TextInputEditText) layout_pwd.findViewById(R.id.edit_RetypeNewpassword_input);

        alertDialog.setView(layout_pwd);

        //set button
        alertDialog.setPositiveButton("Change Password", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final SpotsDialog waitingDialog = new SpotsDialog(WorkerHome.this,R.style.SpotDialog);

                waitingDialog.show();


                if(newPwdTxt.getText().toString().equals(retypePwdTxt.getText().toString()))
                {

                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                    //re-auth
                    AuthCredential credential = EmailAuthProvider.getCredential(email,cuurentPwd_Edit.getText().toString());
                    FirebaseAuth.getInstance().getCurrentUser()
                            .reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                            FirebaseAuth.getInstance().getCurrentUser()
                                                    .updatePassword(retypePwdTxt.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if(task.isSuccessful())
                                                            {
                                                               //update
                                                                Map<String,Object> password= new HashMap<>();

                                                                password.put("password",newPwdTxt.getText().toString());

                                                                DatabaseReference workerInfo = FirebaseDatabase.getInstance().getReference(Common.user_worker);

                                                                workerInfo.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                        .updateChildren(password)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                        if(task.isSuccessful())
                                                                                        {
                                                                                            Toast.makeText(WorkerHome.this,"Password was changed !",Toast.LENGTH_SHORT).show();
                                                                                            waitingDialog.dismiss();

                                                                                        } else
                                                                                        {
                                                                                            Toast.makeText(WorkerHome.this,"Password was changed but Database didn't update",Toast.LENGTH_SHORT).show();

                                                                                        }


                                                                            }
                                                                        });





                                                            } else
                                                            {
                                                                Toast.makeText(WorkerHome.this,"Passwrod doesn't change",Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                    }
                                    else
                                    {
                                            waitingDialog.dismiss();
                                            Toast.makeText(WorkerHome.this,"Wrong Old Password",Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                }

                    else
                {

                    waitingDialog.dismiss();
                    Toast.makeText(WorkerHome.this,"Password doesn't match",Toast.LENGTH_SHORT).show();

                }

            }
        });
         alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialogInterface, int i) {
                 dialogInterface.dismiss();
             }
         });

         //show dialog
        alertDialog.show();

    }

    private void signout() {

        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(WorkerHome.this,sign_in_up_screen.class);
        startActivity(intent);
        finish();

        }
}
