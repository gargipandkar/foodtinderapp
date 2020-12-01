package com.example.placeslib;

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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BackendActivity_UNUSED {

    DatabaseReference ref1;

    public void runCompletionChecks(final Event selectedEvent) {
        //To check if preference has been completed or not
        final boolean[] notDone = new boolean[2];

        // CHECK EVENT STATUS
        if (selectedEvent.getStatus().equals("Waiting for preferences"))
            notDone[0] = true;
        else
            notDone[0] = false;

        // CHECK IF RESTAURANT HAS BEEN DECIDED? IDEALLY EVENT STATUS SHOULD REFLECT THIS
        ref1 = selectedEvent.ref.child("decision");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class).equals("Undecided"))
                    notDone[1] = true;
                else
                    notDone[1] = false;

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        if(notDone[0]) { //if initial preference isnt done, it checks if deadline is crossed or has everyone completed it
            ref1 = selectedEvent.ref.child("prefDateTime");
            final Calendar deadline = Calendar.getInstance();
            ref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long millis = dataSnapshot.getValue(long.class);
                    deadline.setTimeInMillis(millis);
                    if (deadline.after(Calendar.getInstance()))//compare if the deadline is not over
                        complete_init_pref(selectedEvent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }


        else if(notDone[1]) { //if final preference isnt done, it checks if deadline is crossed or has everyone completed it
            ref1 = selectedEvent.ref.child("eventDateTime");
            final Calendar deadline = Calendar.getInstance();
            ref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long millis = dataSnapshot.getValue(long.class);
                    deadline.setTimeInMillis(millis);
                    deadline.add(Calendar.HOUR_OF_DAY, -1);
                    if (deadline.after(Calendar.getInstance()))//compare if the deadline is not over
                        complete_final_pref(selectedEvent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    public void complete_init_pref(Event selectedEvent){
        DatabaseReference ref1 = selectedEvent.ref.child("L_member/no");
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

        ref1 = selectedEvent.ref.child("Preferences/listOfCompleted");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Integer> ls = dataSnapshot.getValue(ArrayList.class);
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

    public void complete_final_pref(final Event selectedEvent){
        DatabaseReference ref1 = selectedEvent.ref.child("L_member/no");
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

        ref1 = selectedEvent.ref.child("restaurant_pref/completed");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Integer> ls = dataSnapshot.getValue(ArrayList.class);
                if(ls.size() == a[0])
                    get_query_params(selectedEvent);
                a[0] = 0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    
    public void get_query_params(Event selectedEvent){
        final String preference[] = new String[1];

        // GET BUDGET PARAMETER TO SEND FOR QUERY
        DatabaseReference ref1 = selectedEvent.ref.child("Preferences/listOfBudget");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Integer> ls = dataSnapshot.getValue(List.class);
                String[] str = {"$","$$","$$$"};
                int[] arr = new int[3];
                int largest = 0;
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
        ref1 = selectedEvent.ref.child("Preferences/listOfLocation");
        final String[] preferenceLoc = new String[1];
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Integer> ls = dataSnapshot.getValue(List.class);
                Set<String> distinct = new HashSet<String>(dataSnapshot.getValue(List.class));
                int maxFreq = 0;
                for (String s: distinct) {
                    int freq = Collections.frequency(ls, s);
                    if (freq>maxFreq){ preferenceLoc[1]=s;}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void assign_restaurant_choices(String[] budget, String[] location){
        //TODO query Places API and send result to database at "listOfRestaurant" child
    }


}
