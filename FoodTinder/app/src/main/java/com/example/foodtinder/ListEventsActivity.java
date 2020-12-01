////package com.example.foodtinder;
////
////import android.os.Bundle;
////import android.util.Log;
////import android.widget.LinearLayout;
////import android.widget.TextView;
////
////import com.google.firebase.database.DataSnapshot;
////import com.google.firebase.database.DatabaseError;
////import com.google.firebase.database.DatabaseReference;
////import com.google.firebase.database.FirebaseDatabase;
////import com.google.firebase.database.ValueEventListener;
////
////import androidx.annotation.NonNull;
////import androidx.appcompat.app.AppCompatActivity;
////
////import java.util.ArrayList;
////
////public class ListEventsActivity extends AppCompatActivity {
////
////    DatabaseReference db, eventCount_ref, events_ref;
////    private int eventCount;
////    public ArrayList<String> eventInfoList = new ArrayList<>();
////
////    protected void onCreate(Bundle savedInstanceState) {
////
////        db = FirebaseDatabase.getInstance().getReference();
////        eventCount_ref = db.child("eventCount");
////        events_ref = db.child("events");
////
////        eventCount_ref.addValueEventListener(new ValueEventListener (){
////            @Override
////            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
////                eventCount = dataSnapshot.getValue(int.class);
////            }
////            @Override
////            public void onCancelled(@NonNull DatabaseError databaseError){
////                Log.w("Check", databaseError.toException());
////            }
////
////        });
////
////        events_ref.addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                for (DataSnapshot eventSnapshot: dataSnapshot.getChildren()){
////                    eventInfoList.add(eventSnapshot.getKey());
////                }
////
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError databaseError) {
////                Log.w("Check", databaseError.toException());
////            }
////        });
////
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_listevents);
////
////        LinearLayout layoutListEvents = findViewById(R.id.listevents_layout);
////        Log.i("Check", eventInfoList.toString());
////        Log.i("Check", "# of events = "+eventCount);
////        for (int i = 0; i < eventCount; i++) {
////            TextView listItem = new TextView(this);
////            listItem.setText("Event "+i);
////            listItem.setId(i);
////            layoutListEvents.addView(listItem);
////        }
////    }
////}
//
//
//
//package com.example.foodtinder;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.util.ArrayList;
//
//public class ListEventsActivity extends AppCompatActivity {
//
//    DatabaseReference db, events_ref;
//
//    ArrayList<String> eventsList = new ArrayList<>();
//    int eventCount;
//
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_listevents);
//
//        // READ LIST OF EVENTS FROM FIREBASE AND PUT INTO LOCAL USER OBJECT
//        db = FirebaseDatabase.getInstance().getReference();
//        events_ref = db.child("USERS").child(User.getId()).child("listOfEvents");
//
//        User.setUserEvents(events_ref, new DatabaseCallback() {
//            @Override
//            public void onCallback(ArrayList<String> ls) {
//                eventsList.addAll(ls);
//                eventCount = eventsList.size();
//                displayList();
//
//                User.activeEvents.clear();
//                User.activeEvents.addAll(ls);
//
//                Log.i("Check", ls.toString());
//                Log.i("Check", User.getId());
//                Log.i("Check", eventsList.toString());
//                Log.i("Check", "# of groups = "+ eventCount);
//
//            }
//
//        });
//
//
//    }
//
//    void displayList(){
//        LinearLayout layoutListEvents = findViewById(R.id.listevents_layout);
//
//        for (int i = 0; i < eventCount; i++) {
//            TextView listItem = new TextView(this);
//            listItem.setText(eventsList.get(i));
//            listItem.setId(i);
//            layoutListEvents.addView(listItem);
//        }
//    }
//}


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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class ListEventsActivity extends AppCompatActivity {

    DatabaseReference db, events_ref;

    ArrayList<String> eventsList = new ArrayList<>();
    ArrayList<Event> eventsInfoList = new ArrayList<>();
    HashMap<String, Event> allEvents = new HashMap<>();
    int eventCount;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listevents);


        // READ LIST OF EVENTS FROM FIREBASE AND PUT INTO LOCAL USER OBJECT
        db = FirebaseDatabase.getInstance().getReference();
        events_ref = db.child("USERS").child(User.getId()).child("listOfEvents");

        User.setUserEvents(events_ref, new DatabaseCallback() {
            @Override
            public void onCallback(ArrayList<String> ls) {
                eventsList.addAll(ls);
                eventCount = eventsList.size();

                User.activeEvents.clear();
                User.activeEvents.addAll(ls);

                infoList();

                //LOG
                Log.i("Check", ls.toString());
                Log.i("Check", User.getId());
                Log.i("Check", eventsList.toString());
                Log.i("Check", "# of groups = "+ eventCount);

            }

//            @Override
//            public void onCallback(Event event){}

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

        Log.i("Check", eventsInfoList.toString());

        for (int i = 0; i < eventCount; i++) {
            TextView listItem = new TextView(this);
            listItem.setText(eventsList.get(i)+eventsInfoList.get(i).name);
            listItem.setId(i);
            layoutListEvents.addView(listItem);
        }
    }
}