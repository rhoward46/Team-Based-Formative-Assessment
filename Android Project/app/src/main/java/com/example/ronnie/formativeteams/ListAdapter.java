package com.example.ronnie.formativeteams;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ListAdapter extends ArrayAdapter<String> {

    private ArrayList<String> mDueDates;
    private ArrayList<String> mTeams;
    private ArrayList<String> mTeamImages;
    private ArrayList<Map.Entry> mPoints;
    private int mId;
    private Float[] mScores;

    //constructor used for incomplete assessments list activity id = 0
    public ListAdapter(Activity context, ArrayList<String> assessments, ArrayList<String> dueDates,
                       int id){
        super(context, 0, assessments);
        mDueDates = dueDates;
        mId = id;
    }

    //constructor used for individual stats
    public ListAdapter(Activity context, ArrayList<String> assessments, Float[] scores, int id){
        super(context, 0, assessments);
        mId = id;
        mScores = scores;
    }

    //constructor used for team leader boards
    public ListAdapter(Activity context, ArrayList<String> teams, ArrayList<Map.Entry> points,
                       ArrayList<String> images, int id){
        super(context, 0, teams);
        mPoints = points;
        mTeamImages = images;
        mId = id;
    }

    // constructor used for team feedback
    public ListAdapter(Activity context, ArrayList<String> assessments, int id){
        super(context, 0, assessments);
        mId = id;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.image_view);
        TextView assessmentView = (TextView) listItemView.findViewById(R.id.text_box_1);
        TextView dateView = (TextView) listItemView.findViewById(R.id.text_box_2);

        //set the text on the two views


        switch(mId){

            case 0://current assessments case
                imageView.setVisibility(View.GONE);
                assessmentView.setText(getItem(position));
                dateView.setText(mDueDates.get(position));
                return listItemView;
            case 1://completed assessments case
                imageView.setVisibility(View.GONE);
                assessmentView.setText(getItem(position));
                dateView.setText(String.valueOf(mScores[position]*100)+"%");
                return listItemView;
            case 2://team leader board case
                imageView.setVisibility(View.GONE);
                Map.Entry entry = mPoints.get(position);
                assessmentView.setText(String.valueOf(position+1) + ") " + entry.getKey().toString());
                assessmentView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                dateView.setText(entry.getValue().toString() + "%");
                dateView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                return listItemView;
            case 3: // team feedback case
                imageView.setVisibility(View.GONE);
                dateView.setVisibility(View.GONE);
                assessmentView.setText(getItem(position));
                assessmentView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                DisplayMetrics displaymetrics = new DisplayMetrics();
                return listItemView;
        }
        return null;
    }
}
