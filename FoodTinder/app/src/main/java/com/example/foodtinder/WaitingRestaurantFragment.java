package com.example.foodtinder;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class WaitingRestaurantFragment extends Fragment {

    // TAG for debugging purposes
    private static final String TAG = "WaitingRestFragment";

    String user_id, event_id;
    Event selectedEvent;

    int[] number = {0};
    HashMap<String, Integer> listRestVotes;
    HashMap<String, HashMap<String, Object>> listRestInfo;
    HashMap<String, ArrayList<String>> listRestPhotos;
    Object[] listRestNames;
    boolean firstEntry;
    boolean checkLastItem = false;

    // Listener is used to call the abstract method in the interface
    private FragmentWaitingRestaurantListener waitingRestaurantFragmentListener;

    // Implement this interface in host Activity (SignOutActivity.java) to transfer data from this Fragment to host Activity
    // Abstract method will be override in host Activity to receive information needed and communicate with the next fragment
    public interface FragmentWaitingRestaurantListener {
        void onListingRestaurant(String event_id, ArrayList<String> restAddr, ArrayList<String> restName, HashMap<String, ArrayList<String>> listRestPhotos, Object[] listRestNames,HashMap<String, Integer> listRestVotes);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_waiting_restaurant, container, false);

        user_id = User.getId();

        selectedEvent = new Event(event_id);
        final DatabaseReference completed_ref = selectedEvent.ref.child("RestaurantPreferences/listOfCompleted");
        final DatabaseReference restVote_ref = selectedEvent.ref.child("RestaurantPreferences").child("listOfVotes");
        final DatabaseReference event_ref = selectedEvent.ref;


        // Add user to list of those who have finished swiping
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
                completed_ref.setValue(completed_ls);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Retrieved list of restaurant options from Google Places API and Firebase Realtime Database
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


                listRestInfo = arr;
                listRestPhotos = arrPhotos;
                listRestNames = arr.keySet().toArray();

                // Retrieve list of votes
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

                        if (number[0]<arr.size() && !arr.isEmpty()){
                            displayList();
                        }
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


    void displayList(){
        ArrayList<String> restName = new ArrayList<>();
        ArrayList<String> restAddr = new ArrayList<>();
        for (int i = 0; i<listRestInfo.size(); i++){
            String rest = (String) listRestNames[i];
            restName.add(rest);
            restAddr.add(String.valueOf(listRestInfo.get(rest).get("formatted_address")));
        }
        waitingRestaurantFragmentListener.onListingRestaurant(event_id, restAddr, restName, listRestPhotos, listRestNames, listRestVotes);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentWaitingRestaurantListener){
            waitingRestaurantFragmentListener = (FragmentWaitingRestaurantListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement WaitingRestaurantFragment");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        waitingRestaurantFragmentListener = null;
    }
}
