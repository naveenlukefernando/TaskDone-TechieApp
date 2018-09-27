package com.pdm.taskdone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.skyfishjy.library.RippleBackground;

public class worker_task_started extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_tasking);

        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.ani_started);
        ImageView imageView=(ImageView)findViewById(R.id.taskdone_logo);
        rippleBackground.startRippleAnimation();
    }
}
