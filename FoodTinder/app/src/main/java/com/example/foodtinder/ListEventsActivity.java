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

    DatabaseReference db, events_ref;

    ArrayList<String> eventsList = new ArrayList<>();
    int eventCount;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listevents);

        // READ LIST OF EVENTS FROM FIREBASE AND PUT INTO LOCAL USER OBJECT
        db = FirebaseDatabase.getInstance().getReference();
        events_ref = db.child("USERS").child(User.getId()).child("listOfEvents");

        User.setUserGroups(events_ref, new DatabaseCallback() {
            @Override
            public void onCallback(ArrayList<String> ls) {
                eventsList.addAll(ls);
                eventCount = eventsList.size();
                displayList();

                User.activeEvents.clear();
                User.activeEvents.addAll(ls);

                Log.i("Check", ls.toString());
                Log.i("Check", User.getId());
                Log.i("Check", eventsList.toString());
                Log.i("Check", "# of groups = "+ eventCount);

            }

        });


    }

    void displayList(){
        LinearLayout layoutListEvents = findViewById(R.id.listevents_layout);

        for (int i = 0; i < eventCount; i++) {
            TextView listItem = new TextView(this);
            listItem.setText(eventsList.get(i));
            listItem.setId(i);
            layoutListEvents.addView(listItem);
        }
    }
}
