package com.example.ronnie.formativeteams;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class CreateAssessmentActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;


    private QuestionCreatorAdapter mAdapter;
    private LinearLayout mScrollView;
    private ArrayList<Integer> mId = new ArrayList<>();
    private ArrayList<Question> mQuestions = new ArrayList<>();
    private EditText mQuestionText;
    private EditText mGoalText;
    private Button mPublishButton;
    private int mQuestionNumber = 0;
    private EditText mAssessmentName;
    private ImageView mPreviousArrow;
    private ImageView mForwardArrow;
    private ImageView mDeleteIcon;

    private LinearLayout mAddChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_assessment);

        mAssessmentName = (EditText) findViewById(R.id.assessment_name);
        mQuestionText = (EditText) findViewById(R.id.question);
        mGoalText = (EditText) findViewById(R.id.goal);
        mPreviousArrow = (ImageView) findViewById(R.id.left_arrow);
        mForwardArrow = (ImageView) findViewById(R.id.right_arrow);
        mDeleteIcon = (ImageView) findViewById(R.id.delete);
        mPublishButton = (Button) findViewById(R.id.publish_quiz);
        mScrollView = (LinearLayout) findViewById(R.id.scroll_view);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAddChoice = (LinearLayout) findViewById(R.id.add_choice);
        // hide the previous button
        mPreviousArrow.setVisibility(View.INVISIBLE);

        setTitleToQuestionNumber();

        // add the first answer choice
        createNewAnswerChoice(null);

        final ArrayList<String> answers = new ArrayList<>();
        answers.add("");


        mAddChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mId.size() < 5) {
                    createNewAnswerChoice(null);
                } else {
                    Toast.makeText(getBaseContext(), "You can only make 5 questions", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mForwardArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkmarkPosition = getCheckmarkPostion();

                if (ensureFinishedQuestion(checkmarkPosition)) {
                    saveQuestion(checkmarkPosition);
                    mQuestionNumber +=1;
                    if (mQuestionNumber == mQuestions.size()){
                        clearView(true);
                    }else{
                        populateQuestion();
                    }

                    if(mQuestionNumber == mQuestions.size() - 1){
                        mForwardArrow.setImageResource(R.drawable.add_sign_48dp);
                    }
                }
            }
        });



        mPreviousArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkMarkPosition = getCheckmarkPostion();

                if (ensureFinishedQuestion(checkMarkPosition)){
                    saveQuestion(checkMarkPosition);
                    mQuestionNumber -=1;
                    populatePreviousQuestion();
                }

                if(mQuestionNumber == mQuestions.size()){
                    mForwardArrow.setImageResource(R.drawable.add_sign_48dp);
                }
            }
        });

        mDeleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete();
            }
        });

        mPublishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkmarkPosition = getCheckmarkPostion();
                // confirms that the user wishes to publish the test
                // if so popupconfimation will write the test to database
                if(Objects.equals(mAssessmentName.getText().toString(), "")){
                    Toast.makeText(getBaseContext(), "Please enter an assessment name", Toast.LENGTH_SHORT).show();
                }else if(ensureFinishedQuestion(checkmarkPosition)){
                    popupConfirmation();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.submit_confirmation, null);
        final PopupWindow popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(findViewById(R.id.constraint), Gravity.CENTER, 0, 0);


        TextView textView = (TextView) customView.findViewById(R.id.alert_view);
        textView.setText("Are you sure you wish to quit building quiz?");
        Button cancel = (Button) customView.findViewById(R.id.cancel_view);
        Button confirm = (Button) customView.findViewById(R.id.confirm_view);

        confirm.setText("Yes");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void setTitleToQuestionNumber() {
        setTitle("Question " + String.valueOf(mQuestionNumber+1));
    }

    private void deleteQuestion(){
        // if there's only one question, prevent a delete

        Log.e(String.valueOf(mQuestionNumber), String.valueOf(mQuestions.size()));

        if(mQuestions.size() == 0){
            Toast.makeText(getBaseContext(), "Cannot delete only question", Toast.LENGTH_SHORT).show();
        }
        // if the first question is being deleted, we can just populate the view
        else if (mQuestionNumber == 0){
            mQuestions.remove(mQuestionNumber);
            if(mQuestions.size() != 0){
                populateQuestion();
            }else{
                clearView(true);
            }
        }
        // if it isn't the first view, we will populate the previous question
        else if(mQuestionNumber < mQuestions.size()){
            mQuestions.remove(mQuestionNumber);
            mQuestionNumber -=1;
            if(mQuestions.size() != 0){
                populateQuestion();
            }else{
                clearView(true);
            }
        }else if(mQuestionNumber == mQuestions.size()){
            mQuestionNumber -=1;
            populateQuestion();
        }

        if(mQuestions.size() == 1){
            mForwardArrow.setImageResource(R.drawable.add_sign_48dp);

        }
    }

    private void confirmDelete(){
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.submit_confirmation, null);
        final PopupWindow popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(findViewById(R.id.constraint), Gravity.CENTER, 0, 0);


        TextView textView = (TextView) customView.findViewById(R.id.alert_view);
        textView.setText("Are you sure you wish to delete this question?");
        Button cancel = (Button) customView.findViewById(R.id.cancel_view);
        Button confirm = (Button) customView.findViewById(R.id.confirm_view);

        confirm.setText("Yes");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteQuestion();
                popupWindow.dismiss();
            }
        });
    }

    private void saveQuestion(int checkmarkPosition){
        Question question = createQuestion(checkmarkPosition);

        if (mQuestionNumber == mQuestions.size()){
            // add the question to the list of built questions
            mQuestions.add(question);
            // clear the view to allow the building of a new question
        }else{
            mQuestions.set(mQuestionNumber, question);
        }
    }

    private void populatePreviousQuestion(){

        // populates the question
        populateQuestion();

        //change add question button to say update question
        mForwardArrow.setImageResource(R.drawable.right_arrow_24dp);
        //remove previous button
    }

    private void populateQuestion(){
        Question question = mQuestions.get(mQuestionNumber);
        clearView(false);
        mGoalText.setText(question.getmGoal());
        mQuestionText.setText(question.getmQuestion());

        for (int i = 0; i < question.getmAnswers().size(); i++){
            createNewAnswerChoice(question.getmAnswers().get(i));
        }

        // check the correct choice
        LinearLayout v = (LinearLayout) mScrollView.getChildAt(question.getmCorrectIndex());
        CheckBox cb = (CheckBox) v.getChildAt(2);
        cb.setChecked(true);
        setTitle("Question " + String.valueOf(mQuestionNumber+1));
    }

    // used to display a confirmation that the teacher wishes to create this quiz
    private void popupConfirmation() {
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.submit_confirmation, null);
        final PopupWindow popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(findViewById(R.id.constraint), Gravity.CENTER, 0, 0);

        TextView textView = (TextView) customView.findViewById(R.id.alert_view);
        textView.setText("Are you sure you want to publish this quiz?");
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
                int checkmarkPosition = getCheckmarkPostion();
                saveQuestion(checkmarkPosition);
                publish();
            }
        });
    }

    private void publish() {
        String assessmentName = mAssessmentName.getText().toString();
        //get the date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("MM/dd/yyyy");
        String date =  mdformat.format(calendar.getTime());
        mDatabase.child("assessments").child(assessmentName).child("date").setValue(date);
        mDatabase.child("assessments").child(assessmentName).child("points").setValue(mQuestions.size());

        // iterate through each question
        for (int i = 0; i < mQuestions.size(); i++) {
            Question question = mQuestions.get(i);

            // set the correct answer
            String questionNumber = "q" + String.valueOf(i + 1);
            mDatabase.child("assessments").child(assessmentName).child(questionNumber).child("correctIndex").setValue(question.getmCorrectIndex());
            // set the goal
            mDatabase.child("assessments").child(assessmentName).child(questionNumber).child("goal").setValue(question.getmGoal());
            // set the question itself
            mDatabase.child("assessments").child(assessmentName).child(questionNumber).child("question").setValue(question.getmQuestion());
            // set the type
            mDatabase.child("assessments").child(assessmentName).child(questionNumber).child("type").setValue(question.getmType());
            // write every answer choice
            for (int j = 0; j < question.getmAnswers().size(); j++) {
                String answer = question.getmAnswers().get(j);
                mDatabase.child("assessments").child(assessmentName).child(questionNumber).child("answers").child(String.valueOf(j)).setValue(answer);
            }
        }
        Toast.makeText(getBaseContext(), "Assessment created!", Toast.LENGTH_SHORT).show();
        finish();
    }

    // returns true if all portions of question are finished
    private boolean ensureFinishedQuestion(int checkmarkPosition) {
        // determine if goal is filled in
        if (Objects.equals(mGoalText.getText().toString(), "")) {
            Toast.makeText(getBaseContext(), "Please fill in Goal", Toast.LENGTH_SHORT).show();
            return false;
        }
        // determine if question is filled in
        else if (Objects.equals(mQuestionText.getText().toString(), "")) {
            Toast.makeText(getBaseContext(), "Please fill in Question", Toast.LENGTH_SHORT).show();
            return false;
        }
        // determine if all EditTexts are filled in
        else if (editTextChecker()) {
            Toast.makeText(getBaseContext(), "Fill in all answer choices", Toast.LENGTH_SHORT).show();
            return false;
        }
        // determine if a choice is selected
        else if (checkmarkPosition == -1) {
            Toast.makeText(getBaseContext(), "Please mark the correct answer choice", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    // clear the screen to allow building of new question
    private void clearView(boolean populateBlankAnswerChoice) {
        //params:
        //populateBlankAnswerChoice will populate a blank answer choice when true
        // if not the first question bring back the previous button, else hide it
        if (mQuestionNumber != 0) {
            mPreviousArrow.setVisibility(View.VISIBLE);
        } else {
            mPreviousArrow.setVisibility(View.INVISIBLE);
        }

        mGoalText.getText().clear();
        mQuestionText.getText().clear();
        mScrollView.removeAllViews();
        // we need to clear the IDS for this question
        mId = new ArrayList<>();

        // bring back the first answer choice if wanted
        if(populateBlankAnswerChoice){
            createNewAnswerChoice(null);
        }
        setTitle("Question " + String.valueOf(mQuestionNumber+1));

    }

    //returns the position of the current checkmark.  returns -1 if none are checked
    private int getCheckmarkPostion() {
        for (int i = 0; i < mScrollView.getChildCount(); i++) {
            LinearLayout v = (LinearLayout) mScrollView.getChildAt(i);
            CheckBox cb = (CheckBox) v.getChildAt(2);
            if (cb.isChecked()) {
                return i;
            }

        }
        return -1;
    }

    // returns true if any EditText in the scrollview is empty
    private boolean editTextChecker() {
        for (int i = 0; i < mScrollView.getChildCount(); i++) {
            LinearLayout v = (LinearLayout) mScrollView.getChildAt(i);
            EditText et = (EditText) v.getChildAt(1);
            if (Objects.equals(et.getText().toString(), "")) {
                return true;
            }
        }
        return false;
    }

    // adds a custom view to the scrollview in the layout
    private void createNewAnswerChoice(String choice) {
        //params:
        // String choice is the text you wish to put in EditText, can be null
        View answerView = LayoutInflater.from(getBaseContext()).inflate(R.layout.list_item_for_questions_creator, null);
        mScrollView.addView(answerView);

        //assign text to EditText if choice != null
        if(!Objects.equals(choice, null)){
            EditText editText = answerView.findViewById(R.id.text_box_1);
            editText.setText(choice);
        }
        //assign this view an integer id so that it can be determined which child it is.
        int tempID = 0;
        if (mId.size() != 0) {
            tempID = mId.get(mId.size() - 1) + 1;
        }
        mId.add(tempID);
        final int id = tempID;

        CheckBox checkBox = (CheckBox) answerView.findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // the following code is used to uncheck other checkmarks in views
                if (isChecked) {
                    //first we get the actual position of this view
                    int position = getPosition(id);
                    //then we uncheck all other views
                    unCheck(position);
                }
            }
        });

        // used to delete choice when button is pressed
        ImageView imageView = (ImageView) answerView.findViewById(R.id.close_button);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getPosition(id);

                deleteChoice(position);
            }
        });


    }

    private void deleteChoice(int position) {
        mScrollView.removeViewAt(position);
        mId.remove(position);
    }

    // used to uncheck other views
    private void unCheck(int position) {
        for (int i = 0; i < mScrollView.getChildCount(); i++) {
            if (i != position) {
                LinearLayout v = (LinearLayout) mScrollView.getChildAt(i);
                CheckBox cb = (CheckBox) v.getChildAt(2);
                if (cb.isChecked()) {
                    cb.toggle();
                }
            }
        }
    }

    //used to get the actual position of the view with a given id
    private int getPosition(int id) {
        for (int i = 0; i < mId.size(); i++) {
            if (Objects.equals(mId.get(i), Integer.valueOf(id))) {
                return i;
            }
        }
        return -1;
    }

    // used to create a new question
    private Question createQuestion(int correctIndex) {
        String goal = mGoalText.getText().toString();
        String question = mQuestionText.getText().toString();
        int type = 0;
        ArrayList<String> answers = new ArrayList<>();

        // build the answers ArrayList
        for (int i = 0; i < mScrollView.getChildCount(); i++) {
            LinearLayout v = (LinearLayout) mScrollView.getChildAt(i);
            EditText et = (EditText) v.getChildAt(1);
            answers.add(et.getText().toString());
        }
        return new Question(goal, question, type, correctIndex, answers);
    }


}

