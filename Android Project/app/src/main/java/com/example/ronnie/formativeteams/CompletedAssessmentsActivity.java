package com.example.ronnie.formativeteams;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import java.util.Objects;

public class CompletedAssessmentsActivity extends AppCompatActivity {

    private ArrayList<String> mCompletedList;
    private DatabaseReference mDatabase;
    private ArrayList<Float> mScores = new ArrayList<>();
    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_assessments);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mCompletedList = new ArrayList<>();

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
                DataSnapshot child = dataSnapshot.child("users").child(mUsername)
                        .child("completedAssessments");
                for (DataSnapshot childSnapshot: child.getChildren()){
                    mCompletedList.add(childSnapshot.getKey());
//                    Log.e(childSnapshot.child("actual_score"). getKey(), childSnapshot.child("actual_score").getValue().toString());
//                    Log.e(childSnapshot.child("possible_score"). getKey(), childSnapshot.child("possible_score").getValue().toString());

                    Float score = Float.parseFloat(childSnapshot.child("actual_score").getValue().toString());
                    score = score / Float.parseFloat(childSnapshot.child("possible_score").getValue().toString());

                    score = Math.round(score *100f) /100f;
                    Log.e("1", score.toString());

                    mScores.add(score);
                }

                Float[] scores = new Float[mScores.size()];
                scores = mScores.toArray(scores);

                // set the adapter and listviews
                ListAdapter listAdapter = new ListAdapter(CompletedAssessmentsActivity.this, mCompletedList,scores, 1);
                ListView listView = (ListView) findViewById(R.id.assessment_list);
                listView.setAdapter(listAdapter);
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
                progressBar.setVisibility(View.GONE);


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String assessment_name = mCompletedList.get(position);
                        Intent intent = new Intent(getBaseContext(), ViewCorrectAnswersActivity.class);
                        intent.putExtra("assessment_name", assessment_name);
                        intent.putExtra("username", mUsername);
                        startActivity(intent);
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
