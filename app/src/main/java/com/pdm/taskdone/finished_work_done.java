package com.pdm.taskdone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.skyfishjy.library.RippleBackground;

public class finished_work_done extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_work_done);



        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.ani_thanks);
        ImageView imageView=(ImageView)findViewById(R.id.ani_thank_img);
        rippleBackground.startRippleAnimation();

        Button btn_contine = (Button) findViewById(R.id.continue_btn);


        btn_contine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(finished_work_done.this,TaskDone.class);
                startActivity(intent);
                finish();



            }
        });



    }
}
