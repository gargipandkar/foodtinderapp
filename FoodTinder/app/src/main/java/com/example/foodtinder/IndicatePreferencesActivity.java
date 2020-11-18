package com.example.foodtinder;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class IndicatePreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
    }

    protected void onStart(){
        super.onStart();
        /*IF FIELD HAS BEEN SET BY HOST THEN DON'T DISPLAY FIELD IN PREFERENCES*/
    }
}
