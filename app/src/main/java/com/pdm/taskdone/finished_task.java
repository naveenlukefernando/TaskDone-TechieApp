package com.pdm.taskdone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class finished_task extends AppCompatActivity {

    int h,m,s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_task);

        h= getIntent().getIntExtra("h",h);
        m= getIntent().getIntExtra("m",m);
        s =getIntent().getIntExtra("s",s);

        Log.d("TIME",String.valueOf(h)+" : "+String.valueOf(m)+" : "+String.valueOf(s));


    }
}
