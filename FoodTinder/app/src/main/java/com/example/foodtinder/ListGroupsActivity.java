package com.example.foodtinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ListGroupsActivity extends AppCompatActivity {
    DatabaseReference db, groups_ref;

    ArrayList<String> groupsList = new ArrayList<>();
    HashMap<String, Group> allGroups = new HashMap<>();
    ArrayList<Group> groupsInfoList = new ArrayList<>();
    int groupCount = 0;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listevents);

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
        LinearLayout layoutListEvents = findViewById(R.id.listevents_layout);

        for (int i = 0; i < groupCount; i++) {
            TextView listItem = new TextView(this);
            listItem.setText(groupsInfoList.get(i).getName()+groupsInfoList.get(i).getMemberCount());
            listItem.setId(i);
            layoutListEvents.addView(listItem);
        }
    }

}
