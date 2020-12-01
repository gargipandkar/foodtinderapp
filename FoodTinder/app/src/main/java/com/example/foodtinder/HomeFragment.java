package com.example.foodtinder;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.cardItems.EventItem;
//import com.example.cardItems.EventListAdapter;
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

public class HomeFragment extends Fragment {

    private FloatingActionButton create_event_btn;
    private FragmentHomeListener homeListener;
    private RecyclerView recyclerView;
    private ArrayList<Event> eventItemArrayList = new ArrayList<>();;
    EventListAdapterFinal eventListAdapter;
    private static final String TAG = "HomeFragment";

    Integer eventId;
    String eventName, eventGroup, eventUserId, eventLocation, eventBudget, eventStatus;
    Long eventDT;

    boolean isList = false;
    int count;

    // listing of events variables
    DatabaseReference db, events_ref;
    ArrayList<String> eventsList = new ArrayList<>();
    ArrayList<Event> eventsInfoList = new ArrayList<>();
    ArrayList<Event> allEvents = new ArrayList<>();
    int eventCount;



    public interface FragmentHomeListener {
        void onCreateEvent();
        void updateHome(boolean checkListNotEmpty, Fragment curFrag);
    }


    public void updateEvent(Integer eventId, String name, String group, String userId, Long dateTime, String budget, String location, String status){
        eventItemArrayList.add(new Event(eventId, name, group, userId, dateTime, location, budget, status));
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_home, container, false);


        if (savedInstanceState != null ){
            //Restore the fragment's instance
        }

        Button event_sel_btn = v.findViewById(R.id.event_sel_btn);
        event_sel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toSwiping = new Intent(getActivity(), Swiping.class);
                startActivity(toSwiping);
            }
        });

        create_event_btn = v.findViewById(R.id.create_event_btn);
        create_event_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeListener.onCreateEvent();

            }
        });


//        latestEvent();
//        Log.i(TAG, "latestEvent");
//        Log.i(TAG, eventsInfoList.get(0).getName());

        buildRecycleView(v);

        return v;
    }


    private void buildRecycleView(View v) {

//        if (isList == true ){
//            recyclerView = v.findViewById(R.id.recycleView);
//            recyclerView.setHasFixedSize(true);
////        eventListAdapter = new EventListAdapterFinal(eventItemArrayList, getContext());
//            // using arraylist of events where list of events are retrieve from firebase
//            eventListAdapter = new EventListAdapterFinal(eventsInfoList, getContext());
//            recyclerView.setAdapter(eventListAdapter);
//            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//            eventListAdapter.setOnItemClickListener(new EventListAdapterFinal.myOnItemClickListener() {
//                @Override
//                public void onItemClick(int position) {
////                eventItemArrayList.get(position).changeText("Clickeddddd");
////                eventListAdapter.notifyItemChanged(position);
//                    Intent toEventSelection = new Intent(getActivity(), EventSelectionActivity.class);
//                    toEventSelection.putExtra("Event Item", eventItemArrayList.get(position));
//                    startActivity(toEventSelection);
//                }
//            });
//        }
        recyclerView = v.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
//        eventListAdapter = new EventListAdapterFinal(eventItemArrayList, getContext());
        // using arraylist of events where list of events are retrieve from firebase
        Log.i(TAG, "buildRecycleView");
//        Log.i(TAG, eventsInfoList.get(0).getName());
        eventListAdapter = new EventListAdapterFinal(eventsInfoList, getContext());
        recyclerView.setAdapter(eventListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        eventListAdapter.setOnItemClickListener(new EventListAdapterFinal.myOnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                eventItemArrayList.get(position).changeText("Clickeddddd");
//                eventListAdapter.notifyItemChanged(position);
                Intent toEventSelection = new Intent(getActivity(), EventSelectionActivity.class);
                toEventSelection.putExtra("Event Item", eventItemArrayList.get(position));
                startActivity(toEventSelection);
            }
        });


    }

//    private boolean checkEventList(ArrayList<Event> eventList) {
//        if (eventList.size() == 0) {
//            isList = false;
//        } else {
//            isList = true;
//        }
//        return isList;
//
//    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null){
//            eventId = bundle.getInt("eventId");
//            eventName = bundle.getString("name");
//            eventGroup = bundle.getString("group");
//            eventUserId = bundle.getString("userId");
//            eventLocation = bundle.getString("location");
//            eventDT = bundle.getLong("dateTime");
//            eventStatus = bundle.getString("status");
//            Log.i(TAG, "reached");
//            Log.i(TAG, eventName);
//            Log.i(TAG, eventStatus);
//            updateEvent(eventId, eventName, eventGroup, eventUserId, eventDT, eventBudget, eventLocation, eventStatus);
//            Log.i(TAG, "to latestEvent()");

            eventsInfoList = bundle.getParcelableArrayList("eventlist");
            Log.i(TAG, eventsInfoList.toString());
            Log.i(TAG, "event list delivered to homefragment");


        } else {
            // retrieve event list from firebase
//            latestEvent();
//            Log.i(TAG, "to latestEvent()");
//            Log.i(TAG, eventsInfoList.get(0).getName());
//            Log.i("after latestEvent", eventsInfoList.toString());

        }
    }

//    private void latestEvent() {
//        // READ LIST OF EVENTS FROM FIREBASE AND PUT INTO LOCAL USER OBJECT
//        db = FirebaseDatabase.getInstance().getReference();
//        events_ref = db.child("USERS").child(User.getId()).child("listOfEvents");
//
//        User.setUserEvents(events_ref, new DatabaseCallback() {
//            @Override
//            public void onCallback(ArrayList<String> ls) {
//                eventsList.addAll(ls);
//                eventCount = eventsList.size();
//
//                User.activeEvents.clear();
//                User.activeEvents.addAll(ls);
//
//                infoList();
//
//                //LOG
//                Log.i("Check", ls.toString());
//                Log.i("Check", User.getId());
//                Log.i("Check", eventsList.toString());
//                Log.i("Check", "# of groups = "+ eventCount);
//
//            }
//
////            @Override
////            public void onCallback(Event event){}
//
//        });
//    }

//    void infoList(){
//        Log.i(TAG, "to infoList()");
//        final DatabaseReference allEvents_ref = db.child("EVENTS");
//        allEvents_ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//    //            GenericTypeIndicator<HashMap<String, Event>> gt = new GenericTypeIndicator<HashMap<String, Event>>() {};
//                GenericTypeIndicator<ArrayList<Event>> gt = new GenericTypeIndicator<ArrayList<Event>>() {};
//                allEvents = snapshot.getValue(gt);
//
//                eventsInfoList.clear();
//                for (String i: eventsList) {
//                    eventsInfoList.add(allEvents.get(Integer.valueOf(i)));
//                }
////                Log.i(TAG, eventsInfoList.toString());
////                Log.i("eventsInfoList name", eventsInfoList.get(0).getName());
//
//                displayList();
////                checkEventList(eventsInfoList);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) { }
//        });
//
//    }

//    void displayList(){
////        LinearLayout layoutListEvents = findViewById(R.id.listevents_layout);
////
//        Log.i("displayList", eventsInfoList.toString());
//        if (eventsInfoList.size() > 0) {
//            isList = true;
//            Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//            homeListener.updateHome(isList, currentFragment);
//        }
//
////
////        for (int i = 0; i < eventCount; i++) {
////            TextView listItem = new TextView(this);
////            listItem.setText(eventsList.get(i)+eventsInfoList.get(i).name);
////            listItem.setId(i);
////            layoutListEvents.addView(listItem);
////        }
//
////        for (int i = 0; i < eventCount; i++) {
////            Log.i(TAG,eventsList.get(i)+eventsInfoList.get(i).name);
////
////        }
//
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentHomeListener){
            homeListener = (FragmentHomeListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentHomeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        homeListener = null;
    }
}
