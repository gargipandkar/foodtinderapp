package com.example.foodtinder;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cardItems.CardStackAdapterFinal;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SwipeTestFragment extends Fragment {

    // TAG for debugging purposes
    private static final String TAG = "SwipeTestFragment";


    private CardStackView cardStackView;
    private CardStackLayoutManager manager;
    private CardStackAdapterFinal adapter;


    // Variables to store list of restaurants and their information
    ArrayList<String> listRestName, listRestAddr;
    HashMap<String, ArrayList<String>> listRestPhotos;
    HashMap<String, Integer> listRestVotes;
    Event selectedEvent;
    String eventId, user_id;
    int number = 0;
    Object[] listRestNames;

    // Listener is used to call the abstract method in the interface
    private SwipeTestFragmentListener listener;

    // Implement this interface in host Activity (SignOutActivity.java) to transfer data from this Fragment to host Activity
    // Abstract method will be override in host Activity to receive information needed and communicate with the next fragment
    public interface SwipeTestFragmentListener {
        void finishSwipe();
    }

    public SwipeTestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        eventId = bundle.getString("eventId");
        listRestName = bundle.getStringArrayList("name");
        listRestAddr = bundle.getStringArrayList("addr");
        listRestPhotos = (HashMap<String, ArrayList<String>>) bundle.getSerializable("photos");
        listRestNames = (Object[]) bundle.getSerializable("listRestNames");
        listRestVotes = (HashMap<String, Integer>) bundle.getSerializable("listRestVotes");
        user_id = User.getId();
        selectedEvent = new Event(eventId);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_swipe_test, container, false);

        // Remove bottom navigation for better swiping UI
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(v.GONE);

        // Initialise Firebase Realtime Database
        final DatabaseReference completed_ref = selectedEvent.ref.child("RestaurantPreferences/listOfCompleted");
        final DatabaseReference restVote_ref = selectedEvent.ref.child("RestaurantPreferences").child("listOfVotes");
        final DatabaseReference event_ref = selectedEvent.ref;

        // Add user to the list of those who have finished swiping
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


        // Check if user swipe left or right and update vote count on Firebase Realtime Database
        CardStackView cardStackView = v.findViewById(R.id.card_stack_view);
        manager = new CardStackLayoutManager(getContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) { }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);
                String rest = listRestName.get(number);

                // Swipe right if user like the listed restaurant
                if (direction == Direction.Right){
                    Toast.makeText(getContext(), "Yes!", Toast.LENGTH_SHORT).show();
                    if (number==(listRestVotes.size() - 1)){
                        // Update Firebase Realtime Database votes list
                        restVote_ref.setValue((listRestVotes));
                        // Check if all members have completed selecting restaurant preference by swiping
                        // If yes, final restaurant location will be updated on the event page in Home Fragment which contains the list of events and their information
                        stillSwiping();
                    }
                    // If user has not finished swiping, go to the next restaurant
                    if (number<listRestName.size()){
                        // Update vote for displayed item
                        int currVal = listRestVotes.get(rest);
                        listRestVotes.put(rest, currVal+1);
                        //MOVE TO NEXT ITEM
                        number++;

                    }
                    Log.i(TAG, "like");
                }
                if (direction == Direction.Top){
                    Toast.makeText(getContext(), "Direction Top", Toast.LENGTH_SHORT).show();
                }

                // Swipe left if user dislike the listed restaurant
                if (direction == Direction.Left){
                    Toast.makeText(getContext(), "Nope!", Toast.LENGTH_SHORT).show();
                    if (number==(listRestVotes.size() - 1)){
                        // Update Firebase Realtime Database votes list
                        restVote_ref.setValue((listRestVotes));
                        // Check if all members have finished swiping
                        // If yes, final restaurant location will be updated on the event page in Home Fragment which contains the list of events and their information
                        stillSwiping();
                    }
                    // If user has not finished swiping, go to the next restaurant
                    if (number<listRestName.size()){
                        number++;
                    }
                    Log.i(TAG, "dilike");
                }
                if (direction == Direction.Bottom){
                    Toast.makeText(getContext(), "Direction Bottom", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCardRewound() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardAppeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
            }

            // When all restaurants are 'swiped', go to back to host Activity (SignOutAcitivity) that implements this fragment
            @Override
            public void onCardDisappeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardDisappeared: " + position + ", nama: " + tv.getText());
                if (position == listRestName.size()-1){
                    BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
                    bottomNavigationView.setVisibility(v.VISIBLE);
                    listener.finishSwipe();
                }
            }
        });
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.HORIZONTAL);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        adapter = new CardStackAdapterFinal(listRestName, listRestAddr, listRestPhotos);
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());

        return v;
    }

    // Function to call to check if all members has completed selecting restaurant preference by swiping
    // Check from Firebase Realtime Database
    public void stillSwiping(){
        Log.i(TAG, "Check who has completed swiping");
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


    // Function to call to update event status when everyone has completed selecting restaurant preference by swiping
    // Updates event status to "Processing"
    private void stopSwiping(final long count){
        Log.i(TAG, "Check if swiping stage over");

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

    // Function to call to get swiping results and determine the most popular restaurant
    // Updates the final restaurant choice and event status on both user's event list page (Home Fragment) and Firebase Realtime Database
    // If everyone has completed selecting restaurant preference by swiping, update event status to "Match found"
    public void findMatch(){
        Log.i(TAG, "Find a match");
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
                else Log.i(TAG, "No list of votes found");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SwipeTestFragmentListener){
            listener = (SwipeTestFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement SwipeTestFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


}