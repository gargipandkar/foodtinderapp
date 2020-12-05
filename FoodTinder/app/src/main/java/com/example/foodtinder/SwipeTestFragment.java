package com.example.foodtinder;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cardItems.CardStackAdapter;
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

    private static final String TAG = "SwipeTestFragment";
    private CardStackView cardStackView;
    private CardStackLayoutManager manager;
    private CardStackAdapterFinal adapter;


    // listing restaurants variable
    ArrayList<String> listRestName, listRestAddr;
    HashMap<String, ArrayList<String>> listRestPhotos;
    HashMap<String, Integer> listRestVotes;
    Event selectedEvent;
    String eventId, user_id;
    int number = 0;
    Object[] listRestNames;

    private SwipeTestFragmentListener listener;
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
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(v.GONE);

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





        CardStackView cardStackView = v.findViewById(R.id.card_stack_view);
        manager = new CardStackLayoutManager(getContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
//                Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);
                String rest = listRestName.get(number);
                Log.i(TAG, "rest name: " + rest);
                if (direction == Direction.Right){
                    Toast.makeText(getContext(), "Direction Right", Toast.LENGTH_SHORT).show();
                    //like
                    if (number==(listRestVotes.size() - 1)){
                        // MAKE NUMBER GO OUT OF RANGE NOW
                        // UPDATE DATABASE VOTES LIST
                        restVote_ref.setValue((listRestVotes));
                        //CHECK IF ALL MEMBERS HAVE FINISHED SWIPING, IF YES MAKE DECISION
                        stillSwiping();
                        // GO BACK TO HOME PAGE
                    }

                    if (number<listRestName.size()){
                        // UPDATE VOTE FOR DISPLAYED ITEM
                        Log.i(TAG, "listrestvotes: "+ listRestVotes.toString());
                        Log.i(TAG, "item num" + number);
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
                if (direction == Direction.Left){
                    Toast.makeText(getContext(), "Direction Left", Toast.LENGTH_SHORT).show();
                    //dislike
                    if (number==(listRestVotes.size() - 1)){
                        // MAKE NUMBER GO OUT OF RANGE NOW
                        // UPDATE DATABASE VOTES LIST
                        restVote_ref.setValue((listRestVotes));
                        //CHECK IF ALL MEMBERS HAVE FINISHED SWIPING, IF YES MAKE DECISION
                        stillSwiping();
                        // GO BACK TO HOME PAGE
                    }

                    if (number<listRestName.size()){
                        //MOVE TO NEXT ITEM
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

            @Override
            public void onCardDisappeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardDisappeared: " + position + ", nama: " + tv.getText());
                if (position == listRestName.size()-1){
//                    restVote_ref.setValue((listRestVotes));
//                    stillSwiping();
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


    private ArrayList<String> addList() {
        ArrayList<String> items = new ArrayList<>();
        items.add("hello");
        items.add("mahjong");
        items.add("grand");
        items.add("moonlight");

        return items;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SwipeTestFragmentListener){
            listener = (SwipeTestFragmentListener) context;
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