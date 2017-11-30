package com.dariaemacs.cmoney;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.RadioButton;

import java.util.ArrayList;

/**
 * Created by dariaemacs on 30.11.17.
 */
public class ImageRadioGroup {
    Context context;

    int credit_values[] = {2, 10, 15};

    int radio_array[] = {R.id.radio_2, R.id.radio_10, R.id.radio_15};
    int image_array[] = {R.id.credits2, R.id.credits10, R.id.credits15};

    ArrayList<RadioButton> radio_buttons = new ArrayList<>();

    int credit_drawable[] = {R.drawable.credits2_one, R.drawable.credits10_one,
            R.drawable.credits15_one};


    ImageRadioGroup(Context context){
        this.context = context;
        for (int i = 0; i < radio_array.length; ++i) {
            RadioButton rb = (RadioButton) ((Activity)context).findViewById(radio_array[i]);
            if (i == 0) {
                rb.setChecked(true);
            }
            radio_buttons.add(rb);
        }
    }

    void radioClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();
        for (int i = 0; i < radio_array.length; ++i) {
            if (checked && view.getId() == radio_array[i]) {
                setChecked(i);
            }
        }
    }

    void setChecked(int i){
        ((MainActivity)context).setValue(credit_values[i]);
        RadioButton rb = radio_buttons.get(i);
        rb.setChecked(true);
        setUnchecked(i);
    }

    void setUnchecked(int i) {
        for (int j = 0; j < radio_buttons.size(); ++j) {
            if (j != i) {
                RadioButton oth_rb = radio_buttons.get(j);
                oth_rb.setChecked(false);
            }
        }
    }

    void imageClicked(View view){
        for (int i = 0; i < image_array.length; ++i) {
            if (view.getId() == image_array[i]) {
                setChecked(i);
            }
        }
    }

    int getImageById(String v){
        int value = Integer.valueOf(v);
        for(int i = 0; i < credit_values.length; ++i){
            if(credit_values[i] == value){
                return credit_drawable[i];
            }
        }
        return 0;
    }

}
