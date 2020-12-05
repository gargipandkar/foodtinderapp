package com.example.foodtinder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SwipingFragment extends Fragment implements View.OnClickListener{

    public static final String TAG = "SwipingFragment";
    Button like_btn, dislike_btn;
    TextView rest_name,rest_desc,rest_rating;
    ImageView rest_image;

    String user_id;
    String event_id;
    Event selectedEvent;

    int[] number = {0};   //FOR ITERATING THROUGH RESTAURANT LIST
    HashMap<String, Object> item;
    ArrayList<String> itemPhotos;
    HashMap<String, Integer> listRestVotes;
    HashMap<String, HashMap<String, Object>> listRestInfo;
    HashMap<String, ArrayList<String>> listRestPhotos;
    Object[] listRestNames;
    boolean firstEntry;
    boolean checkLastItem = false;

    private SwipingFragmentListener listener;
    public interface SwipingFragmentListener {
        void updateRest(String event_id, int number, HashMap<String, Integer> listRestVotes, boolean checkLastItem);
    }

    public SwipingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        firstEntry = bundle.getBoolean("firstEntry");
        if (firstEntry == true){
            event_id = bundle.getString("eventId");
            firstEntry = false;
            Log.i(TAG, "listRestVotes is null");
        } else {
            event_id = bundle.getString("eventId");
            int num = bundle.getInt("num");
            number[0] = num;
            Log.i(TAG, "received in swiping fragment");
            Log.i(TAG, number.toString());
            Log.i(TAG, String.valueOf(num));
            listRestVotes = (HashMap<String, Integer>) bundle.getSerializable("votes");
            checkLastItem = bundle.getBoolean("checkLastItem");
            firstEntry = false;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_swipe, container, false);

        Toolbar toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
//        ((AppCompatActivity)getActivity()).setDisplayHomeAsUpEnabled(true);
//        ((AppCompatActivity)getActivity()).setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).setTitle("Restaurant Preference");

        //IDEALLY DON'T COME HERE ONLY IF EVENT STATUS IS READY TO SWIPE
        //TODO receive Event object from clicking in list of events and put it into selectedEvent
        user_id = User.getId();

        selectedEvent = new Event(event_id);
        final DatabaseReference completed_ref = selectedEvent.ref.child("RestaurantPreferences/listOfCompleted");
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
                    //TODO: PREVENT USER FROM COMING BACK
//                    Intent next = new Intent(Swiping.this, ListEventsActivity.class);
//                    startActivity(next);
                }
                completed_ref.setValue(completed_ls);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        rest_name = v.findViewById(R.id.rest_name);
        rest_desc = v.findViewById(R.id.rest_descr);
        rest_rating = v.findViewById(R.id.rest_rating);
        rest_image = v.findViewById(R.id.rest_image);

        like_btn = v.findViewById(R.id.like);
        dislike_btn = v.findViewById(R.id.dislike);
        like_btn.setOnClickListener(this);
        dislike_btn.setOnClickListener(this);

        //THIS CAN BE REMOVED BUT USE IT INSTEAD OF A LOADING DIALOG
        rest_name.setText("Name");
        rest_desc.setText("Address");
        rest_rating.setText("Rating");

        //assign default image to ImageView here

        // RETRIEVE LIST OF RESTAURANT OPTIONS DETAILS AND IMAGES
        event_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, HashMap<String, Object>>> gt = new GenericTypeIndicator<HashMap<String, HashMap<String, Object>>>() {};
                GenericTypeIndicator<HashMap<String, ArrayList<String>>> gtPhotos = new GenericTypeIndicator<HashMap<String, ArrayList<String>>>() {};

                final HashMap<String, HashMap<String, Object>> arr = new HashMap<>();
                final HashMap<String, ArrayList<String>> arrPhotos= new HashMap<>();


                for (DataSnapshot prop: dataSnapshot.getChildren()){
                    if (prop.getKey().equals("placeDetails")){
                        arr.putAll(prop.getValue(gt));
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
                        } catch (NullPointerException ex) {
                            listRestVotes = new HashMap<>();
                            for (Object i: listRestNames){listRestVotes.put((String)i, 0);}
                        }

                        if (number[0]<arr.size() && !arr.isEmpty())
                            select_restaurant(arr, v);
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


        return v;
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
                    listener.updateRest(event_id, number[0], listRestVotes, checkLastItem);
                }

                if (number[0]<listRestVotes.size()){
                    // UPDATE VOTE FOR DISPLAYED ITEM
                    int currVal = listRestVotes.get(rest);
                    listRestVotes.put(rest, currVal+1);
                    //MOVE TO NEXT ITEM
                    number[0]++;
//                    select_restaurant(listRestInfo,v);
                    listener.updateRest(event_id, number[0], listRestVotes, checkLastItem);

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
                    listener.updateRest(event_id, number[0], listRestVotes, checkLastItem);
                }

                if (number[0]<listRestVotes.size()){
                    Log.i(TAG, "disliked here");
                    //MOVE TO NEXT ITEM
                    number[0]++;
//                    select_restaurant(listRestInfo,v);
                    listener.updateRest(event_id, number[0], listRestVotes, checkLastItem);
                }
            }

            Log.i("Check", "Dislike clicked");
        }
    }

    // GETS RESTAURANT INFORMATION TO DISPLAY AS NEXT ITEM
    public void select_restaurant(final HashMap<String, HashMap<String, Object>> arr, View v) {
        String rest = (String) listRestNames[number[0]];
        item = arr.get(rest);
        rest_name.setText(rest);
        rest_desc.setText((String)item.get("formatted_address"));
        rest_rating.setText((String)item.get("rating"));
        if (listRestPhotos.containsKey(rest)){
            itemPhotos = listRestPhotos.get(rest);
            new DownloadImageTask((ImageView) v.findViewById(R.id.rest_image)).execute(itemPhotos.get(0));
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
                int members = grp.getMemberCount();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SwipingFragmentListener){
            listener = (SwipingFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement SwipingFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
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
