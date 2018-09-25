package com.pdm.taskdone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pdm.taskdone.Common.Common;
import com.pdm.taskdone.Model.User_worker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;
import fr.ganfra.materialspinner.MaterialSpinner;

public class activity_register_new_screen extends AppCompatActivity {


    FirebaseAuth firebaseAuth;
    FirebaseDatabase f_database;
    DatabaseReference users;

    MaterialSpinner spinner;
    List<String> professionistItems = new ArrayList<>();
    ArrayAdapter <String> adapter;


    private Button reg_done_btn;
    private TextInputEditText email,phone,password,re_password;
    private TextInputLayout email_lay,phone_lay,password_lay,re_password_lay;

    String nic,city,name,pro_Pic_url,selected_profession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_screen);

        //initilaize firebase
        firebaseAuth = FirebaseAuth.getInstance();
        f_database = FirebaseDatabase.getInstance();
        users = f_database.getReference(Common.user_worker);

        //getting data from previous screen
         nic = getIntent().getExtras().getString("nic");
         city = getIntent().getExtras().getString("city");
         name = getIntent().getExtras().getString("name");

        //button
         reg_done_btn= findViewById(R.id.reg_done_btn);

         //ini text_input_fields

        email = findViewById(R.id.e_mail_text);
        phone = findViewById(R.id.text_phone_number);
        password = findViewById(R.id.password_text);
        re_password = findViewById(R.id.retype_password_textbox);

        //ini text_lay

                email_lay = findViewById(R.id.textInput_Layer_Email);
                phone_lay = findViewById(R.id.textInput_Layer_Phone_number);
                password_lay = findViewById(R.id.textInput_Layer_password);
                re_password_lay = findViewById(R.id.Textinput_Layer_retype_password);



        initItems();

        spinner = (MaterialSpinner) findViewById(R.id.spinner_profession);
        adapter = new ArrayAdapter<String>(this ,R.layout.support_simple_spinner_dropdown_item,professionistItems);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if (position != -1) // plese choose will notify
                {

                    selected_profession =spinner.getItemAtPosition(position).toString();

                    Log.d("Clicked",selected_profession);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }


    public void  reg_done_click (View v){

        if(!validateEmail () | !validatePhone () |  !validatePassword () | !validate_Re_Password ()) {
            return;
        }

        Toast.makeText(activity_register_new_screen.this, "Loading..",
                Toast.LENGTH_SHORT).show();


            firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                           pro_Pic_url = "https://firebasestorage.googleapis.com/v0/b/taskdone-8edf1.appspot.com/o/default_user_pro.jpg?alt=media&token=e5a670a0-4512-40fa-995c-f9dfaa512c8b";



                            SpotsDialog waiting = new SpotsDialog (activity_register_new_screen.this);

                            //save user to firebase db
                            User_worker user = new User_worker();
                            user.setNIC(nic);
                            user.setProfession(selected_profession);
                            user.setPro_pic_URL(pro_Pic_url);
                            user.setCity(city);
                            user.setName(name);
                            user.setEmail(email.getText().toString());
                            user.setPassword(password.getText().toString());
                            user.setPhone(phone.getText().toString());

                            Toast.makeText(activity_register_new_screen.this, "Loading..",
                                    Toast.LENGTH_SHORT).show();

                            //id email
                            users.child(FirebaseAuth.getInstance().getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    String regScreen = "reg_scn";
                                    String  move_name = name;

                                    Intent intent = new Intent(activity_register_new_screen.this, WorkerHome.class);
                                    intent.putExtra("name",move_name);
                                    intent.putExtra("pro_url",pro_Pic_url);
                                    startActivity(intent);
                                    finish();



                                    Toast.makeText(activity_register_new_screen.this, "Registered Succesfully.",
                                            Toast.LENGTH_LONG).show();


//                                           startActivity(new Intent(activity_register_new_screen.this,WorkerHome.class));
//                                            finish();



                                }


                            })


                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(activity_register_new_screen.this, "Registration Failed.",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });


                        }
                    })


                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(activity_register_new_screen.this, "Registration Failed.",
                                    Toast.LENGTH_LONG).show();
                        }



                    });










    }


    //validation



    private boolean validateEmail ()
    {
        String email = email_lay.getEditText().getText().toString().trim();

        if(email.isEmpty()){
            email_lay.setError("Field can't be Empty.");
            return false;
        }

        {
            email_lay.setError(null);
            return true;
        }

    }

    private boolean validatePhone ()
    {
        String phone_num = phone_lay.getEditText().getText().toString().trim();

        if(phone_num.isEmpty()){
            phone_lay.setError("Field can't be Empty.");
            return false;
        }
        else if(phone_num.length()< 10 | phone_num.length() > 10) {
            phone_lay.setError("Phone number should be 6 characters.");
            return false;
        }
        phone_lay.setError(null);
        return true;
    }

    private boolean validatePassword ()
    {
        String password = password_lay.getEditText().getText().toString().trim();

        if(password.isEmpty()){
            password_lay.setError("Field can't be Empty.");
            return false;
        }
        else if(password.length()< 6) {
            password_lay.setError("Password should be 6 or more characters.");
            return false;
        }
        password_lay.setError(null);
        return true;
    }

    private boolean validate_Re_Password ()
    {
        String re_Password = re_password_lay.getEditText().getText().toString().trim();

        if(re_Password.isEmpty()){
            re_password_lay.setError("Field can't be Empty.");
            return false;
        }
        else if(re_Password.length()< 6) {
            re_password_lay.setError("Password should be 6 or more characters.");
            return false;
        }
        re_password_lay.setError(null);
        return true;
    }

    private void initItems() {


        String[] profession = {"Electrician","A/C Technician","Carpenters","Painters","Welding"};



        ArrayList<String> aList = new ArrayList<String>(Arrays.asList(profession));
        professionistItems.addAll(aList);


    }

}






