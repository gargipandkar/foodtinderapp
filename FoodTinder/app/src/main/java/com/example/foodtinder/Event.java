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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


// Event class to create and access Event object to update Firebase Realtime Database
// Implements parcelable to send objects between fragments
public class Event implements Parcelable {

    // TAG for debugging purposes
    public static final String TAG = "Event object";

    String name, group, host, location, budget, status, dateTimeString, id;
    long eventDateTime, prefDateTime;

    Boolean active;


    static DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    DatabaseReference ref;
    String decision;

    HashMap<String, Restaurant> placeDetails;      //USE IN PLACE OF listOfRestaurants
    HashMap<String, ArrayList<String>> placeDetailsPhotos;
    HashMap<String, Object> RestaurantPreferences;


    Event(){}

    Event(final String id){
        this.id = id; // Use as key for Firebase Database and store a copy of it here
        ref = db.child("EVENTS").child(id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<HashMap<String , Object>> gt = new GenericTypeIndicator<HashMap<String, Object>>() {};
                HashMap<String, Object> info = snapshot.getValue(gt);
                Log.i(TAG, info.toString());
                updateEvent(info, id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("Check", error.toString());
            }
        });
    }

    Event(String  id, String name, String group, String host, Long eventDateTime, String dateTimeString, String location, String budget,  String prefDeadline, String eventStatus){
        this.id = id;
        this.name = name;
        this.group = group;
        this.host = host;
        this.eventDateTime = eventDateTime;
        this.dateTimeString = dateTimeString;
        setPrefDeadline(prefDeadline);
        this.location = location;
        this.budget = budget;
        this.status = eventStatus;
        this.decision = "Undecided";
        this.ref = db.child("EVENTS").child(String.valueOf(this.id));
        this.placeDetails = null;
    }


    protected Event(Parcel in) {
        name = in.readString();
        group = in.readString();
        host = in.readString();
        location = in.readString();
        budget = in.readString();
        status = in.readString();
        eventDateTime = in.readLong();
        dateTimeString = in.readString();
        prefDateTime = in.readLong();
        id = in.readString();
        byte tmpActive = in.readByte();
        active = tmpActive == 0 ? null : tmpActive == 1;
        decision = in.readString();
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


    public String getId() {
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
    public String getDateTimeString() {
        return dateTimeString;
    }
    public long getPrefDateTime(){return this.prefDateTime;}
    public Boolean getActive(){return this.active;}


    public Event updateEvent(HashMap<String, Object> info, String id){
        this.id = id;
        this.name = (String) info.get("name");
        this.group = (String) info.get("group");
        this.host = (String) info.get("host");
        this.eventDateTime = (Long) info.get("eventDateTime");
        this.prefDateTime = (Long) info.get("prefDateTime");
        updateEventStatus(id);
        checkExpiry(id);
        this.decision = (String) info.get("decision");
        this.placeDetails = (HashMap<String, Restaurant>) info.get("placeDetails");
        return this;
    }

    public void updateEventStatus(final String id){
        final String[] chosen = {""};
        ref = db.child("EVENTS").child(String.valueOf(id));
        ref.child("decision").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chosen[0] = snapshot.getValue(String.class);
                setDecision(chosen[0], id);
                if (!chosen[0].equals("Undecided"))
                    setStatus("Match found", id);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    void setStatus(String status, String id){
        ref = db.child("EVENTS").child(String.valueOf(id));
        this.status = status;
        ref.child("status").setValue(status);
    }

    void setDecision(String decision, String id){
        ref = db.child("EVENTS").child(String.valueOf(id));
        this.decision = decision;
        ref.child("decision").setValue(decision);
    }

    public void setPrefDeadline(String prefDeadline){
        Calendar Edt = Calendar.getInstance();
        Edt.setTimeInMillis(this.eventDateTime);
        Calendar Pdt = Calendar.getInstance();
        Pdt.setTimeInMillis(this.eventDateTime);
        switch (prefDeadline){
            case "1 day before": Pdt.add(Calendar.DATE, -1);
                break;

            case "1 week before": Pdt.add(Calendar.WEEK_OF_MONTH, - 1);
                break;

            case "Today": Pdt.set(Calendar.HOUR_OF_DAY, 23);
                Pdt.set(Calendar.MINUTE, 59);
                break;

            default: break;
        }

        // Check if deadline is before but after event creation
        // If check fails, set deadline to 1-hour before event by default
        Calendar Cdt = Calendar.getInstance();
        if (Pdt.after(Edt) || Pdt.before(Cdt)){
            Pdt.setTimeInMillis(this.eventDateTime);
            Pdt.add(Calendar.HOUR_OF_DAY, -1);
        }
        this.prefDateTime = Pdt.getTimeInMillis();
    }

    public void checkExpiry(String id){
        Long now = Calendar.getInstance().getTimeInMillis();
        Log.i("Event Class", "EXPIRY=" + now + "/" + eventDateTime);
        if (eventDateTime < now){
            active = false;
            // Remove from all occurrence of this event from Firebase Realtime Database
            // including from User's list of events
            ref = db.child("EVENTS").child(id);
            ref.removeValue();
            db.child("USERS").child(User.getId()).child("listOfEvents").child(id).removeValue();
        }
        else {active = true;}
    }


    public void passedDeadline(){
        Long now = Calendar.getInstance().getTimeInMillis();
        Log.i(TAG, "PREF="+now+"/"+prefDateTime);
        if (now > prefDateTime){
            pseudoMatch(id);
        }
    }

    public void pseudoMatch(final String id){
        ref = db.child("EVENTS").child(id);
        DatabaseReference restVote_ref = ref.child("RestaurantPreferences/listOfVotes");
        restVote_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<HashMap<String, Integer>> gt = new GenericTypeIndicator<HashMap<String, Integer>>() {};
                HashMap<String, Integer> listVotes = snapshot.getValue(gt);

                Map.Entry<String, Integer> maxEntry = null;
                if (listVotes != null){
                    int max = Collections.max(listVotes.values());

                    for(Map.Entry<String, Integer> entry : listVotes.entrySet()) {
                        Integer value = entry.getValue();
                        if(null != value && max == value)
                            maxEntry = entry;

                    }

                    String rest = maxEntry.getKey();
                    setDecision(rest, id);
                    updateEventStatus(id);
                }
                else Log.i(TAG, "No list of votes found");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
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
        parcel.writeLong(eventDateTime);
        parcel.writeString(dateTimeString);
        parcel.writeLong(prefDateTime);
        parcel.writeString(id);
        parcel.writeByte((byte) (active == null ? 0 : active ? 1 : 2));
        parcel.writeString(decision);
    }

}