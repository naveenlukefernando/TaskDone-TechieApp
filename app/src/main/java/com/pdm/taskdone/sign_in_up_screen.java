package com.pdm.taskdone;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pdm.taskdone.Model.User;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

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
        users = f_database.getReference("Users");

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
        if (!validateEmail() | !validatePassword()){
            return;
        }

        Toast.makeText(sign_in_up_screen.this, "Loading..",
                Toast.LENGTH_SHORT).show();

        //Login

        firebaseAuth.signInWithEmailAndPassword(textinput_edit_Email.getText().toString(),textInput_edit_Password.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(sign_in_up_screen.this,activity_Welcome.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(sign_in_up_screen.this, "Login Failed. Check your credentials.",
                        Toast.LENGTH_LONG).show();
            }
        });

    }


    public void register_click (View v)
    {
        startActivity(new Intent(sign_in_up_screen.this,activity_register_new_screen.class));
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
