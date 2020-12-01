package com.example.foodtinder;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Event implements Parcelable {

    String name, group, host, location, budget, status;
    long eventDateTime, prefDateTime;
    int id;     //USE AS KEY FOR DATABASE, BUT STORE COPY ALSO
    Boolean active;


    static DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    DatabaseReference ref;
    String decision;


    HashMap<String, ArrayList<String>> RestaurantPreferences = null;


    ArrayList<Restaurant> listOfRestaurant;

    Event(){}


    // for swiping.java debugging
    Event(Integer id){
        this.id = id;
        this.ref = db.child("EVENTS").child(String.valueOf(id));
        this.ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<HashMap<String , Object>> gt = new GenericTypeIndicator<HashMap<String, Object>>() {};
                HashMap<String, Object> info = snapshot.getValue(gt);
                updateEvent(info);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("Check", error.toString());
            }
        });
    }



    Event(int id, String name, String group, String host, Long eventDateTime, String location, String budget, String eventStatus){
        this.id = id;
        this.name = name;
        this.group = group;
        this.host = host;
        this.eventDateTime = eventDateTime;
        this.location = location;
        this.budget = budget;
        this.status = eventStatus;
        this.decision = "Undecided";
        this.ref = db.child("EVENTS").child(String.valueOf(this.id));
        this.listOfRestaurant = null;
    }

    protected Event(Parcel in) {
        name = in.readString();
        group = in.readString();
        host = in.readString();
        location = in.readString();
        budget = in.readString();
        status = in.readString();
//        byte tmpActive = in.readByte();
//        active = tmpActive == 0 ? null : tmpActive == 1;
//        decision = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public void createEvent(){}


    public int getId() {
        return id;
    }
    public String getName(){return this.name;}
    public String getGroup(){return this.group;}
    public String getHost(){return this.host;}
    public String getLocation(){return this.location;}
    public String getBudget(){return this.budget;}
    public String getStatus(){return this.status;}
    public String getDecision(){return this.decision;}
    public long getEventDateTime(){return this.eventDateTime;}
    public long getPrefDateTime(){return this.prefDateTime;}
    public Boolean getActive(){return this.active;}
    public ArrayList<Restaurant> getListOfRestaurant() {
        return listOfRestaurant;
    }
    public HashMap<String, ArrayList<String>> getRestaurantPreferences() {
        return RestaurantPreferences;
    }



    @Exclude
    public ArrayList<String> getDisplayDetails(){
        ArrayList<String> display = new ArrayList<>();
        display.add(this.name);
        display.add(this.group);
        display.add(Long.toString(this.eventDateTime));
        display.add(this.status);
//        if (!this.status.equals("Decided"))
//            display.add(this.prefDateTime.toString());
//        if (!this.decision.equals("Undecided"))
//            display.add(this.decision);
        return display;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(group);
        parcel.writeString(host);
        parcel.writeString(location);
        parcel.writeString(budget);
        parcel.writeString(status);
//        parcel.writeByte((byte) (active == null ? 0 : active ? 1 : 2));
        parcel.writeString(decision);
    }

    public void updateEvent(HashMap<String, Object> info){
        this.name = (String) info.get("name");
        this.group = (String) info.get("group");
        this.host = (String) info.get("host");
//        updateEventStatus();
        checkExpiry();
        this.decision = (String) info.get("decision");
        this.listOfRestaurant = (ArrayList<Restaurant>) info.get("listOfRestaurant");
    }

//    public void updateEventStatus(){
//        final String[] chosen = {""};
//        ref.child("decision").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                chosen[0]=snapshot.getValue(String.class);
//                setDecision(chosen[0]);
//                if (!chosen[0].equals("Undecided"))
//                    setStatus("Match found");
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) { }
//        });
//    }

    public void checkExpiry(){
//        Calendar now = Calendar.getInstance();
//        if (this.eventDateTime.after(now))
//            active = false;
//        active = true;
    }

//    void setStatus(String status){
//        this.status = status;
//        ref.child("status").setValue(status);
//    }

//    void setDecision(String decision){
//        this.decision = decision;
//        ref.child("decision").setValue(decision);
//    }
}