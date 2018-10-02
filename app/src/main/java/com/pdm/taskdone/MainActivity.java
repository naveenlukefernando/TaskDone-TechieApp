package com.pdm.taskdone;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pdm.taskdone.Common.Common;
import com.pdm.taskdone.Model.User_worker;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_TIME=3000;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase f_database;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        f_database = FirebaseDatabase.getInstance();
        users = f_database.getReference(Common.user_client);

        //hiding the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash_screen);



        //init paperDb
        Paper.init(MainActivity.this);

        String user = Paper.book().read(Common.user_field);
        String pwd = Paper.book().read(Common.password_field);



        if (user == null || pwd == null)
        {
            //splash screen timeout
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent homeIntent = new Intent(MainActivity.this, sign_in_up_screen.class);

                    startActivity(homeIntent);
                    finish();
                }
            },SPLASH_TIME);

        }

        else {    //splash screen timeout
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Paper.init(MainActivity.this);

                    String user = Paper.book().read(Common.user_field);
                    String pwd = Paper.book().read(Common.password_field);

                    Log.d("Username", " " + user);
                    Log.d("Pass", " " + pwd);

                    if (user != null && pwd != null) {
                        if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(pwd)) {
                            autoLogin(user, pwd);
                        }
                    }
                }
            }, SPLASH_TIME);
        }

        }





    private void autoLogin(String username, String password) {

        //Login

        final AlertDialog waitingDialog = new SpotsDialog(MainActivity.this);
        waitingDialog.show();

        firebaseAuth.signInWithEmailAndPassword(username, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        waitingDialog.dismiss();

                        FirebaseDatabase.getInstance().getReference(Common.user_worker)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Common.currentUser = dataSnapshot.getValue(User_worker.class);
                                        startActivity(new Intent(MainActivity.this, TaskDone.class));
                                        waitingDialog.dismiss();
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });






                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


                waitingDialog.dismiss();
                Toast.makeText(MainActivity.this, "Login Failed. Check your credentials.",
                        Toast.LENGTH_LONG).show();
            }
        });



    }
}
