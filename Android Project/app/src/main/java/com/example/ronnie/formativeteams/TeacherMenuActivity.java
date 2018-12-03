package com.example.ronnie.formativeteams;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class TeacherMenuActivity extends AppCompatActivity {

    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_menu);

        //assign textviews to java objects in order to set on click listeners

        TextView teamCreationView = (TextView) findViewById(R.id.create_team_view);
        TextView studentCreationView = (TextView) findViewById(R.id.create_student_view);
        TextView assessmentCreationView = (TextView) findViewById(R.id.create_assessment_view);

        teamCreationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(TeacherMenuActivity.this, TeamCreationActivity.class);
                startActivity(mIntent);
            }
        });

        studentCreationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(TeacherMenuActivity.this, StudentCreationActivity.class);
                startActivity(mIntent);
            }
        });

        assessmentCreationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(TeacherMenuActivity.this, CreateAssessmentActivity.class);
                startActivity(mIntent);
            }
        });

    }
}
