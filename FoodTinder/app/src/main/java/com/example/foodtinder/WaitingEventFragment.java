package com.example.foodtinder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardItems.EventListAdapterFinal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

//import com.example.cardItems.EventListAdapter;

public class WaitingEventFragment extends Fragment {

    private FloatingActionButton create_event_btn;
    private FragmentWaitingEventListener waitingEventFragmentListener;
    private RecyclerView recyclerView;
    private ArrayList<Event> eventItemArrayList = new ArrayList<>();;
    private static final String TAG = "WaitingEventFragment";


    // listing of events variables
    DatabaseReference db, events_ref;
    ArrayList<String> eventsList = new ArrayList<>();
    ArrayList<Event> eventsInfoList = new ArrayList<>();
    HashMap<String, Event> allEvents = new HashMap<String, Event>();
    int eventCount;



    public interface FragmentWaitingEventListener {
        void onListingEvents(ArrayList<Event> eventArrayList);
    }





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_waiting_event, container, false);

        latestEvent();


        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();

        if (bundle != null){
            eventsInfoList = bundle.getParcelableArrayList("eventlist");
        }
    }

    private void latestEvent() {
        // READ LIST OF EVENTS FROM FIREBASE AND PUT INTO LOCAL USER OBJECT
        db = FirebaseDatabase.getInstance().getReference();
        Log.i(TAG, "waiting event fragment user id");

        events_ref = db.child("USERS").child(User.getId()).child("listOfEvents");

        User.setUserEvents(events_ref, new DatabaseCallback() {
            @Override
            public void onCallback(ArrayList<String> ls) {
                //CHECK IF USER HAS NO EVENTS
                if(!ls.isEmpty()){
                    eventsList.addAll(ls);
                    eventCount = eventsList.size();
                    Log.i(TAG,User.listOfEvents.toString());
                    User.listOfEvents.clear();
                    User.listOfEvents.addAll(ls);
                    Log.i(TAG,User.listOfEvents.toString());
                    infoList();
                } else {
                    displayList();
                }

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
                    try {
                        Event e = allEvents.get(i);
                        e.checkExpiry(i);
                        if (e.decision.equals("Undecided"))
                            e.passedDeadline();
                        if (e.active)
                            eventsInfoList.add(e);
                        eventCount = eventsInfoList.size();
                    }
                    catch(NullPointerException ex){}

                }
                // Log.i("Check", eventsInfoList.toString());
                displayList();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

    void displayList(){
        waitingEventFragmentListener.onListingEvents(eventsInfoList);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentWaitingEventListener){
            waitingEventFragmentListener = (FragmentWaitingEventListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentWaitingEventListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        waitingEventFragmentListener = null;
    }
}
