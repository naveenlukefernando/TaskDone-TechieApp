package com.pdm.taskdone;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pdm.taskdone.Model.User;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class sign_in_up_screen extends AppCompatActivity {

    private Button register_btn,login_btn;
    private TextInputLayout texrinputEmail, textInputPassword;

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

         texrinputEmail = findViewById(R.id.e_mail_text_input);
         textInputPassword = findViewById(R.id.passwordLayout);


         register_btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 showRegisterDialog();
             }
         });



         //add new user
        firebaseAuth.createUserWithEmailAndPassword(texrinputEmail.getEditText().getText().toString(),textInputPassword.getEditText().getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        //save user to firebase db
                            User user = new User();
                            user.setFname();
                    }
                });



    }

    private void showRegisterDialog() {

                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Register");
                dialog.setMessage("Please use email to register.");

        LayoutInflater inflater = LayoutInflater.from(this);
         View register_layout = inflater.inflate(R.layout.activity_register_new_screen,null);

         TextInputLayout Name = register_layout.findViewById(R.id.textInput_Name);
        TextInputLayout phone_num = register_layout.findViewById(R.id.textInput_Phone_number);
        TextInputLayout email = register_layout.findViewById(R.id.textInput_Email);
        TextInputLayout password = register_layout.findViewById(R.id.textInput_password);
        TextInputLayout re_type_password = register_layout.findViewById(R.id.Textinput_retype_password);

        dialog.setView(register_layout);

    }

    // input validation
    public void register_click (View v)
    {

        if (!validateEmail() | !validatePassword()){
            return;
        }


    }

    private boolean validateEmail ()
    {
            String email = texrinputEmail.getEditText().getText().toString().trim();

            if(email.isEmpty()){
                texrinputEmail.setError("Field can't be Empty.");
                return false;
                }

                else if(email.contains("@")){
                    texrinputEmail.setError("@ not included. Please Check");
                    return false;
                }

            {
                    texrinputEmail.setError(null);
                    return true;
            }


    }

    private boolean validatePassword ()
    {
        String password = textInputPassword.getEditText().getText().toString().trim();

        if(password.isEmpty()){
            textInputPassword .setError("Field can't be Empty.");
            return false;
        }
        else if(password.length()< 6) {
            textInputPassword.setError("Password should be 6 or more characters.");
            return false;
        }
        textInputPassword.setError(null);
        return true;
    }







}
