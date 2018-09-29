package com.pdm.taskdone;

import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pdm.taskdone.Common.Common;
import com.pdm.taskdone.Model.FCMResponse;
import com.pdm.taskdone.Model.Notification;
import com.pdm.taskdone.Model.Sender;
import com.pdm.taskdone.Model.Token;
import com.pdm.taskdone.Model.client_model;
import com.pdm.taskdone.Remote.IFCMService;
import com.pdm.taskdone.Remote.IGoogleAPI;
import com.skyfishjy.library.RippleBackground;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class worker_task_started extends AppCompatActivity {

    private Chronometer workerTimer;
    private boolean running;
    private long pause;

    IGoogleAPI mService;
    IFCMService mFCMService;

    DatabaseReference client;


    String TokenclientID , clientID ,clientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_tasking);


        TokenclientID = getIntent().getStringExtra("clientid");

        client = FirebaseDatabase.getInstance().getReference("token");
        final Query query = client.orderByChild("token").equalTo(TokenclientID);






        mService = Common.getGoogleAPI();
        mFCMService = Common.getIFCMService();

        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.ani_started);
        ImageView imageView=(ImageView)findViewById(R.id.taskdone_logo);
        rippleBackground.startRippleAnimation();


//        TokenclientID = getIntent().getStringExtra("clientid");
        Button done = (Button)findViewById(R.id.stop);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren())
                            {
                                 clientID  = snapshot.getKey().toString();

                                    pausetime (TokenclientID);


                                 Log.d("QUERY"," ***** //// **** ::: "+clientID);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });






                Log.d("IDDDD",TokenclientID);
                Toast.makeText(worker_task_started.this,TokenclientID,Toast.LENGTH_SHORT).show();
            }
        });

        workerTimer = (Chronometer)findViewById(R.id.worker_timer);

        if (!running) {

            workerTimer.setBase(SystemClock.elapsedRealtime());
            workerTimer.start();
            running = true;
        }




    }

        public void pausetime (String id)
        {
            if(running)
                workerTimer.stop();
            pause = SystemClock.elapsedRealtime()- workerTimer.getBase();
            running=false;

            String stoptime =  Long.toString(pause);

            int h   = (int)(pause / 3600000);
            int m = (int)(pause - h*3600000)/60000;
            int s= (int)(pause - h*3600000- m*60000)/1000 ;

            Log.d("hours",Integer.toString(h));
            Log.d("minutes",Integer.toString(m));
            Log.d("seconds",Integer.toString(s));


            Intent intent = new Intent(worker_task_started.this,finished_task.class);
            intent.putExtra("h",h);
            intent.putExtra("m",m);
            intent.putExtra("s",s);
            intent.putExtra("id",id);
            startActivity(intent);
            finish();


        }




//    private void worker_done(String clientID) {
//
//        Token token = new Token(clientID);
//
//        Notification notification = new Notification("WorkStopped","Task is Done");
//        Sender sender = new Sender(token.getToken(),notification);
//
//        mFCMService.sendMessage(sender)
//                .enqueue(new Callback<FCMResponse>() {
//                    @Override
//                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
//                        if (response.body().success == 1)
//                        {
//                            Toast.makeText(worker_task_started.this,"Work .",Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<FCMResponse> call, Throwable t) {
//
//                    }
//                });
//
//
//    }

}
