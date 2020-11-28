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
import java.util.Date;
import java.util.List;

public class SubActivity_backend extends AppCompatActivity {

    String event_id;
    int user_id;
    public final static String INTENT_EXCH_RATE = "Exchange Rate";
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.android.subsharedprefs";
    public final static String HOME_KEY = "HOME_KEY"; //A --> Home
    public final static String FOREIGN_KEY = "FOREIGN_KEY"; //B --> Foreign

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        //To check if the location and cost preference has been completed or not
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        final boolean[] var = new boolean[2];

        DatabaseReference ref1 = myRef.child("Event/" + event_id + "/init_pref"); // check if init_preference is completed (can be replace with states)
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                var[0] = dataSnapshot.getValue(Boolean.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

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
            //TODO 4.10 Don't forget to override onPause()
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

        DatabaseReference ref1 = myRef.child("Event/" + event_id + "/restaurant_pref/prefer");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Integer> ls = dataSnapshot.getValue(ArrayList.class);
                int max = ls.get(0);
                for(Integer x:ls)
                    if(x>max)
                        max = x;

                DatabaseReference ref2 = myRef.child("Event/" + event_id + "/final_rest");
                ref2.setValue(ls.indexOf(max));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}