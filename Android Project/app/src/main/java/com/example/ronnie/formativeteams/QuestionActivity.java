package com.example.ronnie.formativeteams;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

public class QuestionActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String mAssignmentName;
    private String mUserName;
    private ArrayList<Question> mQuestionsList;

    // views
    private RadioGroup mRadioGroup;
    private TextView mGoalView;
    private TextView mQuestionView;
    private Button mPreviousButton;
    private Button mNextButton;
    private ProgressBar mProgressBar;
    private int mPosition;                   //used to keep track of progress through Question List
    private int[] mUserChoices;  //used to keep track of each user selection on a question
    private DataSnapshot mDataSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        final TextView textView = (TextView) findViewById(R.id.goal);

        //get the intent that linked to here in order to read the assignment name
        Intent intent = getIntent();
        mAssignmentName = intent.getExtras().getString("assessment_name");
        mUserName = intent.getExtras().getString("username");

        //instantiate global variables
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mQuestionsList = new ArrayList<>();
        mGoalView = (TextView) findViewById(R.id.goal);
        mQuestionView = (TextView) findViewById(R.id.question);
        mPreviousButton = (Button) findViewById(R.id.previous_button);
        mNextButton = (Button) findViewById(R.id.next_button);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mPosition = 0;

        // methods will be called within due to the asynchronous structure of the listener
        // I don't want things loading before the database is finished loading.
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDataSnapshot = dataSnapshot;
                questionBuilder(dataSnapshot);
                mUserChoices = new int[mQuestionsList.size()];
                //Autopopulate all answers to -1
                for(int i = 0; i < mUserChoices.length; i++){
                    mUserChoices[i] = -1;
                }
                viewBuilder(mQuestionsList.get(mPosition));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // onClick listener for the "next" button
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mUserChoices[mPosition] = mRadioGroup.getCheckedRadioButtonId();

                if(!Objects.equals(mNextButton.getText().toString(), "Submit")){
                    mPosition +=1;
                    viewBuilder(mQuestionsList.get(mPosition));
                }else{
                    popupConfirmation();
                }
            }
        });

        // on click listener for the "previous" button
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserChoices[mPosition] = mRadioGroup.getCheckedRadioButtonId();
                mPosition -= 1;
                viewBuilder(mQuestionsList.get(mPosition));
            }
        });
    }

    // used to popup a confirmation dialogue upon the user hitting submit
    private void popupConfirmation(){
        LayoutInflater layoutInflater = (LayoutInflater) QuestionActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.submit_confirmation, null);
        final PopupWindow popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(findViewById(R.id.constraint), Gravity.CENTER, 0, 0);

        Button cancel = (Button) customView.findViewById(R.id.cancel_view);
        Button confirm = (Button) customView.findViewById(R.id.confirm_view);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    // used to submit changes to database and exit question activity
    private void submit(){
        int score = grade();
        for (int i = 0; i < mUserChoices.length; i++){
            Log.e("i", String.valueOf(i));
            mDatabase.child("users").child(mUserName).child("completedAssessments")
                    .child(mAssignmentName).child("answers").child(String.valueOf(i)).setValue(mUserChoices[i]);
        }
        mDatabase.child("users").child(mUserName).child("completedAssessments")
                .child(mAssignmentName).child("actual_score").setValue(score);
        mDatabase.child("users").child(mUserName).child("completedAssessments")
                .child(mAssignmentName).child("possible_score").setValue(mQuestionsList.size());

        updateTeamScore(score);

        Intent intent = new Intent(QuestionActivity.this, MenuActivity.class);
        intent.putExtra("username", mUserName);
        finish();
        startActivity(intent);
    }

    // used to update the cumulative score of the team and the individual contribution to the team
    private void updateTeamScore(int score){
        String current_team = mDataSnapshot.child("users").child(mUserName).child("team").getValue().toString();
        int current_team_score = Integer.parseInt(mDataSnapshot.child("teams").child(current_team)
                .child("cumulative_score").getValue().toString());

        current_team_score += score;
        mDatabase.child("teams").child(current_team).child("cumulative_score").setValue(current_team_score);

        //update individual contribution
        int current_individual_score = Integer.parseInt(mDataSnapshot.child("teams").child(current_team)
                .child(mUserName).getValue().toString());
        current_individual_score += score;
        mDatabase.child("teams").child(current_team).child(mUserName).setValue(current_individual_score);

    }

    // used to get the number of correct responses
    private int grade(){
        int score = 0;
        for(int i = 0; i < mUserChoices.length; i++){
            Question question = mQuestionsList.get(i);
            if(question.getmCorrectIndex()==mUserChoices[i]){
                score +=1;
            }
        }
        return score;
    }

    // fills the view with a question object
    private void viewBuilder(Question question){

        // remove previous button if first question
        if (mPosition == 0){
            mPreviousButton.setVisibility(View.INVISIBLE);
        }else{
            mPreviousButton.setVisibility(View.VISIBLE);
        }
        //change to submit if last question
        if(mPosition == mQuestionsList.size()-1){
            mNextButton.setText("Submit");
        }else{
            mNextButton.setText("Next");
        }

        mGoalView.setText("Goal: " + question.getmGoal());
        mQuestionView.setText(question.getmQuestion());
        RadioButton[] rb = new RadioButton[question.getmAnswers().size()];
        mRadioGroup.removeAllViews();
        for (int i = 0; i < question.getmAnswers().size(); i++){
            rb[i] = new RadioButton(QuestionActivity.this);
            mRadioGroup.addView(rb[i]);
            rb[i].setText(question.getmAnswers().get(i));
            rb[i].setId(i);
            rb[i].setTextSize(20);
            rb[i].setPadding(20,20,20,20);
        }

        // autofill RadioButton if user has previously selected a choice
        mRadioGroup.check(mUserChoices[mPosition]);

    }

    //creates question objects from the database
    private void questionBuilder(DataSnapshot dataSnapshot) {

        for (DataSnapshot questionSnapShots : dataSnapshot.child("assessments").child(mAssignmentName).getChildren()) {

            String currentChild = questionSnapShots.getKey();
            //ignore the date
            if (!Objects.equals(currentChild, "date") && !Objects.equals(currentChild, "points")) {

                // get all the pieces of a given question
                String goal = questionSnapShots.child("goal").getValue().toString();
                String question = questionSnapShots.child("question").getValue().toString();
                int type = Integer.parseInt(questionSnapShots.child("type").getValue().toString());
                int correctIndex = Integer.parseInt(questionSnapShots.child("correctIndex").getValue().toString());
                ArrayList<String> answers = new ArrayList<>();

                for (DataSnapshot answerSnapshot : questionSnapShots.child("answers").getChildren()) {
                    answers.add(answerSnapshot.getValue().toString());
                }

                //create a new question object using the above parts
                mQuestionsList.add(new Question(goal,question,type, correctIndex, answers));
            }
        }
        mProgressBar.setVisibility(View.GONE);
    }
}
