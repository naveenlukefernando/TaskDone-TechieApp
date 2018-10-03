package com.pdm.taskdone;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

// Created by Naveen IT16008892

public class activity_register_top_screen extends AppCompatActivity {

    MaterialSpinner   spinnercity, spinnerprofession;
    List<String> listItems = new ArrayList<>();
    List<String> professionistItems = new ArrayList<>();
    ArrayAdapter <String> adapter;

    private TextInputLayout nic_layout,name_layout;
    private TextInputEditText textinput_nic,text_Name;

    private Button conti_btn;

    private String selected_city, nic,selected_profession;
    private int position_text;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_top_screen);

        initItems();

        spinnercity = (MaterialSpinner) findViewById(R.id.spinner_city);
        adapter = new ArrayAdapter<String>(this ,R.layout.support_simple_spinner_dropdown_item,listItems);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnercity.setAdapter(adapter);

        spinnercity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if (position != -1) // plese choose will notify
                {
                    position_text = position;
                     selected_city =spinnercity.getItemAtPosition(position).toString();

                    Log.d("Clicked",selected_city);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        initItems_Cities();

        spinnerprofession = (MaterialSpinner) findViewById(R.id.spinner_profession);
        adapter = new ArrayAdapter<String>(this ,R.layout.support_simple_spinner_dropdown_item,professionistItems);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerprofession.setAdapter(adapter);


        spinnerprofession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if (position != -1) // plese choose will notify
                {

                    selected_profession =spinnerprofession.getItemAtPosition(position).toString();

                    Log.d("Clicked",selected_profession);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        nic_layout = (TextInputLayout) findViewById(R.id.textInput_Layer_NIC) ;
        textinput_nic = (TextInputEditText)findViewById(R.id.nic_text) ;
        name_layout = (TextInputLayout) findViewById(R.id.txtName_layout) ;
        text_Name = (TextInputEditText)findViewById(R.id.txtName_text) ;
        conti_btn = (Button) findViewById(R.id.continue_btn);



        conti_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//              Log.d("SPINNER",selected_city);
                if(!validateNic () ) {
                    return;
                }



                String nic = textinput_nic.getText().toString();
                String name = text_Name.getText().toString();

                Intent intent = new Intent(activity_register_top_screen.this, activity_register_new_screen.class);
                intent.putExtra("nic",nic);
                intent.putExtra("name",name);
                intent.putExtra("city",selected_city);
                intent.putExtra("profession",selected_profession);


                startActivity(intent);

            }
        });



        }

    private void initItems() {

                String[] cities = {"Colombo","Dehiwala-Mount Lavinia","Moratuwa", "Sri Jayawardenapura Kotte" , "Negombo" , "Kandy" , "Kalmunai", "Vavuniya" , "Galle", "Trincomalee",
                                    "Batticaloa", "Jaffna", "Katunayake", "Dambulla", "Kolonnawa", "Anuradhapura", "Ratnapura", "Badulla", "Matara", "Puttalam", "Chavakacheri", "Kattankudy",
                                "Matale", "Kalutara", "Mannar", "Panadura", "Beruwala", "Ja-Ela", "Point Pedro", "Kelaniya", "Peliyagoda", "Kurunegala", "Wattala", "Gampola", "Nuwara Eliya",
                                "Valvettithurai", "Chilaw", "Eravur", "Avissawella", "Weligama", "Ambalangoda", "Ampara", "Kegalle", "Hatton", "Nawalapitiya", "Balangoda", "Hambantota", "Tangalle",
                                    "Moneragala", "Gampaha", "Horana" , "Wattegama" , "Minuwangoda", "Bandarawela" , "Kuliyapitiya" , "Haputale" , "Talawakele" , "Harispattuwa" , "Kadugannawa", "Embilipitiya"};



        ArrayList<String> aList = new ArrayList<String>(Arrays.asList(cities));
        listItems.addAll(aList);




    }





    private void initItems_Cities() {


        String[] profession = {"Electrician","A/C Technician","Carpenters","Painters","Welding"};



        ArrayList<String> aList = new ArrayList<String>(Arrays.asList(profession));
        professionistItems.addAll(aList);


    }


    private boolean validateNic ()
    {
        String nic = nic_layout.getEditText().getText().toString().trim();

        if(nic.isEmpty()){
            nic_layout.setError("Field can't be Empty.");
            return false;
        }
        else if(nic.length()< 10 | nic.length() > 10) {
            nic_layout.setError("NIC number should be 10 characters.");
            return false;
        }
        nic_layout.setError(null);
        return true;
    }





}
