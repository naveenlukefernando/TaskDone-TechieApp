package com.pdm.taskdone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class finished_task extends AppCompatActivity {

    int h,m,s;

    TextView hour_rate,time_rate,total, full_total ;
    EditText worker_amount;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_task);

        h= getIntent().getIntExtra("h",h);
        m= getIntent().getIntExtra("m",m);
        s =getIntent().getIntExtra("s",s);

        Log.d("TIME",String.valueOf(h)+" : "+String.valueOf(m)+" : "+String.valueOf(s));

        hour_rate = (TextView)findViewById(R.id.time);
        time_rate = (TextView)findViewById(R.id.rate);
        total = (TextView)findViewById(R.id.rate);
        worker_amount = (EditText) findViewById(R.id.type_amount);

        Button sendReciept = (Button) findViewById(R.id.send_receipt);

        hour_rate.setText("300");
        time_rate.setText(h+":"+m+":"+s);





        sendReciept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



    }
}
