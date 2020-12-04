package com.example.foodtinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class ListEventsActivity extends AppCompatActivity {

    DatabaseReference db, events_ref;

    ArrayList<String> eventsList = new ArrayList<>();       //EVENT IDS FOR CURRENT USER
    ArrayList<Event> eventsInfoList = new ArrayList<>();    //RELEVANT
    HashMap<String, Event> allEvents = new HashMap<>();     //ALL EVENT IDS + EVENT OBJECTS
    int eventCount = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listevents);


        // READ LIST OF EVENTS OF THIS USER FROM FIREBASE AND PUT INTO LOCAL USER OBJECT
        db = FirebaseDatabase.getInstance().getReference();
        events_ref = db.child("USERS").child(User.getId()).child("listOfEvents");

        User.setUserEvents(events_ref, new DatabaseCallback() {
            @Override
            public void onCallback(ArrayList<String> ls) {
                //CHECK IF USER HAS NO EVENTS
                if(!ls.isEmpty()){
                    eventsList.addAll(ls);
                    eventCount = eventsList.size();

                    User.listOfEvents.clear();
                    User.listOfEvents.addAll(ls);

                    infoList();
                }

                else { displayList(); }

//                //LOG
//                Log.i("Check", ls.toString());
//                Log.i("Check", User.getId());
//                Log.i("Check", eventsList.toString());
//                Log.i("Check", "# of groups = "+ eventCount);

            }

            @Override
            public void onCallback(Event event){}
            public void onCallback(Group grp){}
            public void onCallback(ArrayList<Restaurant> allRest, boolean done){}

        });


    }

    void infoList(){
        final DatabaseReference allEvents_ref = db.child("EVENTS");
        allEvents_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<HashMap<String, Event>> gt = new GenericTypeIndicator<HashMap<String, Event>>() {};
                allEvents = snapshot.getValue(gt);

                eventsInfoList.clear();
                for (String i: eventsList) {

                    eventsInfoList.add(allEvents.get(i));
                }
                // Log.i("Check", eventsInfoList.toString());
                displayList();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

    void displayList(){
        LinearLayout layoutListEvents = findViewById(R.id.listevents_layout);

//      Log.i("Check", eventsInfoList.toString());
        TextView listItem = new TextView(this);

        if (eventCount>0){
            for (int i = 0; i < eventCount; i++) {
                listItem.setText(eventsList.get(i)+eventsInfoList.get(i).name);
                listItem.setId(i);
            }
        }

        else {
            listItem.setText("NO EVENTS AVAILABLE");
        }

        layoutListEvents.addView(listItem);

    }

    //COPY THIS CHECK INTO CLICKING ON EVENT IN EVENT LIST
    void checkIfEventExists(String eventId){
        events_ref = db.child("EVENTS").child(eventId);
        events_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    Toast.makeText(getApplicationContext(), "EVENT HAS EXPIRED", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}



