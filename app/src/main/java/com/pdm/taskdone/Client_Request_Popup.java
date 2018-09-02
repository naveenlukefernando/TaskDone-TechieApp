package com.pdm.taskdone;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.pdm.taskdone.Common.Common;
import com.pdm.taskdone.Remote.IFCMService;
import com.pdm.taskdone.Remote.IGoogleAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Client_Request_Popup extends AppCompatActivity {


    TextView txttime,txtAdress,txtDistance;
    MediaPlayer mediaPlayer ;

    IGoogleAPI mService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client__request__popup);

        mService = Common.getGoogleAPI();

        //InitView


        txtAdress = (TextView)findViewById(R.id.txtAddress);
        txttime = (TextView)findViewById(R.id.txtTime);
        txtDistance = (TextView)findViewById(R.id.txt_Distance);


        mediaPlayer = MediaPlayer.create(this,R.raw.ringtone);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        if (getIntent() != null)
        {
            double lat = getIntent().getDoubleExtra("lat",-1.0);
            double lng = getIntent().getDoubleExtra("lng",-1.0);

            getDirection(lat,lng);

        }


    }


    private void getDirection(double lat,double lng) {






        String requestApi = null;

        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&"+
                    "origin="+ Common.mLastlocation.getLatitude()+","+Common.mLastlocation.getLongitude()+"&"+
                    "destination="+lat+","+lng+"&"+
                    "key="+getResources().getString(R.string.google_direction_api);

            Log.d("Yeah !!!! ", requestApi);  //print URL for debug



            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());

                                JSONArray routes = jsonObject.getJSONArray("routes");

                                 //getiing the first element
                                JSONObject object = routes.getJSONObject(0);

                                //after getting first element , i named "legs " to an array
                                JSONArray legs = object.getJSONArray("legs");

                                //and get first element of legs array
                                JSONObject legsObject  = legs.getJSONObject(0);

                                //Now getting the distance
                                JSONObject distance = legsObject.getJSONObject("distance");
                                txtDistance.setText(distance.getString("text"));

                                //get the time
                                JSONObject time = legsObject.getJSONObject("duration");
                                txttime.setText(time.getString("text"));

                                //get address
                                String address = legsObject.getString("end_address");
                                txtAdress.setText(address);




                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("fail",""+e);
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(Client_Request_Popup.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();

        }


    }

}
