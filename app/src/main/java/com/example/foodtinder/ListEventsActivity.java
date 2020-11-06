package com.example.foodtinder;

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
    private int eventCount;
    public ArrayList<String> eventInfoList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {

        db = FirebaseDatabase.getInstance().getReference();
        eventCount_ref = db.child("eventCount");
        events_ref = db.child("events");

        eventCount_ref.addValueEventListener(new ValueEventListener (){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                eventCount = dataSnapshot.getValue(int.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){
                Log.w("Check", databaseError.toException());
            }

        });

        events_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot eventSnapshot: dataSnapshot.getChildren()){
                    eventInfoList.add(eventSnapshot.getKey());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Check", databaseError.toException());
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listevents);

        LinearLayout layoutListEvents = findViewById(R.id.listevents_layout);
        Log.i("Check", eventInfoList.toString());
        Log.i("Check", "# of events = "+eventCount);
        for (int i = 0; i < eventCount; i++) {
            TextView listItem = new TextView(this);
            listItem.setText("Event "+i);
            listItem.setId(i);
            layoutListEvents.addView(listItem);
        }
    }
}
