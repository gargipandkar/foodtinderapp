package com.example.foodtinder;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

//import com.example.cardItems.EventListAdapter;

public class WaitingRestaurantFragment extends Fragment {

    private FragmentWaitingRestaurantListener waitingRestaurantFragmentListener;
    private static final String TAG = "WaitingRestFragment";
    private String event_id;
    String user_id;
    Event selectedEvent;
    final int[] number = {0};   //FOR ITERATING THROUGH RESTAURANT LIST
    HashMap<String, Object> item;
    ArrayList<String> itemPhotos;
    HashMap<String, Integer> listRestVotes;
    HashMap<String, HashMap<String, Object>> listRestInfo;
    HashMap<String, ArrayList<String>> listRestPhotos;
    Object[] listRestNames;
    boolean checkLastItem = false;


    public interface FragmentWaitingRestaurantListener {
        void onListingRestaurant(int[] number, HashMap<String, Integer> listRestVotes, HashMap<String, HashMap<String, Object>> listRestInfo, ArrayList<String> restAddr, ArrayList<String> restName);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        event_id = bundle.getString("eventId");
        Log.i(TAG, event_id);
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
                    // TODO: Give a toast in SignOutActivity
//                    Intent next = new Intent(Swiping.this, ListEventsActivity.class);
//                    startActivity(next);
                }
                completed_ref.setValue(completed_ls);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

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
                            Log.i("Check", listRestVotes.toString());
                        } catch (NullPointerException ex) {
                            listRestVotes = new HashMap<>();
                            for (Object i: listRestNames){listRestVotes.put((String)i, 0);}
                        }


                        if (number[0]<arr.size() && !arr.isEmpty()) {
                            select_restaurant(arr);
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


    // GETS RESTAURANT INFORMATION TO DISPLAY AS NEXT ITEM
    public void select_restaurant(final HashMap<String, HashMap<String, Object>> arr) {
        Log.i("Check", arr.toString());
        Log.i("Check", listRestVotes.toString());

        String rest = (String) listRestNames[number[0]];
        item = arr.get(rest);
//        rest_desc.setText((String)item.get("formatted_address"));
//        rest_rating.setText((String)item.get("rating"));
//        if (listRestPhotos.containsKey(rest)){
//            itemPhotos = listRestPhotos.get(rest);
//            new DownloadImageTask((ImageView) findViewById(R.id.rest_image)).execute(itemPhotos.get(0));
//        }

        Log.i("Check", "Item "+number[0]);

    }




    @Override
    public void onStart() {
        super.onStart();

    }

    void displayList(){
        Log.i(TAG, "listing in display list:" + listRestInfo.toString());
        Log.i(TAG, "Size: " + listRestInfo.size());
        ArrayList<String> restName = new ArrayList<>();
        ArrayList<String> restAddr = new ArrayList<>();
        for (int i = 0; i<listRestInfo.size(); i++){
            String rest = (String) listRestNames[i];
            restName.add(rest);
            restAddr.add(String.valueOf(listRestInfo.get(rest).get("formatted_address")));
        }
        Log.i(TAG, "Rest name: "+ restName.toString());
        waitingRestaurantFragmentListener.onListingRestaurant(number, listRestVotes, listRestInfo, restAddr, restName);
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
