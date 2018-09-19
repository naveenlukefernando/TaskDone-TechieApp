package com.pdm.taskdone;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
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
import com.pdm.taskdone.Model.FCMResponse;
import com.pdm.taskdone.Model.Notification;
import com.pdm.taskdone.Model.Sender;
import com.pdm.taskdone.Model.Token;
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
    Button acceptBtn, declineBtn;

    IGoogleAPI mService;
    IFCMService miFCMService;

    double lat,lng;

    String clientID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client__request__popup);

        mService = Common.getGoogleAPI();
        miFCMService = Common.getIFCMService();

        //InitView


        txtAdress = (TextView)findViewById(R.id.txtAddress);
        txttime = (TextView)findViewById(R.id.txtTime);
        txtDistance = (TextView)findViewById(R.id.txt_Distance);

        acceptBtn = (Button) findViewById(R.id.accept_btn);
        declineBtn = (Button)findViewById(R.id.decline_btn);

        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("clicked","Canceeledd!!!!");
                if (!TextUtils.isEmpty(clientID))
                    cancelRequest (clientID);
            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Client_Request_Popup.this,worker_tracking.class);

                Log.d("Hey","Accepted");
                // send client location to new activity
                intent.putExtra("lat",lat);
                intent.putExtra("lng",lng);
                intent.putExtra("clientID",clientID);

                startActivity(intent);
                finish();
            }
        });





        mediaPlayer = MediaPlayer.create(this,R.raw.ringtone);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        if (getIntent() != null)
        {
             lat = getIntent().getDoubleExtra("lat",-1.0);
             lng = getIntent().getDoubleExtra("lng",-1.0);

            clientID = getIntent().getStringExtra("client");


            getDirection(lat,lng);

        }


    }

    private void cancelRequest(String clientID) {

        Token token = new Token(clientID);

        Notification notification = new Notification("Cancel","Worker has cancelled your request.");
        Sender sender = new Sender(token.getToken(),notification);

      miFCMService.sendMessage(sender)
              .enqueue(new Callback<FCMResponse>() {
                  @Override
                  public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                      if (response.body().success == 1)
                      {
                          Toast.makeText(Client_Request_Popup.this,"Cancelled",Toast.LENGTH_SHORT).show();
                          finish();
                      }
                  }

                  @Override
                  public void onFailure(Call<FCMResponse> call, Throwable t) {

                  }
              });


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

    @Override
    protected void onStop() {
        mediaPlayer.release();
        super.onStop();
        }


    @Override
    protected void onPause() {
        mediaPlayer.release();
        super.onPause();
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        mediaPlayer.start();
    }
}
