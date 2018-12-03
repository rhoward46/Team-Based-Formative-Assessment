package com.example.ronnie.formativeteams;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.data.ObjectDataBuffer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

public class CurrentAssessmentsActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ArrayList<String> mAssessmentList;
    private ArrayList<String> mDateList;
    private ArrayList<String> mCompletedList;
    private ProgressBar mProgressBar;
    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_assessments);

        Intent intent = getIntent();
        mUsername = intent.getExtras().getString("username");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAssessmentList = new ArrayList<>();
        mDateList = new ArrayList<>();
        mCompletedList = new ArrayList<>();
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        loadData();
    }

    //used to load data from database
    public void loadData(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // get all assessments
                for(DataSnapshot childSnapshot: dataSnapshot.child("assessments").getChildren()){
                    mAssessmentList.add(childSnapshot.getKey());
                    mDateList.add(childSnapshot.child("date").getValue().toString());
                }

                // get the users completed assessments
                DataSnapshot child = dataSnapshot.child("users").child(mUsername)
                        .child("completedAssessments");
                for (DataSnapshot childSnapshot: child.getChildren()){
                    mCompletedList.add(childSnapshot.getKey());
                }

                // remove items from the list that the user has already completed
                for (int i = 0; i < mCompletedList.size(); i++){
                    for (int j = 0; j < mAssessmentList.size(); j++){
                        if(Objects.equals(mCompletedList.get(i),mAssessmentList.get(j))){
                            mAssessmentList.remove(j);
                            mDateList.remove(j);
                        }
                    }
                }

                // set the adapter and listviews
                ListAdapter listAdapter = new ListAdapter(CurrentAssessmentsActivity.this, mAssessmentList, mDateList, 0);
                ListView listView = (ListView) findViewById(R.id.assessment_list);
                listView.setAdapter(listAdapter);
                mProgressBar.setVisibility(View.GONE);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        // Get the assessment name string
                        TextView assessmentNameView = view.findViewById(R.id.text_box_1);
                        String assessmentName = assessmentNameView.getText().toString();

                        Intent intent = new Intent(CurrentAssessmentsActivity.this, QuestionActivity.class);
                        intent.putExtra("assessment_name", assessmentName);
                        intent.putExtra("username", mUsername);
                        finish();
                        startActivity(intent);
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getBaseContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }

        });
    }

}
