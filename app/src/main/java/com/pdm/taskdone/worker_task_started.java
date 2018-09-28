package com.pdm.taskdone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;

import com.pdm.taskdone.Common.Common;
import com.pdm.taskdone.Model.FCMResponse;
import com.pdm.taskdone.Model.Notification;
import com.pdm.taskdone.Model.Sender;
import com.pdm.taskdone.Model.Token;
import com.pdm.taskdone.Remote.IFCMService;
import com.pdm.taskdone.Remote.IGoogleAPI;
import com.skyfishjy.library.RippleBackground;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class worker_task_started extends AppCompatActivity {

    private Chronometer workerTimer;
    private boolean running;

    IGoogleAPI mService;
    IFCMService mFCMService;


    String clientID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_tasking);


        mService = Common.getGoogleAPI();
        mFCMService = Common.getIFCMService();

        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.ani_started);
        ImageView imageView=(ImageView)findViewById(R.id.taskdone_logo);
        rippleBackground.startRippleAnimation();


        clientID = getIntent().getStringExtra("clientid");
        Button done = (Button)findViewById(R.id.stop);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                worker_done(clientID);
                Log.d("IDDDD",clientID);
            }
        });

        workerTimer = (Chronometer)findViewById(R.id.worker_timer);

        if (!running) {
            workerTimer.start();
            running = true;
        }




    }






    private void worker_done(String clientID) {

        Token token = new Token(clientID);

        Notification notification = new Notification("WorkStopped","Task is Done");
        Sender sender = new Sender(token.getToken(),notification);

        mFCMService.sendMessage(sender)
                .enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if (response.body().success == 1)
                        {
                            Toast.makeText(worker_task_started.this,"Work Done.",Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {

                    }
                });


    }

}
