package com.pdm.taskdone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.LoginFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class finished_task extends AppCompatActivity {

    int h,m,s;

    TextView hour_rate,time_rate,total, full_total,tot_txt,lkr_text,clientname;
    EditText worker_amount;

    double normal_hr_rate = 300 , amount , worker_full_amount , service_fee;

    DatabaseReference client_name_ref;

    IGoogleAPI mService;
    IFCMService mFCMService;

    String clientID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_task);


        mService = Common.getGoogleAPI();
        mFCMService = Common.getIFCMService();

        h= getIntent().getIntExtra("h",h);
        m= getIntent().getIntExtra("m",m);
        s =getIntent().getIntExtra("s",s);

        clientID = getIntent().getStringExtra("id");

            Log.d("ARRIVED NAME"," HELOO ******   "+clientID);

                Log.d("TIME",String.valueOf(h)+" : "+String.valueOf(m)+" : "+String.valueOf(s));

        hour_rate = (TextView)findViewById(R.id.time);
        time_rate = (TextView)findViewById(R.id.rate);
        total = (TextView)findViewById(R.id.rate2);
        full_total = (TextView)findViewById(R.id.total_amount);
        tot_txt = (TextView)findViewById(R.id.tot_textV);
        lkr_text = (TextView)findViewById(R.id.lkr_textV);
        clientname = (TextView)findViewById(R.id.client_name_text);


//        client_name_ref = FirebaseDatabase.getInstance().getReference(clientID);
//        Query name_query = client_name_ref.child("name");
//
//
//
//        name_query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists())
//                {
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
//                    {
//                       String name = snapshot.child("name").getValue().toString();
//                        //clientname.setText(name);
//                        Log.d("QUERY"," ***** //// **** ::: " + name);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });



        worker_amount = (EditText) findViewById(R.id.type_amount);

        Button sendReciept = (Button) findViewById(R.id.send_receipt);
        Button calc = (Button) findViewById(R.id.calc);

        time_rate.setText(String.valueOf(normal_hr_rate));
        hour_rate.setText(h+":"+m+":"+s);

        if(h==1 || h == 0)
        {
            h = 1;
            amount = normal_hr_rate * h ;
            total.setText(String.valueOf(amount));
        }
            else
        {
             amount = normal_hr_rate * h ;
            total.setText(String.valueOf(amount));
        }



        calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String worker_type_amount = worker_amount.getText().toString();
                double worker_rate = Double.parseDouble(worker_type_amount);

                worker_full_amount = amount+ worker_rate;

                tot_txt.setText("Total :");
                lkr_text.setText("LKR");
                full_total.setText(String.valueOf(worker_full_amount));


            }
        });

        sendReciept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                full_total.clearComposingText();
//                full_total.setText("");

                sendreceipt(clientID,worker_full_amount);
                Toast.makeText(finished_task.this,"Total "+worker_full_amount,Toast.LENGTH_SHORT).show();
                Log.d("send","Recipt sent "+worker_full_amount);


            }
        });



    }



    private void sendreceipt(String clientID,double tot ) {

        Token token = new Token(clientID);

        Notification notification = new Notification("receive_payment_method",String.valueOf(tot));
        Sender sender = new Sender(token.getToken(),notification);

        mFCMService.sendMessage(sender)
                .enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if (response.body().success == 1)
                        {
                            Toast.makeText(finished_task.this,"Payment recipt sent! .",Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {

                    }
                });


    }




}
