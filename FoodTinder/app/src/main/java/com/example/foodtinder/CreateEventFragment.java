package com.example.foodtinder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class CreateEventFragment extends Fragment {

    public static final String TAG = "CreateEventFragment";
    Event currEvent;

    TextView btnDatePicker, btnTimePicker;
    EditText txtName;     //ENCAP INTO EVENT CLASS
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String mName, mLocation, mBudget, group, deadline, eventStatus, dateTimeString; //ENCAP INTO EVENT CLASS
    private Long eventDateTimeLong;

    static final Parser parser = Parser.getParser();
    Spinner locationPicker, budgetPicker, groupPicker, deadlinePicker;
    private Calendar eventDateTime = Calendar.getInstance();
    ArrayAdapter<String> dataAdapter;

    Button btnCreateEvent;
    DatabaseReference db, eventCount_ref, events_ref, users_ref;

    public int eventCount;      //ENCAP INTO EVENT CLASS
    public ArrayList<String> eventInfoList = new ArrayList<>();

    private CreateEventFragmentListener listener;
    public interface CreateEventFragmentListener {
        void onNewEventUpdate();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_create_event, container, false);

        Toolbar toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
//        ((AppCompatActivity)getActivity()).setDisplayHomeAsUpEnabled(true);
//        ((AppCompatActivity)getActivity()).setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).setTitle("Create New Event");

        db = FirebaseDatabase.getInstance().getReference();
        events_ref = db.child("EVENTS");
        users_ref = db.child("USERS").child(User.getId());
        txtName = v.findViewById(R.id.in_event_name);
        groupPicker = v.findViewById(R.id.group_options);
        btnDatePicker = v.findViewById(R.id.btn_date);
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
                        dateTimeString = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        btnDatePicker.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        eventDateTime.set(year, monthOfYear, dayOfMonth);
                        eventDateTimeLong = eventDateTime.getTimeInMillis();
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        btnTimePicker = v.findViewById(R.id.btn_time);
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
                        if (hourOfDay > 12 ){
                            dateTimeString += ", " + hourOfDay + "." + minute + "pm";
                        } else {
                            dateTimeString += ", " + hourOfDay + "." + minute + "am";
                        }
                        btnTimePicker.setText(hourOfDay + ":" + minute);
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
                //CHECK IF FORM IS FILLED APPROPRIATELY
                Calendar now = Calendar.getInstance();
                Calendar buffer = Calendar.getInstance();
                buffer.add(Calendar.HOUR_OF_DAY, 2);

                if (group.equals("Select Group") || txtName.getText().equals("") ||
                        eventDateTime == null || btnDatePicker.getText().length()==0 || btnTimePicker.getText().length()==0 ||
                        mLocation.equals("Select Location") || mBudget.equals("Select Budget")) {
                    Toast.makeText(getContext(), "DO NOT LEAVE FIELD BLANK", Toast.LENGTH_LONG).show();
                }

                else if (eventDateTime.getTimeInMillis()<=now.getTimeInMillis()){
                    Toast.makeText(getContext(), "DATE-TIME HAS ALREADY PASSED", Toast.LENGTH_LONG).show();
                }

                else if (eventDateTime.getTimeInMillis()<buffer.getTimeInMillis()){
                    Toast.makeText(getContext(), "PICK A LATER DATE-TIME", Toast.LENGTH_LONG).show();
                }

                //PROCEED WHEN ALL FORM CONTENT IS VALID
                else {
                    eventStatus = "Created";
                    //CONVERT CALENDAR TO LONG
                    Long dt = eventDateTime.getTimeInMillis();
                    mName = txtName.getText().toString();
                    Log.i(TAG, mName);
                    //WRITE TO "EVENTS" DATABASE
                    final String eventId = db.child("EVENTS").push().getKey();
                    currEvent = new Event(eventId, mName, group, User.getId(), eventDateTimeLong, dateTimeString, mLocation, mBudget, eventStatus);
                    events_ref.child(eventId).setValue(currEvent);
                    Log.i("Check", "Event created");

                    //Call API to get restaurants and update status when ready
                    parser.setEventId(eventId);
                    parser.queryInfo.put("min_price", "1");
                    parser.queryInfo.put("max_price", String.valueOf(mBudget.length()));

                    fetchData process = new fetchData();
                    URLGenerator urlGenerator = new URLGenerator();
                    String urlStr = urlGenerator.generateFindPlaceURL(mLocation, "1", "4");
                    process.setType("findplace");
                    process.setUrl(urlStr);
                    process.execute();

                    //UPDATE "USERS", IF NEEDED "GROUPS" DATABASE
                    users_ref.child("listOfEvents").child(eventId).setValue(true);
                    Group.retrieveGroup(group, new DatabaseCallback() {
                        @Override
                        public void onCallback(ArrayList<String> ls) { }
                        @Override
                        public void onCallback(Event event) { }
                        @Override
                        public void onCallback(ArrayList<Restaurant> allRest, boolean done) { }

                        @Override
                        public void onCallback(Group grp) {
                            grp.updateAllUsers(eventId, group);
                        }


                    });


                listener.onNewEventUpdate();

            };
        }; });


        setGroupOptions();


        return v;
    }


    @Override
    public void onStart() {
        super.onStart();

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

    private void setGroupOptions(){
        dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, new ArrayList<String>());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupPicker.setAdapter(dataAdapter);
        final ArrayList<String> groupOptions = new ArrayList<>();
        dataAdapter.add("Select Group");
        User.setUserGroups(users_ref.child("inGroups"), new DatabaseCallback() {
            @Override
            public void onCallback(ArrayList<String> ls) {
                groupOptions.addAll(ls);
//                ArrayList<String> groupNames = new ArrayList<>();
                for(String grpId: groupOptions){
                    Group.retrieveGroup(grpId, new DatabaseCallback() {
                        @Override
                        public void onCallback(ArrayList<String> ls) { }
                        @Override
                        public void onCallback(Event event) { }
                        @Override
                        public void onCallback(ArrayList<Restaurant> allRest, boolean done) { }
                        @Override
                        public void onCallback(Group grp) {
                            dataAdapter.add(grp.name);
                        }
                    });

                    dataAdapter.notifyDataSetChanged();
                    User.inGroups.clear();
                    User.inGroups.addAll(ls);
                }


            }

            @Override
            public void onCallback(Event event) { }
            public void onCallback (Group group){}
            public void onCallback(ArrayList<Restaurant> allRest, boolean done){}
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
