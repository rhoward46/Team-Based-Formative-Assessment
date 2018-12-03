package com.example.ronnie.formativeteams;

import android.content.Intent;
import android.provider.ContactsContract;
import android.service.autofill.Dataset;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private String mUsername;
    private String mPassword;
    private DatabaseReference mDatabase;
    private DataSnapshot mDataSnapShot;
    private boolean mIsUserTeacher = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button login = (Button) findViewById(R.id.login_button);
        final EditText usernameView = (EditText) findViewById(R.id.username_edit_text);
        final EditText passwordView = (EditText) findViewById(R.id.password_edit_text);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final TextView switchUserType = (TextView) findViewById(R.id.teacher_view);

        switchUserType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if the user is a teacher switch back to student
                if (mIsUserTeacher){
                    switchUserType.setText("Login as a teacher");
                    login.setText("Login as a student");
                    mIsUserTeacher = false;
                }else{
                    switchUserType.setText("Login as a student");
                    login.setText("Login as a teacher");
                    mIsUserTeacher = true;
                }
            }
        });

        // get database information for logging in
        getDatabase();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsername = usernameView.getText().toString().toLowerCase().trim();
                mPassword = passwordView.getText().toString().trim();

                if(mUsername.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please enter a username", Toast.LENGTH_LONG).show();
                }else if(mPassword.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please enter a password", Toast.LENGTH_LONG).show();
                }else{
                    checkLoginInfo();
                }

            }
        });
    }

    private void getDatabase(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDataSnapShot = dataSnapshot;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mDataSnapShot = null;
            }
        });
    }

    private void checkLoginInfo(){

       // login as a teacher
       if (mIsUserTeacher){
           Boolean correctUsername = mDataSnapShot.child("teachers").hasChild(mUsername);
           if(correctUsername){
               String actualPassword = mDataSnapShot.child("teachers").child(mUsername).child("password").getValue().toString();
               if(Objects.equals(actualPassword, mPassword)) {
                   login();
               }else{
                   Toast.makeText(getBaseContext(), "Incorrect password", Toast.LENGTH_LONG).show();
               }
           }else{
               Toast.makeText(getBaseContext(), "Incorrect teacher username", Toast.LENGTH_LONG).show();

           }
           // the following only happens if one of the above fail due to the logging in
       }
       // login as a student
       else{
           if(mDataSnapShot == null){
               Toast.makeText(getBaseContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
           }
           else{
               Boolean correctUsername = mDataSnapShot.child("users").hasChild(mUsername);
               if(correctUsername){
                   String actualPassword = mDataSnapShot.child("users").child(mUsername).child("password").getValue().toString();
                   if(Objects.equals(actualPassword, mPassword)) {
                       login();
                   }else{
                       Toast.makeText(getBaseContext(), "Incorrect password", Toast.LENGTH_LONG).show();
                   }
               }else{
                   Toast.makeText(getBaseContext(), "Incorrect username", Toast.LENGTH_LONG).show();

               }
           }


       }

    }

    private void login(){

        // switch to TeacherMenuActivity if teacher else switch to MenuActivity
        Intent loginIntent;
        if (mIsUserTeacher){
            loginIntent = new Intent(LoginActivity.this, TeacherMenuActivity.class);
        }else{
            loginIntent = new Intent(LoginActivity.this, MenuActivity.class);
            String teamName = mDataSnapShot.child("users").child(mUsername).child("team").getValue().toString();
            loginIntent.putExtra("username", mUsername);
            loginIntent.putExtra("team", teamName);
        }

        startActivity(loginIntent);
    }

}
