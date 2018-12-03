package com.example.ronnie.formativeteams;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.Entry.comparingByValue;

public class TeamLeaderboardActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ArrayList<String> mTeams = new ArrayList<>();

    private ArrayList<Map.Entry> mPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_leaderboard);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // dummy data to be deleted later



        // the majority of stuff happens within the datalistener to prevent things loading too soon
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                createLists(dataSnapshot);
                updateList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void createLists(DataSnapshot dataSnapshot){

        // get the team names
        for (DataSnapshot teamSnapshot: dataSnapshot.child("teams").getChildren()){
            mTeams.add(teamSnapshot.getKey());
        }

        // get the total points possible
        int totalPoints = 0;
        for (DataSnapshot pointSnapshot: dataSnapshot.child("assessments").getChildren()){
            totalPoints += Integer.parseInt((pointSnapshot.child("points").getValue().toString()));
        }


        //create a map that stores team names with points
        HashMap<String, Float> unsorted = new HashMap<>();

        for (String team: mTeams){
            Log.e(team, team);
            Float points = Float.parseFloat(dataSnapshot.child("teams").child(team).child("cumulative_score").getValue().toString());
            int members = (int) (dataSnapshot.child("teams").child(team).getChildrenCount() -1);
            if (totalPoints != 0 && members > 0){
                points =  points/totalPoints * 100 / members;
                points = Math.round(points*100f)/100f;
            }else{
                points = (float) 0;
            }

            unsorted.put(team, points);
        }


        //sort the map in order to display in descending order for leaderboard
        mPoints = new ArrayList<Map.Entry>(unsorted.entrySet());
        Collections.sort(mPoints,
                new Comparator() {
                    public int compare(Object o1, Object o2) {
                        Map.Entry e1 = (Map.Entry) o1;
                        Map.Entry e2 = (Map.Entry) o2;
                        return ((Comparable) e2.getValue()).compareTo(e1.getValue());
                    }
                });
    }

    public void updateList(){
        ListAdapter listAdapter = new ListAdapter(this, mTeams, mPoints, mTeams, 2);
        ListView listView = (ListView) findViewById(R.id.assessment_list);
        listView.setAdapter(listAdapter);
    }

}
