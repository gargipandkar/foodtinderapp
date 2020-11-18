package com.example.norman_lee.myapplication;

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

public class Backend extends AppCompatActivity {

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

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("001");
        user_id = 002;
        event_id = "001";
        myRef.child("preference").child("money").child(String.valueOf(user_id)).setValue("0");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        text = findViewById(R.id.text_sub);

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
                DatabaseReference myRef = database.getReference();
                DatabaseReference ref1 = myRef.child("Event/" + event_id + "/preference/deadline");
                ref1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Date date = dataSnapshot.getValue(Date.class);
                        if (date.after(new Date()))//compare if the deadline is not over
                            complete_pref();
                        ArrayList<Integer> ls = dataSnapshot.getValue(ArrayList.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
            //TODO 4.10 Don't forget to override onPause()
        });
    }
    public void complete_pref(){
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
                    //next step
                    a[0] = 0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}