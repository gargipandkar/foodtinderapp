package com.example.foodtinder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateEventFragment extends Fragment {

    public static final String TAG = "CreateEventFragment";
    Event currEvent;

    Button btnDatePicker, btnTimePicker;
    EditText txtDate, txtTime, txtName;     //ENCAP INTO EVENT CLASS
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String mName, mLocation, mBudget, group, deadline, eventStatus; //ENCAP INTO EVENT CLASS
    private Long eventDateTimeLong;
    Boolean checkEvent;

    Spinner locationPicker, budgetPicker, groupPicker, deadlinePicker;
    private Calendar eventDateTime = Calendar.getInstance();
    ArrayAdapter<String> dataAdapter;

    Button btnCreateEvent;

    DatabaseReference db, eventCount_ref, events_ref, users_ref;
    public int eventCount;      //ENCAP INTO EVENT CLASS
    public ArrayList<String> eventInfoList = new ArrayList<>();

    private CreateEventFragmentListener listener;
    public interface CreateEventFragmentListener {
        void onNewEvent(boolean checkEvent, String name, Long dateTime, String budget, String location, String status);
    }

//    public CreateEventFragment() {
//        // Required empty public constructor
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_create_event, container, false);

        txtName = v.findViewById(R.id.in_event_name);
        groupPicker = v.findViewById(R.id.group_options);
        btnDatePicker = v.findViewById(R.id.btn_date);
        btnTimePicker = v.findViewById(R.id.btn_time);
        txtDate = v.findViewById(R.id.in_date);
        txtTime = v.findViewById(R.id.in_time);
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        eventDateTime.set(year, monthOfYear, dayOfMonth);
                        eventDateTimeLong = eventDateTime.getTimeInMillis();
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        btnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        txtTime.setText(hourOfDay + ":" + minute);
                        eventDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        eventDateTime.set(Calendar.MINUTE, minute);
                        eventDateTimeLong = eventDateTime.getTimeInMillis();
                    }
                }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        locationPicker = v.findViewById(R.id.location_options);
        locationPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mLocation = parent.getItemAtPosition(position).toString();
                Log.i("Check", "Location selected: "+ mLocation);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mLocation = "Default";
            }
        });
        budgetPicker = v.findViewById(R.id.budget_options);
        budgetPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mBudget = parent.getItemAtPosition(position).toString();
                Log.i("Check", "Budget selected: "+ mBudget);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mBudget = "Default";
            }
        });
        deadlinePicker = v.findViewById(R.id.deadline_options);
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

        btnCreateEvent = v.findViewById(R.id.create_event_btn);
        btnCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mName = txtName.getText().toString();
                Log.i(TAG, mName);

                if (eventDateTimeLong == null) {
                    checkEvent = false;
                    eventStatus = "Waiting for preferences";
                } else {
                    checkEvent = true;
                    eventStatus = "Ready to swipe";
                }

                listener.onNewEvent(checkEvent, mName, eventDateTimeLong, mBudget, mLocation, eventStatus);
//                Intent toEventSel = new Intent(getActivity(), EventSelectionActivity.class);
//                startActivity(toEventSel);
            }
        });




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




        return v;
    }


    @Override
    public void onStart() {
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


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CreateEventFragmentListener){
            listener = (CreateEventFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement CreateEventFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
