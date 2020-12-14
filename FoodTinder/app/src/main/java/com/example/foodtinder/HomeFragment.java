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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment {

    // TAG for debugging purposes
    private static final String TAG = "HomeFragment";


    private FloatingActionButton create_event_btn;
    private RecyclerView recyclerView;
    EventListAdapterFinal eventListAdapter;
    ArrayList<Event> eventsInfoList = new ArrayList<>();
    boolean hasEvent = false;
    boolean isInGroup = false;
    DatabaseReference db, groups_ref, events_ref;

    // Listener is used to call the abstract method in the interface
    private FragmentHomeListener homeListener;

    // Implement this interface in host Activity (SignOutActivity.java) to transfer data from this Fragment to host Activity
    // Abstract method will be override in host Activity to receive information needed and communicate with the next fragment
    public interface FragmentHomeListener {
        void onCreateEvent();
        void selectRestaurant(String Id, boolean firstEntry);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        // If user has one or more events, get the list of evnts and set hasEvent as true
        // Else hasEvent is false by default
        // To ensure proper display on the screen
        if (bundle != null){
            eventsInfoList = bundle.getParcelableArrayList("eventlist");
            hasEvent = true;
        }

        // Initialise Firebase Realtime Database
        db = FirebaseDatabase.getInstance().getReference();

        groups_ref = db.child("USERS").child(User.getId());
        groups_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot prop: snapshot.getChildren())
                    if (prop.getKey().equals("inGroups")){ isInGroup = true;}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        // If user does not have any events, display "No upcoming events"
        // Else generate a list of events user is in
        if (hasEvent == false){
            TextView noEventText = v.findViewById(R.id.no_event_label);
            TextView createEventText = v.findViewById(R.id.create_event_label);
            noEventText.setText("No upcoming events");
            createEventText.setText("Create new event");
        } else {
            buildRecycleView(v);
        }

        create_event_btn = v.findViewById(R.id.create_event_btn);
        create_event_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If user is in one or more group, allow user to create event
                // Else prevent user from creating event
                if (isInGroup == true){
                    homeListener.onCreateEvent();
                } else {
                    Toast.makeText(getContext(), "You are not in any group.", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "You are not in group");
                }
            }
        });


        return v;
    }

    // Function to call if user has one or more event
    // Returns a list of events user is in
    private void buildRecycleView(View v) {
        recyclerView = v.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        eventListAdapter = new EventListAdapterFinal(eventsInfoList, getContext());
        recyclerView.setAdapter(eventListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Allows user to click on the event
        // If event has not expire, allow user to click on the event
        // to choose restaurant preference
        eventListAdapter.setOnItemClickListener(new EventListAdapterFinal.myOnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.i(TAG, eventsInfoList.get(position).getId());
                String eventId = eventsInfoList.get(position).getId();
                events_ref = db.child("EVENTS").child(eventId);
                events_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            Toast.makeText(getContext(), "EVENT HAS EXPIRED", Toast.LENGTH_SHORT).show();
                        } else {
                            String status = eventsInfoList.get(position).getStatus();
                            if (status.equals("Ready to swipe")){
                                boolean firstEntry = true;
                                homeListener.selectRestaurant(eventsInfoList.get(position).getId(), firstEntry);
                            } else {
                                Toast.makeText(getContext(), "You have chosen your preference", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
        });

    }


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
