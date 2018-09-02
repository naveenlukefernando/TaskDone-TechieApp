package com.pdm.taskdone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Client_Request_Popup extends AppCompatActivity {


    TextView txttime,txtAdress,txtDistance;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client__request__popup);


        //InitView


        txtAdress = (TextView)findViewById(R.id.txtAddress);
        txttime = (TextView)findViewById(R.id.txtTime);
        txtDistance = (TextView)findViewById(R.id.txt_Distance);

    }
}
