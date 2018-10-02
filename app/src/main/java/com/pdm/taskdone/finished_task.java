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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pdm.taskdone.Common.Common;
import com.pdm.taskdone.Model.FCMResponse;
import com.pdm.taskdone.Model.Notification;
import com.pdm.taskdone.Model.Sender;
import com.pdm.taskdone.Model.Token;
import com.pdm.taskdone.Model.User_worker;
import com.pdm.taskdone.Model.client_model;
import com.pdm.taskdone.Remote.IFCMService;
import com.pdm.taskdone.Remote.IGoogleAPI;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class finished_task extends AppCompatActivity {

    int h,m,s;

    TextView hour_rate,time_rate,total, full_total,tot_txt,lkr_text,clientname;
    EditText worker_amount , description;

    double normal_hr_rate = 300 , amount , worker_full_amount , service_fee;

    DatabaseReference client_name_ref;

    IGoogleAPI mService;
    IFCMService mFCMService;

    String TokenClientID, clientId ,timeDuration ,clientName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_task);


        mService = Common.getGoogleAPI();
        mFCMService = Common.getIFCMService();

        h= getIntent().getIntExtra("h",h);
        m= getIntent().getIntExtra("m",m);
        s =getIntent().getIntExtra("s",s);

        timeDuration =  "0"+h + ":"+ "0" + m + ":"+ "0" + s ;

        TokenClientID = getIntent().getStringExtra("id");
        clientId =  getIntent().getStringExtra("cid");


        Query query = FirebaseDatabase.getInstance().getReference(Common.user_client)
                .orderByChild("id").equalTo(clientId);


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {

                        client_model c_model = snapshot.getValue(client_model.class);
                        clientName = c_model.getName();
                        Log.d("GOT IT", " "+clientName);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        hour_rate = (TextView)findViewById(R.id.time);
        time_rate = (TextView)findViewById(R.id.rate);
        total = (TextView)findViewById(R.id.rate2);
        full_total = (TextView)findViewById(R.id.total_amount);
        tot_txt = (TextView)findViewById(R.id.tot_textV);
        lkr_text = (TextView)findViewById(R.id.lkr_textV);
        clientname = (TextView)findViewById(R.id.client_name_text);




        worker_amount = (EditText) findViewById(R.id.type_amount);
        description = (EditText) findViewById(R.id.description_text);


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

            if (!validateTotal ())
            {return;}

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

                if (!validateReceipt () || !validateDescription()){return;}

                submitHisoty(TokenClientID,worker_full_amount);


                        Toast.makeText(finished_task.this, "Total " + worker_full_amount, Toast.LENGTH_SHORT).show();
                        Log.d("send", "Recipt sent " + worker_full_amount);




            }
        });



    }



    private void sendreceipt(String clientID,double tot,String historyID, String Wname , String wId) {


                  String json = toJSONObject(clientID,tot,historyID, Wname,wId);

                  Log.d("JSON /*/*/*/", " "+json);




        Token token = new Token(clientID);

        Notification notification = new Notification("receive_payment_method",json);
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


    private void submitHisoty ( String tokenId,double total)
    {
        if (!validateDescription ())
        {
            return;
        }


        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String date = df.format(c.getTime());


        String desc = description.getText().toString();

        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference workerRef = FirebaseDatabase.getInstance().getReference().child(Common.user_worker).child(currentUid).child("history");

        DatabaseReference clientRef = FirebaseDatabase.getInstance().getReference().child(Common.user_client).child(clientId).child("history");

        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history");




        String reqeustId = historyRef.push().getKey();

        workerRef.child(reqeustId).setValue(true);
        clientRef.child(reqeustId).setValue(true);

        Log.d("GOT IT 222", " "+clientName);

        HashMap map = new HashMap();
        map.put("workerid",currentUid);
        map.put("worker_name",Common.currentUser.getName());
        map.put("worker_propic",Common.currentUser.getPro_pic_URL());
        map.put("time_duration",timeDuration);
        map.put("paid_fee",worker_full_amount+" LKR");
        map.put("date",date);
        map.put("comment","Not Available");
        map.put ("worker_city",Common.currentUser.getCity());
        map.put("clientId",clientId);
        map.put("client_name",clientName);
        map.put("description",desc);
        map.put("type",Common.currentUser.getProfession());
        map.put("rating",0);

        historyRef.child(reqeustId).updateChildren(map);

        sendreceipt(tokenId,total, reqeustId ,Common.currentUser.getName(),currentUid);

        map.clear();


    }

    private static String toJSONObject (String clientId,double tot , String historyKey ,String wusName, String wId)
    {


        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("Wname", wusName);
            jsonObject.put("clientID", clientId);
            jsonObject.put("total",tot);
            jsonObject.put("historyID",historyKey);
            jsonObject.put("wid",wId);


            return jsonObject.toString();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;




    }

    private boolean validateTotal ()
    {
        String tot = worker_amount.getText().toString().trim();

        if(tot.isEmpty()){
            Toast.makeText(finished_task.this,"Please leave your amount.",Toast.LENGTH_SHORT).show();
            return false;
        }

        {

            return true;
        }

    }



    private boolean validateReceipt ()
    {
        String tot = full_total.getText().toString().trim();

        if(tot.isEmpty()){
            Toast.makeText(finished_task.this,"Please Calculate the amount,",Toast.LENGTH_SHORT).show();
            return false;
        }

        {

            return true;
        }

    }


    private boolean validateDescription ()
    {
        String descrip = description.getText().toString().trim();

        if(descrip.isEmpty()){
            Toast.makeText(finished_task.this,"Please leave Description of task.",Toast.LENGTH_SHORT).show();
            return false;
        }

        {

            return true;
        }

    }





}
