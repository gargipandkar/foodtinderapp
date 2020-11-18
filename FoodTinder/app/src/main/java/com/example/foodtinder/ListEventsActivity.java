package com.example.foodtinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListEventsActivity extends AppCompatActivity {

    DatabaseReference db, eventCount_ref, events_ref;

    ArrayList<String> eventInfoList;
    int eventCount;

    Intent intent;
    Bundle eventExtras;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listevents);

        db = FirebaseDatabase.getInstance().getReference();
        eventCount_ref = db.child("eventCount");
        events_ref = db.child("events");

        intent = getIntent();
        eventExtras = intent.getExtras();
        eventInfoList = eventExtras.getStringArrayList("eventInfoList");
        eventCount = eventExtras.getInt("eventCount");


        /*
        //DELETE, USED INTENT AND EXTRAS INSTEAD
        eventCount_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventCount = dataSnapshot.getValue(int.class);
                Log.i("Check", "# of events : "+eventCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Check", databaseError.toException());
            }
        });


        events_ref.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot eventSnapshot: dataSnapshot.getChildren()) {
                    eventInfoList.add(eventSnapshot.getKey());
                    Log.i("Check", eventInfoList.toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Check", databaseError.toException());
            }
        });
        */

    }

    protected void onStart(){
        super.onStart();
        LinearLayout layoutListEvents = findViewById(R.id.listevents_layout);
        //Log.i("Check", eventInfoList.toString());
        //Log.i("Check", "# of events = "+eventCount);
        for (int i = 0; i < eventCount; i++) {
            TextView listItem = new TextView(this);
            listItem.setText(eventInfoList.get(i));
            listItem.setId(i);
            layoutListEvents.addView(listItem);
        }
    }
}
