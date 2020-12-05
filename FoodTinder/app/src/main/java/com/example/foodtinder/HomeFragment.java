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

    private FloatingActionButton create_event_btn;
    private FragmentHomeListener homeListener;
    private RecyclerView recyclerView;
    EventListAdapterFinal eventListAdapter;
    private static final String TAG = "HomeFragment";
    ArrayList<Event> eventsInfoList = new ArrayList<>();
    boolean checkEvent = false;
    Boolean isInGroup = false;
    DatabaseReference db, groups_ref, events_ref;


    public interface FragmentHomeListener {
        void onCreateEvent();
        void selectRestaurant(String Id, boolean firstEntry);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        if (checkEvent == false){
            // no new upcoming events
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
                // TODO: If user is not in any group, give a toast
                if (isInGroup == true){
                    homeListener.onCreateEvent();
                } else {
                    Log.i(TAG, "you are not in group");
                    Context context = getContext();
                    CharSequence text = "You are not in any group.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast informUser = Toast.makeText(context, text, duration);
                    informUser.show();
                }
            }
        });


        return v;
    }


    private void buildRecycleView(View v) {


        recyclerView = v.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        eventListAdapter = new EventListAdapterFinal(eventsInfoList, getContext());
        recyclerView.setAdapter(eventListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Log.i(TAG, eventsInfoList.toString());
        eventListAdapter.setOnItemClickListener(new EventListAdapterFinal.myOnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.i(TAG, "here");
                Log.i(TAG, Integer.toString(position));
//                eventItemArrayList.get(position).changeText("Clickeddddd");
//                eventListAdapter.notifyItemChanged(position);
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null){

            eventsInfoList = bundle.getParcelableArrayList("eventlist");
            checkEvent = true;
        } else {
            checkEvent = false;
        }

        Log.i("check", User.getId());
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
