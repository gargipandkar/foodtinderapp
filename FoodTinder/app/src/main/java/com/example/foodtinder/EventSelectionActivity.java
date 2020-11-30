package com.example.foodtinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardItems.EventItem;

public class EventSelectionActivity extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super .onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_eventselection);

        // get event information homeFragment
        Intent intent = getIntent();
        Event eventItem = intent.getParcelableExtra("Event Item");
        String eventName = eventItem.getName();
        long eventDT = eventItem.getEventDateTime();

        // set event name, date, time
        TextView nameView = findViewById(R.id.event_selection_name);
        nameView.setText(eventName);
        TextView dtView = findViewById(R.id.event_selection_dt);
        dtView.setText(Long.toString(eventDT));
        Log.i("EventSelectionActivity", "reached");


    }


}
