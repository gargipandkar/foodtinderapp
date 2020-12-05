package com.example.foodtinder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

import java.io.InputStream;
import java.lang.reflect.Array;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Swiping extends AppCompatActivity implements View.OnClickListener {

    Button like_btn, dislike_btn;
    TextView rest_name,rest_desc,rest_rating;
    ImageView rest_image;

    String user_id, event_id;
    Event selectedEvent;

    final int[] number = {0};   //FOR ITERATING THROUGH RESTAURANT LIST
    HashMap<String, Object> item;
    ArrayList<String> itemPhotos;
    HashMap<String, Integer> listRestVotes;
    HashMap<String, HashMap<String, Object>> listRestInfo;
    HashMap<String, ArrayList<String>> listRestPhotos;
    Object[] listRestNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //IDEALLY DON'T COME HERE ONLY IF EVENT STATUS IS READY TO SWIPE
        //TODO receive Event object from clicking in list of events and put it into selectedEvent
//        String convertId = getIntent().getStringExtra("eventId");
//        event_id = Integer.getInteger(convertId);

        user_id = User.getId();
        event_id = "-MNl0PsLNSJVK5oZcnCe";        //SHOULD BE RECEIVED FROM CLICKING ON EVENT
        selectedEvent = new Event(event_id);
        final DatabaseReference completed_ref = selectedEvent.ref.child("RestaurantPreferences/listOfCompleted");
//        final DatabaseReference listRest_ref = selectedEvent.ref.child("listOfRestaurant");
        final DatabaseReference restVote_ref = selectedEvent.ref.child("RestaurantPreferences").child("listOfVotes");
        final DatabaseReference event_ref = selectedEvent.ref;


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


//        // RETRIEVE LIST OF RESTAURANT OPTIONS
//        listRest_ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                GenericTypeIndicator<ArrayList<HashMap<String, Object>>> gt = new GenericTypeIndicator<ArrayList<HashMap<String, Object>>>() {};
//                final ArrayList<HashMap<String, Object>> arr = dataSnapshot.getValue(gt);
//
//                // RETRIEVE LIST OF VOTES, IF ANY AND CREATE LOCAL COPY
//                restVote_ref.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        GenericTypeIndicator<ArrayList<Integer>> gt = new GenericTypeIndicator<ArrayList<Integer>>() {};
//                        try {
//                            listRestVotes =  dataSnapshot.getValue(gt);
//                            Log.i("Check", listRestVotes.toString());
//                        } catch (NullPointerException ex) {
//                            listRestVotes = new ArrayList<>();
//                            for (int i=0; i<arr.size(); i++){listRestVotes.add(i, 0);}
//                        }
//
//                        listRestInfo = arr;
//                        if (number[0]<arr.size())
//                            select_restaurant(arr);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                    }
//                });
//
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });


        // RETRIEVE LIST OF RESTAURANT OPTIONS DETAILS AND IMAGES
        event_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//               Log.i("place", String.valueOf(dataSnapshot.getChildrenCount()));
                GenericTypeIndicator<HashMap<String, HashMap<String, Object>>> gt = new GenericTypeIndicator<HashMap<String, HashMap<String, Object>>>() {};
                GenericTypeIndicator<HashMap<String, ArrayList<String>>> gtPhotos = new GenericTypeIndicator<HashMap<String, ArrayList<String>>>() {};
//                GenericTypeIndicator<ArrayList<String>> gtNames = new GenericTypeIndicator<ArrayList<String>>() {};

                final HashMap<String, HashMap<String, Object>> arr = new HashMap<>();
                final HashMap<String, ArrayList<String>> arrPhotos= new HashMap<>();
//                final ArrayList<String> arrNames = new ArrayList<>();

                for (DataSnapshot prop: dataSnapshot.getChildren()){
//                    Log.i("place", prop.toString());
                    if (prop.getKey().equals("placeDetails")){
                        arr.putAll(prop.getValue(gt));
//                        for (DataSnapshot name: prop.getChildren())
//                            arrNames.add(name.getKey());
                    }
                    if (prop.getKey().equals(("placeDetailsPhotos")))
                        arrPhotos.putAll((prop.getValue(gtPhotos)));

                }

                Log.i("placeDetails", arr.toString());
                Log.i("placeDetailPhotos", arrPhotos.toString());

                listRestInfo = arr;
                listRestPhotos = arrPhotos;
                listRestNames = arr.keySet().toArray();

                // RETRIEVE LIST OF VOTES, IF ANY AND CREATE LOCAL COPY
                restVote_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<HashMap<String, Integer>> gt = new GenericTypeIndicator<HashMap<String, Integer>>() {};
                        try {
                            listRestVotes =  dataSnapshot.getValue(gt);
                            Log.i("Check", listRestVotes.toString());
                        } catch (NullPointerException ex) {
                            listRestVotes = new HashMap<>();
                            for (Object i: listRestNames){listRestVotes.put((String)i, 0);}
                        }


                        if (number[0]<arr.size() && !arr.isEmpty())
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
    public void select_restaurant(final HashMap<String, HashMap<String, Object>> arr) {
        Log.i("Check", arr.toString());
        Log.i("Check", listRestVotes.toString());

        String rest = (String) listRestNames[number[0]];
        item = arr.get(rest);
        rest_name.setText(rest);
        Log.i("name", rest_name.getText().toString());
        rest_desc.setText((String)item.get("formatted_address"));
        rest_rating.setText((String)item.get("rating"));
        if (listRestPhotos.containsKey(rest)){
            itemPhotos = listRestPhotos.get(rest);
            new DownloadImageTask((ImageView) findViewById(R.id.rest_image)).execute(itemPhotos.get(0));
        }



//        Log.i("name", rest_name.getText().toString());
//        rest_desc.setText((String)item.get("formattedAddress"));
//        String convert_rating = String.valueOf(item.get("rating"));
//        rest_rating.setText(convert_rating);
//        //TODO assign image link to ImageView
//        ArrayList<String> imgLinks = (ArrayList<String>)item.get("images");
//        new DownloadImageTask((ImageView) findViewById(R.id.rest_image)).execute(imgLinks.get(0));

        Log.i("Check", "Item "+number[0]);

    }

    @Override
    public void onClick(View v) {
        DatabaseReference restVote_ref = selectedEvent.ref.child("RestaurantPreferences").child("listOfVotes");
        String rest = (String) listRestNames[number[0]];

        if (v==like_btn){
            if (rest_name.getText().toString().equals(rest) && number[0]<listRestVotes.size()){
                if (number[0]==(listRestVotes.size() - 1)){
                    // MAKE NUMBER GO OUT OF RANGE NOW
                    number[0]++;
                    // UPDATE DATABASE VOTES LIST
                    //TODO LAST RESTAURANT VOTES NOT BEING UPDATED, CHANGE LOGIC
                    restVote_ref.setValue((listRestVotes));
                    //CHECK IF ALL MEMBERS HAVE FINISHED SWIPING, IF YES MAKE DECISION
                    stillSwiping();
                    // GO BACK TO HOME PAGE
                    Intent next = new Intent(Swiping.this, ListEventsActivity.class);
                    startActivity(next);
                }

                if (number[0]<listRestVotes.size()){
                    // UPDATE VOTE FOR DISPLAYED ITEM
                    int currVal = listRestVotes.get(rest);
                    listRestVotes.put(rest, currVal+1);
                    //MOVE TO NEXT ITEM
                    number[0]++;
                    select_restaurant(listRestInfo);
                }

            }
            Log.i("Check", "Like clicked");
        }


        else if (v==dislike_btn){
            if (rest_name.getText().toString().equals(rest)) {
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
        Log.i("Swiping", "Check who has completed swiping");
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

    private void stopSwiping(final long count){
        Log.i("Swiping", "Check if swiping stage over");

        Group.retrieveGroup(selectedEvent.group, new DatabaseCallback() {
            @Override
            public void onCallback(ArrayList<String> ls) { }
            @Override
            public void onCallback(Event event) { }

            @Override
            public void onCallback(Group grp) {
                int members = grp.memberCount;

                Log.i("Swiping", "Needed "+members+"/Have "+count);
                //if(selectedEvent.passedDeadline() || count==members)
                if (count == members){
                    selectedEvent.status = "Processing";
                    selectedEvent.ref.child("status").setValue(selectedEvent.status);
                    findMatch();
                }
            }

            @Override
            public void onCallback(ArrayList<Restaurant> allRest, boolean done) { }
        });

    }

    // GET SWIPING RESULTS AND DETERMINE MOST POPULAR RESTAURANT
    // UPDATE DECISION TO RESTAURANT NAME AND EVENT STATUS TO MATCH FOUND
    public void findMatch(){
        Log.i("Swiping", "Find a match");
        DatabaseReference restVote_ref = selectedEvent.ref.child("RestaurantPreferences").child("listOfVotes");
        restVote_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<HashMap<String, Integer>> gt = new GenericTypeIndicator<HashMap<String, Integer>>() {};
                HashMap<String, Integer> listVotes = snapshot.getValue(gt);

                Map.Entry<String, Integer> maxEntry = null;
                if (listVotes!=null){
                    int max = Collections.max(listVotes.values());

                    for(Map.Entry<String, Integer> entry : listVotes.entrySet()) {
                        Integer value = entry.getValue();
                        if(null != value && max == value)
                            maxEntry = entry;

                    }

                    String rest = maxEntry.getKey();
                    selectedEvent.setDecision(rest, selectedEvent.getId());
                    selectedEvent.updateEventStatus(selectedEvent.getId());
                }
                else Log.i("Check", "No list of votes found");

                Log.i("Swiping", selectedEvent.decision);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}

class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}