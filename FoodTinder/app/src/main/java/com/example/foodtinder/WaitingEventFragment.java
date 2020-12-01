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
    ArrayList<Event> allEvents = new ArrayList<>();
    int eventCount;



    public interface FragmentWaitingEventListener {
        void onListingEvents(ArrayList<Event> eventArrayList);
    }





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_waiting_event, container, false);


        latestEvent();
//        Log.i(TAG, "latestEvent");
//        Log.i(TAG, eventsInfoList.get(0).getName());

        return v;
    }





    private void latestEvent() {
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
        Log.i(TAG, "to infoList()");
        final DatabaseReference allEvents_ref = db.child("EVENTS");
        allEvents_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
    //            GenericTypeIndicator<HashMap<String, Event>> gt = new GenericTypeIndicator<HashMap<String, Event>>() {};
                GenericTypeIndicator<ArrayList<Event>> gt = new GenericTypeIndicator<ArrayList<Event>>() {};
                allEvents = snapshot.getValue(gt);

                eventsInfoList.clear();
                for (String i: eventsList) {
                    eventsInfoList.add(allEvents.get(Integer.valueOf(i)));
                }
//                Log.i(TAG, eventsInfoList.toString());
//                Log.i("eventsInfoList name", eventsInfoList.get(0).getName());

                displayList();
//                checkEventList(eventsInfoList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

    void displayList(){
//        LinearLayout layoutListEvents = findViewById(R.id.listevents_layout);
//
        Log.i("displayList", eventsInfoList.toString());

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
        waitingEventFragmentListener = null;
    }
}
