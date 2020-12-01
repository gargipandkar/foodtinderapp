package com.example.placeslib;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class IndicatePreferencesActivity_UNUSED extends AppCompatActivity {

    Spinner location_pref_options, budget_pref_options;
    Button btnSubmit;

    //TODO send Event object selectedEvent from clicking event on list
    String user_id = User.getId();
    Event clickedEvent = new Event(3);  //TEMPORARY, GETS EXISTING EVENT

    final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    DatabaseReference budgetPref_ref = clickedEvent.ref.child("Preferences").child("listOfBudget");
    DatabaseReference locationPref_ref = clickedEvent.ref.child("Preferences").child("listOfLocation");
    DatabaseReference completedPref_ref = clickedEvent.ref.child("Preferences").child("listOfCompleted");

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        //TODO add logic to check if field is "To be decided", only then show spinner
        //if budget=="To be decided", show budget spinner
        //if location=="To be decided", show location spinner
        addListenerOnSpinner_budget();
        addListenerOnButton();
        addListenerOnSpinner_location();
    }

    // intialize spinners
    public void addListenerOnSpinner_budget() {
        budget_pref_options = (Spinner) findViewById(R.id.budget_pref_options);
        budget_pref_options.setOnItemSelectedListener(new CustomOnItemSelectedListener_UNUSED());
    }

    public void addListenerOnSpinner_location() {
        location_pref_options = (Spinner) findViewById(R.id.location_pref_options);
        location_pref_options.setOnItemSelectedListener(new CustomOnItemSelectedListener_UNUSED());
    }

    // get the selected dropdown list value
    public void addListenerOnButton() {

        location_pref_options = (Spinner) findViewById(R.id.location_pref_options);
        budget_pref_options = (Spinner) findViewById(R.id.budget_pref_options);
        btnSubmit = (Button) findViewById(R.id.preferences_btn);

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

               // ADD USER TO LIST OF THOSE WHO HAVE FINISHED INDICATING PREFERENCES
                completedPref_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> ls = dataSnapshot.getValue(ArrayList.class);
                        ls.add(user_id);
                        completedPref_ref.push().setValue(ls.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                // WRITE LOCATION PREFERENCE
                locationPref_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> ls = dataSnapshot.getValue(ArrayList.class);
                        ls.add(budget_pref_options.getSelectedItem().toString());
                        locationPref_ref.push().setValue(ls.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                // WRITE BUDGET PREFERENCE
                budgetPref_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> ls = dataSnapshot.getValue(ArrayList.class);
                        ls.add(location_pref_options.getSelectedItem().toString());
                        budgetPref_ref.push().setValue(ls.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                Intent next = new Intent(IndicatePreferencesActivity_UNUSED.this, ListEventsActivity.class);
                startActivity(next);
            }
        });
    
    }
}
