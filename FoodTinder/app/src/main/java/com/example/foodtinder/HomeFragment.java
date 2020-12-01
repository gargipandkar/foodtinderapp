package com.example.foodtinder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.cardItems.EventItem;
//import com.example.cardItems.EventListAdapter;
import com.example.cardItems.EventListAdapterFinal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FloatingActionButton create_event_btn;
    private FragmentHomeListener homeListener;
    private RecyclerView recyclerView;
    private static ArrayList<Event> eventItemArrayList = new ArrayList<>();;
    EventListAdapterFinal eventListAdapter;
    private static final String TAG = "HomeFragment";

    String eventName, eventLocation, eventBudget, eventStatus;
    Long eventDT;





    public interface FragmentHomeListener {
        void onCreateEvent();
    }


    public void updateEvent(String name, Long dt, String budget, String location, String status){
        eventItemArrayList.add(new Event( name, name, name, dt, location, budget, status));
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        buildRecycleView(v);

        if (savedInstanceState != null ){
            //Restore the fragment's instance
        }

        create_event_btn = v.findViewById(R.id.create_event_btn);
        create_event_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeListener.onCreateEvent();

            }
        });

        return v;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's state here
//        if (eventItemArrayList.size() > 0) {
//            for (int i = 0; i < eventItemArrayList.size(); i++){
//                Event event = eventItemArrayList.get(i);
//                event.
//            }
//        }
//        outState.putPa
    }

    private void buildRecycleView(View v) {
        recyclerView = v.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        eventListAdapter = new EventListAdapterFinal(eventItemArrayList, getContext());
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



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null){
            eventName = bundle.getString("name");
            eventLocation = bundle.getString("location");
            eventDT = bundle.getLong("dateTime");
            eventStatus = bundle.getString("status");
            Log.i(TAG, "reached");
            Log.i(TAG, eventName);
            Log.i(TAG, eventStatus);
            updateEvent(eventName, eventDT, eventBudget, eventLocation, eventStatus);
        }
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
