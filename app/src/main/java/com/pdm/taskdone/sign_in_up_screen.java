package com.pdm.taskdone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

// Created by Naveen IT16008892

public class sign_in_up_screen extends AppCompatActivity {

    private Button register_btn,login_btn;
    private  TextInputLayout text_layer_Email, text_layer_password;
    private TextInputEditText textinput_edit_Email, textInput_edit_Password;
    private ConstraintLayout rootLayout;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase f_database;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_up_screen);

        //initilaize firebase
        firebaseAuth = FirebaseAuth.getInstance();
        f_database = FirebaseDatabase.getInstance();
        users = f_database.getReference(Common.user_worker);

        // buttin initlization
         register_btn = (Button) (findViewById(R.id.reg_btn));
         login_btn = (Button) (findViewById(R.id.log_btn));

         textinput_edit_Email = findViewById(R.id.e_mail_text);
         textInput_edit_Password = findViewById(R.id.password_input);

         text_layer_Email = findViewById(R.id.e_mail_text_input_layer);
         text_layer_password = findViewById(R.id.passwordLayout);



    }


    //  login_attepmt
    public void log_click (View v)
    {
        if (!validateEmail() | !validatePassword() | !validateEmailPattern () ){
            return;
        }

        final SpotsDialog waiting = new SpotsDialog (sign_in_up_screen.this);



        Toast.makeText(sign_in_up_screen.this, "Loading..",
                Toast.LENGTH_SHORT).show();

        //Login

        firebaseAuth.signInWithEmailAndPassword(textinput_edit_Email.getText().toString(),textInput_edit_Password.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                       FirebaseDatabase.getInstance().getReference(Common.user_worker)
                               .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                               .addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                       Paper.book().write(Common.user_field,textinput_edit_Email.getText().toString());
                                       Paper.book().write(Common.password_field,textInput_edit_Password.getText().toString());

                                       Common.currentUser = dataSnapshot.getValue(User_worker.class);
                                       waiting.show();
                                       startActivity(new Intent(sign_in_up_screen.this,TaskDone.class));
                                       finish();
                                       waiting.dismiss();
                                   }

                                   @Override
                                   public void onCancelled(@NonNull DatabaseError databaseError) {

                                   }
                               });


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(sign_in_up_screen.this, "Login Failed. Check your credentials.",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private boolean validateEmailPattern() {

        String text = text_layer_Email.getEditText().getText().toString().trim();

        String validemail = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +"\\@" +"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +"(" +"\\." +"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +")+";

        Matcher matcher= Pattern.compile(validemail).matcher(text);



        if (matcher.matches()){
            return  true;


        }
        else
            text_layer_Email.setError("Invalid E-mail.");

        return false;

    }


    public void register_click (View v)
    {
        startActivity(new Intent(sign_in_up_screen.this,activity_register_top_screen.class));
        finish();

    }


    private boolean validateEmail ()
    {
            String email = text_layer_Email.getEditText().getText().toString().trim();

            if(email.isEmpty()){
                text_layer_Email.setError("Field can't be Empty.");
                return false;
                }

            {
                    text_layer_Email.setError(null);
                    return true;
            }


    }

    private boolean validatePassword ()
    {
        String password = text_layer_password.getEditText().getText().toString().trim();

        if(password.isEmpty()){
            text_layer_password.setError("Field can't be Empty.");
            return false;
        }
        else if(password.length()< 6) {
            text_layer_password.setError("Password should be 6 or more characters.");
            return false;
        }
        text_layer_password.setError(null);
        return true;
    }







}
