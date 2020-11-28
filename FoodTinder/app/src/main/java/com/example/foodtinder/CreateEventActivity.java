package com.example.foodtinder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener {

    Event currEvent;

    Button btnDatePicker, btnTimePicker;
    EditText txtDate, txtTime, txtName;     //ENCAP INTO EVENT CLASS
    private int mYear, mMonth, mDay, mHour, mMinute;

    Spinner locationPicker, budgetPicker, groupPicker, deadlinePicker;
    private String location, budget, group, deadline, eventStatus;        //ENCAP INTO EVENT CLASS
    private Calendar eventDateTime = Calendar.getInstance();
    ArrayAdapter<String> dataAdapter;

    Button btnCreateEvent;

    DatabaseReference db, eventCount_ref, events_ref, users_ref;
    public int eventCount;      //ENCAP INTO EVENT CLASS
    public ArrayList<String> eventInfoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventcreation);

        txtName = findViewById(R.id.in_event_name);

        btnDatePicker = findViewById(R.id.btn_date);
        btnTimePicker = findViewById(R.id.btn_time);
        txtDate = findViewById(R.id.in_date);
        txtTime = findViewById(R.id.in_time);

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);

        locationPicker = findViewById(R.id.location_options);
        budgetPicker = findViewById(R.id.budget_options);
        groupPicker = findViewById(R.id.group_options);
        deadlinePicker = findViewById(R.id.deadline_options);

        btnCreateEvent = findViewById(R.id.create_event_btn);
        btnCreateEvent.setOnClickListener(this);

        db = FirebaseDatabase.getInstance().getReference();
        events_ref = db.child("EVENTS");
        eventCount_ref = events_ref.child("eventCount");
        users_ref = db.child("USERS").child(User.getId());

        eventCount_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventCount = dataSnapshot.getValue(int.class);
                Log.i("Check", "# of events : "+eventCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Check", databaseError.toException());
            }
        });

        events_ref.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventInfoList.clear();
                for (DataSnapshot eventSnapshot: dataSnapshot.getChildren()) {
                    eventInfoList.add(eventSnapshot.getKey());
                    Log.i("Check", eventInfoList.toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Check", databaseError.toException());
            }
        });

        setGroupOptions();

    }

    protected void onStart(){
        super.onStart();

        //TODO check if user's group list is empty, show toast "YOU AREN'T IN ANY GROUPS" and don't come to this form

        groupPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                group = parent.getItemAtPosition(position).toString();
                Log.i("Check", "Group selected: "+group);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                group = null;
            }
        });

        locationPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                location = parent.getItemAtPosition(position).toString();
                Log.i("Check", "Location selected: "+location);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                location = "Default";
            }
        });

        budgetPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                budget = parent.getItemAtPosition(position).toString();
                Log.i("Check", "Budget selected: "+budget);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                budget = "Default";
            }
        });

        deadlinePicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                deadline = parent.getItemAtPosition(position).toString();
                Log.i("Check", "Deadline selected: "+deadline);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                deadline = "Default";
            }
        });

    }

    private void setGroupOptions(){
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<String>());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupPicker.setAdapter(dataAdapter);
        final ArrayList<String> groupOptions = new ArrayList<>();
        dataAdapter.add("-- Assign to group --");
        User.setUserGroups(users_ref.child("listOfGroups"), new DatabaseCallback() {
            @Override
            public void onCallback(ArrayList<String> ls) {
                groupOptions.addAll(ls);
                //dataAdapter.clear();
                dataAdapter.addAll(groupOptions);
                dataAdapter.notifyDataSetChanged();

                User.inGroups.clear();
                User.inGroups.addAll(ls);

                Log.i("Check", ls.toString());
                Log.i("Check", User.getId());
                Log.i("Check", groupOptions.toString());
            }
        });

    }

    @Override
    public void onClick(View v) {

        if (v == btnDatePicker) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    eventDateTime.set(year, monthOfYear, dayOfMonth);
                }
                }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }

        if (v == btnTimePicker) {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        txtTime.setText(hourOfDay + ":" + minute);
                        eventDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        eventDateTime.set(Calendar.MINUTE, minute);
                    }
                }, mHour, mMinute, false);
            timePickerDialog.show();
        }
        if (v == btnCreateEvent){
            eventCount++;
            eventCount_ref.setValue(eventCount);
            String eventId = String.valueOf(eventCount);

            //CHECK VALUE OF BUDGET AND LOCATION
            if (!(budget.equals("To be decided") || location.equals("To be decided"))){ eventStatus = "Ready to swipe";}
            else {
                eventStatus = "Waiting for preferences";
                //if (budget.equals("To be decided")){ //SEND AS EXTRA}
                //if (location.equals("To be decided")){ //SEND AS EXTRA}
            }

            //WRITE TO "EVENTS" DATABASE
            currEvent = new Event(txtName.getText().toString(), group, User.getId(), eventDateTime, location, budget, deadline, eventStatus);
            events_ref.child(eventId).setValue(currEvent);
            Log.i("Check", "Event created");

            //UPDATE "USERS", IF NEEDED "GROUPS" DATABASE
            users_ref.child("listOfEvents").child(eventId).setValue(true);
            //TODO update all users belonging to the group

            //NEXT -> SEND HOST TO INDICATE PREFERENCES PAGE OR SWIPE PAGE
            Intent backToHome  = new Intent(CreateEventActivity.this, ListEventsActivity.class);
            startActivity(backToHome);
        }
    }


}