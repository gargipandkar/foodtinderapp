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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Swiping extends AppCompatActivity {

    Button like_btn, dislike_btn;
    TextView rest_name,rest_desc,rest_rating;
    String event_id;
    ImageView rest_image;
    int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);


        user_id = 002;
        event_id = "001";


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        // RETRIEVE LIST OF RESTAURANT OPTIONS
        DatabaseReference ref1 = myRef.child("Event/" + event_id + "/L_restaurant");

        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<Integer,ArrayList> arr =  dataSnapshot.getValue(HashMap.class);
                select_restuarant(arr);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        final DatabaseReference finalRef = myRef.child("Event/" + event_id + "/restaurant_pref/completed");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList completed_ls = dataSnapshot.getValue(ArrayList.class);
                completed_ls.add(user_id);
                finalRef.setValue(completed_ls);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void select_restuarant(HashMap<Integer,ArrayList> arr){
        final int[] number = {0};

        rest_name = findViewById(R.id.rest_name);
        rest_desc = findViewById(R.id.rest_descr);
        rest_rating = findViewById(R.id.rest_rating);
        rest_image = findViewById(R.id.rest_image);

        like_btn = findViewById(R.id.like);
        dislike_btn = findViewById(R.id.dislike);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();
        DatabaseReference ref1 = myRef.child("Event/" + event_id + "/restaurant_pref/prefer");

        // arr SENT FROM DATABASE CALL
        while(number[0] < arr.size()) {

            rest_name.setText(arr.get(number[0]).get(0).toString());
            rest_desc.setText(arr.get(number[0]).get(1).toString());
            rest_rating.setText(arr.get(number[0]).get(2).toString());
            //assign the image here

            // UPDATE THIS ITEM UNDER "RESTAURANT PREFERENCE" DATABASE CHILD SINCE LIKE IS VALUE-ADD THEN MOVE TO NEXT ITEM
            like_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference();

                    final DatabaseReference ref1 = myRef.child("Event/" + event_id + "/restaurant_pref/pref" + String.valueOf(number[0]));
                    ref1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int val = dataSnapshot.getValue(Integer.class);
                            ref1.setValue(val + 1);
                            number[0]++;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });


                }
            });

            //MOVE TO NEXT ITEM, NO NEED TO UPDATE DATABASE SINCE DISLIKE IS ZERO VALUE-ADD
            dislike_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    number[0]++;
                }
            });

        }

    }


}