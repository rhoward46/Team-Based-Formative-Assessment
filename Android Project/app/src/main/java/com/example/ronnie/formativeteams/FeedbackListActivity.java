package com.example.ronnie.formativeteams;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FeedbackListActivity extends AppCompatActivity {

    private ArrayList<String> mAssessmentList;
    private DatabaseReference mDatabase;
    private ArrayList<Float> mScores = new ArrayList<>();
    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_list);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAssessmentList = new ArrayList<>();

        Intent intent = getIntent();
        mUsername = intent.getExtras().getString("username");
        loadData();
    }

    //used to load data from database
    public void loadData(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // get the users completed assessments
                // get all assessments
                for(DataSnapshot childSnapshot: dataSnapshot.child("assessments").getChildren()){
                    mAssessmentList.add(childSnapshot.getKey());
                }

                // set the adapter and listviews
                ListAdapter listAdapter = new ListAdapter(FeedbackListActivity.this, mAssessmentList, 3);
                ListView listView = (ListView) findViewById(R.id.assessment_list);
                listView.setAdapter(listAdapter);
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
                progressBar.setVisibility(View.GONE);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // I should probably implement this TODO:
            }

        });
    }

}
