package com.example.ronnie.formativeteams;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Objects;

public class StudentCreationActivity extends AppCompatActivity {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private Spinner mSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_creation);

        final EditText firstNameET = (EditText) findViewById(R.id.first_name_edit_text);
        final EditText lastNameET = (EditText) findViewById(R.id.last_name_edit_text);
        final EditText userNameET = (EditText) findViewById(R.id.username_edit_text);
        final EditText passwordET = (EditText) findViewById(R.id.password_edit_text);
        Button createStudentButton = (Button) findViewById(R.id.create_student_button);
        mSpinner = (Spinner) findViewById(R.id.team_spinner);

        populateSpinner();



        createStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameET.getText().toString();
                String lastName = lastNameET.getText().toString();
                String username = userNameET.getText().toString();
                String password = passwordET.getText().toString();
                Boolean blankFields = checkBlankFields(firstName, lastName, username, password);

                //usernames will be lowercase always
                username = username.toLowerCase();

                if (!blankFields) {
                    createUser(firstName, lastName, username, password);
                }
            }
        });


    }

    private void populateSpinner(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<String> teamList = new ArrayList<>();

                for(DataSnapshot dataSnapshot1 : dataSnapshot.child("teams").getChildren()){
                    teamList.add(dataSnapshot1.getKey());
                }

//                // Creating adapter for spinner
//                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
//
//                // Drop down layout style - list view with radio button
//                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//                // attaching data adapter to spinner
//                spinner.setAdapter(dataAdapter);

                ArrayAdapter<String> adapter = new ArrayAdapter(StudentCreationActivity.this, android.R.layout.simple_spinner_item, teamList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createUser(final String firstName, final String lastName, final String username, final String password) {


        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // if user already exists alert the teacher
                if (dataSnapshot.child("users").hasChild(username)) {
                    Toast.makeText(StudentCreationActivity.this, "Username already exists", Toast.LENGTH_LONG).show();
                }
                // else make the team and exit the activity
                else {
                    String team = mSpinner.getSelectedItem().toString();

                    //create the user
                    mDatabase.child("users").child(username).child("firstName").setValue(firstName);
                    mDatabase.child("users").child(username).child("lastName").setValue(lastName);
                    mDatabase.child("users").child(username).child("password").setValue(password);
                    mDatabase.child("users").child(username).child("team").setValue(team);

                    // assign student to the correct team and give them a contribution score of 0
                    mDatabase.child("teams").child(team).child(username).setValue(0);

                    Toast.makeText(StudentCreationActivity.this, "User: " + username + " created", Toast.LENGTH_LONG).show();

                    // once the user is made, finish the activity
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // checks that all fields are created correctly
    private boolean checkBlankFields(String firstName, String lastName, String username, String password) {

        if (firstName.isEmpty()) {
            Toast.makeText(StudentCreationActivity.this, "Please enter first name",
                    Toast.LENGTH_LONG).show();
            return true;
        }

        if (lastName.isEmpty()) {
            Toast.makeText(StudentCreationActivity.this, "Please enter last name",
                    Toast.LENGTH_LONG).show();
            return true;
        }

        if (username.isEmpty()) {
            Toast.makeText(StudentCreationActivity.this, "Please enter username",
                    Toast.LENGTH_LONG).show();
            return true;
        }

        if (password.isEmpty()) {
            Toast.makeText(StudentCreationActivity.this, "Please enter password",
                    Toast.LENGTH_LONG).show();
            return true;
        }

        return false;
    }
}
