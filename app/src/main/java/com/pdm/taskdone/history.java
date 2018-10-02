package com.pdm.taskdone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.pdm.taskdone.Model.History_model;

public class history extends AppCompatActivity {


    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter,search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("History");


        recyclerView = (RecyclerView) findViewById(R.id.history_listView);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        fetch();



    }





    private void fetch() {

        Log.d("FIREBASEUI","FETCHINGGGGG");

        Query query = FirebaseDatabase.getInstance()
                .getReference().child("history").orderByChild("workerid").equalTo(FirebaseAuth.getInstance().getUid());



        FirebaseRecyclerOptions<History_model> options = new FirebaseRecyclerOptions.Builder<History_model>()
                .setQuery(query, new SnapshotParser<History_model>() {
                    @NonNull
                    @Override
                    public History_model parseSnapshot(@NonNull DataSnapshot snapshot) {
                        Log.d("BOMBO1","TRIGGEEERED");


                        return new History_model(
                                    snapshot.child("worker_city").getValue().toString()
                                ,snapshot.child("paid_fee").getValue().toString()
                                , snapshot.child("client_name").getValue().toString()
                                , snapshot.child("description").getValue().toString()
                                , snapshot.child("rating").getValue().toString()
                                , snapshot.child("date").getValue().toString());


                    }
                }).build();


        adapter = new FirebaseRecyclerAdapter<History_model,ViewHolder>(options) {


            @Override
            protected void onBindViewHolder(ViewHolder holder, final int  position, final History_model model) {

                holder.setTxttaskName(model.getDescription());
                holder.setClientName(model.getClientName());
                holder.setDatedetail(model.getDate());
                holder.setAddress(model.getWorker_city());
                holder.setRating(Float.parseFloat(model.getRating()));
                holder.setPrice(model.getPaid_fee());



                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(history.this,String.valueOf(position),Toast.LENGTH_SHORT).show();



                    }
                });

            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.history_in_detail, parent, false);

                return new ViewHolder(view);
            }

        };
        recyclerView.setAdapter(adapter);
    }



    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
//        search.stopListening();

    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        public LinearLayout root;
        public TextView task_name;
        public TextView clientName;
        public TextView date;
        public TextView address;
        public RatingBar rating;
        public TextView Textfee;



        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.list_history_root);
            task_name = itemView.findViewById(R.id.taskName);
            clientName = itemView.findViewById(R.id.cleintName);
            date = itemView.findViewById(R.id.date);
            address = itemView.findViewById(R.id.txt_his_address);
            rating = itemView.findViewById(R.id.history_rating);
            Textfee = itemView.findViewById(R.id.price_fee);


        }


        public void setTxttaskName (String string){
                task_name.setText(string);
        }

        public void setClientName (String string){
            clientName.setText(string);
        }

        public void setDatedetail (String string){
            date.setText(string);
        }

        public void setAddress (String string){
            address.setText(string);
        }

        public void setRating (float rate){
            rating.setRating(rate);
        }

        public void setPrice (String string){
            Textfee.setText(string);
        }


    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(history.this, TaskDone.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }

}

