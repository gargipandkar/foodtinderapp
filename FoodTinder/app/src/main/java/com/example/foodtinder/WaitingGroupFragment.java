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
import androidx.recyclerview.widget.RecyclerView;

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

public class WaitingGroupFragment extends Fragment {

    private FragmentWaitingGroupListener waitingGroupFragmentListener;
    private static final String TAG = "WaitingGroupFragment";


    // listing of events variables
    DatabaseReference db, groups_ref;

    ArrayList<String> groupsList = new ArrayList<>();
    HashMap<String, Group> allGroups = new HashMap<>();
    ArrayList<Group> groupsInfoList = new ArrayList<>();
    int groupCount = 0;



    public interface FragmentWaitingGroupListener {
        void onListingGroup(ArrayList<Group> groupArrayList);
    }





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_waiting_group, container, false);

        latestEvent();

        return v;
    }





    private void latestEvent() {
        db = FirebaseDatabase.getInstance().getReference();
        groups_ref = db.child("USERS").child(User.getId()).child("inGroups");

        User.setUserGroups(groups_ref, new DatabaseCallback() {
            @Override
            public void onCallback(ArrayList<String> ls) {
                groupsList.addAll(ls);
                groupCount = groupsList.size();

                User.inGroups.clear();
                User.inGroups.addAll(ls);

                infoList();

                Log.i("Check", ls.toString());
                Log.i("Check", User.getId());
                Log.i("Check", groupsList.toString());
                Log.i("Check", "# of groups = "+ groupCount);

            }

            @Override
            public void onCallback(Event event){}
            public void onCallback (Group group){}
            public void onCallback(ArrayList<Restaurant> allRest, boolean done){}
        });
    }


    void infoList(){
        final DatabaseReference allGroups_ref = db.child("GROUPS");
        allGroups_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<HashMap<String, Group>> gt = new GenericTypeIndicator<HashMap<String, Group>>() {};
                allGroups = snapshot.getValue(gt);

                groupsInfoList.clear();
                for (String i: groupsList) {
                    if (allGroups.containsKey(i))
                        groupsInfoList.add(allGroups.get(i));
                }
                Log.i("Check", groupsInfoList.toString());
                displayList();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

    void displayList(){
        waitingGroupFragmentListener.onListingGroup(groupsInfoList);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentWaitingGroupListener){
            waitingGroupFragmentListener = (FragmentWaitingGroupListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentWaitingEventListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        waitingGroupFragmentListener = null;
    }
}
