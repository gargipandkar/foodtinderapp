//package com.example.foodtinder;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//
//public class ListGroupsActivity extends AppCompatActivity {
//    DatabaseReference db, groups_ref;
//
//    ArrayList<String> groupsList = new ArrayList<>();
//    int groupCount = 0;
//
//
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_listevents);
//
//        db = FirebaseDatabase.getInstance().getReference();
//        groups_ref = db.child("USERS").child(User.getId()).child("listOfGroups");
//
//        User.setUserGroups(groups_ref, new DatabaseCallback() {
//            @Override
//            public void onCallback(ArrayList<String> ls) {
//                groupsList.addAll(ls);
//                groupCount = groupsList.size();
//                displayList();
//
//                User.inGroups.clear();
//                User.inGroups.addAll(ls);
//
//                Log.i("Check", ls.toString());
//                Log.i("Check", User.getId());
//                Log.i("Check", groupsList.toString());
//                Log.i("Check", "# of groups = "+ groupCount);
//
//            }
//
//
//        });
//
//
//    }
//
//    void displayList(){
//        LinearLayout layoutListEvents = findViewById(R.id.listevents_layout);
//
//        for (int i = 0; i < groupCount; i++) {
//            TextView listItem = new TextView(this);
//            listItem.setText(groupsList.get(i));
//            listItem.setId(i);
//            layoutListEvents.addView(listItem);
//        }
//    }
//
//}

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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListGroupsActivity extends AppCompatActivity {
    DatabaseReference db, groups_ref;

    ArrayList<String> groupsList = new ArrayList<>();
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
                displayList();

                User.inGroups.clear();
                User.inGroups.addAll(ls);

                Log.i("Check", ls.toString());
                Log.i("Check", User.getId());
                Log.i("Check", groupsList.toString());
                Log.i("Check", "# of groups = "+ groupCount);

            }


        });


    }

    void displayList(){
        LinearLayout layoutListEvents = findViewById(R.id.listevents_layout);

        for (int i = 0; i < groupCount; i++) {
            TextView listItem = new TextView(this);
            listItem.setText(groupsList.get(i));
            listItem.setId(i);
            layoutListEvents.addView(listItem);
        }
    }

}