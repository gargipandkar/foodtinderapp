package com.example.foodtinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardItems.EventItem;

public class TestEvent extends AppCompatActivity {
    private static final String TAG = "TestEvent";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super .onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_testevent);

//        // get event information homeFragment
//        Intent intent = getIntent();
//        Event event = intent.getParcelableExtra("Event Item Card");
//        String eventName = event.getName();
////        String eventL = event.getLocation();
//        Log.i(TAG, event.getName());
//        Log.i(TAG, event.getGroup());
//        Log.i(TAG, event.getHost());
//
//        // set event name, date, time
//        TextView nameView = findViewById(R.id.test_eventName);
//        nameView.setText(eventName);
//        TextView LocationView = findViewById(R.id.test_eventL);
//        LocationView.setText(eventL);
    }
}
