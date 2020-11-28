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

public class SubActivity_uipref extends AppCompatActivity {

    Spinner spinner1, spinner2;
    Button btnSubmit;
    TextView text;
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

        text = findViewById(R.id.text_sub);

        user_id = 002;
        event_id = "001";

        addItemsOnSpinner2();
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
    }

    // add items into spinner dynamically
    public void addItemsOnSpinner2() {

        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    // get the selected dropdown list value
    public void addListenerOnButton() {

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);


        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference();
                DatabaseReference ref1 = myRef.child("Event/" + event_id + "/preference/completed");
                ref1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<Integer> ls = dataSnapshot.getValue(ArrayList.class);
                        ls.add(user_id);
                        DatabaseReference ref2 = myRef.child("Event/" + event_id + "/preference/completed");
                        ref2.push().setValue(ls.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                ref1 = myRef.child("Event/" + event_id + "/preference/location");
                ref1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> ls = dataSnapshot.getValue(ArrayList.class);
                        ls.add(spinner2.getSelectedItem().toString());
                        DatabaseReference ref2 = myRef.child("Event/" + event_id + "/preference/location");
                        ref2.push().setValue(ls.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                ref1 = myRef.child("Event/" + event_id + "/preference/money");
                ref1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> ls = dataSnapshot.getValue(ArrayList.class);
                        ls.add(spinner1.getSelectedItem().toString());
                        DatabaseReference ref2 = myRef.child("Event/" + event_id + "/preference/money");
                        ref2.push().setValue(ls.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

}