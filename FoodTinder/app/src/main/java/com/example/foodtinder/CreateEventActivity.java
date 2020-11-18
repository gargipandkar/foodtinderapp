package com.example.foodtinder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

import java.util.ArrayList;
import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnDatePicker, btnTimePicker;
    EditText txtDate, txtTime, txtName;     //ENCAP INTO EVENT CLASS
    private int mYear, mMonth, mDay, mHour, mMinute;

    Spinner locationPicker, budgetPicker;
    private String location, budget;        //ENCAP INTO EVENT CLASS

    Button btnCreateEvent;

    DatabaseReference db, eventCount_ref, events_ref;
    public int eventCount;      //ENCAP INTO EVENT CLASS
    public String eventStatus;      //ENCAP INTO EVENT CLASS
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

        btnCreateEvent = findViewById(R.id.create_event_btn);
        btnCreateEvent.setOnClickListener(this);

        db = FirebaseDatabase.getInstance().getReference();
        eventCount_ref = db.child("eventCount");
        events_ref = db.child("events");

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
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

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
                    }
                }, mHour, mMinute, false);
            timePickerDialog.show();
        }
        if (v == btnCreateEvent){
            eventCount++;
            eventCount_ref.setValue(eventCount);
            events_ref.child("E"+eventCount);
            events_ref.child("E"+eventCount).child("Date").setValue(txtDate.getText().toString());
            events_ref.child("E"+eventCount).child("Time").setValue(txtTime.getText().toString());
            events_ref.child("E"+eventCount).child("Location").setValue(location);
            events_ref.child("E"+eventCount).child("Budget").setValue(budget);

            events_ref.child("E"+eventCount).child("Name").setValue(txtName);
            Log.i("Check", "Event created");

            //CHECK VALUE OF BUDGET AND LOCATION
            if (!budget.equals("To be decided") && !location.equals("To be decided")){ eventStatus = "Ready to swipe";}
            else {
                eventStatus = "Waiting for preferences";
                //if (budget.equals("To be decided")){ //SEND AS EXTRA}
                //if (location.equals("To be decided")){ //SEND AS EXTRA}
            }

            events_ref.child("E"+eventCount).child("Status").setValue(eventStatus);
            Log.i("Check", "Event created");

            //NEXT -> SEND HOST TO INDICATE PREFERENCES PAGE OR SWIPE PAGE

            //BELOW CODE IS FROM DISPLAY ALL GROUPS THEN NEXT -> DISPLAY ALL EVENTS
            Intent toListEvents = new Intent(CreateEventActivity.this, ListEventsActivity.class);
            Bundle eventExtras = new Bundle();
            eventExtras.putStringArrayList("eventInfoList", eventInfoList);
            eventExtras.putInt("eventCount", eventCount);
            toListEvents.putExtras(eventExtras);
            startActivity(toListEvents);
        }
    }


}