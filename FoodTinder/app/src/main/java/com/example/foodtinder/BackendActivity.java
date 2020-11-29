package com.example.foodtinder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//TODO change this to Java class, doesn't need to extend AppCompatActivity

public class BackendActivity extends AppCompatActivity {

    String event_id;
    int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sub);

        //To check if the location and cost preference has been completed or not
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        final boolean[] var = new boolean[2];

        // CHECK EVENT STATUS
        DatabaseReference ref1 = myRef.child("Event/" + event_id + "/init_pref"); // check if init_preference is completed (can be replace with states)
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                var[0] = dataSnapshot.getValue(Boolean.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        // CHECK IF RESTAURANT HAS BEEN DECIDED? IDEALLY EVENT STATUS SHOULD REFLECT THIS
        ref1 = myRef.child("Event/" + event_id + "/final_rest"); // check if restaurant has been decided
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class)==null)
                    var[1] = false;
                else
                    var[1] = true;

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        if(var[0]) { //if initial preference isnt done, it checks if deadline is crossed or has everyone completed it
            ref1 = myRef.child("Event/" + event_id + "/preference/deadline");
            ref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Date date = dataSnapshot.getValue(Date.class);
                    if (date.after(new Date()))//compare if the deadline is not over
                        complete_init_pref();
                    ArrayList<Integer> ls = dataSnapshot.getValue(ArrayList.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        // SET DEFAULT
        else if(var[1]) { //if final preference isnt done, it checks if deadline is crossed or has everyone completed it
            ref1 = myRef.child("Event/" + event_id + "/restaurant_pref/deadline");
            ref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Date date = dataSnapshot.getValue(Date.class);
                    if (date.after(new Date()))//compare if the deadline is not over
                        complete_final_pref();
                    ArrayList<Integer> ls = dataSnapshot.getValue(ArrayList.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
    //Don't forget to override onPause()
    public void complete_init_pref(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        DatabaseReference ref1 = myRef.child("Event/" + event_id + "/L_member/no");
        final int[] a = new int[1];
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                a[0] = dataSnapshot.getValue(Integer.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        ref1 = myRef.child("Event/" + event_id + "/preference/completed");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Integer> ls = dataSnapshot.getValue(ArrayList.class);
//                if (ls.contains(user_id));
                if(ls.size() == a[0])
                    //next step which is using the preference and location and then inserting the restaurant list for the user
                    //also make a variable to show that restaurants have been already assigned
                    a[0] = 0; //ignore this assignment
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void complete_final_pref(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        DatabaseReference ref1 = myRef.child("Event/" + event_id + "/L_member/no");
        final int[] a = new int[1];
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                a[0] = dataSnapshot.getValue(Integer.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        ref1 = myRef.child("Event/" + event_id + "/restaurant_pref/completed");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Integer> ls = dataSnapshot.getValue(ArrayList.class);
                if(ls.size() == a[0])
                    assign_restaurant();
                a[0] = 0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void assign_restaurant(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();

        final String preference[] = new String[1];

        // GET BUDGET PARAMETER TO SEND FOR QUERY
        DatabaseReference ref1 = myRef.child("Event/" + event_id + "/preference/money");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Integer> ls = dataSnapshot.getValue(List.class);
                String str[] = {"$","$$","$$$"};
                int arr[] = new int[3];
                int largest = -1;
                for (int i = 0; i<3; ++i) {
                    arr[i] = Collections.frequency(ls, str[i]);
                    if(arr[largest]>arr[i])
                        largest = i;
                }
                int flag = 0;
                for(int i = 0; i<3; ++i){
                    if(arr[i]==arr[largest])
                        flag+=1;
                }
                if(flag>1){
                    preference[0] = "$$"; //if two/three money options have the largest preference, medium range is automatically selected
                }
                else
                    preference[0] = str[largest];
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // GET LOCATION PARAMETER TO SEND FOR QUERY
        ref1 = myRef.child("Event/" + event_id + "/preference/location");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Integer> ls = dataSnapshot.getValue(List.class);
                Set<String> distinct = new HashSet<String>(dataSnapshot.getValue(List.class));
                HashMap<String,Integer> restaurant_pref_no = null;
                int total = 0;
                for (String s: distinct) {
                   restaurant_pref_no.put(s,Collections.frequency(ls, s)) ;
                   total+=Collections.frequency(ls,s);
                }
                for (String s: distinct) {
                    restaurant_pref_no.put(s,Math.round(restaurant_pref_no.get(s)/total));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}