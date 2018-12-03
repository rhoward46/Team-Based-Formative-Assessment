package com.example.ronnie.formativeteams;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

// This activity is used so that the teacher can create new teams
public class TeamCreationActivity extends AppCompatActivity {

    // get the database
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private DataSnapshot mDataSnapshot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_creation);

        final EditText teamNameEditText = (EditText) findViewById(R.id.team_name_edit_text);
        Button createTeamButton = (Button) findViewById(R.id.create_team_button);

        createTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String teamName = teamNameEditText.getText().toString();

                // check if the team name is empty, if not create team upon button click
                if (teamName.isEmpty() || Objects.equals(teamName, "")){
                    Toast.makeText(TeamCreationActivity.this, "Please enter a team name", Toast.LENGTH_LONG).show();
                }else{
                    createTeam(teamName);

                }
            }
        });

    }

    private void createTeam(final String teamName){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // if team already exists alert the user
                if(dataSnapshot.child("teams").hasChild(teamName)){
                    Toast.makeText(TeamCreationActivity.this, "Team name already exists", Toast.LENGTH_LONG).show();
                }
                // else make the team and exit the activity
                else{
                    mDatabase.child("teams").child(teamName).child("cumulative_score").setValue(0);
                    Toast.makeText(TeamCreationActivity.this, "Team: " + teamName + " created", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(TeamCreationActivity.this, TeacherMenuActivity.class);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
