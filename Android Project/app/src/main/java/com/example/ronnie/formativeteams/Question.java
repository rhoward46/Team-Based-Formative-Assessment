package com.example.ronnie.formativeteams;

import java.util.ArrayList;

public class Question {

    private String mGoal;
    private String mQuestion;
    private int mType;
    private int mCorrectIndex;
    private ArrayList<String> mAnswers;

    public Question(String goal, String question, int type, int correctIndex, ArrayList<String> answers){
        mGoal = goal;
        mQuestion = question;
        mType = type;
        mCorrectIndex = correctIndex;
        mAnswers = answers;
    }

    public int getmCorrectIndex() {
        return mCorrectIndex;
    }

    public String getmGoal() {
        return mGoal;
    }

    public String getmQuestion() {
        return mQuestion;
    }

    public int getmType() {
        return mType;
    }

    public ArrayList<String> getmAnswers() {
        return mAnswers;
    }
}
