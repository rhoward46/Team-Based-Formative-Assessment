package com.example.ronnie.formativeteams;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MenuActivity extends AppCompatActivity {

    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Intent intent = getIntent();
        mUsername = intent.getExtras().getString("username");
        String teamName = intent.getExtras().getString("team");

        TextView currentAssessmentsView = (TextView) findViewById(R.id.todo_view);
        TextView completedAssessmentsView = (TextView) findViewById(R.id.completed_view);
        TextView leaderboardView = (TextView) findViewById(R.id.leaderboard_view);
        TextView studentNameView = (TextView) findViewById(R.id.student_name);
        TextView teamNameView = (TextView) findViewById(R.id.team_name);
        TextView feedbackView = (TextView) findViewById(R.id.feedback_view);

        teamNameView.setText(teamName);
        studentNameView.setText(mUsername);

        // link to the current assessments activity
        currentAssessmentsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, CurrentAssessmentsActivity.class);
                intent.putExtra("username", mUsername);
                startActivity(intent);
            }
        });

        // link to the completed assessments activity
        completedAssessmentsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, CompletedAssessmentsActivity.class);
                intent.putExtra("username", mUsername);
                startActivity(intent);
            }
        });

        // link to the leaderboard
        leaderboardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, TeamLeaderboardActivity.class);
                intent.putExtra("username", mUsername);
                startActivity(intent);
            }
        });

        // link to feedback view
        feedbackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, FeedbackListActivity.class);
                intent.putExtra("username", mUsername);
                startActivity(intent);
            }
        });

    }
}
