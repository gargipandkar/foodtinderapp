package com.example.foodtinder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Swiping extends AppCompatActivity implements View.OnClickListener {

    Button like_btn, dislike_btn;
    TextView rest_name,rest_desc,rest_rating;
    ImageView rest_image;

    String user_id;
    Integer event_id;
    Event selectedEvent;

    final int[] number = {0};   //FOR ITERATING THROUGH RESTAURANT LIST
    HashMap<String, Object> item;
    ArrayList<Integer> listRestVotes;
    ArrayList<HashMap<String, Object>> listRestInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //IDEALLY DON'T COME HERE ONLY IF EVENT STATUS IS READY TO SWIPE
        //TODO receive Event object from clicking in list of events and put it into selectedEvent
        user_id = User.getId();
//        String convertId = getIntent().getStringExtra("eventId");
//        event_id = Integer.getInteger(convertId);

//        Log.i("Info argument", convertId);
        //RETRIEVE EVENT TO BE SWIPED FROM FIREBASE
        /*selectedEvent = new Event();
        selectedEvent.retrieveEvent(event_id, new DatabaseCallback() {
            @Override
            public void onCallback(ArrayList<String> ls) { }
            @Override
            public void onCallback(Group grp) { }
            @Override
            public void onCallback(ArrayList<Restaurant> allRest, boolean done) { }

            @Override
            public void onCallback(Event event) {

            }
        });*/
        selectedEvent = new Event(33);


        final DatabaseReference completed_ref = selectedEvent.ref.child("RestaurantPreferences/listOfCompleted");
        final DatabaseReference listRest_ref = selectedEvent.ref.child("listOfRestaurant");
        final DatabaseReference restVote_ref = selectedEvent.ref.child("RestaurantPreferences").child("listOfVotes");

        // ADD USER TO LIST OF THOSE WHO HAVE FINISHED SWIPING
        completed_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<String>> gt = new GenericTypeIndicator<ArrayList<String>>(){};
                ArrayList<String> completed_ls ;
                completed_ls = dataSnapshot.getValue(gt);
                if (completed_ls == null){
                    completed_ls = new ArrayList<String>();
                    completed_ls.add(user_id);
                }
                else if (!completed_ls.contains(user_id)) {
                    completed_ls.add(user_id);
                }
                // IDEALLY SHOULDN'T COME TO THIS CHECK SINCE USER CAN ONLY ACCESS SWIPING ACTIVITY ONCE
                // SEND USER BACK TO HOME IF VISITS AGAIN
                else {
                    Intent next = new Intent(Swiping.this, ListEventsActivity.class);
                    startActivity(next);
                }
                completed_ref.setValue(completed_ls);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);

        rest_name = findViewById(R.id.rest_name);
        rest_desc = findViewById(R.id.rest_descr);
        rest_rating = findViewById(R.id.rest_rating);
        rest_image = findViewById(R.id.rest_image);

        like_btn = findViewById(R.id.like);
        dislike_btn = findViewById(R.id.dislike);
        like_btn.setOnClickListener(this);
        dislike_btn.setOnClickListener(this);

        //THIS CAN BE REMOVED BUT USE IT INSTEAD OF A LOADING DIALOG
        rest_name.setText("Name");
        rest_desc.setText("Address");
        rest_rating.setText("Rating");
        //assign default image to ImageView here


        // RETRIEVE LIST OF RESTAURANT OPTIONS
        listRest_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<HashMap<String, Object>>> gt = new GenericTypeIndicator<ArrayList<HashMap<String, Object>>>() {};
                final ArrayList<HashMap<String, Object>> arr = dataSnapshot.getValue(gt);

                // RETRIEVE LIST OF VOTES, IF ANY AND CREATE LOCAL COPY
                restVote_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        GenericTypeIndicator<ArrayList<Integer>> gt = new GenericTypeIndicator<ArrayList<Integer>>() {};
                        try {
                            listRestVotes =  dataSnapshot.getValue(gt);
                            Log.i("Check", listRestVotes.toString());
                        } catch (NullPointerException ex) {
                            listRestVotes = new ArrayList<>();
                            for (int i=0; i<arr.size(); i++){listRestVotes.add(i, 0);}
                        }

                        listRestInfo = arr;
                        if (number[0]<arr.size())
                            select_restaurant(arr);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


    // GETS RESTAURANT INFORMATION TO DISPLAY AS NEXT ITEM
    public void select_restaurant(final ArrayList<HashMap<String, Object>> arr) {
        Log.i("Check", arr.toString());
        Log.i("Check", listRestVotes.toString());

        // arr RECEIVED AS PARAMETER
        item = arr.get(number[0]);
        //Log.i("restaurant", item.toString());

        rest_name.setText((String)item.get("name"));
        Log.i("name", rest_name.getText().toString());
        rest_desc.setText((String)item.get("formattedAddress"));
        String convert_rating = String.valueOf(item.get("rating"));
        rest_rating.setText(convert_rating);
        //TODO assign image link to ImageView

        Log.i("Check", "Item "+number[0]);

    }

    @Override
    public void onClick(View v) {
        DatabaseReference restVote_ref = selectedEvent.ref.child("RestaurantPreferences").child("listOfVotes");

        if (v==like_btn){
            if (rest_name.getText().toString().equals(item.get("name")) && number[0]<listRestVotes.size()){
                if (number[0]==(listRestVotes.size() - 1)){
                    // MAKE NUMBER GO OUT OF RANGE NOW
                    number[0]++;
                    // UPDATE DATABASE VOTES LIST
                    restVote_ref.setValue((listRestVotes));



                    // GO BACK TO HOME PAGE
                    Intent next = new Intent(Swiping.this, ListEventsActivity.class);
                    startActivity(next);
                }

                if (number[0]<listRestVotes.size()){
                    // UPDATE VOTE FOR DISPLAYED ITEM
                    int currVal = listRestVotes.get(number[0]);
                    listRestVotes.set(number[0], currVal+1);
                    //MOVE TO NEXT ITEM
                    number[0]++;
                    select_restaurant(listRestInfo);
                }

            }
            Log.i("Check", "Like clicked");
        }


        else if (v==dislike_btn){
            if (rest_name.getText().toString().equals(item.get("name"))) {
                if (number[0]==(listRestVotes.size() - 1)){
                    // MAKE NUMBER GO OUT OF RANGE NOW
                    number[0]++;
                    // UPDATE DATABASE VOTES LIST
                    restVote_ref.setValue((listRestVotes));
                    //CHECK IF ALL MEMBERS HAVE FINISHED SWIPING, IF YES MAKE DECISION
                    stillSwiping();
                    // GO BACK TO HOME PAGE
                    Intent next = new Intent(Swiping.this, ListEventsActivity.class);
                    startActivity(next);
                }

                else if (number[0]<listRestVotes.size()){
                    //MOVE TO NEXT ITEM
                    number[0]++;
                    select_restaurant(listRestInfo);
                }
            }

            Log.i("Check", "Dislike clicked");
        }

    }

    // CHECK WHETHER DEADLINE HAS PASSED OR EVERYONE HAS SWIPED
    // UPDATE EVENT STATUS TO PROCESSING
    public void stillSwiping(){
        DatabaseReference countComplete_ref =selectedEvent.ref.child("RestaurantPreferences").child("listOfCompleted");
        countComplete_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long countComplete = snapshot.getChildrenCount();
                stopSwiping(countComplete);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void stopSwiping(long count){
        Group currGroup = new Group(selectedEvent.group);
        int members = currGroup.memberCount;

        //if(selectedEvent.passedDeadline() || count==members)
        if (count == members){
            selectedEvent.status = "Processing";
            selectedEvent.ref.child("status").setValue(selectedEvent.status);
            findMatch();
        }
    }

    // GET SWIPING RESULTS AND DETERMINE MOST POPULAR RESTAURANT
    // UPDATE DECISION TO RESTAURANT NAME AND EVENT STATUS TO MATCH FOUND
    public void findMatch(){
        DatabaseReference restVote_ref = selectedEvent.ref.child("RestaurantPreferences").child("listOfVotes");
        restVote_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<ArrayList<Integer>> gt = new GenericTypeIndicator<ArrayList<Integer>>() {};
                ArrayList<Integer> listVotes = snapshot.getValue(gt);
                int maxVotes = 0;
                int restPos = 0;
                if (listVotes!=null){
                    maxVotes = Collections.max(listVotes);
                    restPos = listVotes.indexOf(maxVotes);

                    String name = selectedEvent.listOfRestaurant.get(restPos).name;

                    selectedEvent.setDecision(name, selectedEvent.getId());
                    selectedEvent.updateEventStatus(selectedEvent.getId());
                }
                else Log.i("Check", "No list of votes found");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}