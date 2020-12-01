package com.example.foodtinder;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static java.util.Calendar.HOUR;

public class BackgroundActivity {

    Integer eventId;
    Event currEvent = new Event(eventId);
    String searchLocation = currEvent.getLocation();
    Integer searchBudget = currEvent.getBudget().length()+1;

    //RETRIEVE ALL RESTAURANTS INFO FROM FIREBASE AND QUERY DATA
    // UPDATE EVENT'S POSSIBLE CHOICES AND EVENT STATUS TO READY TO SWIPE
    public void query(){
        // OPTIMIZE IF POSSIBLE
        currEvent.listOfRestaurant = new ArrayList<>();
        ArrayList<Restaurant> allInfo = Restaurant.retrieveAllRestaurants();    //TODO may need to add callback
        for (Restaurant r: allInfo)
            if (r.location.equals(searchLocation) && r.price_level.equals(searchBudget))
                currEvent.listOfRestaurant.add(r);

        currEvent.ref.child("listOfRestaurant").setValue(currEvent.listOfRestaurant);
        currEvent.status = "Ready to swipe";
        currEvent.ref.child("status").setValue(currEvent.status);
    }

    // CHECK WHETHER DEADLINE HAS PASSED OR EVERYONE HAS SWIPED
    // UPDATE EVENT STATUS TO PROCESSING
    public void stillSwiping(){
        DatabaseReference countComplete_ref =currEvent.ref.child("RestaurantPreferences").child("listOfCompleted");
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
        Group currGroup = new Group(currEvent.group);
        int members = currGroup.memberCount;


        //if(currEvent.passedDeadline() || count==members)
        if (count == members){
            currEvent.status = "Processing";
            currEvent.ref.child("status").setValue(currEvent.status);
        }
    }

    // GET SWIPING RESULTS AND DETERMINE MOST POPULAR RESTAURANT
    // UPDATE DECISION TO RESTAURANT NAME AND EVENT STATUS TO MATCH FOUND
    public void findMatch(){
        DatabaseReference restVote_ref = currEvent.ref.child("RestaurantPreferences").child("listOfVotes");
        restVote_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Integer> listVotes = snapshot.getValue(ArrayList.class);
                int maxVotes = Collections.max(listVotes);
                int restPos = listVotes.indexOf(maxVotes);
                String name = currEvent.listOfRestaurant.get(restPos).name;

                currEvent.setDecision(name);
                currEvent.updateEventStatus();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

}
