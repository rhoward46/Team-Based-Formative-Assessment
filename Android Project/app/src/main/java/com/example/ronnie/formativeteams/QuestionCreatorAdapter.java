package com.example.ronnie.formativeteams;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ronnie.formativeteams.R;

import java.util.ArrayList;
import java.util.Map;



public class QuestionCreatorAdapter extends ArrayAdapter<String> {

    private ArrayList<Boolean> isChecked;

    //constructor used for incomplete assessments list activity id = 0
    public QuestionCreatorAdapter(Activity context, ArrayList<String> answers){
        super(context, 0, answers);
        isChecked = new ArrayList<>();
        isChecked.add(false);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item_for_questions_creator, parent, false);
        }

        EditText text = (EditText) listItemView.findViewById(R.id.text_box_1);
        text.setHint("Input Answer Choice");

        CheckBox checkBox = (CheckBox) listItemView.findViewById(R.id.checkbox);

        Log.e(String.valueOf(position), String.valueOf(isChecked.size()));

        // We need to see if the ArrayList isChecked is the right length
        if(isChecked.size()-1< position){
            int i = isChecked.size();
            while(i <= position){
                isChecked.add(false);
                i++;
                Log.e(String.valueOf(position), String.valueOf(position));
            }
        }

//        for(Boolean bo:isChecked){
//            Log.e("1", String.valueOf(bo));
//        }

        if(isChecked.get(position)){
            checkBox.setChecked(true);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateIsChecked(position);
            }
        });



        return listItemView;

    }

    private void updateIsChecked(int position){
        for(int i = 0; i < isChecked.size(); i++){
            isChecked.set(i, false);
        }

        isChecked.set(position, true);
    }


}
